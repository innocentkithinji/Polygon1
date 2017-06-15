package com.polygon.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.polygon.R;
import com.sinch.verification.Config;
import com.sinch.verification.InitiationResult;
import com.sinch.verification.PhoneNumberUtils;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

public class SetupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private DatabaseReference user;

    private LinearLayout sms_layout;
    private LinearLayout verify_layout;
    private EditText name;
    private EditText Email;
    private TextView confirmNumber;
    private ImageButton editfNumber;
    private EditText number;
    private EditText Otp;
    private Button verify;
    private Button submit;
    private String fullNames;
    private String email;
    private String countryCode;
    private String phoneNumber;


    private CountryCodePicker cpp;


    private ProfileTracker mProfileTracker;
    private final String APPLICATION_KEY = "8a43e0ee-70ab-4563-be7f-0de125d04805";
    private Verification verification;
    private String phoneNumberInE164;
    private Profile profile = Profile.getCurrentProfile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    Intent LoginIntent = new Intent(SetupActivity.this, LoginActivity.class);
                    startActivity(LoginIntent);
                }
            }
        };

        user = FirebaseDatabase.getInstance().getReference().child("Users");

        sms_layout = (LinearLayout) findViewById(R.id.layout_sms);
        verify_layout = (LinearLayout) findViewById(R.id.layout_otp);
        verify_layout.setVisibility(View.GONE);
        verify = (Button) findViewById(R.id.btn_verify_otp);
        Otp = (EditText) findViewById(R.id.inputOtp);

        cpp = (CountryCodePicker) findViewById(R.id.cpp);
        number = (EditText) findViewById(R.id.inputMobile);
        Email = (EditText) findViewById(R.id.inputEmail);
        name = (EditText) findViewById(R.id.inputName);

        confirmNumber = (TextView) findViewById(R.id.confirm_number);
        editfNumber = (ImageButton) findViewById(R.id.edit_confirm_number);


        submit = (Button) findViewById(R.id.btn_submit);
        cpp.setDefaultCountryUsingPhoneCode(254);
        cpp.resetToDefaultCountry();

        if (fbisloggedin()) {
            name.setText(profile.getName());

        }
        Email.setText(mAuth.getCurrentUser().getEmail());
        editfNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms_layout.setVisibility(View.VISIBLE);
                verify_layout.setVisibility(View.GONE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email)) {
                    email = Email.getText().toString();
                } else {
                    Email.setText(email);
                }
                if (TextUtils.isEmpty(fullNames)) {
                    fullNames = name.getText().toString();
                } else {
                    name.setText(fullNames);
                }
                countryCode = cpp.getSelectedCountryCode();
                phoneNumber = number.getText().toString();
                Email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        final String Changed = s.toString();
                        DatabaseReference emails = user.child("Emails");

                        emails.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               // if (dataSnapshot.hasChild(Changed)) {
                                    Email.setError("Email already in use by someone else");
                               // }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
                if (!TextUtils.isEmpty(fullNames) && !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(email)) {
                    sms_layout.setVisibility(View.GONE);
                    verify_layout.setVisibility(View.VISIBLE);
                    Config config = SinchVerification.config().applicationKey(APPLICATION_KEY).context(getApplicationContext()).build();
                    VerificationListener listener = new MyVerificationListener();
                    phoneNumberInE164 = "+" + countryCode + phoneNumber;
                    DatabaseReference currentUser = user.child(mAuth.getCurrentUser().getUid());
                    currentUser.child("Name").setValue(fullNames);
                    currentUser.child("Email").setValue(email);
                    currentUser.child("Mobile").setValue(phoneNumberInE164);
                    if (fbisloggedin()) {
                        currentUser.child("Image").setValue(profile.getProfilePictureUri(400, 400).toString());
                    }
                    startActivity(new Intent(SetupActivity.this, MainActivity.class));
                    finish();
//                    confirmNumber.setText(phoneNumberInE164);
//                    verification = SinchVerification.createSmsVerification(config, phoneNumberInE164, listener);
//                    verification.initiate();
                } else {
                    if (TextUtils.isEmpty(fullNames))
                        name.setError("");
                    if (TextUtils.isEmpty(phoneNumber))
                        number.setError("Provide contact nummber");
                    if (TextUtils.isEmpty(email))
                        Email.setError("Provide email");
                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verification.verify(Otp.getText().toString());
            }
        });

    }

    private boolean fbisloggedin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private class MyVerificationListener implements VerificationListener {
        @Override
        public void onInitiated(InitiationResult initiationResult) {

        }

        @Override
        public void onInitiationFailed(Exception e) {

        }

        @Override
        public void onVerified() {

        }

        @Override
        public void onVerificationFailed(Exception e) {

        }
    }
}