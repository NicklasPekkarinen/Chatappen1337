package com.autorave.chatapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autorave.chatapp.Adapters.ContactsListAdapter;
import com.autorave.chatapp.NewGroupFragment;
import com.autorave.chatapp.R;
import com.autorave.chatapp.Templates.User;
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
public class NewMessageFragment extends Fragment {

    private TextView cancelBtn;
    private RelativeLayout newGroupBtn;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ContactsListAdapter contactsListAdapter;
    private List<User> mContacts;


    public NewMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_message, container, false);

        cancelBtn = view.findViewById(R.id.cancel_new_message);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, new ChatsFragment()).remove(NewMessageFragment.this).commit();
            }
        });

        searchView = view.findViewById(R.id.search_new_message);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactsListAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsListAdapter.filter(newText);
                return false;
            }
        });

        newGroupBtn = view.findViewById(R.id.new_group_clickable_area);
        newGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.drawer_layout, new NewGroupFragment(), null).commit();
            }
        });

        recyclerView = view.findViewById(R.id.contacts_new_message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mContacts = new ArrayList<>();
        getUsers();

        return view;
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

                contactsListAdapter = new ContactsListAdapter(mContacts, getContext());
                recyclerView.setAdapter(contactsListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
