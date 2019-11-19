package com.autorave.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements ChatsFragment.OnFragmentInteractionListener {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNav;
    private CircleImageView profileImage;
    private TextView userName;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ImageView logoutBtn;
    private androidx.appcompat.widget.SearchView searchView;
    private ContactsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        frameLayout = findViewById(R.id.frame_layout);
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.username_toolbar);
        logoutBtn = findViewById(R.id.logout_btn);
        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Autorave", "text change");
                fragment.getFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Autorave", "text change");
                fragment.getFilter(newText);
                return false;
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.getUsername());
                profileImage.setImageResource(R.mipmap.ic_launcher);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchView.setIconified(false);
                fragment = new ContactsFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, null).commit();
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ChatsFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {
                case R.id.nav_chats:
                    selectedFragment = new ChatsFragment();
                    break;
                case R.id.nav_groups:
                    selectedFragment = new GroupChatsFragment();
                    break;
                case R.id.nav_contacts:
                    selectedFragment = new ContactsFragment();
                    fragment = (ContactsFragment)selectedFragment;
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment, null).commit();

            return true;
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
