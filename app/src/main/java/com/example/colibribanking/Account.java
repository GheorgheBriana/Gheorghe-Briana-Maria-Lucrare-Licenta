package com.example.colibribanking;

import static android.content.Context.MODE_PRIVATE;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.db.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Account extends Fragment {

    private FloatingActionButton actionBtn;
    private ListView listAcc;

    private String json;
    private Gson gson;
    private Profile userProfile;
    private SharedPreferences userPreferences;
    private TextView txtTitle;
    private TextView txtDetail;
    private EditText editAccount;
    private EditText edtInitBalance;
    private Button btnCancel;
    private Button btnAddAccount;
    private SharedPreferences prefs;
    private boolean displayAccount;
    String salary, payday, incomes, rates;
    private Dialog dialog;
    private int accountIndex;
    public Account() {}
    private View.OnClickListener addAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId())
            {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnAddAccount.getId())
            {
                addAccount();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayAccount = false;
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            displayAccount = bundle.getBoolean("DisplayAccountDialog", false);
        }
        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);
        prefs = getActivity().getSharedPreferences(userProfile.getUser(), MODE_PRIVATE);

        salary = prefs.getString("salary", "");
        payday = prefs.getString("payday", "");
        incomes = prefs.getString("incomes", "");
        rates = prefs.getString("rates", "");


        if(payday.getBytes().length > 0){

            String actualDay = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
            String actualMonthandYear = new SimpleDateFormat("MMyyyy").format(Calendar.getInstance().getTime());

            Timer timer = new Timer();
            String ziPlata = payday;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if(Integer.valueOf(ziPlata) <= Integer.valueOf(actualDay) ){

                        String preferences = prefs.getString("isInsert", "");
                        if(preferences.equals(""))
                        {
                            makeDeposit();
                            SharedPreferences.Editor myEdit = prefs.edit();
                            myEdit.putString("isInsert", String.valueOf(actualMonthandYear));

                            myEdit.apply();
                        }

                        else if((Integer.valueOf(actualMonthandYear)) > Integer.valueOf(preferences))
                        {
                            makeDeposit();
                            SharedPreferences.Editor myEdit = prefs.edit();
                            myEdit.putString("isInsert", actualMonthandYear);

                            myEdit.apply();
                        }
                    }
                }
            };

            Date date = generateDate(payday );
            timer.schedule(task, date);
        }

    }
    public static Date generateDate(String payday) {
        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        }
        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        String[] arr = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            arr = dtf.format(now).split("/");
        }
        int[] currentTime = new int[arr.length];
        for (int i = 0; i < currentTime.length; i++) {
            currentTime[i] = Integer.parseInt(arr[i]);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(payday));
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
               Date date = calendar.getTime();

        return date;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_, container, false);

        actionBtn = rootView.findViewById(R.id.action_btn);

        listAcc = rootView.findViewById(R.id.lst_accounts);
        txtTitle = rootView.findViewById(R.id.txt_title_msg);
        txtDetail = rootView.findViewById(R.id.txtView_detail);

        getActivity().setTitle("Accounts");
        ((DrawerActivity) getActivity()).showDrawerBtn();

        setAccount();

        if (displayAccount) {
            accountDialog();
            displayAccount = false;
        }

        return rootView;
    }

    private void makeDeposit() {

        int selectedAccountIndex = 0;

        double depositAmount = 0;
        int salaryInt = 0;
        int incomesInt = 0;
        int ratesInt = 0;
        try {
            if(salary.length() > 0){
                salaryInt = Integer.valueOf(salary);
            }
            if(incomes.length() > 0){
                incomesInt = Integer.valueOf(incomes);
            }
            if(rates.length() > 0){
                ratesInt = Integer.valueOf(rates);
            }


            double sum = 0;

            if(salaryInt > 0){
                sum+=salaryInt;
            }

            if(incomesInt > 0){
                sum-=incomesInt;
            }
            if(ratesInt > 0){
                sum-=ratesInt;
            }

            depositAmount = sum;
        } catch (Exception e) {
            e.printStackTrace();
        }

            com.example.colibribanking.Model.Account account = userProfile.getAccs().get(selectedAccountIndex);
            account.addDeposit(depositAmount);

            SharedPreferences.Editor prefsEditor = userPreferences.edit();
            gson = new Gson();
            json = gson.toJson(userProfile);
            prefsEditor.putString("LastProfileUsed", json).apply();

            Database database = new Database(getContext());
            database.overAccount(userProfile, account);
            database.saveTransaction(userProfile, account.getNo(),
                    account.getTransactionArrayList().get(account.getTransactionArrayList().size()-1));
    }
    private void accountDialog() {

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.account_form);

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnAddAccount = dialog.findViewById(R.id.btn_add);
        editAccount = dialog.findViewById(R.id.edtxt_name);
        edtInitBalance = dialog.findViewById(R.id.edtxt_bal);


        btnCancel.setOnClickListener(addAccountClickListener);
        btnAddAccount.setOnClickListener(addAccountClickListener);

        dialog.show();

    }

    private void setAccount() {
        accountIndex = 0;

        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userProfile.getAccs().size() >= 10) {
                    Toast.makeText(getActivity(), "You have reached the maximum accounts", Toast.LENGTH_SHORT).show();
                } else {
                    accountDialog();
                }
            }
        });

        if (userProfile.getAccs().size() == 0) {
            txtTitle.setText("Add an Account with the button below");
            txtDetail.setVisibility(View.GONE);
            listAcc.setVisibility(View.GONE);
        } else {
            txtTitle.setText("Select an Account to view Transactions");
            txtDetail.setVisibility(View.VISIBLE);
            listAcc.setVisibility(View.VISIBLE);
        }

        com.example.colibribanking.Adapters.Account adapter = new com.example.colibribanking.Adapters.Account(this.getActivity(), R.layout.lst_accounts, userProfile.getAccs());
        listAcc.setAdapter(adapter);

        listAcc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                accountIndex = i;
                viewAccount();
            }
        });
    }



    private void addAccount() {

        String balanceAcc = edtInitBalance.getText().toString();

        double initDeposit = 0;
        boolean isNum = false;
        if (!(editAccount.getText().toString().equals(""))) {

            try {
                initDeposit = Double.parseDouble(edtInitBalance.getText().toString());
                isNum = true;
            } catch (Exception e) {
                if (!edtInitBalance.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    edtInitBalance.getText().clear();
                }
            }

            if (editAccount.getText().toString().length() > 10) {

                Toast.makeText(this.getActivity(), R.string.account_name_check, Toast.LENGTH_SHORT).show();
                editAccount.getText().clear();

            } else if ((isNum) || balanceAcc.equals("")) {

                boolean match = false;

                for (int i = 0; i < userProfile.getAccs().size(); i++) {
                    if (editAccount.getText().toString().equalsIgnoreCase(userProfile.getAccs().get(i).getAccName())) {
                        match = true;
                    }
                }

                if (!match) {

                    Database database = new Database(getActivity().getApplicationContext());

                    userProfile.addAcc(editAccount.getText().toString(), 0);

                    if (!balanceAcc.equals("")) {
                        if (isNum) {
                           if (initDeposit >= 0.01) {
                               userProfile.getAccs().get(userProfile.getAccs().size()-1).addDeposit(initDeposit);
                               database.saveTransaction(userProfile, userProfile.getAccs().get(userProfile.getAccs().size()-1).getNo(), userProfile.getAccs().get(userProfile.getAccs().size()-1).getTransactionArrayList().get(userProfile.getAccs().get(userProfile.getAccs().size()-1).getTransactionArrayList().size()-1));
                           }
                        }
                    }

                    database.saveAccount(userProfile, userProfile.getAccs().get(userProfile.getAccs().size()-1));

                    Toast.makeText(this.getActivity(), R.string.account_added, Toast.LENGTH_SHORT).show();

                    if (userProfile.getAccs().size() == 1) {
                        txtTitle.setText("Select an Account");
                        txtDetail.setVisibility(View.VISIBLE);
                        listAcc.setVisibility(View.VISIBLE);
                    }
                    ArrayList<com.example.colibribanking.Model.Account> acc = userProfile.getAccs();

                    com.example.colibribanking.Adapters.Account adapter = new com.example.colibribanking.Adapters.Account(getActivity(), R.layout.lst_accounts, acc);
                    listAcc.setAdapter(adapter);

                    SharedPreferences.Editor prefsEditor = userPreferences.edit();
                    String json = gson.toJson(userProfile);
                    prefsEditor.putString("LastProfileUsed", json).apply();

                    dialog.dismiss();

                } else {
                    Toast.makeText(this.getActivity(), R.string.account_name_exist, Toast.LENGTH_SHORT).show();
                    editAccount.getText().clear();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please enter name", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewAccount() {
        Transaction transactionsFragment = new Transaction();
        Bundle bundle = new Bundle();
        bundle.putInt("SelectedAccount", accountIndex);

        transactionsFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, transactionsFragment,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }

}
