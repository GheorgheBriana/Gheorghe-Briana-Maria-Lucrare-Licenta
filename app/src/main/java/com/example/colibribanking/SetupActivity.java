package com.example.colibribanking;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.db.Database;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SetupActivity extends Fragment {
    private Button confirmButton;
    EditText editSalary, editPayday, editIncomes, editBankRates;
    private SharedPreferences prefs;
    private SharedPreferences sh;
    private Profile userProfile;
    private Gson gson;
    private String json;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_setup, container, false);

        Database database = new Database(getActivity().getApplicationContext());
        ArrayList<Profile> profiles = database.getProfiles();

        SharedPreferences userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        String json = userPreferences.getString("LastProfileUsed", "");
        gson = new Gson();
        userProfile = gson.fromJson(json, Profile.class);

        prefs = getActivity().getSharedPreferences(userProfile.getUser(), MODE_PRIVATE);

        confirmButton = rootView.findViewById(R.id.btn_setting_profile);
        editSalary = rootView.findViewById(R.id.edt_salary);
        editPayday = rootView.findViewById(R.id.edt_salary_receipt);
        editIncomes = rootView.findViewById(R.id.edt_incomes);
        editBankRates = rootView.findViewById(R.id.edt_bank_rates);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor myEdit = prefs.edit();

                myEdit.putString("salary", editSalary.getText().toString());
                myEdit.putString("payday", editPayday.getText().toString());
                myEdit.putString("incomes", editIncomes.getText().toString());
                myEdit.putString("rates", editBankRates.getText().toString());
                myEdit.apply();

                Class fragmentClass = null;
                fragmentClass = MainActivity.class;
                FragmentManager fragmentManager = getFragmentManager();

                try {
                    Fragment fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                    Toast.makeText(getActivity(), "Profile settings have been updated!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(prefs != null){

            String salary = prefs.getString("salary", editSalary.getText().toString());
            String payday = prefs.getString("payday", editPayday.getText().toString());
            String incomes = prefs.getString("incomes", editIncomes.getText().toString());
            String rates = prefs.getString("rates", editBankRates.getText().toString());

            editSalary.setText(String.valueOf(salary));
            editPayday.setText(String.valueOf(payday));
            editIncomes.setText(String.valueOf(incomes));
            editBankRates.setText(String.valueOf(rates));
        }

        try
            {
                Timer timer = new Timer();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(editPayday.getText().toString()));
                calendar.set(Calendar.HOUR_OF_DAY, 18);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date date = calendar.getTime();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //makeDeposit();
                    }
                }, date);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        return rootView;
    }
//    private void makeDeposit() {
//
//        int selectedAccountIndex = 0;
//
//        double depositAmount = 0;
//        boolean isNum = false;
//
//        try {
//            //initilization
//            int salaryInt = Integer.valueOf(editSalary.getText().toString());
//            int incomesInt = Integer.valueOf(editIncomes.getText().toString());
//            int ratesInt = Integer.valueOf(editBankRates.getText().toString());
//            double sum = 0;
//
//            if(salaryInt > 0){
//                sum+=salaryInt;
//            }
//
//            if(incomesInt > 0){
//                sum-=incomesInt;
//            }
//            if(ratesInt > 0){
//                sum-=ratesInt;
//            }
//
//            depositAmount = sum;
//            isNum = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//            com.example.colibribanking.Model.Account account = userProfile.getAccounts().get(selectedAccountIndex);
//            account.addDeposit(depositAmount);
//            SharedPreferences userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
//
//            SharedPreferences.Editor prefsEditor = userPreferences.edit();
//            gson = new Gson();
//            json = gson.toJson(userProfile);
//            prefsEditor.putString("LastProfileUsed", json).apply();
//
//            Database database = new Database(getContext());
//            database.overwriteAccount(userProfile, account);
//            database.saveTransaction(userProfile, account.getNo(),
//                    account.getTransactions().get(account.getTransactions().size()-1));
//    }

}