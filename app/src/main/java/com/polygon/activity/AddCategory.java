package com.polygon.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polygon.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddCategory extends AppCompatActivity {
    private static final int CATEG_IMAGE = 1;
    private ImageButton selectCategImage;
    private EditText catName;
    private FloatingActionButton addcateg;

    DatabaseReference Category;
    private Uri outputFile, mImageUri = null;
    private File tempOutPutFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        selectCategImage = (ImageButton) findViewById(R.id.cat_image_select);
        catName = (EditText) findViewById(R.id.cat_name);
        addcateg = (FloatingActionButton) findViewById(R.id.add_categ);
        tempOutPutFile = new File(getExternalCacheDir(), "temp-out.jpg");
        Category = FirebaseDatabase.getInstance().getReference().child("Categories");


        selectCategImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        addcateg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcategory();

            }
        });
    }

    private void addcategory() {
        if (mImageUri != null && !TextUtils.isEmpty(catName.getText().toString())){
            StorageReference categImgs = FirebaseStorage.getInstance().getReference().child("CategImage")
                    .child(mImageUri.getLastPathSegment());
            Toast.makeText(this, "Uploading", Toast.LENGTH_SHORT).show();
            categImgs.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    DatabaseReference new_categ = Category.push();
                    new_categ.child("Image").setValue(taskSnapshot.getDownloadUrl().toString());
                    new_categ.child("Title").setValue(catName.getText().toString().trim());
                    Toast.makeText(AddCategory.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            });
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
        startActivityForResult(chooser, CATEG_IMAGE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CATEG_IMAGE) {
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
            selectCategImage.setImageURI(mImageUri);
        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Exception error = result.getError();
                Log.e("CroppingError", error.toString());
            }
        }
    }

}
