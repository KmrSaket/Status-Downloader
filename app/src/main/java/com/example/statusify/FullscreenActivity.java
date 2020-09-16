package com.example.statusify;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.statusify.adapter.FavsFragAdapter;
import com.example.statusify.adapter.ViewPagerAdapter;
import com.example.statusify.datamodel.DataModel;
import com.example.statusify.interfaces.ViewPagerInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class FullscreenActivity extends AppCompatActivity implements ViewPagerInterface {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    ArrayList<DataModel> statuses = new ArrayList<>();
    String appType = null;
    String fragmentName = null;
    String adapterName = null;
    Integer position =0;
    File[]  files;

    public String folderName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        viewPager2 = findViewById(R.id.vp);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            folderName = extras.getString("foldername");
            appType = extras.getString("appType");
            position = extras.getInt("itemPosition");
            adapterName = extras.getString("adapterName");
            fragmentName = extras.getString("fragmentName");
        }

        if (folderName.contains("/Statusify/")) {
            int startIndex = folderName.indexOf("/Statusify/");
            int lastIndex = folderName.lastIndexOf("/");
            folderName = folderName.substring(startIndex,lastIndex);
        }else {
            folderName = "/"+appType+"/Media/.Statuses/";
        }

        loadMedia();
    }

    public void loadMedia() {
        String path = Environment.getExternalStorageDirectory().toString()+folderName;
        File directory = new File(path);

        files = directory.listFiles();
        statuses.clear();
        if (directory.isDirectory()) {
            if (Build.VERSION.SDK_INT < 23) {
                if (adapterName!=null && adapterName.equals("recents")) {
                    displayRecentsfiles(files, viewPager2);
//                    Toast.makeText(getApplicationContext(), "rec", Toast.LENGTH_SHORT).show();
                }else{
                    displayfiles(files, viewPager2);
                }

            } else if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                if (adapterName!=null && adapterName.equals("recents")) {
                    displayRecentsfiles(files, viewPager2);
//                    Toast.makeText(getApplicationContext(), "rec", Toast.LENGTH_SHORT).show();
                }else{
                    displayfiles(files, viewPager2);
                }
            }
        }
    }

    private void displayRecentsfiles(File[] files, ViewPager2 datalist) {
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2) {
                return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
            }
        });

        String DownloadFolder = Environment.getExternalStorageDirectory() +
                File.separator +
                getApplicationContext().getString(R.string.app_name) +
                File.separator +
                appType +
                File.separator +
                "downloads"+
                File.separator;
        String FavFolder = Environment.getExternalStorageDirectory() +
                File.separator +
                getApplicationContext().getString(R.string.app_name) +
                File.separator +
                appType +
                File.separator +
                "favourites"+
                File.separator;

        int i=0;
        for (File file : files) {
            if (i>5) break;
            if (!file.isDirectory() && !file.getName().contains(".nomedia")) {
                if (new File(DownloadFolder + file.getName()).exists()){
                    if (new File(FavFolder + file.getName()).exists()) {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, true,
                                    DownloadFolder+file.getName(),FavFolder+file.getName()));
                        else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, false,
                                DownloadFolder+file.getName(),FavFolder+file.getName()));
                    }
                    else {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, true,
                                    DownloadFolder+file.getName(),null));
                        else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, false,
                                DownloadFolder+file.getName(),null));
                    }
                }
                else {
                    if (new File(FavFolder + file.getName()).exists()) {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, true,
                                    null,FavFolder+file.getName()));
                        else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, false,
                                null,FavFolder+file.getName()));
                    }
                    else {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, false, true,
                                    null,null));
                        else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, false, false,
                                null,null));
                    }
                }
            }
            Log.d("fav", " " + FavFolder+file.getName());
        }

        viewPagerAdapter = new ViewPagerAdapter(statuses,getApplicationContext(),appType,fragmentName);
        datalist.setAdapter(viewPagerAdapter);
        viewPager2.setCurrentItem(position,false);
        viewPagerAdapter.notifyDataSetChanged();

    }

    private void displayfiles(File[] files, ViewPager2 datalist) {
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        String DownloadFolder = Environment.getExternalStorageDirectory() +
                File.separator +
                getApplicationContext().getString(R.string.app_name) +
                File.separator +
                appType +
                File.separator +
                "downloads"+
                File.separator;
        String FavFolder = Environment.getExternalStorageDirectory() +
                File.separator +
                getApplicationContext().getString(R.string.app_name) +
                File.separator +
                appType +
                File.separator +
                "favourites"+
                File.separator;

        for (File file : files) {
            if (!file.isDirectory()) {
                if (!file.getName().contains(".nomedia")) {
                    if (new File(DownloadFolder + file.getName()).exists()){
                        if (new File(FavFolder + file.getName()).exists()) {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, true,
                                        DownloadFolder+file.getName(),FavFolder+file.getName()));
                            else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, false,
                                    DownloadFolder+file.getName(),FavFolder+file.getName()));
                        }
                        else {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, true,
                                        DownloadFolder+file.getName(),null));
                            else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, false,
                                    DownloadFolder+file.getName(),null));
                        }
                    }
                    else {
                        if (new File(FavFolder + file.getName()).exists()) {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, true,
                                null,FavFolder+file.getName()));
                            else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, false,
                                    null,FavFolder+file.getName()));
                        }
                        else {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, false, true,
                                        null,null));
                            else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, false, false,
                                    null,null));
                        }
                    }
                }
            }
        }
        viewPagerAdapter = new ViewPagerAdapter(statuses,getApplicationContext(),appType,fragmentName);
        datalist.setAdapter(viewPagerAdapter);
        viewPager2.setCurrentItem(position,false);
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void goBack() {
        finish();
    }


}