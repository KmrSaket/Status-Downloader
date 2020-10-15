package com.example.statusify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.statusify.blur.BlurLayout;
import com.example.statusify.fragments.FavsFragment;
import com.example.statusify.fragments.SavedFragment;
import com.example.statusify.fragments.WappBFragment;
import com.example.statusify.fragments.WappFragment;
import com.example.statusify.interfaces.DeleteListener;
import com.example.statusify.interfaces.FragmentChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.snatik.storage.Storage;


import java.io.File;
import java.net.URLConnection;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class MainActivity extends AppCompatActivity implements FragmentChangeListener, OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    FrameLayout FragHolder, FragmentHolder;
    BottomNavigationView btm_nav_bar;
    Fragment selectedFragment = new WappFragment();
    ImageView fabicon,fabiconWA,fabiconWAB, backbtn;
    BlurLayout blurLayout;
//    BlurView blurViewOld;
    String appType = null;
    Bundle bundle = new Bundle();
    public String dot_StatusFolder = "";

    NestedScrollView nestedScrollView;
    Toolbar toolbar;
    NavigationView nav;
    private DrawerLayout drawerLayout;

    Switch aSwitch;
    ImageView toolbarWA,toolbarWAB;

    TextView title,subtitle,fragTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        StringBuilder sb = new StringBuilder();
//        sb.append(Environment.getExternalStorageDirectory());
//        sb.append(File.separator);
//        sb.append(getResources().getString(R.string.foldername));
//        sb.append(File.separator);
//        checkAndCreateFolder(sb.toString());


        blurLayout = findViewById(R.id.blurLayout);
        blurLayout.setOnClickListener(this);

//        blurViewOld = findViewById(R.id.blur);
//        blurViewOld.setOnClickListener(this);
//        blurBackground();


//        appType = "WhatsApp";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appType = extras.getString("appType");
        }

        initialize();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.rateus:
                        try {
                            Intent rate = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                            rate.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(rate);
                        } catch (ActivityNotFoundException unused) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")));
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.contactus:
                        Toast.makeText(getApplicationContext(), "Contact Us", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

//                    case R.id.changelang:
//                        Toast.makeText(getApplicationContext(), "Change Lang", Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;
//                    case R.id.changestorage:
//                        Toast.makeText(getApplicationContext(), "Change Storage", Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;

                    case R.id.privacypolicy:
                        Toast.makeText(getApplicationContext(), "Privacy Policy", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.shareapp:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey,\n" +
                                "\n" +
                                "Status-e-fyi is a fast, simple and best app that I use to download status from WhatsApp!" +
                                "\n" + "https://play.google.com/store/apps/details?id=com.whatsapp");
                        startActivity(Intent.createChooser(shareIntent, "Hey, Status-e-fyi is a fast, simple and best app that I use to download status from WhatsApp!"));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });



    }


    private void checkAndCreateFolder(String s) {
        if (!new File(s).exists() ) {
            new File(s).mkdir();
        }
        if (!new File(s + "WhatsApp").exists()){
            new File(s + "WhatsApp").mkdir();
        }
        if (!new File(s + "WhatsApp Bussiness").exists()){
            new File(s + "WhatsApp Bussiness").mkdir();
        }
    }

    private void initialize() {
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        fragTitle = findViewById(R.id.fragTitle);

        btm_nav_bar = findViewById(R.id.btm_nav_bar);
        FragmentHolder = findViewById(R.id.Fragmentholder);
        btm_nav_bar.setOnNavigationItemSelectedListener(this);

        nav = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        FragHolder = findViewById(R.id.Fragholder);
        fabicon = findViewById(R.id.fabicon);
        fabicon.setOnClickListener(this);
        fabiconWAB = findViewById(R.id.fabiconWAB);
        fabiconWAB.setOnClickListener(this);
        fabiconWA = findViewById(R.id.fabiconWA);
        fabiconWA.setOnClickListener(this);
        aSwitch = findViewById(R.id.toolSwitch);
        aSwitch.setOnClickListener(this);
        toolbarWA = findViewById(R.id.WA);
        toolbarWA.setOnClickListener(this);
        toolbarWAB = findViewById(R.id.WAB);
        toolbarWAB.setOnClickListener(this);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(this);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open,
                R.string.close);
        toggle.setDrawerIndicatorEnabled(false);
//        drawerLayout.addDrawerListener(toggle);
        toggle.setHomeAsUpIndicator(R.drawable.navigation_drawer_menu_toggle);
        toggle.setToolbarNavigationClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
//        toggle.syncState();

        dot_StatusFolder = "/" + appType + "/Media/.Statuses";
        bundle.putString("appType", appType);

        selectedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.Fragholder, selectedFragment).commit();


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    appType = "WhatsApp Bussiness";
                    subtitle.setText(appType);
                    bundle.putString("appType", appType);
                    selectedFragment = new WappBFragment();
                }
                else {
                    appType = "WhatsApp";
                    subtitle.setText(appType);
                    bundle.putString("appType", appType);
                    selectedFragment = new WappFragment();
                }
                selectedFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragholder, selectedFragment).commit();
                title.setText("Status Feeds");
                subtitle.setVisibility(View.VISIBLE);
            }
        });
    }

//    private void blurBackground() {
//        float radius = 5f;
//
//        View decorView = getWindow().getDecorView();
//        ViewGroup rootView = decorView.findViewById(android.R.id.content);
//
//        Drawable windowBackground = decorView.getBackground();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            blurViewOld.setupWith(rootView)
//                    .setFrameClearDrawable(windowBackground)
//                    .setBlurAlgorithm(new RenderScriptBlur(this))
//                    .setBlurRadius(radius)
//                    .setHasFixedTransformationMatrix(true);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        reqPermissions();
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.WA:
                aSwitch.setChecked(false);
                toolbarWAB.setImageResource(R.drawable.toolbar_wab_inactive);
                toolbarWA.setImageResource(R.drawable.toolbar_wa_active);
                break;
            case R.id.WAB:
                aSwitch.setChecked(true);
                toolbarWAB.setImageResource(R.drawable.toolbar_wab_active);
                toolbarWA.setImageResource(R.drawable.toolbar_wa_inactive);
                break;
            case R.id.backbtn:
                onBackPressed();
                break;
            case R.id.fabiconWA:
                Intent launchIntentForPackageWA;
                launchIntentForPackageWA = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                if (appInstalledOrNot("WhatsApp")) {
                    startActivity(launchIntentForPackageWA);
                } else {
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
                    } catch (ActivityNotFoundException unused) {
                        Toast.makeText(this, "Whatsapp not install in your device!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.fabiconWAB:
                Intent launchIntentForPackageWAB = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                if (appInstalledOrNot("WhatsApp Bussiness")) {
                    startActivity(launchIntentForPackageWAB);
                } else {
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp.w4b")));
                    } catch (ActivityNotFoundException unused) {
                        Toast.makeText(this, "Whatsapp not install in your device!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.fabicon:
            case R.id.blurLayout:
                fabicon.setImageResource(blurLayout.getVisibility() == View.INVISIBLE ?
                        R.drawable.fabicon_cross : R.drawable.fab_icon);
                if (blurLayout.getVisibility() == View.INVISIBLE) {
                    fabOpenAnimateMaster();
                }
                else {
                    fabCloseAnimateMaster();
                }
                blurLayout.setVisibility(blurLayout.getVisibility() == View.VISIBLE ?
                    View.INVISIBLE:View.VISIBLE);
                break;
        }
    }



    // -----FAB animation (start)------

    private void fabCloseAnimateMaster(){
        reverseanimatefabIcon_1();
        reverseanimatefabIcon_2();
        reverseanimatefabIcon_3();
    }

    private void fabOpenAnimateMaster(){
        animatefabIcon_1();
        animatefabIcon_2();
        animatefabIcon_3();
    }

    private void reverseanimatefabIcon_3() {
        findViewById(R.id.gototext).setVisibility(View.INVISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(
                R.id.gototext), "translationY", 0);
        Interpolator interpolator = PathInterpolatorCompat.create(0.000f,0.655f,0.065f,1.140f);
        animation.setInterpolator(interpolator);
        animation.setDuration(500);
        animation.start();
    }

    private void animatefabIcon_3() {
        float _210dp = getResources().getDimension(R.dimen._210dp);
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(
                R.id.gototext), "translationY", -_210dp);
        Interpolator interpolator = PathInterpolatorCompat.create(0.000f,0.655f,0.065f,1.140f);
        animation.setInterpolator(interpolator);
        animation.setDuration(500);
        animation.start();
        findViewById(R.id.gototext).setVisibility(View.VISIBLE);
    }

    private void reverseanimatefabIcon_2() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(
                R.id.fabiconWA), "translationY", 0);
        Interpolator interpolator = PathInterpolatorCompat.create(0.000f,0.655f,0.065f,1.140f);
        animation.setInterpolator(interpolator);
        animation.setDuration(500);
        animation.start();
    }

    private void animatefabIcon_2() {
        float _140dp = getResources().getDimension(R.dimen._140dp);
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(
                R.id.fabiconWA), "translationY", -_140dp);
        Interpolator interpolator = PathInterpolatorCompat.create(0.000f,0.655f,0.065f,1.140f);
        animation.setInterpolator(interpolator);
        animation.setDuration(500);
        animation.start();
    }

    private void reverseanimatefabIcon_1() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(
                R.id.fabiconWAB), "translationY", 0);
        Interpolator interpolator = PathInterpolatorCompat.create(0.000f,0.655f,0.065f,1.140f);
        animation.setInterpolator(interpolator);
        animation.setDuration(500);
        animation.start();
    }

    private void animatefabIcon_1() {
        float _70dp = getResources().getDimension(R.dimen._70dp);
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(
                R.id.fabiconWAB), "translationY", -_70dp);
        Interpolator interpolator = PathInterpolatorCompat.create(0.000f,0.655f,0.065f,1.140f);
        animation.setInterpolator(interpolator);
        animation.setDuration(500);
        animation.start();
    }
    // -----FAB animation (end)------





    private void reqPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }



    private boolean appInstalledOrNot(String appType) {
        try {
            if (appType.equals("WhatsApp"))
                getPackageManager().getPackageInfo("com.whatsapp",0);
            else if (appType.equals("WhatsApp Bussiness"))
                getPackageManager().getPackageInfo("com.whatsapp.w4b",0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }


    @Override
    public void replaceFragment(Fragment fragment,LinearLayout linearLayout, String titleText) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragmentTransaction.setReorderingAllowed(true)
                    .addSharedElement(linearLayout, linearLayout.getTransitionName());
        }
        fragmentTransaction.replace(R.id.Fragholder, fragment, fragment.toString());
        fragmentTransaction.commit();
        fragTitle.setText(titleText);
        fragTitle.setVisibility(View.VISIBLE);

        subtitle.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        toolbarWA.setVisibility(View.GONE);
        toolbarWAB.setVisibility(View.GONE);
        backbtn.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollTo(-500, -500);
            }
        }, 500);
//        title.setGravity(Gravity.CENTER);
//        ConstraintLayout.LayoutParams params =new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                , ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(0, (int) getResources().getDimension(R.dimen._4sdp),0,0);
//        title.setLayoutParams(params);
    }

    @Override
    public void replaceFragment(Fragment fragment, String titleText) {

//        ConstraintLayout.LayoutParams params =new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                , ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(0, (int) getResources().getDimension(R.dimen._4sdp),0,0);
//        title.setLayoutParams(params);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Fragholder, fragment, fragment.toString());
        fragmentTransaction.commit();
        fragTitle.setText(titleText);
        fragTitle.setGravity(Gravity.CENTER);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollTo(-500, -500);
            }
        }, 500);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Fragholder, fragment, fragment.toString());
        fragmentTransaction.commit();
        toolbarWA.setVisibility(View.VISIBLE);
        toolbarWAB.setVisibility(View.VISIBLE);
        backbtn.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollTo(-500, -500);
            }
        }, 500);
    }

    @Override
    public void replaceFragment(Fragment fragment, LinearLayout linearLayout, RecyclerView recyclerView, String titleText) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragmentTransaction.setReorderingAllowed(true)
                    .addSharedElement(linearLayout, linearLayout.getTransitionName());
            fragmentTransaction.setReorderingAllowed(true)
                    .addSharedElement(recyclerView, "grid");
        }
        fragmentTransaction.replace(R.id.Fragholder, fragment, fragment.toString());
        fragmentTransaction.commit();
        fragTitle.setText(titleText);
        fragTitle.setVisibility(View.VISIBLE);

        subtitle.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        toolbarWA.setVisibility(View.GONE);
        toolbarWAB.setVisibility(View.GONE);
        backbtn.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollTo(-500, -500);
            }
        }, 500);
    }


    @Override
    public void onBackPressed() {
        if (blurLayout.getVisibility() == View.VISIBLE)
            fabicon.callOnClick();
        else if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.Fragholder);
            if (fragment instanceof FavsFragment || fragment instanceof SavedFragment){
                    replaceFragment(selectedFragment);
                    title.setVisibility(View.VISIBLE);
                    subtitle.setVisibility(View.VISIBLE);
                    fragTitle.setVisibility(View.GONE);
//                    scroll to top
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        nestedScrollView.scrollTo(-500, -500);
//                    }
//                }, 500);
                nestedScrollView.scrollTo(-500, -500);

            }else if (fragment instanceof WappFragment || fragment instanceof WappBFragment){
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.allfeeds:
                Log.d("btm", "all");
                break;
            case R.id.favourites:
                Log.d("btm", "fav");
                break;
            case R.id.downloadedStatus:
                Log.d("btm", "down");
                break;
        }
        return true;
    }

//    @Override
//    public void delete(int position, String filepath) {
//        Log.d("del", position + "");
//        Storage storage =  new Storage(getApplicationContext());
//
//        try {
//            storage.deleteFile(filepath);
//        }
//        catch (Exception e){
//            Log.d("error", "dd");
//        }
//
//    }
}