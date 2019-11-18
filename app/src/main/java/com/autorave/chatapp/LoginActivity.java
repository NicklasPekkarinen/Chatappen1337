package com.autorave.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailID, passWord;
    CheckBox showPassword;
    Button btnLogin;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Checks if a user is already logged in and skips LoginActivity
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailID = findViewById(R.id.edit_email);
        passWord = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.button_login);
        showPassword = findViewById(R.id.checkbox);

        //Method to show or hide password
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }

    public void loginProses(View view) {
        //checks if email and password is empty
        String email = emailID.getText().toString();
        String pwd = passWord.getText().toString();
        if(email.isEmpty()){
            emailID.setError("Please enter your email");
            emailID.requestFocus();
        }
        else if(pwd.isEmpty()){
            passWord.setError("Please enter your password");
            passWord.requestFocus();
        }
        else{
            mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login error please login again", Toast.LENGTH_SHORT).show();
                    } else {
                        // checks if the user has verified their email, if so the user can log in.
                        boolean isEmailVerified = mFirebaseUser.isEmailVerified();
                        if (isEmailVerified) {
                            Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Please Verify your email", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if(mFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                }

            }
        };

    }
    //onClick textview to sign up
    public void toSignUp(View view){
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }
    //onClick textview to forgot email activity
    public void forgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }
}
