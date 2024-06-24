package com.example.colibribanking.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    public enum TRANSACTION_TYPE {
        PAYMENT,
        TRANSFER,
        DEPOSIT
    }

    public static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd - hh:mm a");

    private String transID;
    private String timestamp;
    private String sendingAcc;
    private String destinationAcc;
    private String payee;
    private double amount;
    private TRANSACTION_TYPE transType;
    private long databasebId;

    public Transaction (String transID, String payee, double amount) {
        this.transID = transID;
        timestamp = SIMPLE_DATE_FORMAT.format(new Date());
        this.payee = payee;
        this.amount = amount;
        transType = TRANSACTION_TYPE.PAYMENT;
    }
    public Transaction(String transID, String timestamp, double amount, long databasebId) {
        this(transID, amount);
        this.timestamp = timestamp;
        this.databasebId = databasebId;
    }
    public Transaction (String transID, String timestamp, String payee, double amount, long databasebId) {
        this(transID, payee, amount);
        this.timestamp = timestamp;
        this.databasebId = databasebId;
    }
    public Transaction(String transID, String sendingAcc, String destinationAcc, double amount) {
        this.transID = transID;
        this.timestamp = SIMPLE_DATE_FORMAT.format(new Date());
        this.sendingAcc = sendingAcc;
        this.destinationAcc = destinationAcc;
        this.amount = amount;
        transType = TRANSACTION_TYPE.TRANSFER;
    }

    public Transaction(String transID, String timestamp, String sendingAcc, String destinationAcc, double amount, long databasebId) {
        this(transID, sendingAcc, destinationAcc, amount);
        this.timestamp = timestamp;
        this.databasebId = databasebId;
    }
    public Transaction(String transID, double amount) {
        this.transID = transID;
        timestamp = SIMPLE_DATE_FORMAT.format(new Date());
        this.amount = amount;
        transType = TRANSACTION_TYPE.DEPOSIT;
    }





    public String getTransID() { return transID; }
    public String getTimestamp() { return timestamp; }
    public String getSendingAcc() {
        return sendingAcc;
    }
    public String getDestinationAcc() {
        return destinationAcc;
    }
    public String getPayee() { return payee; }
    public double getAmount() {
        return amount;
    }
    public TRANSACTION_TYPE getTransType() {
        return transType;
    }

    public void setDatabasebId(long databasebId) { this.databasebId = databasebId; }

}
