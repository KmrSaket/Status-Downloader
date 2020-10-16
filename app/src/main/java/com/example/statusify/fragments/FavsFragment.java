package com.example.statusify.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.example.statusify.R;
import com.example.statusify.adapter.FavsFragAdapter;
import com.example.statusify.adapter.SavedFragAdapter;
import com.example.statusify.datamodel.DataModel;
import com.example.statusify.interfaces.FragmentChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class FavsFragment extends Fragment {

    File[]  files;
    public String folderName = null;
    RecyclerView FavouriteRecyclerView;
    List<DataModel> statuses = new ArrayList<>();
    FavsFragAdapter favsFragAdapter;
    GridLayoutManager gridLayoutManager;
    String appType = "WhatsApp";

    ConstraintLayout emptyerror;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favs, container, false);

        FavouriteRecyclerView = view.findViewById(R.id.FavouriteRecyclerView);

        folderName = "/Statusify/" + appType + "/favourites";
        gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        FavouriteRecyclerView.setLayoutManager(gridLayoutManager);
        FavouriteRecyclerView.setNestedScrollingEnabled(false);

        emptyerror = view.findViewById(R.id.emptyerror);

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

        emptyerror.setVisibility(directory.length() == 0?View.VISIBLE:View.GONE);
        FavouriteRecyclerView.setVisibility(directory.length() != 0?View.VISIBLE:View.INVISIBLE);

        files = directory.listFiles();
        statuses.clear();
        if (directory.isDirectory()) {
            if (Build.VERSION.SDK_INT < 23) {
                displayfiles(files, FavouriteRecyclerView);
            } else if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                displayfiles(files, FavouriteRecyclerView);
            }
        }
    }

    private void displayfiles(File[] files, RecyclerView datalist) {
        Arrays.sort(files, new Comparator<File>() {
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

        for (File file : files) {
            if (!file.isDirectory()) {
                if (!file.getName().contains(".nomedia")) {
                    if (new File(DownloadFolder + file.getName()).exists()) {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4")) {
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, true));
                        } else
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), true, true, false));
                    } else {
                        if (file.getAbsolutePath().substring(file.getPath().lastIndexOf(".")).equals(".mp4")) {
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, true));
                        } else
                            statuses.add(new DataModel(file.getAbsolutePath(), file.getName(), false, true, false));
                    }
                }
            }
        }
            favsFragAdapter = new FavsFragAdapter(statuses, getActivity(), appType);
            datalist.setAdapter(favsFragAdapter);
            favsFragAdapter.notifyDataSetChanged();

    }
}
