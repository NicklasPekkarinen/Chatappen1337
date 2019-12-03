package com.autorave.chatapp.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autorave.chatapp.Adapters.ChatsListAdapter;
import com.autorave.chatapp.Notifications.Token;
import com.autorave.chatapp.R;
import com.autorave.chatapp.Templates.ChatInfo;
import com.autorave.chatapp.Templates.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChatsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ChatsListAdapter chatsListAdapter;
    private List<User> mContacts;
    private List<String> usersList;

    private FirebaseUser fbUser;
    private DatabaseReference dbReference;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chats_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Chats");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatInfo chat = snapshot.getValue(ChatInfo.class);

                    if (chat != null && fbUser != null) {
                        if (chat.getSender().equals(fbUser.getUid())) {

                            usersList.add(chat.getReceiver());

                        } else if (chat.getReceiver().equals(fbUser.getUid())) {

                            usersList.add(chat.getSender());
                        }
                    } else {
                        return;
                    }
                }
                getChats();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uodateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }
    private void uodateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fbUser.getUid()).setValue(token1);

    }

    private void getChats() {

        mContacts = new ArrayList<>();
        dbReference = FirebaseDatabase.getInstance().getReference("Users");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mContacts.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (String id : usersList) {

                        if (user.getId().equals(id)) {

                            if (!mContacts.contains(user)) {
                                mContacts.add(user);
                            }
                        }
                    }
                }
                chatsListAdapter = new ChatsListAdapter(mContacts, getContext());
                recyclerView.setAdapter(chatsListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
