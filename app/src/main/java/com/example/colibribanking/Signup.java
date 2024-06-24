package com.example.colibribanking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.db.Database;
import java.util.ArrayList;

public class Signup extends Fragment {

    private EditText firstName;
    private EditText lastName;
    private EditText country;
    private EditText username;
    private EditText password;
    private EditText passwordConfirm;
    private TextView goLogin;
    public Signup() {}

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == goLogin.getId()) {
                loginAccount();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_register, container, false);
        goLogin = rootView.findViewById(R.id.btn_go_login);
        firstName = rootView.findViewById(R.id.edt_first_name);
        lastName = rootView.findViewById(R.id.edt_last_name);
        username = rootView.findViewById(R.id.edt_username);
        password = rootView.findViewById(R.id.edt_password);
        passwordConfirm = rootView.findViewById(R.id.edt_password_confirm);
        country = rootView.findViewById(R.id.edt_country);


        Button createAccount = rootView.findViewById(R.id.create_account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        goLogin.setOnClickListener(onClickListener);

        return rootView;
    }

    private void loginAccount() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_, new Login())
                .addToBackStack(null)
                .commit();
    }

    private void createUser() {

        Database database = new Database( getActivity().getApplicationContext());
        ArrayList<Profile> profiles = database.getProfiles();
        boolean username = false;

        for (int iProfile = 0; iProfile < profiles.size(); iProfile++) {
            if (this.username.getText().toString().equals(profiles.get(iProfile).getUser())) {
                username = true;
            }
        }
        String pass = password.getText().toString();
        if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("") || country.getText().toString().equals("") ||
                this.username.getText().toString().equals("") || password.getText().toString().equals("") || passwordConfirm.getText().toString().equals("")) {
            Toast.makeText(getActivity(), R.string.blank_fields, Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 8) {
            Toast.makeText(getActivity(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
        }
        else if (!(password.getText().toString().equals(passwordConfirm.getText().toString()))) {
            Toast.makeText(getActivity(), R.string.password_incorrect, Toast.LENGTH_SHORT).show();
        }
        else if (username) {
            Toast.makeText(getActivity(), "A user with this username exists", Toast.LENGTH_SHORT).show();
        }
        else {
            Profile userProfile = new Profile(firstName.getText().toString(), lastName.getText().toString(), country.getText().toString(),
                    this.username.getText().toString(), password.getText().toString());

            database.saveNewUser(userProfile);

            Bundle bundle = new Bundle();
            bundle.putString("Username", userProfile.getUser());
            bundle.putString("Password", userProfile.getPassword());

            ((LaunchActivity) getActivity()).profileCreated(bundle);
        }
    }



}
