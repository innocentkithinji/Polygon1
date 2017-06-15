package com.polygon.views;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.polygon.R;
import com.polygon.app.baseActivity;

import java.util.ArrayList;

/**
 * Created by innocen on 3/7/2017.
 */

public class NavDrawer {
    private ArrayList<NavDrawerItem> items;
    private NavDrawerItem selectedItem;


    protected baseActivity activity;
    protected DrawerLayout drawerLayout;
    protected ViewGroup navDrawerGroup;

    public NavDrawer(baseActivity activity) {
        this.activity = activity;
        items = new ArrayList<>();
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawerLayout);
        navDrawerGroup = (ViewGroup) activity.findViewById(R.id.nav_drawer);

        if (drawerLayout == null || navDrawerGroup == null) {
            throw new RuntimeException("NO drawerLayout or view group");
        }

        Toolbar toolbar = activity.getToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_dehaze_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOpen(!isOpen());
            }
        });

    }


    public void addItem(NavDrawerItem item) {
        items.add(item);
        item.navDrawer = this;

    }

    public boolean isOpen() {
        return drawerLayout.isDrawerOpen(Gravity.START);
    }

    public void setOpen(boolean isOpen) {
        if (isOpen)
            drawerLayout.openDrawer(Gravity.START);
        else
            drawerLayout.closeDrawer(Gravity.START);

    }

    public void setSelectedItem(NavDrawerItem item) {
        if (selectedItem != null)
            selectedItem.SetSelected(false);

        selectedItem = item;
        selectedItem.SetSelected(true);
    }


    public void create() {
        LayoutInflater inflater = activity.getLayoutInflater();
        for (NavDrawerItem item : items) {
            item.inflate(inflater, navDrawerGroup);
        }
    }

    public static abstract class NavDrawerItem {
        protected NavDrawer navDrawer;

        public abstract void inflate(LayoutInflater inflater, ViewGroup Container);

        public abstract void SetSelected(boolean isSelected);
    }

    public static class BasicNavDrawerItem extends NavDrawerItem implements View.OnClickListener {
        private String text;
        private String badge;
        private int iconDrawable;
        private int containerId;


        private ImageView icon;
        private TextView textView;
        private TextView badgeTextView;
        private View view;
        private int defaultColor;

        public BasicNavDrawerItem(String text, String badge, int iconDrawable, int containerId) {
            this.text = text;
            this.badge = badge;
            this.iconDrawable = iconDrawable;
            this.containerId = containerId;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView) {
            ViewGroup container = (ViewGroup) navDrawerView.findViewById(containerId);
            if (container == null){
                throw new RuntimeException("Cannot find view group");
            }

            view = inflater.inflate(R.layout.list_item_nav_drawer, container, false);
            container.addView(view);
            view.setOnClickListener(this);

            icon = (ImageView) view.findViewById(R.id.list_item_nav_drawer_icon);
            textView = (TextView) view.findViewById(R.id.list_item_nav_drawer_title);
            badgeTextView = (TextView) view.findViewById(R.id.list_item_nav_drawer_badge);

            defaultColor = textView.getCurrentTextColor();
            icon.setImageResource(iconDrawable);
            textView.setText(text);
            if (badge != null)
                badgeTextView.setText(badge);
            else
                badgeTextView.setVisibility(View.GONE);
        }

        @Override
        public void SetSelected(boolean isSelected) {
            if (isSelected){
                view.setBackgroundResource(R.drawable.selected_resource);
                textView.setTextColor(navDrawer.activity.getResources().getColor(R.color.selected_item_text_color));
            }else{
                view.setBackground(null);
                textView.setTextColor(defaultColor);
            }
        }

        public void setText(String text) {
            this.text = text;

            if (view != null){
                textView.setText(text);
            }
        }

        public void setBadge(String badge) {
            this.badge = badge;
            if (view !=null){
                if (badge!= null){
                    badgeTextView.setVisibility(View.GONE);
                }else {
                    badgeTextView.setVisibility(View.VISIBLE);
                }
            }
        }

        public void setIcon(int iconDrawable) {
            this.iconDrawable = iconDrawable;
            if (view != null){
                icon.setImageResource(iconDrawable);
            }
        }

        @Override
        public void onClick(View v) {
            navDrawer.setSelectedItem(this);
        }


    }

    public static class ActivityNavDrawerItem extends BasicNavDrawerItem {
        private final Class TargetActivity;

        public ActivityNavDrawerItem(Class TargetActivity, String text, String badge, int iconDrawable, int containerId) {
            super(text, badge, iconDrawable, containerId);
            this.TargetActivity = TargetActivity;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView) {
            super.inflate(inflater, navDrawerView);
            if (navDrawer.activity.getClass() == TargetActivity){
                navDrawer.setSelectedItem(this);
            }
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            navDrawer.setOpen(false);
            if (navDrawer.activity.getClass() == TargetActivity){
                return;
            }

            navDrawer.activity.startActivity(new Intent(navDrawer.activity, TargetActivity));
            //navDrawer.activity.finish();
        }
    }
}
