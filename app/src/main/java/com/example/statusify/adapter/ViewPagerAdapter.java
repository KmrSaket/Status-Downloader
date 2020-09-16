package com.example.statusify.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.statusify.R;
import com.example.statusify.blur.BlurLayout;
import com.example.statusify.datamodel.DataModel;
import com.snatik.storage.Storage;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>{

    ArrayList<DataModel> dataModels;
    public Context context;
    String appName;
    String fragmentName;

    public ViewPagerAdapter(ArrayList<DataModel> dataModels, Context context, String appName , String fragmentName){
        this.dataModels = dataModels;
        this.context = context;
        this.appName = appName;
        this.fragmentName = fragmentName;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{


        SeekBar progressBar;
        VideoView videoholder;
        ImageView back,share,delete,playBtn;
        LottieAnimationView favourite;
        ImageView download;
        public ImageView imageholder;
        ConstraintLayout alertBox;
        Button alertDelte,alertCancel;
        BlurLayout blurLayout;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            alertCancel = itemView.findViewById(R.id.alert_cancel);
            alertCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blurLayout.setVisibility(View.INVISIBLE);
                    alertBox.setVisibility(View.INVISIBLE);
                }
            });
            alertDelte = itemView.findViewById(R.id.alert_delete);
            blurLayout = itemView.findViewById(R.id.blurLayout);
            blurLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blurLayout.setVisibility(View.INVISIBLE);
                    alertBox.setVisibility(View.INVISIBLE);
                }
            });
            alertBox = itemView.findViewById(R.id.alertBox);
            share = itemView.findViewById(R.id.share);
            delete = itemView.findViewById(R.id.delete);
            back = itemView.findViewById(R.id.backbtn);
            favourite = itemView.findViewById(R.id.favourite);
            imageholder = itemView.findViewById(R.id.imageholder);
            download = itemView.findViewById(R.id.download);
            videoholder = itemView.findViewById(R.id.videoholder);
            playBtn = itemView.findViewById(R.id.playBtn);
            progressBar = itemView.findViewById(R.id.videoProgressbar);
            progressBar.setClickable(false);

            favourite.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(context.getResources().getColor(R.color.heart_Enabled_fullscreen), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_item,parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataModel dataModel = this.dataModels.get(position);
        final File file = new File(dataModel.getFilePath());
        if (!file.isDirectory()){

            Log.d("VVV", dataModel.getIsVideo() + " " + position+1);
            if (!dataModel.getIsVideo()){
                try {
                    Glide.with(context).load(file).
                            listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                holder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                holder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.imageholder);

                }catch (Exception e){
                    e.printStackTrace();
                }
                holder.playBtn.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
            }
            else {
                try {
                    Glide.with(context).load(file).
                            listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                holder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                holder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.imageholder);

                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("shown", "this");
                holder.videoholder.setVisibility(View.VISIBLE);
                holder.playBtn.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);
                Uri videopath = Uri.parse(file.getPath());
                holder.videoholder.setVideoURI(videopath);
                final double[] startTime = {0};
                final double[] finalTime = {0};
                final Handler myHandler = new Handler();

                holder.playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imageholder.setVisibility(View.GONE);
                        if (holder.videoholder.isPlaying()){
                            holder.videoholder.pause();
                            holder.playBtn.setImageResource(R.drawable.playbtn_fullscreen);
                        }else {
                            holder.videoholder.start();
                            finalTime[0] = holder.videoholder.getDuration();
                            startTime[0] = holder.videoholder.getCurrentPosition();
//                            if (oneTimeOnly == 0) {
                                holder.progressBar.setMax((int) finalTime[0]);
//                                oneTimeOnly = 1;
//                            }
                            holder.progressBar.setProgress((int)startTime[0]);
                            myHandler.postDelayed(UpdateVideoTime,10);
                            holder.playBtn.setImageResource(R.drawable.pausebtn_fullscreen);

                        }

                    }
                    Runnable UpdateVideoTime = new Runnable() {
                        public void run() {
                            startTime[0] = holder.videoholder.getCurrentPosition();
                            holder.progressBar.setProgress((int)startTime[0]);
                            myHandler.postDelayed(this, 10);
                        }
                    };
                });
                holder.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            holder.progressBar.setProgress(progress);
                            holder.videoholder.seekTo(progress);
                        }
                        else if (!fromUser && progress==seekBar.getMax()){
                            holder.imageholder.setVisibility(View.VISIBLE);
                            holder.videoholder.setVisibility(View.VISIBLE);
                            holder.progressBar.setProgress(0);
                            holder.playBtn.setImageResource(R.drawable.playbtn_fullscreen);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

            }
        }


        holder.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)holder.itemView.getContext()).finish();
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType(URLConnection.guessContentTypeFromName(dataModel.getFileName()));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse("file://"+dataModel.getFilePath()));
                holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share image using"));

            }
        });

        switch (fragmentName) {
            case "Wapp":
                if (dataModel.getIsDownloaded()) {
                    holder.download.setColorFilter(context.getResources().getColor(R.color.fullscreen_downloadDisablecolor));
                    holder.delete.setColorFilter(context.getResources().getColor(R.color.fullscreen_deleteEnablecolor));
                } else {
                    holder.delete.setColorFilter(context.getResources().getColor(R.color.disablecolor));
                    holder.download.setColorFilter(context.getResources().getColor(R.color.enablecolor));
                }

                if (dataModel.getIsFavourite()){
                    holder.favourite.setProgress(1f);
                }else {
                    holder.favourite.setProgress(0f);
                }

                holder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!dataModel.getIsDownloaded()) {
                            holder.download.setColorFilter(context.getResources().getColor(R.color.fullscreen_downloadDisablecolor));
                            holder.delete.setColorFilter(context.getResources().getColor(R.color.fullscreen_deleteEnablecolor));
                            dataModel.setDownloaded(true);
                            dataModel.setDownPath(save(dataModel.getFilePath(), context, "downloads"));
                        }
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataModel.getIsDownloaded()){
                            holder.alertBox.setVisibility(View.VISIBLE);
                            holder.blurLayout.setVisibility(View.VISIBLE);
                            holder.alertDelte.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteMedia(dataModel.getDownPath(), context);
                                    holder.delete.setColorFilter(context.getResources().getColor(R.color.disablecolor));
                                    holder.download.setColorFilter(context.getResources().getColor(R.color.enablecolor));
                                    dataModel.setDownloaded(false);
                                    holder.alertBox.setVisibility(View.INVISIBLE);
                                    holder.blurLayout.setVisibility(View.INVISIBLE);
                                }
                            });


                        }
                    }
                });

                holder.favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataModel.getIsFavourite()){
                            holder.favourite.setSpeed(-1f);
                            holder.favourite.playAnimation();
                            deleteMedia(dataModel.getFavPath(), context);
                            dataModel.setFavourite(false);
                        }else {
                            holder.favourite.setSpeed(1f);
                            holder.favourite.playAnimation();
                            dataModel.setFavourite(true);
                            dataModel.setFavPath(save(dataModel.getFilePath(), context,"favourites"));
                        }
                    }
                });
                break;


            case "down":

                if (dataModel.getIsFavourite()) holder.favourite.setProgress(1f);
                else holder.favourite.setProgress(0f);

                if (dataModel.getIsDownloaded()) {
                    holder.download.setColorFilter(context.getResources().getColor(R.color.fullscreen_downloadDisablecolor));
                    holder.delete.setColorFilter(context.getResources().getColor(R.color.fullscreen_deleteEnablecolor));
                } else {
                    holder.delete.setColorFilter(context.getResources().getColor(R.color.disablecolor));
                    holder.download.setColorFilter(context.getResources().getColor(R.color.enablecolor));
                }

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.blurLayout.setVisibility(View.VISIBLE);
                        holder.alertBox.setVisibility(View.VISIBLE);
                        holder.alertDelte.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteMedia(dataModel.getDownPath(), context);
                                updateRecyclerView(position);
                                holder.blurLayout.setVisibility(View.INVISIBLE);
                                holder.alertBox.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                });

                holder.favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataModel.getIsFavourite()){
                            holder.favourite.setSpeed(-1f);
                            holder.favourite.playAnimation();
                            deleteMedia(dataModel.getFavPath(), context);
                            dataModel.setFavourite(false);
                        }else {
                            holder.favourite.setSpeed(1f);
                            holder.favourite.playAnimation();
                            dataModel.setFavPath(save(dataModel.getDownPath(), context,"favourites"));
                            dataModel.setFavourite(true);
                        }
                    }
                });
                break;


            case "fav":
                if (dataModel.getIsFavourite()) holder.favourite.setProgress(1f);
                else holder.favourite.setProgress(0f);

                if (dataModel.getIsDownloaded()) {
                    holder.download.setColorFilter(context.getResources().getColor(R.color.fullscreen_downloadDisablecolor));
                    holder.delete.setColorFilter(context.getResources().getColor(R.color.fullscreen_deleteEnablecolor));
                } else {
                    holder.delete.setColorFilter(context.getResources().getColor(R.color.disablecolor));
                    holder.download.setColorFilter(context.getResources().getColor(R.color.enablecolor));
                }

                holder.favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataModel.getIsFavourite()){
                            holder.favourite.playAnimation();
                            deleteMedia(dataModel.getFavPath(), context);
                            updateRecyclerView(position);
                        }
                    }
                });

                holder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!dataModel.getIsDownloaded()) {
                            dataModel.setDownPath(save(dataModel.getFilePath(), context, "downloads"));
                            holder.download.setColorFilter(context.getResources().getColor(R.color.fullscreen_downloadDisablecolor));
                            holder.delete.setColorFilter(context.getResources().getColor(R.color.fullscreen_deleteEnablecolor));
                            dataModel.setDownloaded(true);
                        }
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataModel.getIsDownloaded()){
                            holder.blurLayout.setVisibility(View.VISIBLE);
                            holder.alertBox.setVisibility(View.VISIBLE);
                            holder.alertDelte.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteMedia(dataModel.getDownPath(), context);
                                    holder.delete.setColorFilter(context.getResources().getColor(R.color.fullscreen_deleteDisablecolor));
                                    holder.download.setColorFilter(context.getResources().getColor(R.color.fullscreen_downloadEnablecolor));
                                    dataModel.setDownloaded(false);
                                    holder.alertBox.setVisibility(View.INVISIBLE);
                                    holder.blurLayout.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                });
                break;
        }

    }


    public String save(String str, Context context, String subFolder){
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
            if (subFolder.equals("favourites"))
                Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
            else Toast.makeText(context, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
            return sb3;
        }catch (Exception e){
            Toast.makeText(context, "Error Ocurred", Toast.LENGTH_SHORT).show();
        }
        return null;
    }



    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    private void updateRecyclerView(int position) {
        dataModels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataModels.size());
    }

    public void deleteMedia(String str, Context context){
        Storage storage = new Storage(context);
        try {
            storage.deleteFile(str);
            if (str.contains("favourites"))
                Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();
            else  Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "Error Ocurred", Toast.LENGTH_SHORT).show();
        }

    }

//    @Override
//    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
////        if (holder.playBtn.getVisibility() == View.VISIBLE)
////        {
////            holder.imageholder.setVisibility(View.VISIBLE);
////            holder.playBtn.setImageResource(R.drawable.playbtn_fullscreen);
////        }
//        super.onViewDetachedFromWindow(holder);
//    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
