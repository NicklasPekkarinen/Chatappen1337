package com.autorave.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    Button btnLogin;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailID = findViewById(R.id.edit_email);
        passWord = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.button_login);

    }


    public void loginProses(View view) {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    Toast.makeText(LoginActivity.this,"You are loged in",Toast.LENGTH_SHORT).show();
                    //Intent to home activity

                }
                else {
                    Toast.makeText(LoginActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                }

            }
        };

        String email = emailID.getText().toString();
        String pwd = passWord.getText().toString();
        if(email.isEmpty()){
            emailID.setError("Please enter an email");
            emailID.requestFocus();
        }
        else if(pwd.isEmpty()){
            passWord.setError("Please enter your password");
            passWord.requestFocus();
        }
        else if(email.isEmpty() && pwd.isEmpty()){
            Toast.makeText(this,"please enter your information",Toast.LENGTH_SHORT).show();
        }
        else if(!(email.isEmpty() && pwd.isEmpty())){
            mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"Login error please login again",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this,"You made it to your conversations",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        else {
            Toast.makeText(LoginActivity.this,"Error occurred",Toast.LENGTH_SHORT).show();

        }

    }
    public void toSignUp(View view){
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
