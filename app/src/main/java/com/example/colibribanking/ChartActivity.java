package com.example.colibribanking;
//last update 21/06/2024 ora 10:20

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.colibribanking.Model.Payee;
import com.example.colibribanking.Model.Profile;
import com.example.colibribanking.Model.Transaction;
import com.google.gson.Gson;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ChartActivity extends Fragment {

    PieChart pieChart;
    LinearLayout chirie;
    LinearLayout mancare;
    LinearLayout transport;
    LinearLayout cumparaturi;
    LinearLayout utilitati;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Profile userProfile;
    LinearLayout lLayout;
    Button resetToGeneral;
    LinearLayout LL_Inner;
    View View;
    TextView view_predict;
    private Spinner spnSelectPayee;
    private ArrayAdapter<Payee> payeeAdapter;
    private ArrayList<Payee> payees;
    Button btn_predict, btn_report;

    int monthReport = 0;
    int yearReport = 0;

    //Bitmap bmp, scaledbmp;
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 30,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_chart, container, false);

        pieChart = rootView.findViewById(R.id.piechart);
        chirie = rootView.findViewById(R.id.Chirie);
        mancare = rootView.findViewById(R.id.Mancare);
        transport = rootView.findViewById(R.id.Transport);
        cumparaturi = rootView.findViewById(R.id.Cumparaturi);
        utilitati = rootView.findViewById(R.id.Utilitati);
        lLayout = rootView.findViewById(R.id.parent);;
        resetToGeneral = rootView.findViewById(R.id.resetToGeneral);
        spnSelectPayee = rootView.findViewById(R.id.spinner_predict);
        btn_predict = rootView.findViewById(R.id.btn_predict);
        view_predict = rootView.findViewById(R.id.view_predict);
        btn_report = rootView.findViewById(R.id.btn_report);
        setData();

        payees = userProfile.getPayees();
        payeeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, payees);
        payeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectPayee.setAdapter(payeeAdapter);

        dateButton = rootView.findViewById(R.id.datePickerButton);
        dateButton.setText("Select year and month");

        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDatePicker();
                datePickerDialog.show();

            }
        });

        resetToGeneral.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setData();
                dateButton.setText("Select year and month");
            }
        });

        btn_predict.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new CountDownTimer(6000, 5000) {
                    public void onTick(long millisUntilFinished) {
                        view_predict.setText("Prediction loading... ");
                    }
                    String predict = Prediction(spnSelectPayee);
                    public void onFinish() {

                        if(predict.equals("0")){
                            view_predict.setText("You have not made enough payments!");
                        }else
                            view_predict.setText("Next month's expenses: ~ " +  predict +" LEI");
                    }
                }.start();
            }
        });

//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.about_icon_24);
//        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        if (checkPermission()) {
            Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
            Uri u = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, u);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        }

        btn_report.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(dateButton.getText() == "Select year and month"){
                    Toast.makeText(getActivity(), "Please select year and month!", Toast.LENGTH_SHORT).show();
                }else{
                    generatePDF();
                }
            }
        });

        return rootView;
    }

    private void generatePDF() {
        try {

            SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = userPreferences.getString("LastProfileUsed", "");
            userProfile = gson.fromJson(json, Profile.class);

            int nr = userProfile.getAccs().size();

            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            for(int i = 0; i<nr; i++){
                transactions.addAll(userProfile.getAccs().get(i).getTransactionArrayList()) ;
            }

            ArrayList<Transaction> transList = new ArrayList<Transaction>();
            for(Transaction trans : transactions){
                String luna =  removeZero(trans.getTimestamp().substring(5,7));
                String an = trans.getTimestamp().substring(0,4);
                if(trans.getTransType() == Transaction.TRANSACTION_TYPE.PAYMENT && luna.equals(String.valueOf(monthReport))
                        && an.equals(String.valueOf(yearReport)) ){
                    transList.add(trans);
                }
            }

            if(transList.size() < 1){
                Toast.makeText(getActivity(), "No Available Data", Toast.LENGTH_SHORT).show();
            }
            else {
                String timeStamp = new SimpleDateFormat("dd.MM.yyyy - HHmmss").format(Calendar.getInstance().getTime());

                File file = new File(Environment.getExternalStorageDirectory(), timeStamp + ".pdf");
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                addContent(document, transList, yearReport, monthReport);
                Toast.makeText(getActivity(), "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private  void addContent(Document document, ArrayList<Transaction> transList, int year, int month) throws DocumentException {

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 2);

        Anchor anchor = new Anchor("Expense Report: " + getMonthFormat(month) + " " + year, catFont);
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

        Paragraph subPara = new Paragraph("Detailed Report", subFont);
        Section subCatPart = catPart.addSection(subPara);
        subCatPart.add(paragraph);
        createDetailedTable(subCatPart, transList);

        Paragraph subPara2 = new Paragraph("Total Report Categories", subFont);
        Section subCatPart2 = catPart.addSection(subPara2);
        subCatPart2.add(paragraph);
        createGeneralTable(subCatPart2, transList);

        document.add(catPart);
        Paragraph empty = new Paragraph();
        addEmptyLine(empty, 3);
        document.add(empty);

        Paragraph signature = new Paragraph();
        signature.add("Report generated by: " + userProfile.getFirstName() + ", " + new Date());
        document.add(signature);

    }
    private  void createGeneralTable(Section subCatPart, ArrayList<Transaction> transList)
            throws BadElementException {

        PdfPTable table = new PdfPTable(2);

        PdfPCell c1 = new PdfPCell(new Phrase("Category"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Price"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        table.setHeaderRows(1);

        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        double sum4 = 0;
        double sum5 = 0;

        for(Transaction trans : transList)
        {
                if(trans.getPayee().equals("Rent (P1)")){
                    sum1 += trans.getAmount();
                }
                else if (trans.getPayee().equals("Food and Restaurant (P2)")) {
                    sum2 += trans.getAmount();
                }
                else if (trans.getPayee().equals("Transport and car (P3)")) {
                    sum3 += trans.getAmount();
                }
                else if (trans.getPayee().equals("Various purchases (P4)")) {
                    sum4 += trans.getAmount();
                }
                else if (trans.getPayee().equals( "Utility (P5)")) {
                    sum5 += trans.getAmount();
                }
        }

        if (sum1 > 0){
            table.addCell("Rent (P1)");
            table.addCell(Double.toString(sum1));
        }

        if (sum2 > 0) {
            table.addCell("Food and Restaurant (P2)");
            table.addCell(Double.toString(sum2));
        }

        if (sum3 > 0) {
            table.addCell("Transport and car (P3)");
            table.addCell(Double.toString(sum3));
        }

        if (sum4 > 0) {
            table.addCell("Various purchases (P4)");
            table.addCell(Double.toString(sum4));
        }

        if (sum5 > 0) {
            table.addCell("Utility (P5)");
            table.addCell(Double.toString(sum5));
        }

        subCatPart.add(table);
    }

    private  void createDetailedTable(Section subCatPart, ArrayList<Transaction> transList)
            throws BadElementException {

        PdfPTable table = new PdfPTable(3);

        PdfPCell c1 = new PdfPCell(new Phrase("Category"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Price"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        for(Transaction trans : transList)
        {
                table.addCell(trans.getPayee());
                table.addCell(Double.toString(trans.getAmount()));
                table.addCell(trans.getTimestamp());
        }
        subCatPart.add(table);
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE);

        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,MANAGE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(getActivity(), "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String Prediction(Spinner spinner)
    {
        int selectedPayeeIndex = spnSelectPayee.getSelectedItemPosition();
        String selectedPayee = userProfile.getPayees().get(selectedPayeeIndex).toString();

        double sum = 0;
        int diffMonth = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        int nr = userProfile.getAccs().size();

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        for(int i = 0; i<nr; i++){
            transactions.addAll(userProfile.getAccs().get(i).getTransactionArrayList()) ;
        }

        ArrayList<Transaction> transList = new ArrayList<Transaction>();
        for(Transaction trans : transactions){
            if(trans.getTransType() == Transaction.TRANSACTION_TYPE.PAYMENT){

                if(trans.getPayee().equals(selectedPayee)){
                    transList.add(trans);
                    sum += trans.getAmount();
                }
            }
        }

        if(sum > 0){
            Collections.sort(transList, new TransactionComparator());

            Transaction trFirst = transList.get(0);
            Transaction trLast = transList.get(transList.size()-1);

            String first =  removeZero(trFirst.getTimestamp().substring(0,10));
            String last =  removeZero(trLast.getTimestamp().substring(0,10));

            try {

                Calendar startCalendar = new GregorianCalendar();
                startCalendar.setTime(format.parse(first));
                Calendar endCalendar = new GregorianCalendar();
                endCalendar.setTime(format.parse(last));

                int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if(diffMonth > 1){
                return String.format("%.2f", sum/diffMonth);
            }else
                return "0";

        }
        else return "0";
    }

    class TransactionComparator implements Comparator<Transaction> {
        public int compare(Transaction transOne, Transaction transTwo) {

            java.util.Date dateOne = null;
            Date dateTwo = null;

            try {
                dateOne = Transaction.SIMPLE_DATE_FORMAT.parse(transOne.getTimestamp());
                dateTwo = Transaction.SIMPLE_DATE_FORMAT.parse(transTwo.getTimestamp());
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

    private void setData()
    {
        SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);
        payees = userProfile.getPayees();
        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        double sum4 = 0;
        double sum5 = 0;

        pieChart.clearChart();
        chirie.setVisibility(View.GONE);
        mancare.setVisibility(View.GONE);
        transport.setVisibility(View.GONE);
        cumparaturi.setVisibility(View.GONE);
        utilitati.setVisibility(View.GONE);


        int nr = userProfile.getAccs().size();

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        for(int i = 0; i<nr; i++){
            transactions.addAll(userProfile.getAccs().get(i).getTransactionArrayList()) ;
        }

        ArrayList<Transaction> transList = new ArrayList<Transaction>();
        for(Transaction trans : transactions){
            if(trans.getTransType() == Transaction.TRANSACTION_TYPE.PAYMENT){
                transList.add(trans);
                if(trans.getPayee().equals("Rent (P1)")){
                    sum1 += trans.getAmount();

                }
                else if (trans.getPayee().equals("Food and Restaurant (P2)")) {
                    sum2 += trans.getAmount();
                }
                else if (trans.getPayee().equals("Transport and car (P3)")) {
                    sum3 += trans.getAmount();

                }
                else if (trans.getPayee().equals("Various purchases (P4)")) {
                    sum4 += trans.getAmount();

                }
                else if (trans.getPayee().equals( "Utility (P5)")) {
                    sum5 += trans.getAmount();
                }

            }
        }

        if (sum1 > 0){

            chirie.setVisibility(VISIBLE);
            pieChart.addPieSlice(
                    new PieModel(
                            "Rent",
                            (float)sum1,
                            Color.parseColor("#FFA726")));
        }
        if (sum2 > 0) {
            mancare.setVisibility(VISIBLE);
            pieChart.addPieSlice(
                    new PieModel(
                            "Food and Restaurant",
                            (float)sum2,
                            Color.parseColor("#66BB6A")));
        }
        if (sum3 > 0) {
            transport.setVisibility(VISIBLE);
            pieChart.addPieSlice(
                    new PieModel(
                            "Transport and car",
                            (float)sum3,
                            Color.parseColor("#EF5350")));
        }
        if (sum4 > 0) {
            cumparaturi.setVisibility(VISIBLE);
            pieChart.addPieSlice(
                    new PieModel(
                            "Various purchases",
                            (float)sum4,
                            Color.parseColor("#29B6F6")));
        }
        if (sum5 > 0) {
            utilitati.setVisibility(VISIBLE);
            pieChart.addPieSlice(
                    new PieModel(
                            "Utility" ,
                            (float)sum5,
                            Color.parseColor("#EEFB07")));
        }

        if(pieChart.getData().isEmpty()){
            btn_predict.setVisibility(android.view.View.GONE);
            spnSelectPayee.setVisibility(android.view.View.GONE);
            view_predict.setVisibility(android.view.View.GONE);
            btn_report.setVisibility(android.view.View.GONE);

        }else{
            btn_predict.setVisibility(VISIBLE);
            spnSelectPayee.setVisibility(android.view.View.VISIBLE);
            view_predict.setVisibility(android.view.View.VISIBLE);
            btn_report.setVisibility(android.view.View.VISIBLE);
        }
        pieChart.startAnimation();
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(month, year);
                dateButton.setText(date);
                setDataWithDate(month, year);
                yearReport = year;
                monthReport = month;
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
    }

    public static String removeZero(String str)
    {
        int i = 0;
        while (i < str.length() && str.charAt(i) == '0')
            i++;

        StringBuffer sb = new StringBuffer(str);
        sb.replace(0, i, "");
        return sb.toString();
    }

    private void setDataWithDate(int month, int year)
    {
        SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        double sum4 = 0;
        double sum5 = 0;

        pieChart.clearChart();
        chirie.setVisibility(View.GONE);
        mancare.setVisibility(View.GONE);
        transport.setVisibility(View.GONE);
        cumparaturi.setVisibility(View.GONE);
        utilitati.setVisibility(View.GONE);

        chirie.setMinimumHeight(0);
        mancare.setMinimumHeight(0);
        transport.setMinimumHeight(0);
        cumparaturi.setMinimumHeight(0);
        utilitati.setMinimumHeight(0);

        int nr = userProfile.getAccs().size();

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        for(int i = 0; i<nr; i++){
            transactions.addAll(userProfile.getAccs().get(i).getTransactionArrayList()) ;
        }

        ArrayList<Transaction> transList = new ArrayList<Transaction>();
        for(Transaction trans : transactions){
            String luna =  removeZero(trans.getTimestamp().substring(5,7));
            String an = trans.getTimestamp().substring(0,4);
            if(trans.getTransType() == Transaction.TRANSACTION_TYPE.PAYMENT && luna.equals(String.valueOf(month))
                && an.equals(String.valueOf(year)) ){

                transList.add(trans);
                if(trans.getPayee().equals("Rent (P1)")){
                    sum1 += trans.getAmount();
                }
                else if (trans.getPayee().equals("Food and Restaurant (P2)")) {
                    sum2 += trans.getAmount();
                }
                else if (trans.getPayee().equals("Transport and car (P3)")) {
                    sum3 += trans.getAmount();
                }
                else if (trans.getPayee().equals("Various purchases (P4)")) {
                    sum4 += trans.getAmount();
                }
                else if (trans.getPayee().equals( "Utility (P5)")) {
                    sum5 += trans.getAmount();
                }

            }
        }

        if (sum1 > 0){
            chirie.setVisibility(VISIBLE);
            chirie.setMinimumHeight(15);
            pieChart.addPieSlice(
                    new PieModel(
                            "Rent",
                            (float)sum1,
                            Color.parseColor("#FFA726")));
        }
        if (sum2 > 0) {
            mancare.setVisibility(VISIBLE);
            mancare.setMinimumHeight(15);
            pieChart.addPieSlice(
                    new PieModel(
                            "Food and Restaurant",
                            (float)sum2,
                            Color.parseColor("#66BB6A")));
        }
        if (sum3 > 0) {
            transport.setVisibility(VISIBLE);
            transport.setMinimumHeight(15);
            pieChart.addPieSlice(
                    new PieModel(
                            "Transport and car",
                            (float)sum3,
                            Color.parseColor("#EF5350")));
        }
        if (sum4 > 0) {
            cumparaturi.setVisibility(VISIBLE);
            cumparaturi.setMinimumHeight(15);
            pieChart.addPieSlice(
                    new PieModel(
                            "Various purchases",
                            (float)sum4,
                            Color.parseColor("#29B6F6")));
        }
        if (sum5 > 0) {
            utilitati.setVisibility(VISIBLE);
            utilitati.setMinimumHeight(15);
            pieChart.addPieSlice(
                    new PieModel(
                            "Utility" ,
                            (float)sum5,
                            Color.parseColor("#EEFB07")));
        }

        if(pieChart.getData().isEmpty()){
            btn_predict.setVisibility(android.view.View.GONE);
            spnSelectPayee.setVisibility(android.view.View.GONE);
            view_predict.setVisibility(android.view.View.GONE);
            btn_report.setVisibility(android.view.View.GONE);

        }else{
            btn_predict.setVisibility(VISIBLE);
            spnSelectPayee.setVisibility(android.view.View.VISIBLE);
            view_predict.setVisibility(android.view.View.VISIBLE);
            btn_report.setVisibility(android.view.View.VISIBLE);
        }
        pieChart.startAnimation();
    }

    private String makeDateString(int month, int year)
    {
        return getMonthFormat(month) + " - " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

}