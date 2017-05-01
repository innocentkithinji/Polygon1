package com.polygon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polygon.R;
import com.polygon.app.baseActivity;
import com.polygon.views.MainNavDrawer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddShopActivity extends baseActivity {
    private static final int PROFILE_PIC_REQUEST = 2;
    ImageButton cat_image;
    Button submit;
    EditText cat_name;

    private File tempOutPutFile;
    private Uri mImageUri = null;

    private Uri outputFile;
    private ProgressDialog mProgress;

    private DatabaseReference Categories;
    private StorageReference mStorageref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        setNavDrawer(new MainNavDrawer(this));



        cat_image = (ImageButton) findViewById(R.id.cat_image);
        submit = (Button) findViewById(R.id.cat_submit);
        cat_name = (EditText) findViewById(R.id.cat_name);
        tempOutPutFile = new File(getExternalCacheDir(), "temp-image.jpg");

        Categories = FirebaseDatabase.getInstance().getReference().child("Categories");
        mStorageref = FirebaseStorage.getInstance().getReference();
        Categories.keepSynced(true);

        mProgress = new ProgressDialog(this);
        cat_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                startActivityForResult(chooser, PROFILE_PIC_REQUEST);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCategory();
            }
        });
    }

    private void postCategory() {
        final String Name = cat_name.getText().toString();

        if (!TextUtils.isEmpty(Name) && mImageUri!=null){
            mProgress.setMessage("Uploading Category");
            mProgress.show();

            StorageReference cat_filepath = mStorageref.child("Category_images").child(mImageUri.getLastPathSegment());

            cat_filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downLoadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newCategory = Categories.push();
                    newCategory.child("Title").setValue(Name);
                    newCategory.child("Image").setValue(downLoadUrl.toString());
                    cat_name.setText("");
                    cat_image.setImageResource(R.mipmap.ic_account_box_white_24dp);
                    mProgress.dismiss();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_PIC_REQUEST) {

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
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                cat_image.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("Cropping", error.toString());
            }
        }
    }
}
