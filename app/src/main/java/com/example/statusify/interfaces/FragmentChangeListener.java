package com.example.statusify.interfaces;

import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public interface FragmentChangeListener {
    void onClick(View view);


    public void replaceFragment(Fragment fragment, LinearLayout linearLayout, String titleText);
    public void replaceFragment(Fragment fragment, String titleText);
    public void replaceFragment(Fragment fragment);
    public void replaceFragment(Fragment fragment, LinearLayout linearLayout, RecyclerView recyclerView, String titleText);

}
