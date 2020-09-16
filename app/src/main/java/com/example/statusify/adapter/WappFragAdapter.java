package com.example.statusify.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.statusify.FullscreenActivity;
import com.example.statusify.MainActivity;
import com.example.statusify.datamodel.DataModel;
import com.example.statusify.R;
import com.google.android.material.card.MaterialCardView;
import com.snatik.storage.Storage;

import java.io.File;

import java.util.ArrayList;

public class WappFragAdapter extends RecyclerView.Adapter<WappFragAdapter.ViewHolder> {


    private ArrayList<DataModel> dataModels;
    public Context context;
    String appName;

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView fav,download;
        private Context context;

        ImageView status, playBtn;

        ConstraintLayout parentLayout;
        public ProgressBar progressBar;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            download = itemView.findViewById(R.id.download);

            fav = itemView.findViewById(R.id.favourite);
            status = itemView.findViewById(R.id.image);
            parentLayout = itemView.findViewById(R.id.WApp_gridItemParentLayout);
            progressBar = itemView.findViewById(R.id.progress);
            playBtn = itemView.findViewById(R.id.playBtn);
            context = itemView.getContext();
        }
    }

        public WappFragAdapter(ArrayList<DataModel> dataModels, Context context , String appName) {
        this.dataModels = dataModels;
        this.context = context;
        this.appName = appName;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wappfrag_grid_item_layout,parent ,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d("Custom Log by Saket", "In home screen onBindViewHolder is called for :- " + position);
        final DataModel dataModel = this.dataModels.get(position);
        if (dataModel.getIsDownloaded()){
            holder.download.setColorFilter(context.getResources().getColor(R.color.grid_download_enabled));
        }else{
            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveMedia(dataModel.getFilePath(),context,"downloads");
                    holder.download.setColorFilter(context.getResources().getColor(R.color.grid_download_enabled));
                }
            });
        }


        if (dataModel.getIsVideo())
            holder.playBtn.setVisibility(View.VISIBLE);

//        if (dataModel.getIsFavourite())
//            holder.fav.setVisibility(View.GONE);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,FullscreenActivity.class);
                i.putExtra("foldername", dataModel.getFilePath());
                i.putExtra("itemPosition", holder.getAdapterPosition());
                i.putExtra("appType", appName);
                i.putExtra("fragmentName", "Wapp");
                context.startActivity(i);
            }
        });

//        holder.download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveMedia(dataModel.getFilePath(),context,"downloads");
//                holder.download.setVisibility(View.INVISIBLE);
//            }
//        });




    }


    @Override
    public int getItemCount() {
        return dataModels.size();
    }



    public void saveMedia(String str, Context context, String subFolder){
        Storage storage = new Storage(context);
        try {
            String sb2 = Environment.getExternalStorageDirectory() +
                    File.separator +
                    context.getString(R.string.app_name) +
                    File.separator +
                    appName +
                    File.separator +
                    subFolder;
            if (!new File(sb2).exists()){
                new File(sb2).mkdir();
            }

            String sb3 = sb2 +
                    File.separator +
                    new File(str).getName();
            storage.copy(str, sb3);
            Toast.makeText(context, "Added to "+ subFolder +" Successfully", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "Error Ocurred", Toast.LENGTH_SHORT).show();
        }

    }

}
