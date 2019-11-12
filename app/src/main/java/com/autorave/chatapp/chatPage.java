package com.autorave.chatapp;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatPage extends AppCompatActivity {


    private TextView username;
    private CircleImageView image;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private ImageButton sendBtn;
    private EditText messageSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);

        Intent intent = getIntent();
        final String userId = intent.getStringExtra("userId");

        username = findViewById(R.id.chats_username);
        image = findViewById(R.id.user_chats_image);
        sendBtn = findViewById(R.id.btn_send_text);
        messageSend = findViewById(R.id.text_send_message);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageSend.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), userId, msg);
                } else {
                    Toast.makeText(chatPage.this, "Type in a message", Toast.LENGTH_LONG).show();
                }

                messageSend.setText("");
            }
        });


    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);



    }
}
