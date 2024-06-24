package com.example.colibribanking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.db.Database;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import java.util.Locale;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public enum navigationId {
        DASHBOARD,
        ACCS
    }

    private DrawerLayout drawer;
    private NavigationView nav;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private SharedPreferences userPref;
    private Gson gson;
    private String json;

    private Profile userProfile;

    private Dialog dialog;
    private Spinner listAccounts;
    private ArrayAdapter<com.example.colibribanking.Model.Account> accountAd;
    private EditText editDeposit;
    private Button cancel;
    private Button deposit;

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("DisplayAccountDialog", true);
                Navigation(navigationId.ACCS, bundle);
            }
        }
    };

    private View.OnClickListener depositClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == cancel.getId()) {
                dialog.dismiss();

                Navigation(navigationId.ACCS, null);

                Toast.makeText(DrawerActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == deposit.getId())
            {
                makeDeposit();
            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);

        drawer = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_open, R.string.navigation_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav = findViewById(R.id.nav_);
        nav.setNavigationItemSelectedListener(this);

        userPref = this.getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPref.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        loadDataBase();

        SharedPreferences.Editor prefsEditor = userPref.edit();
        json = gson.toJson(userProfile);
        prefsEditor.putString("LastProfileUsed", json).apply();

        setup();

        Navigation(navigationId.DASHBOARD, null);
    }

    public void Navigation(navigationId id, Bundle bundle) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == navigationId.DASHBOARD) {
            fragmentTransaction.replace(R.id.content, new MainActivity()).commit();
            nav.setCheckedItem(R.id.dashboard);

            setTitle("Dashboard");

        } else if (id == navigationId.ACCS) {
            Account account = new Account();
            if (bundle != null) {
                account.setArguments(bundle);
            }
            fragmentTransaction.replace(R.id.content, account).commit();
            nav.setCheckedItem(R.id.accounts);

            setTitle("Accounts");
        }

        drawer.closeDrawers();
    }

    private void setup() {

        View view = nav.getHeaderView(0);

        TextView txtName = view.findViewById(R.id.text_name);
        TextView textUsername = view.findViewById(R.id.text_user);

        String name = userProfile.getFirstName() + " " + userProfile.getLastName();
        txtName.setText(name);

        textUsername.setText(userProfile.getUser());
    }

    private void loadDataBase() {
        Database database = new Database(getApplicationContext());

        userProfile.setPayees(database.getPayeesUser(userProfile.getDatabaseId()));
        userProfile.setAccs(database.getAccountsUser(userProfile.getDatabaseId()));

        for (int iAcc = 0; iAcc < userProfile.getAccs().size(); iAcc++) {
            userProfile.getAccs().get(iAcc).setTransactionArrayList(database.getTransactionsAccount(userProfile.getDatabaseId(), userProfile.getAccs().get(iAcc).getNo()));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }



    public void showUpBtn() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void showDrawerBtn() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_open, R.string.navigation_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.syncState();
    }

    private void displayAccount(String option) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(String.format("%s Error", option))
                .setMessage(String.format("Add another account.", option, option.toLowerCase()))
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("Add Account", dialogClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void makeDeposit() {

        int accountIndex = listAccounts.getSelectedItemPosition();

        double deposit = 0;
        boolean isNum = false;

        try {
            deposit = Double.parseDouble(editDeposit.getText().toString());
            isNum = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deposit < 0.01 && !isNum) {
            Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_SHORT).show();
        } else {

            com.example.colibribanking.Model.Account acc = userProfile.getAccs().get(accountIndex);
            acc.addDeposit(deposit);

            SharedPreferences.Editor prefsEditor = userPref.edit();
            gson = new Gson();
            json = gson.toJson(userProfile);
            prefsEditor.putString("LastProfileUsed", json).apply();

            Database database = new Database(getApplicationContext());
            database.overAccount(userProfile, acc);
            database.saveTransaction(userProfile, acc.getNo(),
                    acc.getTransactionArrayList().get(acc.getTransactionArrayList().size()-1));

            Toast.makeText(this, "Deposit of " + String.format(Locale.getDefault(), "%.2f",deposit)+ " LEI" + " " + "made successfully", Toast.LENGTH_SHORT).show();

            accountAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userProfile.getAccs());
            accountAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listAccounts.setAdapter(accountAd);

            dialog.dismiss();
            drawer.closeDrawers();
            Navigation(navigationId.ACCS, null);
        }
    }

    private void displayDeposit() {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.deposit_form);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Navigation(navigationId.ACCS, null);
                Toast.makeText(DrawerActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        listAccounts = dialog.findViewById(R.id.list_accounts);
        accountAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userProfile.getAccs());
        accountAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listAccounts.setAdapter(accountAd);
        listAccounts.setSelection(0);

        editDeposit = dialog.findViewById(R.id.edit_deposit);

        cancel = dialog.findViewById(R.id.btn_cancel_);
        deposit = dialog.findViewById(R.id.button_deposit);

        cancel.setOnClickListener(depositClick);
        deposit.setOnClickListener(depositClick);

        dialog.show();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        userPref = this.getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPref.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Class fragmentClass = null;
        String title = item.getTitle().toString();

        switch(item.getItemId()) {
            case R.id.dashboard:
                fragmentClass = MainActivity.class;
                break;
            case R.id.accounts:
                fragmentClass = Account.class;
                break;
            case R.id.nav_deposit:
                if (userProfile.getAccs().size() > 0) {
                    displayDeposit();
                } else {
                    displayAccount("Deposit");
                }
                break;
            case R.id.nav_transfer:
                if (userProfile.getAccs().size() < 2) {
                    displayAccount("Transfer");
                } else {
                    title = "Transfer";
                    fragmentClass = Transfer.class;
                }
                break;
            case R.id.nav_payment:
                if (userProfile.getAccs().size() < 1) {
                    displayAccount("Payment");
                } else {
                    title = "Payment";
                    fragmentClass = Payment.class;
                }
                break;
            case R.id.nav_report:
                title = "Report";
                fragmentClass = ChartActivity.class;
                break;
            case R.id.nav_setup:
                title = "Report";
                fragmentClass = SetupActivity.class;
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragmentClass = MainActivity.class;
        }

        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

            item.setChecked(true);
            setTitle(title);
            drawer.closeDrawers();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


}
