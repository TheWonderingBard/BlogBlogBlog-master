package com.example.blogblogblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.TimeZoneFormat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class add_blog extends Fragment {

    private Uri uri = null;
    private ImageView addImage;
    private EditText content, title;
    private ImageView postBlog;
    private TextView addImag;
    private FragmentContainerView fragmentContainerView;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference main_path;

    private String userId;
    private Bitmap bitmap;
    private String randomName;
    private BottomNavigationView bottomNavigationView;
    private String ref_for_img = null;

    @Override
    public  View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_blog, container, false);

        fragment = new HomeFragment();
       final ImageView Image=new ImageView(getActivity());
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_main);
        fragmentTransaction = getParentFragmentManager().beginTransaction();


        main_path=FirebaseStorage.getInstance().getReference();
        fragmentContainerView = view.findViewById(R.id.fragment_container_main);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();



        addImage = view.findViewById(R.id.addpost_img);
        content = view.findViewById(R.id.addpost_content);
        title = view.findViewById(R.id.addpost_title);
        postBlog = view.findViewById(R.id.addpost_post);
        progressBar = view.findViewById(R.id.addpost_progress_bar);
        addImag = view.findViewById(R.id.plain_text);



        View.OnClickListener uploadImg = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.INVISIBLE);
                addImag.setText("");
            }
            private void imagePicker() {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(getContext(), add_blog.this);
            }
        };




        View.OnClickListener postblog = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String contentTxt = content.getText().toString();
                if (TextUtils.isEmpty(contentTxt)) {
                    Toast.makeText(getContext(), "Write Something Interesting no ?", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    final String titleTxt = title.getText().toString();
                    if (TextUtils.isEmpty(titleTxt)) {
                        Toast.makeText(getContext(), "Add A Cool Title", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        randomName= UUID.randomUUID().toString();
                        //starting to upload the data
                        if (uri != null) {
                            final String[] bob = new String[1];
                            addImage.setDrawingCacheEnabled(true);
                            addImage.buildDrawingCache();
                            final Bitmap bitmap = ((BitmapDrawable) addImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
                            final StorageReference posts_directory = main_path.child("Posts").child(userId);
                            bitmap.compress(Bitmap.CompressFormat.JPEG,75,baos);
                            byte[] data=baos.toByteArray();

                            UploadTask EndPoint1 = (UploadTask) posts_directory
                                    .child(randomName+".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            bob[0] = taskSnapshot.getStorage().getDownloadUrl().toString();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Err:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                 }
                             });
                        Map<String, Object> postmap = new HashMap<>();

                        postmap.put("timestamp", FieldValue.serverTimestamp());
                        String refrence = bob[0];
                        postmap.put("userId", userId);
                        postmap.put("title", titleTxt);
                        postmap.put("content", contentTxt);
                        postmap.put("image_url", refrence);
                        firestore.collection("Posts").add(postmap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    bottomNavigationView.setSelectedItemId(R.id.menu_home_1);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    Toast.makeText(getContext(), "Upload Successful", Toast.LENGTH_LONG);
                                } else {
                                    Toast.makeText(getContext(), "Upload Unsuccessful: " + task.getException().getMessage(), Toast.LENGTH_LONG);

                                }
                            }
                        });
                    }
                    else {

                            Map<String, Object> postmap = new HashMap<>();
                            postmap.put("user", userId);
                            postmap.put("timestamp", FieldValue.serverTimestamp());
                            postmap.put("title", titleTxt);
                            postmap.put("content", contentTxt);
                            postmap.put("image", null);
                            firestore.collection("Posts").add(postmap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        bottomNavigationView.setSelectedItemId(R.id.menu_home_1);
                                        fragmentTransaction.replace(R.id.fragment_container_main, fragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                        Toast.makeText(getContext(), "Upload Successful", Toast.LENGTH_LONG);
                                    } else {
                                        Toast.makeText(getContext(), "Upload Unsuccessful: " + task.getException().getMessage(), Toast.LENGTH_LONG);

                                    }
                                }
                            });
                        }
                    }
                }

            }

        };


        addImage.setOnClickListener(uploadImg);
    if(firebaseAuth.getCurrentUser()!=null) {
        postBlog.setOnClickListener(postblog);
    }
    
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                addImage.setImageURI(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
