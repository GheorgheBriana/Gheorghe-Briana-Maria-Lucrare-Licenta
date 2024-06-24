package com.example.colibribanking;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.colibribanking.Model.Account;
import com.example.colibribanking.Model.Profile;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Transaction extends Fragment {

    public enum TypeFilter {
        ALL_TRANSACTIONS(0),
        PAYMENTS(1),
        TRANSFERS(2),
        DEPOSITS(3);

        private final int filterID;
        TypeFilter(int filterID) {
            this.filterID = filterID;
        }

        public TypeFilter getTransFilter(int index) {
            for (TypeFilter filter : TypeFilter.values()) {
                if (filter.filterID == index) {
                    return filter;
                }
            }
            return null;
        }
    }

    public enum DateFilter {
        OLDEST_NEWEST(0),
        NEWEST_OLDEST(1);

        private final int dateFilterID;
        DateFilter(int dateFilterID) {
            this.dateFilterID = dateFilterID;
        }

        public DateFilter getDateFilter(int index) {
            for (DateFilter filter : DateFilter.values()) {
                if (filter.dateFilterID == index) {
                    return filter;
                }
            }
            return null;
        }

    }

    class Comparator implements java.util.Comparator<com.example.colibribanking.Model.Transaction> {
        public int compare(com.example.colibribanking.Model.Transaction transOne, com.example.colibribanking.Model.Transaction transTwo) {

            Date dateOne = null;
            Date dateTwo = null;

            try {
                dateOne = com.example.colibribanking.Model.Transaction.SIMPLE_DATE_FORMAT.parse(transOne.getTimestamp());
                dateTwo = com.example.colibribanking.Model.Transaction.SIMPLE_DATE_FORMAT.parse(transTwo.getTimestamp());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dateOne.compareTo(dateTwo) > 0) {
                return (1);
            } else if (dateOne.compareTo(dateTwo) < 0) {
                return (-1);
            } else if (dateOne.compareTo(dateTwo) == 0) {
                return (1);
            }
            return (1);
        }
    }

    private TextView name;
    private TextView balance;
    private TextView message;
    private TextView transMessage;
    private TextView payMessage;
    private TextView depMessage;
    private Spinner accs;
    private Spinner typeFilter;
    private Spinner DateFilterList;
    private TypeFilter transFilter;
    private DateFilter dateFilter;

    Spinner.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId() == accs.getId()) {
                indexSelected = i;
                name.setText("Account: " + userProfile.getAccs().get(indexSelected).toTransString());
                balance.setText("Balance: " + String.format(Locale.getDefault(), "%.2f"+ " LEI",userProfile.getAccs().get(indexSelected).getBalance()));
            }
            else if (adapterView.getId() == typeFilter.getId()) {
                transFilter = transFilter.getTransFilter(i);
            }
            else if (adapterView.getId() == DateFilterList.getId()) {
                dateFilter = dateFilter.getDateFilter(i);
            }

            setAdapter(indexSelected, transFilter, dateFilter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private ListView lsttransactions;

    private Profile userProfile;

    private int indexSelected;

    public Transaction() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        getActivity().setTitle("Transactions");
        indexSelected = bundle.getInt("SelectedAccount", 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_transaction, container, false);

        name = rootView.findViewById(R.id.account);
        balance = rootView.findViewById(R.id.account_balance);

        message = rootView.findViewById(R.id.no_trans);
        payMessage = rootView.findViewById(R.id.no_payments);
        transMessage = rootView.findViewById(R.id.no_transfers);
        depMessage = rootView.findViewById(R.id.no_dep);

        accs = rootView.findViewById(R.id._accounts);
        typeFilter = rootView.findViewById(R.id.type_filter);
        DateFilterList = rootView.findViewById(R.id.date_filter);

        lsttransactions = rootView.findViewById(R.id.list_transactions);

        ((DrawerActivity) getActivity()).showUpBtn();

        setData();
        return rootView;
    }

    private void setData() {

        SharedPreferences preferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        transFilter = TypeFilter.ALL_TRANSACTIONS;
        dateFilter = DateFilter.OLDEST_NEWEST;

        setAdapter(indexSelected, transFilter, dateFilter);

        setSpn();
        accs.setSelection(indexSelected);

        name.setText("Account: " + userProfile.getAccs().get(indexSelected).toTransString());
        balance.setText("Balance: " + String.format(Locale.getDefault(), "%.2f "+ " LEI",userProfile.getAccs().get(indexSelected).getBalance()));
    }
    private void setAdapter(int selectedAccountIndex, TypeFilter transFilter, DateFilter dateFilter) {
        ArrayList<com.example.colibribanking.Model.Transaction> transactions = userProfile.getAccs().get(selectedAccountIndex).getTransactionArrayList();

        depMessage.setVisibility(GONE);
        transMessage.setVisibility(GONE);
        payMessage.setVisibility(GONE);

        if (transactions.size() > 0) {

            message.setVisibility(GONE);
            lsttransactions.setVisibility(VISIBLE);

            if (dateFilter == DateFilter.OLDEST_NEWEST) {
                Collections.sort(transactions, new Comparator());
            } else if (dateFilter == DateFilter.NEWEST_OLDEST) {
                Collections.sort(transactions, Collections.reverseOrder(new Comparator()));
            }

            if (transFilter == TypeFilter.ALL_TRANSACTIONS) {
                com.example.colibribanking.Adapters.Transaction transaction = new com.example.colibribanking.Adapters.Transaction(getActivity(), R.layout.lst_transactions, transactions);
                lsttransactions.setAdapter(transaction);
            }
            else if (transFilter == TypeFilter.PAYMENTS) {
                showPayments(transactions);
            }
            else if (transFilter == TypeFilter.TRANSFERS) {
                showTransfers(transactions);
            }
            else if (transFilter == TypeFilter.DEPOSITS) {
                showDeposits(transactions);
            }

        } else {
            message.setVisibility(VISIBLE);
            lsttransactions.setVisibility(GONE);
        }

    }
    private void setSpn() {

        ArrayAdapter<Account> accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, userProfile.getAccs());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accs.setAdapter(accountAdapter);

        ArrayAdapter<String> AdapterType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.trans_filters));
        AdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeFilter.setAdapter(AdapterType);

        ArrayAdapter<String> dateFilter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.date_filters));
        dateFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DateFilterList.setAdapter(dateFilter);

        accs.setOnItemSelectedListener(itemSelectedListener);
        typeFilter.setOnItemSelectedListener(itemSelectedListener);
        DateFilterList.setOnItemSelectedListener(itemSelectedListener);

    }



    private void showPayments(ArrayList<com.example.colibribanking.Model.Transaction> transactions) {
        ArrayList<com.example.colibribanking.Model.Transaction> payments = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransType() == com.example.colibribanking.Model.Transaction.TRANSACTION_TYPE.PAYMENT) {
                payments.add(transactions.get(i));
            }
        }
        if (payments.size() == 0) {
            payMessage.setVisibility(VISIBLE);
            lsttransactions.setVisibility(GONE);
        } else {
            lsttransactions.setVisibility(VISIBLE);
            com.example.colibribanking.Adapters.Transaction transaction = new com.example.colibribanking.Adapters.Transaction(getActivity(), R.layout.lst_transactions, payments);
            lsttransactions.setAdapter(transaction);
        }
    }
    private void showDeposits(ArrayList<com.example.colibribanking.Model.Transaction> transactions) {
        ArrayList<com.example.colibribanking.Model.Transaction> deposits = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransType() == com.example.colibribanking.Model.Transaction.TRANSACTION_TYPE.DEPOSIT) {
                deposits.add(transactions.get(i));
            }
        }
        if (deposits.size() == 0) {
            depMessage.setVisibility(VISIBLE);
            lsttransactions.setVisibility(GONE);
        } else {
            lsttransactions.setVisibility(VISIBLE);
            com.example.colibribanking.Adapters.Transaction transaction = new com.example.colibribanking.Adapters.Transaction(getActivity(), R.layout.lst_transactions, deposits);
            lsttransactions.setAdapter(transaction);
        }
    }
    private void showTransfers(ArrayList<com.example.colibribanking.Model.Transaction> transactions) {
        ArrayList<com.example.colibribanking.Model.Transaction> transfers = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransType() == com.example.colibribanking.Model.Transaction.TRANSACTION_TYPE.TRANSFER) {
                transfers.add(transactions.get(i));
            }
        }
        if (transfers.size() == 0) {
            transMessage.setVisibility(VISIBLE);
            lsttransactions.setVisibility(GONE);
        } else {
            lsttransactions.setVisibility(VISIBLE);
            com.example.colibribanking.Adapters.Transaction transaction = new com.example.colibribanking.Adapters.Transaction(getActivity(), R.layout.lst_transactions, transfers);
            lsttransactions.setAdapter(transaction);
        }
    }



}
