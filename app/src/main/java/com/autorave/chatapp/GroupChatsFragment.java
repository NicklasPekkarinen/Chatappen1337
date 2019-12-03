package com.autorave.chatapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroupChatsListAdapter groupChatsListAdapter;
    private List<Group> mGroups;
    private List<String> groupsList;

    private FirebaseUser fbUser;
    private DatabaseReference dbReference;


    public GroupChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chats, container, false);

        recyclerView = view.findViewById(R.id.group_chats_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupsList = new ArrayList<>();

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(fbUser.getUid()).child("Groups");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    groupsList.add(snapshot.getValue(String.class));
                }
                getChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void getChats() {

        mGroups = new ArrayList<>();

        for (int i = 0; i < groupsList.size(); i++) {
            dbReference = FirebaseDatabase.getInstance().getReference("GroupInfo");
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mGroups.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Group group = snapshot.getValue(Group.class);

                        for (String id : groupsList) {

                            if (group.getId().equals(id)) {

                                if (!mGroups.contains(group)) {
                                    mGroups.add(group);
                                }
                            }
                        }
                    }
                    groupChatsListAdapter = new GroupChatsListAdapter(mGroups, getContext());
                    recyclerView.setAdapter(groupChatsListAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
