package com.autorave.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatPage extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton sendButton;
    EditText sendMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);


        profile_image = findViewById(R.id.user_image);
        username = findViewById(R.id.chats_username);
        sendButton = findViewById(R.id.btn_send_text);
        sendMsg = findViewById(R.id.text_send_message);


        //what class should have the intent?
        Intent intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = sendMsg.getText().toString();
                if(!msg.equals(" ")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                } else {
                    Toast.makeText(chatPage.this, "Type in a message", Toast.LENGTH_SHORT).show();
                }
                sendMsg.setText("");
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);


    }

    private void sendMessage(String sender,String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //collecting data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("Conversations").push().setValue(hashMap);


    }
}
