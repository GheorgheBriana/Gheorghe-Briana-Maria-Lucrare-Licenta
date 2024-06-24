package com.example.colibribanking.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.colibribanking.R;
import java.util.ArrayList;

public class Transaction extends ArrayAdapter<com.example.colibribanking.Model.Transaction> {

    private Context cont;
    private int res;

    public Transaction(Context cont, int res, ArrayList<com.example.colibribanking.Model.Transaction> transactions) {
        super(cont, res, transactions);

        this.cont = cont;
        this.res = res;
    }

    @Override
    @NonNull
    public View getView (int pos, View view, @NonNull ViewGroup parent) {

        if (view == null) {

            LayoutInflater layoutInflater = ((Activity) cont).getLayoutInflater();
            view = layoutInflater.inflate(res, parent, false);
        }

        com.example.colibribanking.Model.Transaction trans = getItem(pos);

        ImageView imageView = view.findViewById(R.id.image_trans);
        TextView transDate = view.findViewById(R.id.trans_date);
        TextView info = view.findViewById(R.id.info);
        TextView transTitle = view.findViewById(R.id.transaction_type);

        info.setVisibility(View.VISIBLE);
        TextView amount = view.findViewById(R.id.amount);

        transTitle.setText(trans.getTransType().toString() + " - " + trans.getTransID());
        transDate.setText(trans.getTimestamp());
        amount.setText("Amount: " + String.format("%.2f",trans.getAmount()) + " LEI");

        if (trans.getTransType() == com.example.colibribanking.Model.Transaction.TRANSACTION_TYPE.PAYMENT) {
            imageView.setImageResource(R.drawable.payment_icon);
            info.setText("To Payee: " + trans.getPayee());
            amount.setTextColor(Color.RED);
        }
        else if (trans.getTransType() == com.example.colibribanking.Model.Transaction.TRANSACTION_TYPE.DEPOSIT) {
            imageView.setImageResource(R.drawable.deposit_icon);
            info.setVisibility(View.GONE);
            amount.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
        }
        else if (trans.getTransType() == com.example.colibribanking.Model.Transaction.TRANSACTION_TYPE.TRANSFER) {
            imageView.setImageResource(R.drawable.transfer_icon);
            info.setText("From: " + trans.getSendingAcc() + " - " + "To: " + trans.getDestinationAcc());
            amount.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_light));
        }


        return view;
    }
}
