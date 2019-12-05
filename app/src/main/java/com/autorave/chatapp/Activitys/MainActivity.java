package com.autorave.chatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.autorave.chatapp.Fragments.ChatsFragment;
import com.autorave.chatapp.Fragments.ContactsFragment;
import com.autorave.chatapp.GroupChatsFragment;
import com.autorave.chatapp.Fragments.NewMessageFragment;
import com.autorave.chatapp.R;
import com.autorave.chatapp.Templates.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements ChatsFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNav;
    private CircleImageView profileImage;
    private TextView userName;
    private CircleImageView drawerProfileImage;
    private TextView drawerUserName;
    private TextView drawerEmail;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private androidx.appcompat.widget.SearchView searchView;
    private ContactsFragment fragment;
    private FloatingActionButton fab;
    private NavigationView navigationView;

    StorageReference storageReference;
    private final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.floatingActionButton2);
        bottomNav = findViewById(R.id.bottom_nav);
        frameLayout = findViewById(R.id.frame_layout);
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.username_toolbar);

        navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        drawerProfileImage = headerView.findViewById(R.id.drawer_profile_picture);
        drawerUserName = headerView.findViewById(R.id.drawer_username);
        drawerEmail = headerView.findViewById(R.id.drawer_user_email);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

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
                drawerUserName.setText(user.getUsername());
                drawerEmail.setText(user.getEmail());

                if (user.getImageURL().equals("default")) {

                    profileImage.setImageResource(R.mipmap.ic_launcher);
                    drawerProfileImage.setImageResource(R.mipmap.ic_launcher);

                } else {

                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileImage);
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(drawerProfileImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new NewMessageFragment(), null).commit();
            }
        });

        drawerProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
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

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." +
                    getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", mUri);
                        databaseReference.updateChildren(hashMap);
                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(MainActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.drawer_sign_out:
                FirebaseAuth.getInstance().signOut();
                status("offline");
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }
        return true;
    }

    private void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        databaseReference.updateChildren(hashMap);
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
    }
}
