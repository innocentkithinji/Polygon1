package com.polygon.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.polygon.R;
import com.polygon.app.baseActivity;
import com.polygon.views.MainNavDrawer;
import com.squareup.picasso.Picasso;

public class BrowseActivities extends baseActivity {
    RecyclerView recyclerView;
    DatabaseReference Categories;
    Query Categoriesq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_activities);
        setNavDrawer(new MainNavDrawer(this));
        recyclerView = (RecyclerView) findViewById(R.id.browse_categories);
        Categories = FirebaseDatabase.getInstance().getReference().child("Categories");
        Categoriesq = Categories.orderByChild("Title");
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<com.polygon.listeners.Categories, CategoryViewHolder> firebrecyclerAdapter = new FirebaseRecyclerAdapter<com.polygon.listeners.Categories, CategoryViewHolder>(
                com.polygon.listeners.Categories.class,
                R.layout.categories_item,
                CategoryViewHolder.class,
                Categoriesq
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, com.polygon.listeners.Categories model, int position) {
                    viewHolder.setCategoryImage(model.getImage(), getApplicationContext());
                    viewHolder.setCategoryName(model.getTitle());
            }
        };
        recyclerView.setAdapter(firebrecyclerAdapter);
    }

   public static class CategoryViewHolder extends RecyclerView.ViewHolder{

       View mView;
       public CategoryViewHolder(View itemView) {
           super(itemView);

           mView = itemView;
       }

       public void setCategoryName(String categ_name){
           TextView category_Name = (TextView) mView.findViewById(R.id.choice_name);
           category_Name.setText(categ_name);
       }
       public void setCategoryImage(String ImageUrl, Context context){
           ImageView category_image = (ImageView) mView.findViewById(R.id.choice_image);
           Picasso.with(context)
                   .load(ImageUrl)
                   .into(category_image);
       }

   }
}
