package com.polygon.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.polygon.R;
import com.polygon.activity.BrowseActivities;
import com.polygon.activity.MainActivity;
import com.polygon.activity.OwnerShopActivity;
import com.polygon.activity.ShopSetupActivity;
import com.polygon.activity.ViewShopsActivity;
import com.polygon.app.baseActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by innocen on 3/7/2017.
 */

public class MainNavDrawer extends NavDrawer {
    private static final Object RC_SIGN_IN = 1;
    private final TextView userName;
    private final ImageView userppic;

    private FirebaseAuth mAuth;
    public MainNavDrawer(baseActivity activity) {
        super(activity);
        mAuth = FirebaseAuth.getInstance();


        //nav drawer Buyer Items
        addItem(new ActivityNavDrawerItem(MainActivity.class, "Home", null, R.drawable.ic_home, R.id.nav_drawer_buyer_options));
        addItem(new ActivityNavDrawerItem(BrowseActivities.class, "View by Category", null, R.drawable.ic_dehaze, R.id.nav_drawer_buyer_options));


        //nav drawer Seller Options:
//        addItem(new ActivityNavDrawerItem(AddShopActivity.class,"Add Category", null, R.drawable.ic_dehaze, R.id.sellCategory));
        addItem(new ActivityNavDrawerItem(ShopSetupActivity.class,"Add shop", null, R.drawable.ic_shop, R.id.sellCategory));
        addItem(new ActivityNavDrawerItem(ViewShopsActivity.class, "My Shops", null, R.drawable.ic_briefcase, R.id.sellCategory));
        // nav drawer bottom items
        addItem(new BasicNavDrawerItem("Logout", null, R.drawable.ic_power_settings_new, R.id.nav_drawer_bottom_items){
                    @Override
                    public void onClick(View v) {
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
                        super.onClick(v);
                    }
                }
        );


        userName = (TextView) navDrawerGroup.findViewById(R.id.nav_drawer_display_Name);
        userppic = (ImageView) navDrawerGroup.findViewById(R.id.nav_drawer_user_avatar);
        Profile profile = Profile.getCurrentProfile();
        userName.setText(""+profile.getName());
        String Imageurl = profile.getProfilePictureUri(400, 400).toString();
        Picasso.with(activity)
                .load(Imageurl)
                .into(userppic);

    }
}
