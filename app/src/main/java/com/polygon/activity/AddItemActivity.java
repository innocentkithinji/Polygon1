package com.polygon.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private EditText ItemAmount;
    private EditText ItemPrice;
    private EditText ItemCompPrice;

    public String shop_City;
    public String shop_Id;

    private File tempOutPutFile;

    private DatabaseReference Categories;
    private DatabaseReference Inallshops;
    private DatabaseReference InshopCity;
    private FirebaseAuth mAuth;
    private DatabaseReference userShop;
    private List<String> categories;
    private Uri outputFile, mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        tempOutPutFile = new File(getExternalCacheDir(), "temp-out.jpg");
        shop_Id = getIntent().getExtras().getString("ShopId");
        shop_City = getIntent().getExtras().getString("ShopCity");
        categoryACTV = (AutoCompleteTextView) findViewById(R.id.ItemCategory);
        ItemImage = (ImageView) findViewById(R.id.addItem_imageView);
        ItemAmount = (EditText) findViewById(R.id.Item_add_Name);
        ItemPrice = (EditText) findViewById(R.id.Item_add_price);
        ItemCompPrice = (EditText) findViewById(R.id.Item_add_compPrice);

        Categories = FirebaseDatabase.getInstance().getReference().child("Categories");
        Inallshops = FirebaseDatabase.getInstance().getReference().child("Shops").child("" + "allShops").child("" + shop_Id);
        InshopCity = FirebaseDatabase.getInstance().getReference().child("Shops").child("" + shop_City).child("" + shop_Id);

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
                    .setAspectRatio(12, 7)
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


}
