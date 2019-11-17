package com.autorave.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {
    private EditText emailID, passWord, userName;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mRootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth =FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();

        emailID = findViewById(R.id.edit_email);
        passWord = findViewById(R.id.edit_password);
        userName = findViewById(R.id.user_name);
        CheckBox showPassword = findViewById(R.id.check_box);

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
    //Sign up method
    public void signUp(View view) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        String email = emailID.getText().toString();
        String pwd = passWord.getText().toString();

        //checks if any of the fields are empty
        if(email.isEmpty()){
            emailID.setError("Please enter your email");
            emailID.requestFocus();
        }
        else if(pwd.isEmpty()){
            passWord.setError("Please enter your password");
            passWord.requestFocus();
        }
            // If all field are field this method crates user
            mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        insertValuesToFirebase();

                    }
                    else{
                        Toast.makeText(SignUpActivity.this,"SignUp failed",Toast.LENGTH_SHORT).show();

                    }
                }
            });
    }
    // Method to insert all values in database, like Email and password and username
    private void insertValuesToFirebase(){
        String uN = userName.getText().toString().trim();
        final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String userID = firebaseUser.getUid();
        User user = new User(uN,userID);

        mRootReference.child("Users").child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    if (user != null) {

                        //Email verification sent
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SignUpActivity.this,"Account Created, Please verify your Email ",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this,"Sign up failed, Please try again ",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //back to Login screen after successful sign up
                        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }
    public void backToLogin(View view){
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }

}
