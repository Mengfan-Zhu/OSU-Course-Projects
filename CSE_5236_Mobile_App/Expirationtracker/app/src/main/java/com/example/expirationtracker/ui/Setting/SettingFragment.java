package com.example.expirationtracker.ui.Setting;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.expirationtracker.R;
import com.example.expirationtracker.ui.Authentication.AuthActivity;
import com.example.expirationtracker.ui.NavActivity;
import com.google.firebase.auth.FirebaseAuth;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener{

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SettingFragment.
     */
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        Button nameButton = view.findViewById(R.id.name_setting);
        Button passwordButton = view.findViewById(R.id.password_setting);
        Button logoutButton = view.findViewById(R.id.btn_logout);
        nameButton.setOnClickListener(this);
        passwordButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent newIntent = null;
        switch (view.getId()) {
            case R.id.name_setting:
                newIntent = new Intent(getActivity(), NavActivity.class);
                newIntent.putExtra("content", "NAME_SETTING");
                startActivity(newIntent);
                break;
            case R.id.password_setting:
                newIntent = new Intent(getActivity(), NavActivity.class);
                newIntent.putExtra("content", "PASSWORD_SETTING");
                startActivity(newIntent);
                break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                newIntent = new Intent(getActivity(), AuthActivity.class);
                startActivity(newIntent);
                getActivity().finish();
                break;
        }
    }
}
