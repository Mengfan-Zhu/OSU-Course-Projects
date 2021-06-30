package com.example.expirationtracker.ui.Authentication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import com.example.expirationtracker.R;
import com.example.expirationtracker.model.User;
import com.example.expirationtracker.ui.NavActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */

public class RegisterFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private static String TAG = "Register";
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtConfirm;
    private DatabaseReference mDatabase;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(getString(R.string.register_frg), getString(R.string.on_create_view));

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Activity activity = getActivity();
        if (activity != null){
            mEtUsername = v.findViewById(R.id.et_email);
            mEtPassword = v.findViewById(R.id.et_password);
            mEtConfirm = v.findViewById(R.id.et_repassword);
            Button btnAdd = v.findViewById(R.id.btn_register);
            btnAdd.setOnClickListener(this);
        }

        return v;

    }
    @Override
    public void onClick(View view){
        if (view.getId() == R.id.btn_register) {
            createAccount();
        }
    }

    private void createAccount() {
        FragmentActivity activity = getActivity();
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String confirm = mEtConfirm.getText().toString();
        if (activity != null) {
            if (password.equals(confirm) && !username.equals("") && !password.equals("")) {
                Log.wtf(TAG, "createAccount:" + username);
                final User newUser = new User(username, username);
                mAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        FragmentActivity activity = getActivity();
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(activity.getApplicationContext(), "Register success",
                                        Toast.LENGTH_SHORT).show();
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("users").child(Objects.requireNonNull(mAuth.getUid())).setValue(newUser);
                                Intent intent = new Intent(getActivity(), NavActivity.class);
                                intent.putExtra("content", "HOME");
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(activity.getApplicationContext(), "Register fail",
                                        Toast.LENGTH_SHORT).show();
                                 mEtPassword.setText("");
                                 mEtConfirm.setText("");
                            }
                        }
                    });
            } else if ((username.equals("")) || (password.equals("")) || (confirm.equals(""))) {
                Toast.makeText(activity.getApplicationContext(), "Username or Password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity.getApplicationContext(), "Password not match. Please enter again", Toast.LENGTH_SHORT).show();
                mEtUsername.setText("");
                mEtPassword.setText("");
                mEtConfirm.setText("");
            }
        }
    }


}

