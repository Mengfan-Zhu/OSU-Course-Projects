package com.example.expirationtracker.ui.Authentication;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import com.example.expirationtracker.R;
import com.example.expirationtracker.ui.NavActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private FirebaseAuth mAuth;
    private static String TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, getString(R.string.on_create_view));
        View view;
        Activity activity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        if (activity != null) {
            int rotation = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                view = inflater.inflate(R.layout.fragment_login, container, false);
            } else {
                view = inflater.inflate(R.layout.fragment_login, container, false);
            }
        }
        else {
            view = inflater.inflate(R.layout.fragment_login, container, false);
        }

        mUsernameEditText = view.findViewById(R.id.et_email);
        mPasswordEditText = view.findViewById(R.id.et_password);

        Button loginButton = view.findViewById(R.id.btn_login);
        if (loginButton != null) {
            loginButton.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            loginIn();
        }
    }
    private void loginIn() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (!username.equals("") && !password.equals("")) {
            // sign in with email address and password
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        FragmentActivity activity = getActivity();

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(activity.getApplicationContext(), "Authentication success.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), NavActivity.class);
                                intent.putExtra("content", "HOME");
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(activity.getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }else{
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Username or Password cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}