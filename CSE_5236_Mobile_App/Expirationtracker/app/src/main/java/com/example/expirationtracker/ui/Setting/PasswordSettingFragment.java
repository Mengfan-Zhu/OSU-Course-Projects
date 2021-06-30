package com.example.expirationtracker.ui.Setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.expirationtracker.R;
import com.example.expirationtracker.model.User;
import com.example.expirationtracker.ui.NavActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordSettingFragment extends Fragment implements View.OnClickListener{
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private View mView;
    private User mUser;
    public PasswordSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PasswordSettingFragment.
     */
    public static PasswordSettingFragment newInstance() {
        return new PasswordSettingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_password_setting, container, false);
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(mAuth.getUid()));
        Button saveButton = mView.findViewById(R.id.btn_password_setting_save);
        saveButton.setOnClickListener(this);
        return mView;
    }
    @Override
    public void onStart(){
        super.onStart();
        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get user info
                mUser = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUserReference.addListenerForSingleValueEvent(mUserListener);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_password_setting_save) {
            String oldPassword = ((EditText) mView.findViewById(R.id.old_password)).getText().toString();
            String newPassword = ((EditText) mView.findViewById(R.id.new_password)).getText().toString();
            String confirmPassword = ((EditText) mView.findViewById(R.id.confirm)).getText().toString();
            if (oldPassword.length() == 0) {
                Toast.makeText(mActivity.getApplicationContext(), "Password cannot be empty. ",
                        Toast.LENGTH_SHORT).show();
            } else {
                AuthCredential credential = EmailAuthProvider.getCredential(mUser.getUserName(), oldPassword);
                Objects.requireNonNull(mAuth.getCurrentUser()).reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (confirmPassword.length() > 0 && newPassword.length() > 0 && confirmPassword.equals(newPassword) && !oldPassword.equals(newPassword)) {
                                mAuth.getCurrentUser().updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mActivity.getApplicationContext(), "Password update successfully. ",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent newIntent = new Intent(mActivity, NavActivity.class);
                                                    newIntent.putExtra("content", "SETTING");
                                                    startActivity(newIntent);
                                                } else {
                                                    Toast.makeText(mActivity.getApplicationContext(), "Password update failed.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else if (newPassword.length() == 0 || confirmPassword.length() == 0) {
                                Toast.makeText(mActivity.getApplicationContext(), "Password cannot be empty. ",
                                        Toast.LENGTH_SHORT).show();
                            } else if (!newPassword.equals(confirmPassword)) {
                                Toast.makeText(mActivity.getApplicationContext(), "New Password does not match.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mActivity.getApplicationContext(), "Password should not be same.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mActivity.getApplicationContext(), "Password update failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
            ((EditText) mView.findViewById(R.id.old_password)).setText("");
            ((EditText) mView.findViewById(R.id.new_password)).setText("");
            ((EditText) mView.findViewById(R.id.confirm)).setText("");
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mUserListener != null) {
            mUserReference.removeEventListener(mUserListener);
            mUserListener = null;
        }
        Runtime.getRuntime().gc();
    }
}
