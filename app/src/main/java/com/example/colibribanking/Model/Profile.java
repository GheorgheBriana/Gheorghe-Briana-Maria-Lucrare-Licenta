package com.example.colibribanking.Model;

import java.util.ArrayList;

public class Profile {

    private String firstName;
    private String lastName;
    private String country;
    private String username;
    private String password;
    private ArrayList<Account> accs;
    private ArrayList<Payee> payees;
    private long dbId;

    public Profile (String firstName, String lastName, String country, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.username = username;
        this.password = password;
        accs = new ArrayList<>();
        payees = new ArrayList<>();
    }

    public Profile (String firstName, String lastName, String country, String username, String password, long dbId) {
        this(firstName, lastName, country, username, password);
        this.dbId = dbId;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUser() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Account> getAccs() { return accs; }

    public ArrayList<Payee> getPayees() { return payees; }
    public String getCountry() {
        return country;
    }

    public long getDatabaseId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }


    public void addAcc(String accountName, double accountBalance) {
        String accNo = "A" + (accs.size() + 1);
        Account account = new Account(accountName, accNo, accountBalance);
        accs.add(account);
    }
    public void setAccs(ArrayList<Account> accs) {
        this.accs = accs;
    }

    public void addTransfer(Account sending, Account receiving, double amount) {

        sending.setBalance(sending.getBalance() - amount);
        receiving.setBalance(receiving.getBalance() + amount);

        int sendingAcc = 0;
        int receivingAcc = 0;
        for (int i = 0; i < sending.getTransactionArrayList().size(); i ++) {
            if (sending.getTransactionArrayList().get(i).getTransType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                sendingAcc++;
            }
        }
        for (int i = 0; i < receiving.getTransactionArrayList().size(); i++) {
            if (receiving.getTransactionArrayList().get(i).getTransType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                receivingAcc++;
            }
        }

        sending.getTransactionArrayList().add(new Transaction("T" + (sending.getTransactionArrayList().size() + 1) + "-T" + (sendingAcc+1), sending.toTransString(), receiving.toTransString(), amount));
        receiving.getTransactionArrayList().add(new Transaction("T" + (receiving.getTransactionArrayList().size() + 1) + "-T" + (receivingAcc+1), sending.toTransString(), receiving.toTransString(), amount));
    }
    public void setPayees(ArrayList<Payee> payees) {
        this.payees = payees;
    }
    public void addPayee(String payeeName) {
        String payeeID = "P" + (payees.size() + 1);
        Payee payee = new Payee(payeeID, payeeName, 0);
        payees.add(payee);
    }


}
