package com.example.colibribanking;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.colibribanking.Model.Profile;
import com.google.gson.Gson;

import java.util.Calendar;

public class MainActivity extends Fragment {

    private ImageView logoImage;
    private TextView welcome;
    private TextView message;
    private Button addAcc;

    public MainActivity() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.activity_dashboard, container, false);

        logoImage = rootView.findViewById(R.id.imag_time_id);
        welcome = rootView.findViewById(R.id.txView_welcome);
        message = rootView.findViewById(R.id.txtView_detail);
        addAcc = rootView.findViewById(R.id.btn_add_acc);

        setData();
        return rootView;

    }

    private void setData() {

        SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        Profile userProfile = gson.fromJson(json, Profile.class);



        if (userProfile.getAccs().size() == 0) {
            message.setVisibility(View.VISIBLE);
            addAcc.setVisibility(View.VISIBLE);
            message.setText("You do not have any accounts, click below to add an account");
        } else {
            message.setVisibility(View.GONE);//TEMP to clear field
            addAcc.setVisibility(View.GONE);

        }

        StringBuilder welcomeString = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 5 && timeOfDay < 10) {
            welcomeString.append(getString(R.string.good_morning));
        } else if (timeOfDay >= 10 && timeOfDay < 19) {
            welcomeString.append(getString(R.string.good_afternoon));
        } else {
            welcomeString.append(getString(R.string.good_evening));
        }

        welcomeString.append(", ")
                .append(userProfile.getFirstName())
                .append(". Welcome to the Colibri Banking. ")
                .append(getString(R.string.happy))
                .append(" ");

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String[] days = getResources().getStringArray(R.array.days);
        String dow = "";

        addAcc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("DisplayAccountDialog", true);
                ((DrawerActivity) getActivity()).Navigation(DrawerActivity.navigationId.ACCS, bundle);
            }
        });

        switch(day) {
            case Calendar.SUNDAY:
                dow = days[0];
                break;
            case Calendar.MONDAY:
                dow = days[1];
                break;
            case Calendar.TUESDAY:
                dow = days[2];
                break;
            case Calendar.WEDNESDAY:
                dow = days[3];
                break;
            case Calendar.THURSDAY:
                dow = days[4];
                break;
            case Calendar.FRIDAY:
                dow = days[5];
                break;
            case Calendar.SATURDAY:
                dow = days[6];
                break;
            default:
                break;
        }

        welcomeString.append(dow)
                .append(".");

        welcome.setText(welcomeString.toString());
    }

}
