package com.example.statusify.fragments;

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
import android.widget.LinearLayout;


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

    ArrayList<DataModel> Allstatuses = new ArrayList<>();
    ArrayList<DataModel> RecentStatuses = new ArrayList<>();
    RecyclerView AllFeedsRecyclerView,RecentStatusRecyclerView;
    WappFragAdapter adapter;
    RecentsFragAdapter adapterRecent;
    String appType = null;
    File[]  files;
    public String dot_StatusFolder = "";
    GridLayoutManager gridLayoutManager;
    Bundle bundle = new Bundle();
    LinearLayout recentstile;
    ConstraintLayout AppNotInstalled;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appType = getArguments().getString("appType");
        final View view = inflater.inflate(R.layout.fragment_wapp, container, false);
        AllFeedsRecyclerView = view.findViewById(R.id.AllFeedsRecyclerView);
        recentstile = view.findViewById(R.id.recentstile);
        AppNotInstalled = view.findViewById(R.id.AppNotInstalled);
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.exit_frag_transition));

        final LinearLayout linearLayout = view.findViewById(R.id.navBar);
//        final RecyclerView recyclerView = view.findViewById(R.id.AllFeedsRecyclerView);
        view.findViewById(R.id.favouriteImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr=new FavsFragment();
                bundle.putString("appType", appType);
                fr.setArguments(bundle);
                FragmentChangeListener fc=(FragmentChangeListener)getActivity();
                fc.replaceFragment(fr, linearLayout, AllFeedsRecyclerView, "Favourites");
//                fc.replaceFragment(fr,linearLayout,"Favourites");
            }
        });
        view.findViewById(R.id.downloadImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr=new SavedFragment();
                bundle.putString("appType", appType);
                fr.setArguments(bundle);
                FragmentChangeListener fc=(FragmentChangeListener)getActivity();
                fc.replaceFragment(fr, linearLayout, AllFeedsRecyclerView, "Downloads");
//                fc.replaceFragment(fr,linearLayout,"Downloads");
            }
        });

        if (!appInstalledOrNot(appType)){
            view.findViewById(R.id.recentstile).setVisibility(View.GONE);
            view.findViewById(R.id.RecentStatusRecyclerView).setVisibility(View.GONE);
            view.findViewById(R.id.AllFeedsRecyclerView).setVisibility(View.GONE);
            view.findViewById(R.id.AppNotInstalled).setVisibility(View.VISIBLE);
            return view;
        }



        dot_StatusFolder = "/" + appType + "/Media/.Statuses";
        gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        AllFeedsRecyclerView.setLayoutManager(gridLayoutManager);
        AllFeedsRecyclerView.setNestedScrollingEnabled(false);


        RecentStatusRecyclerView = view.findViewById(R.id.RecentStatusRecyclerView);
        RecentStatusRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (appInstalledOrNot(appType))
            loadMedia();
    }




    public void loadMedia() {

        String path = Environment.getExternalStorageDirectory().toString()+dot_StatusFolder;

        File directory = new File(path);
        files = directory.listFiles();


//        AppNotInstalled.setVisibility(directory.length() == 0?View.VISIBLE:View.GONE);
//        recentstile.setVisibility(directory.length() != 0?View.VISIBLE:View.GONE);

        Allstatuses.clear();
        RecentStatuses.clear();
        if (directory.isDirectory()) {
            if (Build.VERSION.SDK_INT < 23) {
                displayfilesAllfeeds(files);
                displayfilesRecents(files);
            } else if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                displayfilesAllfeeds(files);
                displayfilesRecents(files);
            }
        }
    }

    private void displayfilesRecents(File[] files) {
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2) {
                return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
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
        int i=0;
        for (File file : files) {
            if (i>5) break;
            if (!file.isDirectory() && !file.getName().contains(".nomedia")) {
                if (new File(DownloadFolder + file.getName()).exists()){
                    if (new File(FavFolder + file.getName()).exists()) {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, true));
                        else RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, false));
                    }
                    else {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, true));
                        else RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, false));
                    }
                }
                else {
                    if (new File(FavFolder + file.getName()).exists()) {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, true));
                        else RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, false));
                    }
                    else {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4"))
                            RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, true));
                        else RecentStatuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false, false));
                    }
                }
                i++;
            }
        }
        adapterRecent = new RecentsFragAdapter(RecentStatuses, getActivity(), appType);
        RecentStatusRecyclerView.setAdapter(adapterRecent);
        adapterRecent.notifyDataSetChanged();
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
