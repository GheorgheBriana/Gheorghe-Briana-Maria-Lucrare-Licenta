package com.example.colibribanking.Model;

public class Payee {

    private String payeeID;
    private String payeeName;
    private long databaseId;

    public Payee (String payeeID, String payeeName) {
        this.payeeID = payeeID;
        this.payeeName = payeeName;
    }

    public Payee (String payeeID, String payeeName, long databaseId) {
        this(payeeID, payeeName);
        this.databaseId = databaseId;
    }

    public String getPayeeName() {
        return payeeName;
    }
    public String getPayeeID() { return payeeID; }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public String toString() { return (payeeName + " (" + payeeID + ")"); }
}
