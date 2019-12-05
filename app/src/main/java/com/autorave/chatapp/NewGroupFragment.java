package com.autorave.chatapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.autorave.chatapp.Activitys.GroupChatPage;
import com.autorave.chatapp.Fragments.NewMessageFragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class NewGroupFragment extends Fragment {

    private TextView backBtn;
    private TextView nextBtn;
    private RecyclerView recyclerView;
    private GroupContactsAdapter groupContactsAdapter;
    private List<User> mContacts;
    private List<User> currentSelectedContacts;
    private User user;
    private String groupContactNames;

    public NewGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_group, container, false);

        backBtn = view.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.drawer_layout, new NewMessageFragment()).commit();
            }
        });

        recyclerView = view.findViewById(R.id.contacts_new_group);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mContacts = new ArrayList<>();
        currentSelectedContacts = new ArrayList<>();
        getUsers();

        nextBtn = view.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                final HashMap<String, List<User>> hashMap = new HashMap<>();
                final HashMap<String, Object> hashMap1 = new HashMap<>();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                final String groupKey = databaseReference.push().getKey();

                getData();
            }
        });

        return view;
    }

    private void getData() {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fbUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                useData(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void useData(User user) {

        currentSelectedContacts.add(user);
        groupContactNames = currentSelectedContacts.get(0).getUsername() + ", ";

        for (int i = 1; i < currentSelectedContacts.size(); i++) {
            groupContactNames += (currentSelectedContacts.get(i).getUsername() + ", ");
        }

        groupContactNames = groupContactNames.replaceAll(", $", "");

        HashMap<String, List<User>> hashMap = new HashMap<>();
        HashMap<String, Object> hashMap1 = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        String groupKey = databaseReference.push().getKey();

        hashMap.put("members", currentSelectedContacts);
        hashMap1.put("id", groupKey);
        hashMap1.put("name", groupContactNames);

        databaseReference.child(groupKey).setValue(hashMap);
        databaseReference.child(groupKey).updateChildren(hashMap1);

        for (int i = 0; i < currentSelectedContacts.size(); i++) {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(currentSelectedContacts.get(i).getId()).child("groups");
            databaseReference1.push().setValue(groupKey);
        }

        Intent intent = new Intent(getContext(), GroupChatPage.class);
        intent.putExtra("groupId", groupKey);
        getContext().startActivity(intent);
    }

    private void getUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mContacts.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    if (user != null && firebaseUser != null) {
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mContacts.add(user);
                        }
                    }
                }

                groupContactsAdapter = new GroupContactsAdapter(mContacts, getContext(), new GroupContactsAdapter.OnItemCheckListener() {
                    @Override
                    public void onItemCheck(User user) {
                        currentSelectedContacts.add(user);
                        if (currentSelectedContacts.size() > 1) {
                            nextBtn.setTextColor(getResources().getColor(R.color.black));
                            nextBtn.setClickable(true);
                        } else {
                            nextBtn.setTextColor(getResources().getColor(R.color.darkGrey));
                            nextBtn.setClickable(false);
                        }
                    }

                    @Override
                    public void onItemUncheck(User user) {
                        currentSelectedContacts.remove(user);
                        if (currentSelectedContacts.size() > 1) {
                            nextBtn.setTextColor(getResources().getColor(R.color.black));
                            nextBtn.setClickable(true);
                        } else {
                            nextBtn.setTextColor(getResources().getColor(R.color.darkGrey));
                            nextBtn.setClickable(false);
                        }
                    }
                });
                recyclerView.setAdapter(groupContactsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
