package com.pesanmerpati.firebaseloginauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class HomeActivity extends AppCompatActivity {

    EditText mEditTextName;
    Button mButtonSave, mButtonLogout;
    TextView mTextViewVerified;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        mEditTextName = findViewById(R.id.editText_name);
        mTextViewVerified = findViewById(R.id.textView_verified);

        mButtonSave = findViewById(R.id.button_save);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        loadUserInformation();

        mButtonLogout = findViewById(R.id.button_log_out);
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });


    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        String displayName = user.getDisplayName();

        if (user != null) {
            if (user.getDisplayName() != null) {
                mEditTextName.setText(displayName);
            }

            if (user.isEmailVerified()) {
                mTextViewVerified.setText("Email verified");
            } else {
                mTextViewVerified.setText("Email not verified (click to verified)");
                mTextViewVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(HomeActivity.this, "Code verification sent to your email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void saveUserInformation() {
        String displayName = mEditTextName.getText().toString();

        if (displayName.isEmpty()) {
            mEditTextName.setError("Field is required");
            mEditTextName.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(HomeActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
