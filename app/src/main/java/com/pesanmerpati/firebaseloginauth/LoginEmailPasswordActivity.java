package com.pesanmerpati.firebaseloginauth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailPasswordActivity extends AppCompatActivity {

    EditText mEditTextEmail, mEditTextPassword;
    TextView mTextViewGoToSignUp, mTextViewResetPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email_password);

        mAuth = FirebaseAuth.getInstance();

        mEditTextEmail = findViewById(R.id.editText_email);
        mEditTextPassword = findViewById(R.id.editText_password);
        mTextViewGoToSignUp = findViewById(R.id.textView_goto_signup);

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });

        mTextViewGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToSignUp();
            }
        });

        mTextViewResetPassword = findViewById(R.id.textView_reset_password);
        mTextViewResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogResetPassword();
            }
        });
    }

    private void showDialogResetPassword() {
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("RESET PASSWORD");
        mDialog.setMessage("Please use your registered email to receive code.");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_reset_password, null);

        final EditText mEditTextEmailResetPassword = register_layout.findViewById(R.id.editText_email);


        mDialog.setView(register_layout);

        mDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.sendPasswordResetEmail(mEditTextEmailResetPassword.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginEmailPasswordActivity.this, "Sent", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginEmailPasswordActivity.this, "Email doesn't exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginEmailPasswordActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void loginProcess() {

        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditTextEmail.setError("Email is not valid");
            mEditTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mEditTextPassword.setError("Required");
            mEditTextPassword.requestFocus();
        }

        if (password.length() < 6) {
            mEditTextPassword.setError("Required");
            mEditTextPassword.requestFocus();
        }

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginEmailPasswordActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginEmailPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void GoToSignUp() {
        startActivity(new Intent(LoginEmailPasswordActivity.this, SignUpActivity.class));
        finish();
    }
}
