package com.example.colibribanking;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.db.Database;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Login extends Fragment {

    private Bundle bundle;
    private String username;
    private Button buttonLogin;
    private CheckBox Remember;
    private TextView CreateAcc;
    private String password;
    private Profile lastProfileUsed;
    private Gson gson;
    private String json;
    private EditText LoginUsername;
    String date;
    private EditText LoginPassword;
    private SharedPreferences userPreferences;

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == buttonLogin.getId()) {
                LoginAccount();
            } else if (view.getId() == CreateAcc.getId()) {
                createAccount();
            }
        }
    };

    public Login() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_login, container, false);
        LoginUsername = rootView.findViewById(R.id.Insert_Email);
        LoginPassword = rootView.findViewById(R.id.Insert_Password);
        buttonLogin = rootView.findViewById(R.id.Login_Button);
        Remember = rootView.findViewById(R.id.remember);
        CreateAcc = rootView.findViewById(R.id.registerLink);
        date = getResources().getString(R.string.date);
        getActivity().setTitle(getResources().getString(R.string.app_name));
        ((LaunchActivity) getActivity()).removeUpButton();

        setData();



        if (bundle != null) {
            LoginUsername.setText(username);
            LoginPassword.setText(password);
            Remember.setChecked(true);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = this.getArguments();
        if (bundle != null) {
            username = bundle.getString("Username", "");
            password = bundle.getString("Password", "");
        }
    }

    private void setData() {

        buttonLogin.setOnClickListener(click);
        CreateAcc.setOnClickListener(click);

        userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);

        Remember.setChecked(userPreferences.getBoolean("rememberMe", false));
        if (Remember.isChecked()) {

            gson = new Gson();
            json = userPreferences.getString("LastProfileUsed", "");
            lastProfileUsed = gson.fromJson(json, Profile.class);

            LoginUsername.setText(lastProfileUsed.getUser());
            LoginPassword.setText(lastProfileUsed.getPassword());

        }

    }

    private void createAccount() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_, new Signup())
                .addToBackStack(null)
                .commit();
    }

    private void LoginAccount() {
            try {
                Database database = new Database(getActivity().getApplicationContext());
                ArrayList<Profile> profiles = database.getProfiles();

                boolean match = false;

                if (profiles.size() > 0) {
                    for (int i = 0; i < profiles.size(); i++) {
                        if (LoginUsername.getText().toString().equals(profiles.get(i).getUser()) && LoginPassword.getText().toString().equals(profiles.get(i).getPassword())) {

                            match = true;

                            userPreferences.edit().putBoolean("rememberMe", Remember.isChecked()).apply();

                            lastProfileUsed = profiles.get(i);

                            SharedPreferences.Editor prefsEditor = userPreferences.edit();
                            gson = new Gson();
                            json = gson.toJson(lastProfileUsed);
                            prefsEditor.putString("LastProfileUsed", json).apply();

                            ((LaunchActivity) getActivity()).login();
                        }
                    }
                    if (!match) {
                        Toast.makeText(getActivity(), R.string.wrong_login, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.wrong_login, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    @Override
    public void onStop() {
        if (lastProfileUsed != null) {
            if (LoginUsername.getText().toString().equals(lastProfileUsed.getUser()) && LoginPassword.getText().toString().equals(lastProfileUsed.getPassword())) {
                userPreferences.edit().putBoolean("rememberMe", Remember.isChecked()).apply();
            } else {
                userPreferences.edit().putBoolean("rememberMe", false).apply();
            }
        }
        super.onStop();
    }

}
