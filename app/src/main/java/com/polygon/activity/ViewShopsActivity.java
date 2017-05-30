package com.polygon.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polygon.R;
import com.polygon.app.baseActivity;
import com.polygon.listeners.viewShops;
import com.polygon.views.MainNavDrawer;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class ViewShopsActivity extends baseActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shops);
        setNavDrawer(new MainNavDrawer(this));


        floatingActionButton = (FloatingActionButton) findViewById(R.id.view_shop_fab);
        mAuth = FirebaseAuth.getInstance();
        userShops = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Shops");

        recyclerView = (RecyclerView) findViewById(R.id.shops_view_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent setupShopInt = new Intent(ViewShopsActivity.this, ShopSetupActivity.class);
                startActivity(setupShopInt);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<viewShops, viewShopsHolder> viewshopsAdapter = new FirebaseRecyclerAdapter<viewShops, viewShopsHolder>(
                viewShops.class,
                R.layout.viewshop,
                viewShopsHolder.class,
                userShops
        ) {
            @Override
            protected void populateViewHolder(viewShopsHolder viewHolder, viewShops model, int position) {
                int Opening = Integer.parseInt(model.getOpening());
                int Closing = Integer.parseInt(model.getClosing());
                final String id = getRef(position).getKey();
                final String city = model.getCity();

                viewHolder.setTitle(model.getName());
                viewHolder.setImage(model.getImage(), getApplicationContext());
                viewHolder.setPlace(model.getPlace());
                viewHolder.setOpenStat(Opening, Closing);

                viewHolder.shopView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent shopMan = new Intent(ViewShopsActivity.this, OwnerShopActivity.class);
                        shopMan.putExtra("ShopId", id);
                        shopMan.putExtra("City", city);
                        startActivity(shopMan);
                    }
                });
            }
        };
        recyclerView.setAdapter(viewshopsAdapter);
    }

    public static class viewShopsHolder extends RecyclerView.ViewHolder {

        View shopView;

        public viewShopsHolder(View itemView) {
            super(itemView);

            shopView = itemView;
        }

        public void setTitle(String Title) {
            TextView shopTitle = (TextView) shopView.findViewById(R.id.view_shop_name);
            shopTitle.setText(Title);
        }

        public void setImage(String Image, Context context) {
            ImageView shopImage = (ImageView) shopView.findViewById(R.id.view_shop_image);
            Picasso.with(context)
                    .load(Image)
                    .into(shopImage);
        }

        public void setPlace(String Area) {
            TextView area = (TextView) shopView.findViewById(R.id.view_shop_place);
            area.setText(Area);
        }

        public void setOpenStat(int Opening, int Closing) {
            TextView Openstat = (TextView) shopView.findViewById(R.id.view_shop_status);
            Calendar calendar = Calendar.getInstance();
            int Hours = calendar.HOUR_OF_DAY * 3600;
            int min = calendar.MINUTE * 60;
            int second_of_day = Hours + min;

            if (second_of_day >= Opening && second_of_day <= Closing) {
                Openstat.setText("Open");
            } else {
                Openstat.setText("Closed");
            }

        }
    }

}
