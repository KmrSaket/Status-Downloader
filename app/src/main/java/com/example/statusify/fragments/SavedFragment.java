package com.example.statusify.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.example.statusify.blur.BlurLayout;
import com.example.statusify.datamodel.DataModel;
import com.example.statusify.R;
import com.example.statusify.adapter.SavedFragAdapter;
import com.example.statusify.interfaces.DeleteListener;
import com.example.statusify.interfaces.FragmentChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class SavedFragment extends Fragment implements DeleteListener{

    SharedPreferences sharedPreferences;
    File[]  files;
    public String folderName = null;
    RecyclerView DownloadedStatusRecyclerView;
    List<DataModel> statuses = new ArrayList<>();
    SavedFragAdapter adapter;
    GridLayoutManager gridLayoutManager;
    String appType;
    ConstraintLayout emptyerror;

    BlurLayout blurLayout;
    ConstraintLayout alertBox;
    Button cancel,delete;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        appType = getResources().getString(R.string.appType);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        appType = sharedPreferences.getString("appType", appType);
        DownloadedStatusRecyclerView = view.findViewById(R.id.DownloadedStatusRecyclerView);
        folderName = "/Statusify/" + appType + "/downloads";
        gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        DownloadedStatusRecyclerView.setLayoutManager(gridLayoutManager);
        DownloadedStatusRecyclerView.setNestedScrollingEnabled(false);

        emptyerror = view.findViewById(R.id.emptyerror);
        blurLayout = view.findViewById(R.id.blurLayout);
        alertBox = view.findViewById(R.id.alertBox);
        cancel = view.findViewById(R.id.alert_cancel);
        delete = view.findViewById(R.id.alert_delete);
        cancel.setOnClickListener((View.OnClickListener) getActivity());
        delete.setOnClickListener((View.OnClickListener) getActivity());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMedia();
    }

    public void loadMedia() {
        String path = Environment.getExternalStorageDirectory().toString()+folderName;
        File directory = new File(path);
        files = directory.listFiles();
        Log.d("ddd", " + " + directory.length());
        emptyerror.setVisibility(directory.length() == 0?View.VISIBLE:View.GONE);
        DownloadedStatusRecyclerView.setVisibility(directory.length() != 0?View.VISIBLE:View.INVISIBLE);
        statuses.clear();
        if (directory.isDirectory()) {
            if (Build.VERSION.SDK_INT < 23) {
                displayfiles(files, DownloadedStatusRecyclerView);
            } else if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                displayfiles(files, DownloadedStatusRecyclerView);
            }
        }
    }

    private void displayfiles(File[] files, RecyclerView datalist) {
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        String FavFolder = Environment.getExternalStorageDirectory() +
                File.separator +
                getActivity().getString(R.string.app_name) +
                File.separator +
                appType +
                File.separator +
                "favourites"+
                File.separator;

        for (File file : files) {
            if (!file.isDirectory()) {
                if (!file.getName().contains(".nomedia")) {
                        if (new File(FavFolder + file.getName()).exists()) {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4")){
                                statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true,true));
                            }
                            else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true,false));
                        }
                        else {
                            if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4")){
                                statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false,true));
                            }
                            else statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, false,false));
                        }
                }
            }
        }
        adapter = new SavedFragAdapter(statuses,getActivity(),appType, SavedFragment.this);
        datalist.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean delete(int position, String filepath) {
        blurLayout.setVisibility(View.VISIBLE);
        alertBox.setVisibility(View.VISIBLE);
        final Boolean[] returning = new Boolean[1];
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returning[0] = true;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returning[0] = false;
            }
        });
        return returning[0];
    }

}