package com.autorave.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autorave.chatapp.Notifications.APIService;
import com.autorave.chatapp.Notifications.Client;
import com.autorave.chatapp.Notifications.Data;
import com.autorave.chatapp.Notifications.MyResponse;
import com.autorave.chatapp.Notifications.Sender;
import com.autorave.chatapp.Notifications.Token;
import com.autorave.chatapp.SQLite.ContactsProfileActivity;
import com.autorave.chatapp.SQLite.NameChangeDBHelper;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.sql.SQLData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatPage extends AppCompatActivity {

    NameChangeDBHelper nameChangeDBHelper;

    CircleImageView profile_image;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Button camera_button;
    List<ChatInfo> chat;
    RecyclerView recyclerView;
    ImageView imageView;
    private String filepath; //För att hålla koll på sökväen
    private int REQUEST_PICTURE_CAPTURE = 1;

    ImageButton btnSend;
    EditText messageSend;

    ChatAdapter chatAdapter;

    ValueEventListener seenListener;

    Intent intent;

    String userid;
    Boolean notify = false;
    APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);

        nameChangeDBHelper = new NameChangeDBHelper(this);
        //final ArrayList<String> SQLData = (ArrayList)nameChangeDBHelper.getDataSQL();

        recyclerView = findViewById(R.id.id_text);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.chats_username);
        btnSend = findViewById(R.id.send_button);
        messageSend = findViewById(R.id.message_send);

        intent = getIntent();
        final String userId = intent.getStringExtra("userId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        camera_button = findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = messageSend.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,msg);
                } else {
                    Toast.makeText(ChatPage.this, "Type in a message", Toast.LENGTH_SHORT).show();
                }

                messageSend.setText("");

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatPage.this, ContactsProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ArrayList<String> SQLData = (ArrayList)nameChangeDBHelper.getDataSQL();
                if (SQLData != null && SQLData.get(1).equals(user.getId())) {
                    username.setText(SQLData.get(0));
                } else {
                    username.setText(user.getUsername());
                }
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(ChatPage.this).load(user.getImageURL()).into(profile_image);
                }

                readMessage(firebaseUser.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userId);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kolla att result code överenstämmer med den vi skickade

        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK){

                // Få Imageview dimensioner

                int imageViewWidth = imageView.getWidth();
                int imageViewHeight = imageView.getHeight();
                String log = "Imageview Width: " + imageViewWidth + " ImageView height: " + imageViewHeight;

                // Skapa Bitmap options så att de endast kan vara så stora som imageview

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filepath, options);

                // Beräkna bildens skala

                int scaleFactor = Math.min(options.outWidth / imageViewWidth, options.outHeight / imageViewHeight);
                String log3 = "options out. Width:: " + options.outWidth + "  height: " + options.outHeight;
                Log.d("TAG", log3);

                // Reset options to a new object and apply the scale

                options = new BitmapFactory.Options();
                options.inSampleSize = scaleFactor;

                // Decode the image

                Bitmap image = BitmapFactory.decodeFile(filepath, options);
                String log2 = "Image Width: " + image.getWidth() + " Image height: " + image.getHeight();
                Log.d("TAG", log2);

                // Set image to imageView

                imageView.setImageBitmap(image);
            }
        }
    }

    private void startCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;

            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo cannot be generated, pls try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            }
        }
    }
    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddmmyyyyhhmmss", Locale.getDefault()).format(new Date());
        String pictureFile = "pic_" + timeStamp;

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(dir, "child");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {

            }
        }
        File image = File.createTempFile( pictureFile, ".jpg", storageDir);

        filepath = image.getAbsolutePath();
        return image;
    } //

    private void seenMessage(final String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatInfo chatInfo = snapshot.getValue(ChatInfo.class);
                    if(chatInfo.getReceiver().equals(firebaseUser.getUid()) && chatInfo.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify){
                    Log.d("MarcusTag","if notify");
                sendNotification(receiver,user.getUsername(),msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void sendNotification(String reciver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(reciver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",
                            userid);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(ChatPage.this,"Faild",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(final String myId, final String userId, final String imageUrl){
        chat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatInfo chatInfo = snapshot.getValue(ChatInfo.class);
                    if(chatInfo.getReceiver().equals(myId) && chatInfo.getSender().equals(userId) ||
                            chatInfo.getReceiver().equals(userId) && chatInfo.getSender().equals(myId)){
                                          chat.add(chatInfo);
                    }

                    chatAdapter = new ChatAdapter(ChatPage.this,chat, imageUrl );
                    recyclerView.setAdapter(chatAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        reference.removeEventListener(seenListener);
    }
}
