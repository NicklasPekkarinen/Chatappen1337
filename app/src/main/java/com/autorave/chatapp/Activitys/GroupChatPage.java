package com.autorave.chatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.autorave.chatapp.Adapters.GroupChatAdapter;
import com.autorave.chatapp.Notifications.APIService;
import com.autorave.chatapp.Notifications.Client;
import com.autorave.chatapp.R;
import com.autorave.chatapp.Templates.GroupChatInfo;
import com.autorave.chatapp.Templates.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatPage extends AppCompatActivity {

    List<String> listUserIds;
    List<User> userList;
    String users;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton btnSend;
    EditText messageSend;

    GroupChatAdapter groupChatAdapter;
    List<GroupChatInfo> chat;

    RecyclerView recyclerView;

    Intent intent;

    Boolean notify = false;
    APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_page);

        recyclerView = findViewById(R.id.groupchat_id_text);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        profile_image = findViewById(R.id.groupchat_profile_image);
        username = findViewById(R.id.groupchat_username);
        btnSend = findViewById(R.id.groupchat_send_button);
        messageSend = findViewById(R.id.groupchat_message_send);

        listUserIds = new ArrayList<>();
        userList = new ArrayList<>();
        users = "";

        intent = getIntent();
        final String groupId = intent.getStringExtra("groupId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                userList = group.getMembers();
                for (int i = 0; i < userList.size(); i++) {
                    listUserIds.add(userList.get(i).getId());
                }

                username.setText(group.getName());

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notify = true;
                        String msg = messageSend.getText().toString();
                        if(!msg.equals("")){
                            listUserIds.remove(firebaseUser.getUid());
                            sendMessage(firebaseUser.getUid(), listUserIds, msg);
                        } else {
                            Toast.makeText(GroupChatPage.this, "Type in a message", Toast.LENGTH_SHORT).show();
                        }

                        messageSend.setText("");
                    }
                });
                readMessage(firebaseUser.getUid(), listUserIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final List<String> receivers, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receivers", receivers);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("GroupChats").push().setValue(hashMap);
    }

    private void readMessage(final String myId, final List<String> userIds){
        chat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupChats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChatInfo groupChatInfo = snapshot.getValue(GroupChatInfo.class);
                    for (int i = 0; i < groupChatInfo.getReceivers().size(); i++) {
                        if (groupChatInfo.getReceivers().get(i).equals(myId) && groupChatInfo.getSender().equals(userIds.get(i))) {

                            chat.add(groupChatInfo);
                        }
                    }
                    if (groupChatInfo.getSender().equals(myId)) {
                        chat.add(groupChatInfo);
                    }
                    groupChatAdapter = new GroupChatAdapter(GroupChatPage.this, chat);
                    recyclerView.setAdapter(groupChatAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
