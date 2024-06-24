package com.example.colibribanking.Model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.colibribanking.Model.Account;
import com.example.colibribanking.Model.Payee;
import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.Transaction;
import java.util.ArrayList;

public class Database {

    private SQLiteDatabase database;
    private SQLiteOpenHelper helper;
    private static final String DATABASE_NAME = "colibriDATABASE.db";
    private static final int DATABASE_VERS = 3;
    private static final String USER_TABLE = "Profiles";
    private static final String USER_ID = "_ProfileID";
    private static final String COUNTRY = "country";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final int COLUMN_PROFILE_ID = 0;
    private static final int COLUMN_FIRST_NAME = 1;
    private static final int COLUMN_LAST_NAME = 2;
    private static final int COLUMN_COUNTRY = 3;
    private static final int COLUMN_USERNAME = 4;
    private static final int COLUMN_PASSWORD = 5;
    private static final String PAYEE_TABLE = "Payees";
    private static final String PAYEE_ID = "_PayeeID";
    private static final String PAYEE_NAME = "PayeeName";
    private static final int COLUMN_PAYEE_ID = 1;
    private static final int COLUMN_PAYEE_NAME = 2;
    private static final String ACCOUNT_TABLE = "Accounts";
    private static final String ACCOUNT_NUMBER = "_AccountNo";
    private static final String ACCOUNT_NAME = "AccountName";
    private static final String ACCOUNT_BALANCE = "AccountBalance";
    private static final int COLUMN_ACCOUNT_NUMBER = 1;
    private static final int COLUMN_ACCOUNT_NAME = 2;
    private static final int COLUMN_ACCOUNT_BALANCE = 3;
    private static final String TRANSACTION_TABLE = "Transactions";
    private static final String TRANSACTION_ID = "_TransactionID";
    private static final String TIMESTAMP = "Timestamp";
    private static final String SENDING_ACCOUNT = "SendingAccount";
    private static final String DESTINATION_ACCOUNT = "DestinationAccount";
    private static final String TRANSACTION_PAYEE = "Payee";
    private static final String TRANSACTION_AMOUNT = "Amount";
    private static final String TRANS_TYPE = "Type";
    private static final int COLUMN_TRANSACTION_ID = 2;
    private static final int TIMESTAMP_COLUMN = 3;
    private static final int COLUMN_SENDING_ACCOUNT = 4;
    private static final int COLUMN_DESTINATION_ACCOUNT = 5;
    private static final int COLUMN_TRANSACTION_PAYEE = 6;
    private static final int COLUMN_TRANSACTION_AMOUNT = 7;
    private static final int COLUMN_TRANSACTION_TYPE = 8;

    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + USER_TABLE + " (" +
                    USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIRSTNAME + " TEXT, " +
                    LASTNAME + " TEXT, " +
                    COUNTRY + " TEXT, " +
                    USERNAME + " TEXT, " +
                    PASSWORD + " TEXT)";

    private static final String CREATE_PAYEES_TABLE =
            "CREATE TABLE " + PAYEE_TABLE + " (" +
                    USER_ID + " INTEGER NOT NULL, " +
                    PAYEE_ID + " TEXT NOT NULL, " +
                    PAYEE_NAME + " TEXT, " +
                    "PRIMARY KEY(" + USER_ID + "," + PAYEE_ID + "), " +
                    "FOREIGN KEY(" + USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "))";

    private static final String CREATE_ACCOUNTS_TABLE =
            "CREATE TABLE " + ACCOUNT_TABLE + " (" +
                    USER_ID + " INTEGER NOT NULL, " +
                    ACCOUNT_NUMBER + " TEXT NOT NULL, " +
                    ACCOUNT_NAME + " TEXT, " +
                    ACCOUNT_BALANCE + " REAL, " +
                    "PRIMARY KEY(" + USER_ID + "," + ACCOUNT_NUMBER + "), " +
                    "FOREIGN KEY(" + USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "))";

    private static final String CREATE_TRANSACTIONS_TABLE =
            "CREATE TABLE " + TRANSACTION_TABLE + " (" +
                    USER_ID + " INTEGER NOT NULL, " +
                    ACCOUNT_NUMBER + " TEXT NOT NULL, " +
                    TRANSACTION_ID + " TEXT NOT NULL, " +
                    TIMESTAMP + " TEXT, " +
                    SENDING_ACCOUNT + " TEXT, " +
                    DESTINATION_ACCOUNT + " TEXT, " +
                    TRANSACTION_PAYEE + " TEXT, " +
                    TRANSACTION_AMOUNT + " REAL, " +
                    TRANS_TYPE + " TEXT, " +
                    "PRIMARY KEY(" + USER_ID + "," + ACCOUNT_NUMBER + "," + TRANSACTION_ID + "), " +
                    "FOREIGN KEY(" + USER_ID + "," + ACCOUNT_NUMBER + ") REFERENCES " +
                    ACCOUNT_TABLE + "(" + USER_ID + "," + ACCOUNT_NUMBER + ")," +
                    "FOREIGN KEY(" + USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "))";

    public Database(Context context){
        helper = new DBHelper(context, DATABASE_NAME, DATABASE_VERS);
    }

    public void saveNewUser(Profile profile) {

        database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COUNTRY, profile.getCountry());
        values.put(FIRSTNAME, profile.getFirstName());
        values.put(LASTNAME, profile.getLastName());
        values.put(USERNAME, profile.getUser());
        values.put(PASSWORD, profile.getPassword());

        long id = database.insert(USER_TABLE, null, values);
        profile.setDbId(id);

        ArrayList<Payee> payeesList = new ArrayList<Payee>();
        payeesList.add(new Payee("P1","Rent"));
        payeesList.add(new Payee("P2","Food and Restaurant"));
        payeesList.add(new Payee("P3","Transport and car"));
        payeesList.add(new Payee("P4","Various purchases"));
        payeesList.add(new Payee("P5","Utility"));

        for (Payee payee : payeesList){
            savePayee(profile, payee);
        }
        database.close();
    }

    public void savePayee(Profile profile, Payee payee) {
        database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(USER_ID, profile.getDatabaseId());
        values.put(PAYEE_ID, payee.getPayeeID());
        values.put(PAYEE_NAME, payee.getPayeeName());

        long id = database.insert(PAYEE_TABLE, null, values);
        payee.setDatabaseId(id);

        database.close();
    }

    public void saveTransaction(Profile profile, String accountNo, Transaction transaction) {
        database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID, profile.getDatabaseId());
        values.put(ACCOUNT_NUMBER, accountNo);
        values.put(TRANSACTION_ID, transaction.getTransID());
        values.put(TIMESTAMP, transaction.getTimestamp());

        if (transaction.getTransType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
            values.put(SENDING_ACCOUNT, transaction.getSendingAcc());
            values.put(DESTINATION_ACCOUNT, transaction.getDestinationAcc());
            values.putNull(TRANSACTION_PAYEE);
        } else if (transaction.getTransType() == Transaction.TRANSACTION_TYPE.PAYMENT) {
            values.putNull(SENDING_ACCOUNT);
            values.putNull(DESTINATION_ACCOUNT);
            values.put(TRANSACTION_PAYEE, transaction.getPayee());
        } else if (transaction.getTransType() == Transaction.TRANSACTION_TYPE.DEPOSIT) {
            values.putNull(SENDING_ACCOUNT);
            values.putNull(DESTINATION_ACCOUNT);
            values.putNull(TRANSACTION_PAYEE);
        }

        values.put(TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(TRANS_TYPE, transaction.getTransType().toString());

        long id = database.insert(TRANSACTION_TABLE, null, values);
        transaction.setDatabasebId(id);
        database.close();
    }

    public void overAccount(Profile profile, Account account) {

        database = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(USER_ID,profile.getDatabaseId());
        cv.put(ACCOUNT_NUMBER,account.getNo());
        cv.put(ACCOUNT_NAME,account.getAccName());
        cv.put(ACCOUNT_BALANCE, account.getBalance());

        database.update(ACCOUNT_TABLE, cv, USER_ID + "=? AND " + ACCOUNT_NUMBER +"=?",
                new String[] {String.valueOf(profile.getDatabaseId()), account.getNo()});
        database.close();
    }

    public void saveAccount(Profile profile, Account account) {

        database = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(USER_ID, profile.getDatabaseId());
        cv.put(ACCOUNT_NUMBER, account.getNo());
        cv.put(ACCOUNT_NAME, account.getAccName());
        cv.put(ACCOUNT_BALANCE, account.getBalance());

        long id = database.insert(ACCOUNT_TABLE, null, cv);
        account.setDbID(id);
        database.close();
    }

    public ArrayList<Profile> getProfiles(){

        ArrayList<Profile> profiles = new ArrayList<>();
        database = helper.getReadableDatabase();
        Cursor cursor = database.query(USER_TABLE, null,null,null,null,
                null, null);
        getProfCursor(profiles, cursor);

        cursor.close();
        database.close();
        return profiles;
    }

    private void getProfCursor(ArrayList<Profile> arrayList, Cursor curs) {
        while (curs.moveToNext()){

            long id = curs.getLong(COLUMN_PROFILE_ID);
            String firstName = curs.getString(COLUMN_FIRST_NAME);
            String lastName = curs.getString(COLUMN_LAST_NAME);
            String country = curs.getString(COLUMN_COUNTRY);
            String username = curs.getString(COLUMN_USERNAME);
            String password = curs.getString(COLUMN_PASSWORD);

            arrayList.add(new Profile(firstName, lastName, country, username, password, id));
        }
    }

    public ArrayList<Payee> getPayeesUser(long id) {
        ArrayList<Payee> payeeArrayList = new ArrayList<>();
        database = helper.getReadableDatabase();
        Cursor curs = database.query(PAYEE_TABLE, null, null, null, null,
                null ,null);
        getPayees(id, payeeArrayList, curs);
        curs.close();
        database.close();

        return payeeArrayList;
    }
    private void getPayees(long profilId, ArrayList<Payee> payees, Cursor curs) {

        while (curs.moveToNext()) {

            if (profilId == curs.getLong(COLUMN_PROFILE_ID)) {
                long profId = curs.getLong(COLUMN_PROFILE_ID);
                String id = curs.getString(COLUMN_PAYEE_ID);
                String name = curs.getString(COLUMN_PAYEE_NAME);
                payees.add(new Payee(id, name, profId));
            }
        }
    }

    public ArrayList<Transaction> getTransactionsAccount(long id, String accountNumber) {
        ArrayList<Transaction> transactionArrayList = new ArrayList<>();
        database = helper.getReadableDatabase();
        Cursor curs = database.query(TRANSACTION_TABLE, null, null, null, null,
                null ,null);
        getTransCurs(id, accountNumber, transactionArrayList, curs);
        curs.close();
        database.close();
        return transactionArrayList;
    }

    private void getTransCurs(long profId, String accountNumber, ArrayList<Transaction> transactionArrayList, Cursor curs) {

        while (curs.moveToNext()) {

            if (profId == curs.getLong(COLUMN_PROFILE_ID)) {
                long id = curs.getLong(COLUMN_PROFILE_ID);
                if (accountNumber.equals(curs.getString(COLUMN_ACCOUNT_NUMBER))) {
                    String transId = curs.getString(COLUMN_TRANSACTION_ID);
                    String dateTIme = curs.getString(TIMESTAMP_COLUMN);
                    String sendingAcc = curs.getString(COLUMN_SENDING_ACCOUNT);
                    String destinationAcc = curs.getString(COLUMN_DESTINATION_ACCOUNT);
                    String payee = curs.getString(COLUMN_TRANSACTION_PAYEE);
                    double amount = curs.getDouble(COLUMN_TRANSACTION_AMOUNT);
                    Transaction.TRANSACTION_TYPE transType = Transaction.TRANSACTION_TYPE.valueOf(curs.getString(COLUMN_TRANSACTION_TYPE));

                    if (transType == Transaction.TRANSACTION_TYPE.PAYMENT) {
                        transactionArrayList.add(new Transaction(transId, dateTIme, payee, amount, id));
                    } else if (transType == Transaction.TRANSACTION_TYPE.TRANSFER) {
                        transactionArrayList.add(new Transaction(transId, dateTIme, sendingAcc, destinationAcc, amount, id));
                    } else if (transType == Transaction.TRANSACTION_TYPE.DEPOSIT) {
                        transactionArrayList.add(new Transaction(transId, dateTIme, amount, id));
                    }
                }
            }
        }
    }

    public ArrayList<Account> getAccountsUser(long id) {

        ArrayList<Account> accountArrayList = new ArrayList<>();
        database = helper.getReadableDatabase();
        Cursor curs = database.query(ACCOUNT_TABLE, null, null, null, null,
                null ,null);
        getAccsCursor(id, accountArrayList, curs);
        curs.close();
        database.close();
        return accountArrayList;
    }
    private void getAccsCursor(long profId, ArrayList<Account> accountArrayList, Cursor curs) {

        while (curs.moveToNext()) {

            if (profId == curs.getLong(COLUMN_PROFILE_ID)) {
                long id = curs.getLong(COLUMN_PROFILE_ID);
                String accNumber = curs.getString(COLUMN_ACCOUNT_NUMBER);
                String accName = curs.getString(COLUMN_ACCOUNT_NAME);
                double balance = curs.getDouble(COLUMN_ACCOUNT_BALANCE);

                accountArrayList.add(new Account(accName, accNumber, balance, id));
            }
        }
    }

    private static class DBHelper extends SQLiteOpenHelper{

        private DBHelper(Context cont, String name, int vers) {
            super(cont, name, null, vers);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_USERS_TABLE);
            database.execSQL(CREATE_PAYEES_TABLE);
            database.execSQL(CREATE_ACCOUNTS_TABLE);
            database.execSQL(CREATE_TRANSACTIONS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int old, int newV) {

            database.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + PAYEE_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
            onCreate(database);
        }
    }
}
