package com.autorave.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
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

    }
    public void signUp(View view) {
        mFirebaseAuth = FirebaseAuth.getInstance();
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
        else if(!(email.isEmpty() && pwd.isEmpty())){
            mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        insertValuesToFirebase();
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();

                    }
                    else{
                        Toast.makeText(SignUpActivity.this,"SignUp failed",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        else {
            Toast.makeText(SignUpActivity.this,"Error occurred",Toast.LENGTH_SHORT).show();
        }

    }

    private void insertValuesToFirebase(){
        String uN = userName.getText().toString().trim();
        User userName = new User(uN);
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        mRootReference.child("Users").child(user.getUid()).setValue(userName);


        Toast.makeText(SignUpActivity.this,"Account Created, Welcome :)",Toast.LENGTH_SHORT).show();
    }

    public void backToLogin(View view){
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);

    }

}
