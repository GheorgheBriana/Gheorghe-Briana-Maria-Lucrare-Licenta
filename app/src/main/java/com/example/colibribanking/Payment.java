package com.example.colibribanking;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.colibribanking.Model.Account;
import com.example.colibribanking.Model.Payee;
import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.db.Database;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Locale;

public class Payment extends Fragment {

    private Spinner spinnerAccs;
    private TextView textMessage;
    private Spinner spinnerPayee;
    private EditText edtPayment;
    private Button btnPayment;
    private Dialog dialog;
    private ArrayList<Account> accs;
    private ArrayAdapter<Account> acc;
    private String json;
    private Profile userProfile;
    private Gson gson;
    private SharedPreferences preferences;
    private ArrayList<Payee> payeesList;
    private ArrayAdapter<Payee> adapterList;
    private EditText payeeName;
    private Button btnCancel;
    private Button addPayee;

    private View.OnClickListener addPayeeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Payee Creation Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == addPayee.getId()) {
                addPayee();
            }
        }
    };



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnPayment.getId()) {
                addPayment();
            }
        }
    };


    public Payment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_payment, container, false);
        btnPayment = rootView.findViewById(R.id.btn_make_payment);
        spinnerAccs = rootView.findViewById(R.id.spn_select_acc);
        spinnerPayee = rootView.findViewById(R.id.spn_select_payee);
        edtPayment = rootView.findViewById(R.id.edt_payment_amount);
        textMessage = rootView.findViewById(R.id.txt_no_payees);



        setData();

        return rootView;
    }

    private void setData() {

        preferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = preferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        btnPayment.setOnClickListener(onClickListener);


        accs = userProfile.getAccs();
        acc = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accs);
        acc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAccs.setAdapter(acc);

        payeesList = userProfile.getPayees();

        adapterList = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, payeesList);
        adapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPayee.setAdapter(adapterList);

        checkPayeeInformation();
    }




    private void addPayment() {

        boolean isNum = false;
        double paymentVAlue = 0;

        try {
            paymentVAlue = Double.parseDouble(edtPayment.getText().toString());
            if (Double.parseDouble(edtPayment.getText().toString()) >= 0.01) {
                isNum = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNum) {

            int itemPosition = spinnerAccs.getSelectedItemPosition();

            if (paymentVAlue > userProfile.getAccs().get(itemPosition).getBalance()) {
                Toast.makeText(getActivity(), "Insufficient Funds", Toast.LENGTH_SHORT).show();
            } else {

                int index = spinnerPayee.getSelectedItemPosition();

                String selectedPayee = userProfile.getPayees().get(index).toString();

                userProfile.getAccs().get(itemPosition).addPayment(selectedPayee, paymentVAlue);

                accs = userProfile.getAccs();
                spinnerAccs.setAdapter(acc);
                spinnerAccs.setSelection(itemPosition);

                Database database = new Database(getActivity().getApplicationContext());
                database.saveTransaction(userProfile, userProfile.getAccs().get(itemPosition).getNo(), userProfile.getAccs().get(itemPosition).getTransactionArrayList().get(userProfile.getAccs().get(itemPosition).getTransactionArrayList().size()-1));
                database.overAccount(userProfile, userProfile.getAccs().get(itemPosition));

                SharedPreferences.Editor prefs = preferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefs.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Payment of " + String.format(Locale.getDefault(), "%.2f", paymentVAlue) + " LEI"+ " successfully made", Toast.LENGTH_SHORT).show();
                edtPayment.getText().clear();
            }
        } else {
            Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            edtPayment.getText().clear();
        }
    }

    private void addPayee() {
        if (!(payeeName.getText().toString().equals(""))) {

            boolean match = false;
            for (int i = 0; i < userProfile.getPayees().size(); i++) {
                if (payeeName.getText().toString().equalsIgnoreCase(userProfile.getPayees().get(i).getPayeeName())) {
                    match = true;
                }
            }

            if (!match) {
                userProfile.addPayee(payeeName.getText().toString());

                payeeName.setText("");

                textMessage.setVisibility(GONE);
                spinnerPayee.setVisibility(VISIBLE);
                edtPayment.setVisibility(VISIBLE);
                btnPayment.setVisibility(VISIBLE);

                payeesList = userProfile.getPayees();
                spinnerPayee.setAdapter(adapterList);
                spinnerPayee.setSelection(userProfile.getPayees().size()-1);

                Database database = new Database(getActivity().getApplicationContext());
                database.savePayee(userProfile, userProfile.getPayees().get(userProfile.getPayees().size()-1));

                SharedPreferences.Editor prefsEditor = preferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Payee Added Successfully", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            } else {
                Toast.makeText(getActivity(), "A Payee with that name already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPayeeInformation() {

        if (userProfile.getPayees().size() == 0)
        {
            textMessage.setVisibility(VISIBLE);
            btnPayment.setVisibility(GONE);
            spinnerPayee.setVisibility(GONE);
            edtPayment.setVisibility(GONE);
        }
        else
        {
            textMessage.setVisibility(GONE);
            btnPayment.setVisibility(VISIBLE);
            spinnerPayee.setVisibility(VISIBLE);
            edtPayment.setVisibility(VISIBLE);
        }
    }

}
