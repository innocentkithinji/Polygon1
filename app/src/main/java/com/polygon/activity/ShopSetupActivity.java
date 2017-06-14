package com.polygon.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.polygon.app.baseActivity;
import com.polygon.views.MainNavDrawer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ShopSetupActivity extends baseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int PLACE_PICKER_REQUEST = 0;
    private static final int SHOP_BUNNER_REQUEST = 1;
    private static final int SHOP_IMAGE_REQUEST = 2;
    private ImageButton bunnerButton;
    private EditText shop_name, shopDesc, shopID;
    private ImageView correct, good;
    private TextView counter;
    private Button locationBtn, closeHour, openHour;
    private File tempOutPutFile;
    private DrawerLayout drawerLayout;
    private double lon;
    private double lat;
    private GoogleApiClient mGoogleApiClient;
    private Uri outputFile, mImageUri = null;
    private String openSec, closeSec, city;
    private int mOPenHour, mOPenMin, mCloseHour, mCloseMin;
    private DatabaseReference mDatabase;
    private DatabaseReference UserShops;
    private ProgressDialog progressDialog;
    private Place place;
    private String address;
    private String color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_setup);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shops");
        UserShops = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        progressDialog = new ProgressDialog(this);
        city = null;
        getSupportActionBar().setTitle("Create a Shop");
        setNavDrawer(new MainNavDrawer(this));

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        good = (ImageView) findViewById(R.id.shop_setup_good);
        correct = (ImageView) findViewById(R.id.shop_setup_error);
        locationBtn = (Button) findViewById(R.id.choose_Location);
        bunnerButton = (ImageButton) findViewById(R.id.shop_setup_bunner);
        shop_name = (EditText) findViewById(R.id.shop_setup_Name);
        shopID = (EditText) findViewById(R.id.shop_setup_id);
        shopDesc = (EditText) findViewById(R.id.shop_setup_description);
        openHour = (Button) findViewById(R.id.open_hours);
        closeHour = (Button) findViewById(R.id.close_hours);
        counter = (TextView) findViewById(R.id.shop_setup_counter);
        tempOutPutFile = new File(getExternalCacheDir(), "temp-out.jpg");
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlace();
            }
        });

        bunnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(SHOP_BUNNER_REQUEST);
            }
        });


        openHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                mOPenHour = calendar.get(Calendar.HOUR_OF_DAY);
                mOPenMin = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ShopSetupActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        openSec = "" + ((hourOfDay * 3600) + (minute * 60));
                        if (minute < 10) {
                            openHour.setText(hourOfDay + ":" + "0" + minute);
                        } else {
                            openHour.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, mOPenHour, mOPenMin, false);

                timePickerDialog.show();
            }
        });

        closeHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                mCloseHour = calendar.get(Calendar.HOUR_OF_DAY);
                mCloseMin = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(ShopSetupActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        closeSec = "" + ((hourOfDay * 3600) + (minute * 60));
                        if (minute < 10) {
                            closeHour.setText(hourOfDay + ":" + "0" + minute);
                        } else {
                            closeHour.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, mOPenHour, mOPenMin, false);

                timePickerDialog.show();
            }
        });

        shopID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                DatabaseReference allshopsId = mDatabase.child("allShops");
                allshopsId.keepSynced(true);

                if ((editable.toString()).length() < 5)
                    correct.setVisibility(View.VISIBLE);

                else {
                    correct.setVisibility(View.GONE);
                    allshopsId.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(editable.toString())) {
                                shopID.setError("ShopID already in-use!");
                            } else {
                                good.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        shopDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                counter.setText("" + (300 - editable.toString().length()));
            }
        });

    }

    private void pickPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Intent intent;
        try {
            intent = builder.build(ShopSetupActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    private void chooseImage(int i) {
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
        startActivityForResult(chooser, i);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                place = PlacePicker.getPlace(data, this);
                if (place.getAddress() != null) {
                    address = place.getAddress().toString();
                    locationBtn.setText(address);
                    lat = place.getLatLng().latitude;
                    lon = place.getLatLng().longitude;
                    Geocoder geo = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> plAddress = geo.getFromLocation(lat, lon, 2);
                        if (plAddress.size() > 0) {
                            city = plAddress.get(0).getLocality();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ShopSetupActivity.this, "place not picked", Toast.LENGTH_SHORT).show();
                }
            }

            if (requestCode == SHOP_BUNNER_REQUEST || requestCode == SHOP_IMAGE_REQUEST) {
                Uri tempFileUri = Uri.fromFile(tempOutPutFile);

                if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE))) {
                    outputFile = data.getData();
                } else {
                    outputFile = tempFileUri;
                }

                if (requestCode == SHOP_BUNNER_REQUEST) {
                    CropImage.activity(outputFile)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(12, 7)
                            .start(this);
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                mImageUri = result.getUri();
                bunnerButton.setImageURI(mImageUri);
            }

        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Exception error = result.getError();
                Log.e("CroppingError", error.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.done) {
            addShop();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addShop() {

        final String shop_Name = shop_name.getText().toString().trim();
        final String shop_id = shopID.getText().toString().trim();
        final String shop_desc = shopDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(shop_Name) && !TextUtils.isEmpty(shop_desc) && !TextUtils.isEmpty(shop_id)
                && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(openSec) && !TextUtils.isEmpty(closeSec)) {
            int shopIdlen = shop_id.length();
            if (shopIdlen >= 5) {
                StorageReference images = FirebaseStorage.getInstance().getReference().child("ShopImages");
                StorageReference image_push = images.child(mImageUri.getLastPathSegment());
                image_push.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downLoadUrl = taskSnapshot.getDownloadUrl();
                        DatabaseReference newShop = mDatabase.child("allShops").child(shop_id);
                        DatabaseReference user_shop = UserShops.child("Shops").child(shop_id);
                        DatabaseReference currentCityShops = mDatabase.child("Cities").child(city).child(shop_id);
                        GeoFire allShopsGeo = new GeoFire(newShop);
                        newShop.child("Name").setValue(shop_Name);
                        newShop.child("Image").setValue(downLoadUrl.toString());
                        newShop.child("Description").setValue(shop_desc);
                        newShop.child("Place").setValue(address);
                        newShop.child("Longitude").setValue(lon);
                        newShop.child("Latitude").setValue(lat);
                        newShop.child("Opening").setValue(openSec);
                        newShop.child("Closing").setValue(closeSec);
                        newShop.child("ShopId").setValue(shop_id);
                        newShop.child("City").setValue(city);
                        newShop.child("OwnerId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        allShopsGeo.setLocation(shop_id, new GeoLocation(lat, lon));

                        GeoFire user_shop_geop = new GeoFire(user_shop);
                        user_shop.child("Name").setValue(shop_Name);
                        user_shop.child("Image").setValue(downLoadUrl.toString());
                        user_shop.child("Description").setValue(shop_desc);
                        user_shop.child("Place").setValue(address);
                        user_shop.child("Longitude").setValue(lon);
                        user_shop.child("Latitude").setValue(lat);
                        user_shop.child("Opening").setValue(openSec);
                        user_shop.child("Closing").setValue(closeSec);
                        user_shop.child("ShopId").setValue(shop_id);
                        user_shop.child("City").setValue(city);
                        user_shop.child("OwnerId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        allShopsGeo.setLocation(shop_id, new GeoLocation(lat, lon));

                        GeoFire cityGeo = new GeoFire(currentCityShops);
                        currentCityShops.child("Name").setValue(shop_Name);
                        currentCityShops.child("Image").setValue(downLoadUrl.toString());
                        currentCityShops.child("Description").setValue(shop_desc);
                        currentCityShops.child("Place").setValue(address);
                        currentCityShops.child("Longitude").setValue(lon);
                        currentCityShops.child("Latitude").setValue(lat);
                        currentCityShops.child("Opening").setValue(openSec);
                        currentCityShops.child("Closing").setValue(closeSec);
                        currentCityShops.child("ShopId").setValue(shop_id);
                        currentCityShops.child("City").setValue(city);
                        currentCityShops.child("OwnerId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        cityGeo.setLocation(shop_id, new GeoLocation(lat, lon));
//                        progressDialog.dismiss();
                    }
                });
                
            } else {
                shopID.setError("Less than 5 Characters");
            }
        } else {
            if (TextUtils.isEmpty(shop_Name))
                shop_name.setError("Provide Name");
            if (TextUtils.isEmpty(shop_desc))
                shopDesc.setError("Provide a Description");
            if (TextUtils.isEmpty(shop_id))
                shopID.setError("Id cannot be null");
            if (TextUtils.isEmpty(city)) {
                Snackbar snackbar = Snackbar
                        .make(drawerLayout, "please choose a city", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Choose", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pickPlace();
                            }
                        });
                snackbar.show();
            }
            if (TextUtils.isEmpty(openSec)){
                openHour.setError("Choose opening time");
            }
            if (TextUtils.isEmpty(closeSec)){
                closeHour.setError("Choose closing Time");
            }
        }

        Intent show_shop = new Intent(ShopSetupActivity.this, ViewShopsActivity.class);
        startActivity(show_shop);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
