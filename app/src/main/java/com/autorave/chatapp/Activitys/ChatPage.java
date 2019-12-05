package com.autorave.chatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autorave.chatapp.Adapters.ChatAdapter;
import com.autorave.chatapp.Notifications.APIService;
import com.autorave.chatapp.Notifications.Client;
import com.autorave.chatapp.Notifications.Data;
import com.autorave.chatapp.Notifications.MyResponse;
import com.autorave.chatapp.Notifications.Sender;
import com.autorave.chatapp.Notifications.Token;
import com.autorave.chatapp.R;
import com.autorave.chatapp.SQLite.ContactsProfileActivity;
import com.autorave.chatapp.SQLite.NameChangeDBHelper;
import com.autorave.chatapp.Templates.ChatInfo;
import com.autorave.chatapp.Templates.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    ImageView image_send;
    ImageView Image_receive;
    StorageReference storageReference;

    CircleImageView profile_image;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Button camera_button;
    List<ChatInfo> chat;
    RecyclerView recyclerView;
    ImageView imageView;
    String userId;
    private int REQUEST_PICTURE_CAPTURE = 1;
    Bitmap sentPhotoBitmap;
    private String mUri;

    ImageButton btnSend;
    EditText messageSend;
    private String filepath;

    ChatAdapter chatAdapter;

    ValueEventListener seenListener;

    Intent intent;

    String userid;
    private DatabaseReference databaseReference;
    Boolean notify = false;
    APIService apiService;

   private int STORAGE_PERMISSION_CODE = 1;
   private int CAMERA_PERMISSION_CODE = 10;

    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);

        storageReference = FirebaseStorage.getInstance().getReference("camerapics");

        nameChangeDBHelper = new NameChangeDBHelper(this);


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
        userId = intent.getStringExtra("userId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        camera_button = findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChatPage.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ChatPage.this,Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){

                    startCamera();
                }
                else{
                    requestStoragePermission();
                }
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

                for (int i = 0; i < SQLData.size(); i++) {
                    if (SQLData != null && SQLData.get(i).equals(user.getId())) {
                        username.setText(SQLData.get(i-1));
                    }
                }
                if (username.getText().length() <= 0) {
                    Log.d("Autorave", user.getUsername());
                    username.setText(user.getUsername());
                }
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessage(firebaseUser.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userId);

    }
    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
               new AlertDialog.Builder(this)
                       .setTitle("Storage permission needed")
                       .setMessage("You need allow this to save your pictures to your phone")
                       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ChatPage.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                           }
                       })
                       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                           }
                       })
                       .create().show();
               if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                  new AlertDialog.Builder(this)
                  .setTitle("Camera permission needed")
                  .setMessage("You need to allow this to save your pictures to your phone")
                  .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          ActivityCompat.requestPermissions(ChatPage.this,new String[] {Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                      }
                  })
                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.dismiss();
                      }
                  })
                  .create().show();
               }
        }else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
             startCamera();
        }else{
             Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {

                sentPhotoBitmap = (Bitmap) data.getExtras().get("data");
                uploadImage(sentPhotoBitmap);
        }
    }

    private void startCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = ChatPage.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(Bitmap bitmap) {


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".JPEG");

        /*uploadTask = fileReference.putBytes(byteArrayOutputStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ChatPage.this, "Photo uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatPage.this, "Photo failed to upload.", Toast.LENGTH_SHORT).show();
            }
        });*/

        uploadTask = fileReference.putBytes(byteArrayOutputStream.toByteArray());
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>> () {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri picuterUri = task.getResult();
                    mUri = picuterUri.toString();
                    sendMessage(firebaseUser.getUid(), userId, mUri);
                }
            }
        });
    }

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

    private void sendMessage(String sender, final String receiver, final String message){

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
