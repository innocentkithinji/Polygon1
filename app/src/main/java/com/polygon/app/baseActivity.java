package com.polygon.app;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.polygon.R;
import com.polygon.views.NavDrawer;

/**
 * Created by innocen on 3/7/2017.
 */

public class baseActivity extends AppCompatActivity {
    protected  PolyApp application;
    protected Toolbar toolbar;
    protected NavDrawer navDrawer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (PolyApp) getApplication();

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        if (toolbar!=null){
            setSupportActionBar(toolbar);
        }

    }

    public void setNavDrawer(NavDrawer drawer){
        this.navDrawer = drawer;
        this.navDrawer.create();
    }


    public Toolbar getToolbar() {
        return toolbar;
    }
}
