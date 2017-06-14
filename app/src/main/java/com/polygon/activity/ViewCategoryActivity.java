package com.polygon.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polygon.R;
import com.polygon.app.baseActivity;
import com.polygon.fragments.OverViewFragment;
import com.polygon.listeners.ItemView;
import com.polygon.views.MainNavDrawer;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ViewCategoryActivity extends baseActivity {
    private RecyclerView Category_items;

    private DatabaseReference Categoriesitems;

    private String categoryId, Categoryname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);

        Category_items = (RecyclerView) findViewById(R.id.CategoryItemsRecycler);
        categoryId = getIntent().getExtras().getString("categoryKey");

        final DatabaseReference Category = FirebaseDatabase.getInstance().getReference().child("Categories").child(categoryId);
        Categoriesitems = Category.child("Items");

        Category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Categoryname = dataSnapshot.child("Title").getValue().toString();
                getSupportActionBar().setTitle(Categoryname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        Category_items.setLayoutManager(layoutManager);


        setNavDrawer(new MainNavDrawer(this));
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ItemView, CategoryItems> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ItemView, CategoryItems>(
                ItemView.class,
                R.layout.itemtemplete,
                CategoryItems.class,
                Categoriesitems
        ) {
            @Override
            protected void populateViewHolder(CategoryItems viewHolder, ItemView model, int position) {
                viewHolder.setImage(model.getImage(), getApplicationContext());
                viewHolder.setItemName(model.getName());
                viewHolder.setPlace(model.getPlace());
                viewHolder.setItemPrice(model.getPrice());
                viewHolder.setComparePrice(model.getComparePrice());

            }

        };
        Category_items.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static class CategoryItems extends RecyclerView.ViewHolder {

        View mView;

        public CategoryItems(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setItemName(String categ_name) {
            TextView category_Name = (TextView) mView.findViewById(R.id.Item_name);
            category_Name.setText(categ_name);
        }

        public void setImage(String ImageUrl, Context context) {
            ImageView Item_image = (ImageView) mView.findViewById(R.id.Item_image);
            Picasso.with(context)
                    .load(ImageUrl)
                    .into(Item_image);
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
            plac.setText(Place);
        }
    }
}
