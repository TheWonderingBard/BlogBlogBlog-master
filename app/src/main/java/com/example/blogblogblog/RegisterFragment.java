package com.example.blogblogblog;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RegisterFragment extends Fragment {
    private EditText username, password, email, pass_conf;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private ImageView register_btn;
    private FirebaseFirestore firestore;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);
        register_btn = view.findViewById(R.id.register_button);
        username = view.findViewById(R.id.register_username);
        password = view.findViewById(R.id.register_password);
        progressBar = view.findViewById(R.id.register_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        fragmentTransaction = getParentFragmentManager().beginTransaction();
        firestore = FirebaseFirestore.getInstance();
        fragment = new LoginFragment();
        pass_conf = view.findViewById(R.id.register_password_confirm);
        email = view.findViewById(R.id.register_email);
        register_btn.setOnClickListener(adduser);
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    private View.OnClickListener adduser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String password1 = password.getText().toString(),
                    password2 = pass_conf.getText().toString(),
                    email_send = email.getText().toString(),
                    user = username.getText().toString();
            //if passwords don't match
            if (!password1.equals(password2)) {
                Toast.makeText(getContext(), "Passwords do not match please try again", Toast.LENGTH_SHORT).show();

            } else {
                //if any field is empty
                if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2) || TextUtils.isEmpty(email_send) || TextUtils.isEmpty(user)) {
                    Toast.makeText(getContext(), "One Of The fields is empty, please fill them all to proceed", Toast.LENGTH_SHORT).show();
                } else {
                    //making the bar visible
                    progressBar.setVisibility(View.VISIBLE);
                    //adding the user to the db
                    mAuth.createUserWithEmailAndPassword(email_send, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userID = mAuth.getCurrentUser().getUid();
                                Map<String, String> userMap = new HashMap<>();
                                userMap.put("name", user);
                                userMap.put("image", null);
                                firestore.collection("Users").document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getContext(), "File Store Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        }
                                    }
                                });
                            } else {
                                String errMsg = Objects.requireNonNull(task.getException()).getMessage();
                                Toast toast = Toast.makeText(getContext(), "Error:" + errMsg, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }
    };
}

