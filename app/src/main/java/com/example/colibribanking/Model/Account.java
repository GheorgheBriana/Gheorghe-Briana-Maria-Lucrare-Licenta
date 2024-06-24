package com.example.colibribanking.Model;

import java.util.ArrayList;
import java.util.Locale;

public class Account {
    private String accName;
    private String accNo;
    private double balance;
    private ArrayList<Transaction> transactionArrayList;
    private long dbID;

    public Account (String accName, String accNo, double balance) {
        this.accName = accName;
        this.accNo = accNo;
        this.balance = balance;
        transactionArrayList = new ArrayList<>();
    }

    public Account (String accName, String accNo, double balance, long dbID) {
        this(accName, accNo, balance);
        this.dbID = dbID;
    }

    public String getAccName() {
        return accName;
    }
    public String getNo() {
        return accNo;
    }
    public double getBalance() {
        return balance;
    }

    public void setDbID(long dbID) { this.dbID = dbID; }

    public void setBalance(double accountBalance) { this.balance = accountBalance; }

    public ArrayList<Transaction> getTransactionArrayList() {
        return transactionArrayList;
    }

    public void addPayment(String payee, double amount) {
        balance -= amount;

        int paymentCount = 0;

        for (int i = 0; i < transactionArrayList.size(); i++) {
            if (transactionArrayList.get(i).getTransType() == Transaction.TRANSACTION_TYPE.PAYMENT)  {
                paymentCount++;
            }
        }
        Transaction payment = new Transaction("T" + (transactionArrayList.size() + 1) + "-P" + (paymentCount+1), payee, amount);
        transactionArrayList.add(payment);
    }

    public void addDeposit(double amount) {
        balance += amount;

        int depositsCount = 0;

        for (int i = 0; i < transactionArrayList.size(); i++) {
            if (transactionArrayList.get(i).getTransType() == Transaction.TRANSACTION_TYPE.DEPOSIT)  {
                depositsCount++;
            }
        }
        Transaction deposit = new Transaction("T" + (transactionArrayList.size() + 1) + "-D" + (depositsCount+1), amount);
        transactionArrayList.add(deposit);
    }

    public String toString() {
        return (accName + " (" + String.format(Locale.getDefault(), "%.2f", balance) + " LEI)" );
    }

    public String toTransString() { return (accName + " (" + accNo + ")"); }

    public void setTransactionArrayList(ArrayList<Transaction> transactionArrayList) {
        this.transactionArrayList = transactionArrayList;
    }
}
