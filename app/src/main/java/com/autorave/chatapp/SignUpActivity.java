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


public class SignUpActivity extends AppCompatActivity {
    EditText emailID, passWord;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailID = findViewById(R.id.edit_email);
        passWord = findViewById(R.id.edit_password);

    }
    public void signUp(View view) {
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
            mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this,"SignUp unsuccessful",Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(SignUpActivity.this,"Account created",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                }
            });
        }
        else {
            Toast.makeText(SignUpActivity.this,"Error occurred",Toast.LENGTH_SHORT).show();

        }


    }
    public void backToLogin(View view){
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);

    }

}
