package com.polygon.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polygon.R;
import com.polygon.app.baseActivity;
import com.polygon.listeners.Categories;
import com.polygon.listeners.ItemView;
import com.polygon.views.MainNavDrawer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends baseActivity {
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mCategoryDatabase;
    private RecyclerView categories;
    private Query withItems;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Home");



        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }else{
                }
            }
        };
        if (mAuth.getCurrentUser() != null){
            setNavDrawer(new MainNavDrawer(this));
        }

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);


        mCategoryDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");
        mCategoryDatabase.keepSynced(true);




        categories = (RecyclerView) findViewById(R.id.Categories_recycler);
        categories.setLayoutManager(new LinearLayoutManager(this));

//        LinearLayout fbad = (LinearLayout) findViewById(R.id.banner_container);
//        AdView adView = new AdView(getApplicationContext(), "400051760355146_405056099854712", AdSize.BANNER_HEIGHT_50);
//        fbad.addView(adView);
//        adView.loadAd();
//        fbad.setVisibility(View.VISIBLE);


        checkUserExists();


    }


    private void checkUserExists() {
        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Categories, MainCategoryViewHolder> fbAdapter = new FirebaseRecyclerAdapter<Categories, MainCategoryViewHolder>(
                Categories.class,
                R.layout.category_recyclers,
                MainCategoryViewHolder.class,
                mCategoryDatabase

        ) {
            @Override
            protected void populateViewHolder(final MainCategoryViewHolder viewHolder, final Categories model, final int position) {
                categoryId = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setItems(getApplication().getApplicationContext(), categoryId);
                viewHolder.initialize();
                viewHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        categoryId = getRef(position).getKey();
                        Intent intent = new Intent(MainActivity.this, ViewCategoryActivity.class);
                        intent.putExtra("categoryKey", categoryId);
                        startActivity(intent);
                    }
                });
            }
        };

        categories.setAdapter(fbAdapter);
    }


    public static class MainCategoryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private DatabaseReference mCategoryItemsDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");
        private Button moreBtn;

        RecyclerView category_items;
        public MainCategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            category_items = (RecyclerView) mView.findViewById(R.id.main_category_list_items);
        }


        public void initialize(){
            moreBtn = (Button) mView.findViewById(R.id.main_act_category_more_btn);
        }


        public void setTitle(String title) {
            TextView Title = (TextView) mView.findViewById(R.id.main_act_category_name);
            Title.setText(title);
        }

        public void setItems(final Context context, String category) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            category_items.setLayoutManager(layoutManager);
            FirebaseRecyclerAdapter<ItemView,ItemsViewHolder > categoryItemAdapter = new FirebaseRecyclerAdapter<ItemView, ItemsViewHolder>(
                    ItemView.class,
                    R.layout.main_categories_template,
                    ItemsViewHolder.class,
                    mCategoryItemsDatabase.child(category).child("Items")
                    ) {
                @Override
                protected void populateViewHolder(final ItemsViewHolder viewHolder, final ItemView model, int position) {
                    viewHolder.setImage(model.getImage(), context);
                    final String itemKey = getRef(position).getKey();
                    viewHolder.setItemName(model.getName());
                    viewHolder.setPlace(model.getPlace());
                    viewHolder.setItemPrice(model.getPrice());
                    viewHolder.setComparePrice(model.getComparePrice());
                    viewHolder.setSavingpercent(model.getPrice(),model.getComparePrice());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewHolder.openItem(model.getOwnerID(),context, itemKey);
                        }
                    });
                }
            };

            category_items.setAdapter(categoryItemAdapter);
        }


    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ItemsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setItemName(String categ_name) {
            TextView category_Name = (TextView) mView.findViewById(R.id.Item_name);
            category_Name.setText(categ_name);
        }

        public void setImage(final String ImageUrl, final Context context) {
            final ImageView Item_image = (ImageView) mView.findViewById(R.id.Item_image);
            Picasso.with(context)
                    .load(ImageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(Item_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context)
                                    .load(ImageUrl)
                                    .into(Item_image);
                        }
                    });
        }

        public void setItemPrice(String Price) {
            TextView ItemPrice = (TextView) mView.findViewById(R.id.itemprice);
            ItemPrice.setText(Price);
        }

        public void setComparePrice(String ComPrice) {
            TextView compprice = (TextView) mView.findViewById(R.id.compareprice);
            compprice.setText(ComPrice);
        }

        public void setPlace(String Place) {
            TextView plac = (TextView) mView.findViewById(R.id.item_place);
            plac.setVisibility(View.GONE);
        }

        public void setSavingpercent(String Price, String comPrice) {
            TextView savperc = (TextView) mView.findViewById(R.id.item_per_discount);
            savperc.setVisibility(View.GONE);

        }

        public void openItem(String Ownerid, Context context, String itemKey) {
            try {
                if (Ownerid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(context, "Owner", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ViewItemOwnerActivity.class);
                    intent.putExtra("ItemKey", itemKey);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Owner", Toast.LENGTH_SHORT).show();
                    Intent viewItem = new Intent(context, ViewItem.class);
                    viewItem.putExtra("Mode", 1);
                    viewItem.putExtra("ItemKey", itemKey);
                    viewItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     context.startActivity(viewItem);
                }
            }catch (NullPointerException e){}

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

}