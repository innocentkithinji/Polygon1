package com.polygon.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polygon.R;
import com.polygon.activity.AddItemActivity;
import com.polygon.activity.BrowseActivities;
import com.polygon.activity.OwnerShopActivity;
import com.polygon.listeners.Categories;
import com.polygon.listeners.ItemView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;


public class OverViewFragment extends Fragment {

    public String shopId;
    public DatabaseReference shop;
    public DatabaseReference allshops;
    public DatabaseReference cityshop;
    public DatabaseReference userShop;
    private DatabaseReference shopItemsales;
    private String shopCity;
    private FloatingActionButton addItemFab;

    private RecyclerView shopItems;
    private TextView shopPlace;
    private TextView shopName;
    private TextView shopDesc;
    private ImageView shopImage;
    private DatabaseReference shopDetails;

    public OverViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        shopId = ((OwnerShopActivity) getActivity()).getShopId();
        shopCity = ((OwnerShopActivity) getActivity()).getShopCity();
        shop = FirebaseDatabase.getInstance().getReference().child("Shops");
        allshops = shop.child("allShops");
        if (shopCity == null) {
            shopCity = ((OwnerShopActivity) getActivity()).getShopCity();
        }
            cityshop = shop.child(shopCity);

        userShop = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_over_view, container, false);

        shopImage = (ImageView) view.findViewById(R.id.specShopImage);
        shopDesc = (TextView) view.findViewById(R.id.specShopDesc);
        shopName = (TextView) view.findViewById(R.id.specShopName);
        shopPlace = (TextView) view.findViewById(R.id.specShopPlace);
        shopItems = (RecyclerView) view.findViewById(R.id.shopItemsRecycler);
        addItemFab = (FloatingActionButton) view.findViewById(R.id.Item_add_fab);
        shopDetails = userShop.child("Shops").child(shopId);
        Toast.makeText(getApplicationContext(), ""+shopId, Toast.LENGTH_SHORT).show();
        shopItemsales = shopDetails.child("Items");


        shopDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.with(getActivity().getApplicationContext())
                        .load(dataSnapshot.child("Image").getValue().toString())
                        .into(shopImage);
                shopName.setText(dataSnapshot.child("Name").getValue().toString());
                shopDesc.setText(dataSnapshot.child("Description").getValue().toString());
                shopPlace.setText(dataSnapshot.child("Place").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        shopItems.setLayoutManager(layoutManager);


        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addItem = new Intent(getApplicationContext(), AddItemActivity.class);
                addItem.putExtra("ShopId", shopId);
                if (shopCity == null){
                    shopCity = ((OwnerShopActivity) getActivity()).getShopCity();
                }
                addItem.putExtra("ShopCity", shopCity);
                startActivity(addItem);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ItemView, ItemsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<com.polygon.listeners.ItemView, ItemsViewHolder>(
                ItemView.class,
                R.layout.itemtemplete,
                ItemsViewHolder.class,
                shopItemsales
        ) {
            @Override
            protected void populateViewHolder(ItemsViewHolder viewHolder, ItemView model, int position) {
                    viewHolder.setImage(model.getImage(), getApplicationContext());
                    viewHolder.setItemName(model.getName());
                    viewHolder.setPlace(model.getPlace());
                    viewHolder.setItemPrice(model.getPrice());
                    viewHolder.setComparePrice(model.getComparePrice());
            }
        };
        shopItems.setAdapter(firebaseRecyclerAdapter);
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

        public void setItemPrice(String Price){
            TextView ItemPrice = (TextView) mView.findViewById(R.id.itemprice);
            ItemPrice.setText(Price);
        }

        public void setComparePrice(String ComPrice){
            TextView compprice = (TextView) mView.findViewById(R.id.compareprice);
            compprice.setText(ComPrice);
        }

        public void setPlace (String Place){
            TextView plac = (TextView) mView.findViewById(R.id.item_place);
            plac.setText(Place);
        }



    }

}
