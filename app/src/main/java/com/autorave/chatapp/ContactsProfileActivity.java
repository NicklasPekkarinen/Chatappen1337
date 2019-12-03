package com.autorave.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsProfileActivity extends AppCompatActivity implements NicknameDialog.NicknameDialogListener {

    private CircleImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private Button changeNicknameBtn;
    private Intent intent;
    private FirebaseUser fbUser;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_profile);

        profileImage = findViewById(R.id.contacts_profile_image);
        userName = findViewById(R.id.contacts_profile_username);
        userEmail = findViewById(R.id.contacts_profile_email);
        changeNicknameBtn = findViewById(R.id.change_nickname_btn);
        intent = getIntent();

        final String userId = intent.getStringExtra("userId");

        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.getUsername());
                userEmail.setText(user.getEmail());
                if(user.getImageURL().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(ContactsProfileActivity.this).load(user.getImageURL()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeNicknameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    private void openDialog() {
        NicknameDialog nicknameDialog = new NicknameDialog();
        nicknameDialog.show(getSupportFragmentManager(), "Edit nickname");
    }

    @Override
    public void applyText(String nickname) {
        // Det här är det sparade nicknamet
    }
}
