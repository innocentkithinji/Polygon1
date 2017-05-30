package com.polygon.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polygon.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {
    private static final int ITEM_IMAGE = 1;
    private AutoCompleteTextView categoryACTV;
    private ImageView ItemImage;
    private EditText ItemName;
    private EditText ItemPrice;
    private EditText ItemCompPrice;
    private FloatingActionButton doneFab;

    public String CategoryId = null;
    public String shop_City;
    public String shop_Id;

    private File tempOutPutFile;

    private DatabaseReference Categories;
    private DatabaseReference Inallshops;
    private DatabaseReference InshopCity;
    private FirebaseAuth mAuth;
    private List<String> categories;
    private Uri outputFile, mImageUri = null;
    private EditText ItemAmount;
    private DatabaseReference InOwner;
    private DatabaseReference item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        tempOutPutFile = new File(getExternalCacheDir(), "temp-out.jpg");
        shop_Id = getIntent().getExtras().getString("ShopId");
        shop_City = getIntent().getExtras().getString("ShopCity");
        categoryACTV = (AutoCompleteTextView) findViewById(R.id.ItemCategory);
        ItemImage = (ImageView) findViewById(R.id.addItem_imageView);
        ItemName = (EditText) findViewById(R.id.Item_add_Name);
        ItemAmount = (EditText) findViewById(R.id.Item_add_amount);
        ItemAmount.setText("1");
        ItemPrice = (EditText) findViewById(R.id.Item_add_price);
        ItemCompPrice = (EditText) findViewById(R.id.Item_add_compPrice);
        doneFab = (FloatingActionButton) findViewById(R.id.Item_add_amount_Fab);

        Categories = FirebaseDatabase.getInstance().getReference().child("Categories");
        Inallshops = FirebaseDatabase.getInstance().getReference().child("Shops").child("allShops").child("" + shop_Id).child("Items");
        InshopCity = FirebaseDatabase.getInstance().getReference().child("Shops").child("Cities").child("" + shop_City).child("" + shop_Id).child("Items");
        InOwner = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Shops").child(shop_Id).child("Items");

        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addItem();
            }
        });
        Categories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories = new ArrayList<>();
                ArrayAdapter<String> categAdatapter = new ArrayAdapter<String>(AddItemActivity.this,
                        android.R.layout.simple_spinner_item, categories);

                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    String categoryName = category.child("Title").getValue().toString();
                    categories.add(categoryName);
                    categAdatapter.add(categoryName);
                }
                categoryACTV.setAdapter(categAdatapter);

                categoryACTV.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        final String catName = editable.toString();
                        DatabaseReference rCateg = FirebaseDatabase.getInstance().getReference().child("Categories");
                        rCateg.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot p : dataSnapshot.getChildren()){
                                    if (p.child("Title").getValue().toString().equals(catName)){
                                        CategoryId = p.getKey();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == ITEM_IMAGE) {
            Uri tempFileUri = Uri.fromFile(tempOutPutFile);

            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE))) {
                outputFile = data.getData();
            } else {
                outputFile = tempFileUri;
            }
            CropImage.activity(outputFile)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            ItemImage.setImageURI(mImageUri);
        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Exception error = result.getError();
                Log.e("CroppingError", error.toString());
            }
        }
    }

    private void chooseImage() {
        List<Intent> imageCaptureIntents = new ArrayList<>();
        List<ResolveInfo> imageCaptureActivities = getPackageManager()
                .queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);

        for (ResolveInfo info : imageCaptureActivities) {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempOutPutFile));
            imageCaptureIntents.add(captureIntent);
        }

        Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
        selectImageIntent.setType("image/*");

        Intent chooser = Intent.createChooser(selectImageIntent, "Choose Avatar");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, imageCaptureIntents.toArray(new Parcelable[imageCaptureActivities.size()]));
        startActivityForResult(chooser, ITEM_IMAGE);

    }

    private void addItem() {
        final String name = ItemName.getText().toString().trim();
        final String amount = ItemAmount.getText().toString().trim();
        final String price = ItemPrice.getText().toString().trim();
        final String Category  = categoryACTV.getText().toString().trim();
        String CompPrice = ItemCompPrice.getText().toString().trim();



        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(amount) && !TextUtils.isEmpty(price)
                && !TextUtils.isEmpty(CompPrice) && !TextUtils.isEmpty(CategoryId) && !TextUtils.isEmpty(Category)
                && mImageUri != null) {
            final DatabaseReference categoryItem = FirebaseDatabase.getInstance().getReference().child("Categories").child(CategoryId).child("Items");
            StorageReference images = FirebaseStorage.getInstance().getReference().child("ShopImages");
            StorageReference image_push = images.child(mImageUri.getLastPathSegment());
            final String finalCompPrice = CompPrice;
            image_push.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downLoadUrl = taskSnapshot.getDownloadUrl().toString();
                    Toast.makeText(AddItemActivity.this, "Added to Catalogue", Toast.LENGTH_SHORT).show();
                    Log.e("Next Load Polygon", "From Here");
                    addinDb(InOwner, name, price, amount, finalCompPrice, shop_City);
                    item.child("Image").setValue(downLoadUrl);
                    Toast.makeText(AddItemActivity.this, "Owner", Toast.LENGTH_SHORT);
                    addinDb(Inallshops, name, price, amount, finalCompPrice, shop_City);
                    item.child("Image").setValue(downLoadUrl);
                    Toast.makeText(AddItemActivity.this, "AllShops", Toast.LENGTH_SHORT);
                    addinDb(InshopCity, name, price, amount, finalCompPrice, shop_City);
                    item.child("Image").setValue(downLoadUrl);
                    Toast.makeText(AddItemActivity.this, "City", Toast.LENGTH_SHORT);
                    addinDb(categoryItem, name, price, amount, finalCompPrice, shop_City);
                    item.child("Image").setValue(downLoadUrl);
                    Toast.makeText(AddItemActivity.this, "Category", Toast.LENGTH_SHORT);
                    finish();
                }
            });
        }else {
            if (TextUtils.isEmpty(name))
                ItemName.setError("Name Cannot be empty");
            if (TextUtils.isEmpty(amount))
                ItemAmount.setError("Please give a stock");
            if (TextUtils.isEmpty(price))
                ItemPrice.setError("Specify a Price for your Product");
        }
    }

    private void addinDb(DatabaseReference dbref, String Name, String Price, String Amount, String Compare, String Place) {
        item = dbref.push();
        item.child("Name").setValue(Name);
        item.child("Price").setValue(Price);
        item.child("ComparePrice").setValue(Compare);
        item.child("Amount").setValue(Amount);
        item.child("Place").setValue(Place);
    }

}
