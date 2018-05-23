package com.pesanmerpati.firebaseloginauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.security.Signature;

public class SignUpActivity extends AppCompatActivity {

    EditText mEditTextEmail, mEditTextPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mEditTextEmail = findViewById(R.id.editText_email);
        mEditTextPassword = findViewById(R.id.editText_password);

        findViewById(R.id.button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupProcess();
            }
        });

        findViewById(R.id.textView_goto_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginEmailPasswordActivity.class));
                finish();
            }
        });
    }

    private void signupProcess() {
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditTextEmail.setError("Enter a valid email address");
            mEditTextEmail.requestFocus();
        }

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
