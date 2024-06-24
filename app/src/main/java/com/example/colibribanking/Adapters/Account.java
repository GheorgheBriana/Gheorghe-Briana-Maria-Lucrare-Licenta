package com.example.colibribanking.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.colibribanking.R;

import java.util.ArrayList;


public class Account extends ArrayAdapter<com.example.colibribanking.Model.Account> {

    private Context cont;
    private int res;

    public Account(Context cont, int res, ArrayList<com.example.colibribanking.Model.Account> accs) {
        super(cont, res, accs);

        this.cont = cont;

        this.res = res;
    }

    @Override
    @NonNull
    public View getView (int pos, View View, @NonNull ViewGroup parent) {

        if (View == null) {

            LayoutInflater layoutInflater = ((Activity) cont).getLayoutInflater();
            View = layoutInflater.inflate(res, parent, false);
        }

        com.example.colibribanking.Model.Account acc = getItem(pos);

        TextView accName = View.findViewById(R.id.account);

        accName.setText(acc.getAccName());

        TextView acctNr = View.findViewById(R.id.acc_nr);

        acctNr.setText(cont.getString(R.string.account_no) + " " + acc.getNo());

        TextView balance = View.findViewById(R.id.balance);

        balance.setText("Account balance: " + String.format("%.2f",acc.getBalance())+ " LEI");
        return View;
    }
}
