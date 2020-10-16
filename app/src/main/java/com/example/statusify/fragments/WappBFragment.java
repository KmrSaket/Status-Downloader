package com.example.statusify.fragments;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.statusify.R;
import com.example.statusify.adapter.RecentsFragAdapter;
import com.example.statusify.adapter.WappFragAdapter;
import com.example.statusify.datamodel.DataModel;
import com.example.statusify.interfaces.FragmentChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class WappBFragment extends Fragment {
    ArrayList<DataModel> Allstatuses = new ArrayList<>();
    RecyclerView AllFeedsRecyclerView;
    WappFragAdapter adapter;
    String appType = "WhatsApp";
    File[]  files;
    public String dot_StatusFolder = "";
    GridLayoutManager gridLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wapp_b, container, false);
        AllFeedsRecyclerView = view.findViewById(R.id.AllFeedsRecyclerView);

        if (!appInstalledOrNot(appType)){
            view.findViewById(R.id.AllFeedsRecyclerView).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.AppNotInstalled).setVisibility(View.VISIBLE);
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
        if (appInstalledOrNot(appType))
            loadMedia();
    }

    public void loadMedia() {

        String path = Environment.getExternalStorageDirectory().toString()+dot_StatusFolder;

        File directory = new File(path);
        files = directory.listFiles();

        Allstatuses.clear();
        if (directory.isDirectory()) {
            if (Build.VERSION.SDK_INT < 23) {
                displayfiles(files);
            } else if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                displayfiles(files);
            }
        }
    }
    private void displayfiles(File[] files) {
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
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