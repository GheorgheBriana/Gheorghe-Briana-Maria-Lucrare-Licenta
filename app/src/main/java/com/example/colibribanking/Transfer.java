package com.example.colibribanking;

import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.colibribanking.Model.Account;
import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.db.Database;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Locale;

public class Transfer extends Fragment {

    private Spinner selectAccount;
    private EditText sum;
    private Spinner selectAccountNo2;
    private Button confirm;

    ArrayList<Account> accs;
    ArrayAdapter<Account> accAdap;

    SharedPreferences userpref;
    Gson gson;
    String json;
    Profile userProfile;

    public Transfer() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.activity_transfer, container, false);

        selectAccount = rootView.findViewById(R.id.select_acc);
        sum = rootView.findViewById(R.id.sum);
        selectAccountNo2 = rootView.findViewById(R.id.select_acc2);
        confirm = rootView.findViewById(R.id.confirm);

        setData();

        return rootView;
    }

    private void setData() {

        userpref = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);

        gson = new Gson();
        json = userpref.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransfer();
            }
        });

        Adapters();
    }


    private void addTransfer() {

        int recAccIndex = selectAccountNo2.getSelectedItemPosition();
        boolean isNum = false;
        double amount = 0;

        try {
            amount = Double.parseDouble(sum.getText().toString());
            isNum = true;
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_SHORT).show();
        }
        if (isNum) {
            if (selectAccount.getSelectedItemPosition() == recAccIndex) {
                Toast.makeText(getActivity(), "You cannot make a transfer to the same account", Toast.LENGTH_SHORT).show();
            }
            else if(amount < 0.01) {
                Toast.makeText(getActivity(), "The minimum amount is 0.01 LEI", Toast.LENGTH_SHORT).show();

            } else if (amount > userProfile.getAccs().get(selectAccount.getSelectedItemPosition()).getBalance()) {

                Account acc = (Account) selectAccount.getSelectedItem();
                Toast.makeText(getActivity(), "The account," + " " + acc.toString() + " " + "does not have sufficient funds", Toast.LENGTH_LONG).show();
            } else {

                int AccIndex = selectAccount.getSelectedItemPosition();
                
                Account sending = (Account) selectAccount.getItemAtPosition(AccIndex);
                Account receiving = (Account) selectAccountNo2.getItemAtPosition(recAccIndex);

                userProfile.addTransfer(sending, receiving, amount);

                selectAccount.setAdapter(accAdap);
                selectAccountNo2.setAdapter(accAdap);

                selectAccount.setSelection(AccIndex);
                selectAccountNo2.setSelection(recAccIndex);

                Database database = new Database(getActivity().getApplicationContext());

                database.overAccount(userProfile, sending);
                database.overAccount(userProfile, receiving);

                database.saveTransaction(userProfile, sending.getNo(),
                        sending.getTransactionArrayList().get(sending.getTransactionArrayList().size()-1));
                database.saveTransaction(userProfile, receiving.getNo(),
                        receiving.getTransactionArrayList().get(receiving.getTransactionArrayList().size()-1));


                SharedPreferences.Editor prefsEditor = userpref.edit();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Transfer of " + String.format(Locale.getDefault(), "%.2f"+ " LEI",amount) + " successfully made", Toast.LENGTH_SHORT).show();
                sum.getText().clear();
            }
        }
    }


    private void Adapters() {
        accs = userProfile.getAccs();
        accAdap = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accs);
        accAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectAccount.setAdapter(accAdap);
        selectAccountNo2.setAdapter(accAdap);
        selectAccountNo2.setSelection(1);
    }

}
