package com.example.statusify.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.sip.SipSession;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.statusify.FullscreenActivity;
import com.example.statusify.datamodel.DataModel;
import com.example.statusify.R;
import com.example.statusify.interfaces.DeleteListener;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.List;

public class SavedFragAdapter extends RecyclerView.Adapter<SavedFragAdapter.ViewHolder> {

    List<DataModel> dataModels;
    public Context context;
    String appName;
    DeleteListener deleteListener;

//    public interface DeleteListener{
//        void foo(int position);
//    }
    public SavedFragAdapter(List<DataModel> dataModels, Context context, String appName, DeleteListener deleteListener) {
        this.dataModels = dataModels;
        this.context = context;
        this.appName = appName;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.savedfrag_grid_item_layout,parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d("Custom Log by Saket", "In Downloaded status screen onBindViewHolder is called for :- " + position);
        final DataModel dataModel = dataModels.get(position);
        File file = new File(dataModel.getFilePath());
        if (!file.isDirectory()){
            try {
                Glide.with(context).load(file).
                        placeholder(R.color.cardView_default_color).
                        listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.status);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(deleteListener.delete(position,dataModel.getFilePath())){
                    deleteMedia(dataModel.getFilePath(),context);
                    updateRecyclerView(position);
//                }

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FullscreenActivity.class);
                i.putExtra("foldername", dataModel.getFilePath());
                i.putExtra("itemPosition", holder.getAdapterPosition());
                i.putExtra("appType", appName);
                i.putExtra("fragmentName", "down");
                context.startActivity(i);
            }
        });

        if (dataModel.getIsVideo())
            holder.playBtn.setVisibility(View.VISIBLE);
    }

    private void updateRecyclerView(int position) {
        dataModels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataModels.size());
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private Context context;
        public ImageView delete,playBtn;
        public ImageView status;
        public ProgressBar progressBar;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            status = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progress);
            context = itemView.getContext();
            playBtn = itemView.findViewById(R.id.playBtn);
        }
    }

    public void deleteMedia(String str, Context context){
        Storage storage = new Storage(context);
        try {
            String sb2 = Environment.getExternalStorageDirectory() +
                    File.separator +
                    context.getString(R.string.app_name) +
                    File.separator +
                    appName +
                    File.separator +
                    "downloads";
            if (!new File(sb2).exists()){
                new File(sb2).mkdir();
            }

            String sb3 = sb2 +
                    File.separator +
                    new File(str).getName();
            storage.deleteFile(sb3);

        }catch (Exception e){
            Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
        }

    }

}
