package com.example.blogblogblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SecondPage extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FragmentContainerView fragmentContainerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("message");
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        firestore = FirebaseFirestore.getInstance();
        fragmentContainerView = findViewById(R.id.fragment_container_main);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Blog BLog Blog!");


        if (mAuth.getCurrentUser() != null) {
            bottomNavigationView = findViewById(R.id.bottom_nav_main);
            userId = mAuth.getCurrentUser().getUid(); bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_logout: {
                            logout();
                            return true;
                        }
                        case R.id.menu_add_blog: {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                                    new add_blog()).commit();
                            return true;
                        }
                        case R.id.menu_settings: {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                                    new SettingsFragment()).commit();
                            return true;
                        }
                        case R.id.menu_home_1: {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                                    new HomeFragment()).commit();
                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });
            firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Error Getting Data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        if (task.getResult().exists()) {
                            if (task.getResult().getString("image") != null) {
                                bottomNavigationView.setSelectedItemId(R.id.menu_home_1);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                                        new HomeFragment()).commit();
                            } else {
                                bottomNavigationView.setSelectedItemId(R.id.menu_settings);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                                        new SettingsFragment()).commit();
                            }

                        }
                    }
                }
            });




        }
    }



    private void logout () {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.toolbar_main, menu);
            return true;
        }
}
