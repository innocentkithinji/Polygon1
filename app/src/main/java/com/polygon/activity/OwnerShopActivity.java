package com.polygon.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polygon.R;
import com.polygon.app.baseActivity;
import com.polygon.fragments.MessagesFragment;
import com.polygon.fragments.OrdersFragment;
import com.polygon.fragments.OverViewFragment;
import com.polygon.fragments.StatFragments;
import com.polygon.views.MainNavDrawer;

import java.util.ArrayList;
import java.util.List;

public class OwnerShopActivity extends baseActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private DatabaseReference Dbroot = FirebaseDatabase.getInstance().getReference();
    private int[] tabIcons = {
            R.drawable.ic_shop_front,
            R.drawable.ic_chat,
            R.drawable.ic_orders,
            R.drawable.ic_show_chart
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String shopId = getIntent().getExtras().getString("ShopId");
        setContentView(R.layout.activity_shop);
        setNavDrawer(new MainNavDrawer(this));
        getSupportActionBar().setTitle(shopId);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OverViewFragment(), "Overview");
        adapter.addFragment(new MessagesFragment(), "Messages");
        adapter.addFragment(new OrdersFragment(), "Orders");
        adapter.addFragment(new StatFragments(), "Stats");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

}
