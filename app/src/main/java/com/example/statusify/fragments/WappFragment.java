package com.example.statusify.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;


import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.example.statusify.adapter.RecentsFragAdapter;
import com.example.statusify.datamodel.DataModel;
import com.example.statusify.R;
import com.example.statusify.adapter.WappFragAdapter;
import com.example.statusify.interfaces.FragmentChangeListener;


import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Comparator;



public class WappFragment extends Fragment {

    SharedPreferences sharedPreferences;
    ArrayList<DataModel> Allstatuses = new ArrayList<>();
    RecyclerView AllFeedsRecyclerView;
    WappFragAdapter adapter;
    String appType;
    File[]  files;
    public String dot_StatusFolder = "";
    GridLayoutManager gridLayoutManager;
    ConstraintLayout AppNotInstalled;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wapp, container, false);
        appType = getResources().getString(R.string.appType);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        appType = sharedPreferences.getString("appType", appType);
        AllFeedsRecyclerView = view.findViewById(R.id.AllFeedsRecyclerView);
        AppNotInstalled = view.findViewById(R.id.AppNotInstalled);

        if (!appInstalledOrNot(appType)){
            return view;
        }
        dot_StatusFolder = "/" + appType + "/Media/.Statuses";
        gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        AllFeedsRecyclerView.setLayoutManager(gridLayoutManager);
        AllFeedsRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!appInstalledOrNot(appType)){
            getView().findViewById(R.id.AllFeedsRecyclerView).setVisibility(View.GONE);
            setAppNotInstalledView(appType);
            getView().findViewById(R.id.AppNotInstalled).setVisibility(View.VISIBLE);
        }
        if (appInstalledOrNot(appType))
            loadMedia();
    }


    private void setAppNotInstalledView(String appType) {
        if (appType.equals(getResources().getString(R.string.appType))){
            ImageView logo = getView().findViewById(R.id.AppNotInstalledIMG);
            logo.setImageResource(R.drawable.fab_icon);
            TextView title = getView().findViewById(R.id.AppNotInstalledTitle);
            title.setText(R.string.whatsapp_not_found);
        }else {
            ImageView logo = getView().findViewById(R.id.AppNotInstalledIMG);
            logo.setImageResource(R.drawable.fabicon_wab);
            TextView title = getView().findViewById(R.id.AppNotInstalledTitle);
            title.setText(R.string.whatsapp_business_not_found);
        }

    }



    public void loadMedia() {

        String path = Environment.getExternalStorageDirectory().toString()+dot_StatusFolder;

        File directory = new File(path);
        files = directory.listFiles();

        if ((files.length == 0) || (files.length == 1 && files[0].getName().equals(".nomedia"))){
            setNoStatusView(appType);
            getView().findViewById(R.id.goViewStatus).setVisibility(View.VISIBLE);
        }else {
            getView().findViewById(R.id.goViewStatus).setVisibility(View.GONE);
        }


//        AppNotInstalled.setVisibility(directory.length() == 0?View.VISIBLE:View.GONE);
//        recentstile.setVisibility(directory.length() != 0?View.VISIBLE:View.GONE);

        Allstatuses.clear();
        if (directory.isDirectory()) {
            if (Build.VERSION.SDK_INT < 23) {
                displayfilesAllfeeds(files);
            } else if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                displayfilesAllfeeds(files);
            }
        }
    }

    private void setNoStatusView(String appType) {
        TextView title = getView().findViewById(R.id.goViewStatusSubTitle);
        if (appType.equals(getResources().getString(R.string.appType))) title.setText(R.string.go_to_whatsapp_and_view_status);
        else title.setText(R.string.go_to_whatsapp_business_and_view_status);
    }


    private void displayfilesAllfeeds(File[] files) {
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        String DownloadFolder = Environment.getExternalStorageDirectory() +
                File.separator +
                getActivity().getString(R.string.app_name) +
                File.separator +
                appType +
                File.separator +
                "downloads"+
                File.separator;
        String FavFolder = Environment.getExternalStorageDirectory() +
                File.separator +
                getActivity().getString(R.string.app_name) +
                File.separator +
                appType +
                File.separator +
                "favourites"+
                File.separator;
        for (File file : files) {
            if (!file.isDirectory() && !file.getName().contains(".nomedia")) {
                    if (new File(DownloadFolder + file.getName()).exists()){
                        if (new File(FavFolder + file.getName()).exists()) {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, true));
                            else Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, false));
                        }
                        else {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, true));
                            else Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, false));
                        }
                    }
                    else {
                        if (new File(FavFolder + file.getName()).exists()) {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, true));
                            else Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, false));
                        }
                        else {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                                Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, false, true));
                            else Allstatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, false, false));
                        }
                    }
            }
        }
        adapter = new WappFragAdapter(Allstatuses , getActivity() , appType);
        AllFeedsRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean appInstalledOrNot(String appType) {
        try {
            if (appType.equals("WhatsApp"))
                getActivity().getPackageManager().getPackageInfo("com.whatsapp",0);
            else if (appType.equals("WhatsApp Bussiness"))
                getActivity().getPackageManager().getPackageInfo("com.whatsapp.w4b",0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

}

