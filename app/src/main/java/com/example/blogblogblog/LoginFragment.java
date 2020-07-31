package com.example.blogblogblog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class LoginFragment extends Fragment {
    private EditText log_pass, log_email;
    private FirebaseAuth mAuth;
    private ProgressBar loginbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        log_email = view.findViewById(R.id.login_email);
        log_pass = view.findViewById(R.id.login_pass);
        loginbar = view.findViewById(R.id.login_progress);
        ImageView loginbtn = view.findViewById(R.id.login_btn);
        loginbtn.setOnClickListener(login);
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    private View.OnClickListener login = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //checking variables
            String password = log_pass.getText().toString();
            String email = log_email.getText().toString();
            //if email is empty
            if (TextUtils.isEmpty(email)) {
                Toast toast = Toast.makeText(getActivity(), "Please Enter an email", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //if password is empty
                if (TextUtils.isEmpty(password)) {
                    Toast toast = Toast.makeText(getActivity(), "Please Enter A password", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //making the bar visible
                    loginbar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                loginbar.setVisibility(View.INVISIBLE);
                                String err = task.getException().getMessage();
                              Toast toast=  Toast.makeText(getActivity(), "Error: " + err, Toast.LENGTH_LONG);
                              toast.show();
                            } else {
                                Intent gotoafterlogin = new Intent(getActivity(), SecondPage.class);
                               Toast toast= Toast.makeText(getActivity(), "Logged In Successfully", Toast.LENGTH_SHORT);
                               toast.show();
                                startActivity(gotoafterlogin);
                            }
                            loginbar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }

        }
    };
}
