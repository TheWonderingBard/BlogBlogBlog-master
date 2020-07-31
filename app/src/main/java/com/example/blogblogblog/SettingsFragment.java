package com.example.blogblogblog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private CircleImageView profile_pic;
    private Uri uri = null;
    private EditText username;
    private FirebaseAuth mAuth;
    private Button ChangeImageAndUser, ChangeDisplayName, ChangeImage;
    private StorageReference userImagesProfiles;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private FragmentContainerView fragmentContainerView;
    private FirebaseFirestore firestore;
    private Fragment fragment;
    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        profile_pic = (CircleImageView) view.findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView=getActivity().findViewById(R.id.bottom_nav_main);
        final String userid = mAuth.getCurrentUser().getUid();
        ChangeDisplayName = view.findViewById(R.id.Change_username);
        fragment = new HomeFragment();
        View.OnClickListener changeIMG;
        StorageReference main_path = FirebaseStorage.getInstance().getReference();
        final StorageReference profile_pics_folder = main_path.child("Profile_Images");
        fragmentContainerView = view.findViewById(R.id.fragment_container_main);
        fragmentTransaction = getParentFragmentManager().beginTransaction();
        userImagesProfiles = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        ChangeImageAndUser = view.findViewById(R.id.change_Both);
        ChangeImage = view.findViewById(R.id.change_profile_pic);
        progressBar = view.findViewById(R.id.settings_progress_bar);
        username = view.findViewById(R.id.settings_input_username);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        @GlideModule
        class MyAppGlideModule extends AppGlideModule {

            @Override
            public void registerComponents(Context context, Glide glide, Registry registry) {
                // Register FirebaseImageLoader to handle StorageReference
                registry.append(StorageReference.class, InputStream.class,
                        new FirebaseImageLoader.Factory());
            }
        }

        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image=task.getResult().getString("image");
                        username.setHint(name);
                        profile_pics_folder.child(userid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                             Picasso.get().load(uri).into(profile_pic);
                             progressBar.setVisibility(View.INVISIBLE);
                           }
                       });
                    }
                }
            }
        });

        progressBar.setVisibility(View.INVISIBLE);


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "Premission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        imagePicker();
                    }
                } else
                    imagePicker();
            }

            private void imagePicker() {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), SettingsFragment.this);
            }
        });



        ChangeImageAndUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = username.getText().toString();
                if (uri == null || TextUtils.isEmpty(user_name)) {
                    Toast.makeText(getContext(), "Make Sure You Picked and Image And chose a name.", Toast.LENGTH_LONG).show();
                } else {
                    String randomText= UUID.randomUUID().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    final String userID = firebaseAuth.getCurrentUser().getUid();
                    final StorageReference image_path = userImagesProfiles.child(userID+".png");
                    image_path.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Map<String, String> userMap = new HashMap<>();
                                    userMap.put("name", user_name);
                                    userMap.put("image", String.valueOf(taskSnapshot.getStorage().getDownloadUrl()));
                                    firestore.collection("Users").document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getContext(), "File Store Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                bottomNavigationView.setSelectedItemId(R.id.menu_home_1);
                                                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                                                fragmentTransaction.addToBackStack(null);
                                                fragmentTransaction.commit();
                                            }
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Image Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        changeIMG = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri == null) {
                    Toast.makeText(getContext(), "Please Pick A Picture", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    final String userID = firebaseAuth.getCurrentUser().getUid();
                    final StorageReference image_path = userImagesProfiles.child(userID + ".jpg");
                    image_path.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    Map<String, String> userMap = new HashMap<>();
                                    userMap.put("image", String.valueOf(taskSnapshot.getStorage().getDownloadUrl()));
                                    firestore.collection("Users").document(userID).set(userMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "File Store Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                bottomNavigationView.setSelectedItemId(R.id.menu_home_1);
                                                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                                                fragmentTransaction.addToBackStack(null);
                                                fragmentTransaction.commit();
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        //if there something went wrong with the image mapping
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                ;
            }
        };
        ChangeImage.setOnClickListener(changeIMG);
        View.OnClickListener changeDisName;
        changeDisName = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = username.getText().toString();

                if (TextUtils.isEmpty(user_name)) {
                    Toast.makeText(getContext(), "Please Enter A New Username", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    final String userID = firebaseAuth.getCurrentUser().getUid();
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("name", user_name);
                    firestore.collection("Users").document(userID).set(userMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "File Store Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Mapping Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        ChangeDisplayName.setOnClickListener(changeDisName);
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                profile_pic.setImageURI(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}