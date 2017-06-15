package com.polygon.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polygon.R;
import com.polygon.app.baseActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ViewItemOwnerActivity extends baseActivity {



    private int changemode = 0;
    private ImageView itemImage;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemCompare;
    private EditText itemAmount;
    private EditText itemLocality;
    private EditText itemDescription;
    private AutoCompleteTextView categoryACTV;
    private TextView shopId;
    private EditText shoppingamount;
    private Button addToShoppingcart;
    private ActionMode  editItemMode;



    private DatabaseReference root;
    private DatabaseReference Itemdb;
    private DatabaseReference inallshops;
    private DatabaseReference inOwner;
    private DatabaseReference inCity;
    private DatabaseReference user;
    private DatabaseReference categories, ItemCategory;

    public String itemKey, name, Price, compPrice, Locality;
    private String itemShopId, description, Description;
    private String uid, itemCategory;
    private String place;
    private String ownerId;
    private String dispamount;
    private DatabaseReference item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_owner);

        itemImage = (ImageView) findViewById(R.id.Item_Owner_display_image);
        itemName = (EditText) findViewById(R.id.Item_Owner_display_Name);
        name = itemName.getText().toString();
        itemPrice = (EditText) findViewById(R.id.Item_Owner_display_price);
        Price = itemPrice.getText().toString();
        itemCompare = (EditText) findViewById(R.id.Item_Owner_display_compare_price);
        compPrice = itemCompare.getText().toString();
        itemLocality = (EditText) findViewById(R.id.Item_Owner_display_place);
        Locality = itemLocality.getText().toString();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        itemDescription = (EditText) findViewById(R.id.Item_Owner_display_description);
        Description = itemDescription.getText().toString();
        itemAmount = (EditText) findViewById(R.id.Item_Owner_display_amount);
        dispamount = itemAmount.getText().toString();
        categoryACTV = (AutoCompleteTextView) findViewById(R.id.Item_Owner_display_category);
        shopId = (TextView) findViewById(R.id.Item_Owner_display_shopId);



        itemKey = getIntent().getExtras().getString("ItemKey");
        root = FirebaseDatabase.getInstance().getReference();
        categories = root.child("Categories");
        Itemdb = root.child("allItems").child(itemKey);
        Itemdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String ItemUrl = dataSnapshot.child("Image").getValue().toString();
                String item_Price = dataSnapshot.child("Price").getValue().toString();
                String itemCPrice = dataSnapshot.child("ComparePrice").getValue().toString();
                String item_name = dataSnapshot.child("Name").getValue().toString();
                ownerId = dataSnapshot.child("OwnerID").getValue().toString();
                if (dataSnapshot.hasChild("Description")) {
                    description = "" + dataSnapshot.child("Description").getValue().toString();
                }
                itemShopId = dataSnapshot.child("ShopId").getValue().toString();
                itemCategory = dataSnapshot.child("Category").getValue().toString();
                place = dataSnapshot.child("Place").getValue().toString();
                inallshops = root.child("Shops").child("allShops").child(itemShopId)
                        .child("Items").child(itemKey);
                inOwner = root.child("users").child(ownerId)
                        .child(itemShopId)
                        .child("Items").child(itemKey);
                inCity = root.child("Shops").child("Cities").child(place)
                        .child(itemShopId).child("Items").child(itemKey);
                ItemCategory = categories.child(itemCategory).child("Items")
                        .child(itemKey);
                Picasso.with(getApplicationContext())
                        .load(ItemUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(itemImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext())
                                        .load(ItemUrl)
                                        .into(itemImage);
                            }
                        });
                itemDescription.setText(description);
                itemCompare.setText(itemCPrice);
                itemPrice.setText(item_Price);
                itemName.setText(item_name);
                shopId.setText(itemShopId);
                categoryACTV.setText(itemCategory);
                itemLocality.setText(place);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changestate(int editingState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.itemowner_editting, menu);


        return true;
    }

    private void addinDb(DatabaseReference dbref, String Name, String Price, String Amount, String Compare, String Place) {
        item = dbref;
        item.child("Name").setValue(Name);
        item.child("Price").setValue(Price);
        item.child("ComparePrice").setValue(Compare);
        item.child("Amount").setValue(Amount);
        item.child("Place").setValue(Place);
    }



    private class EditItemActionCallback extends ViewItem implements ActionMode.Callback  {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.itemowner_editting, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.done_editing){
                addinDb(Itemdb, name,Price, dispamount, compPrice, place);
                item.child("Description").setValue(description);
                addinDb(inallshops, name,Price, dispamount, compPrice, place);
                item.child("Description").setValue(description);
                addinDb(inOwner, name,Price, dispamount, compPrice, place);
                item.child("Description").setValue(description);
                addinDb(inCity, name,Price, dispamount, compPrice, place);
                item.child("Description").setValue(description);
                addinDb(ItemCategory, name,Price, dispamount, compPrice, place);
                item.child("Description").setValue(description);
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
