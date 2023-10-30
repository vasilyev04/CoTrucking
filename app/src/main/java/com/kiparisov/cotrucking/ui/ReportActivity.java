package com.kiparisov.cotrucking.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityReportBinding;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.Report;

import java.util.Calendar;

public class ReportActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference reports;
    private ActivityReportBinding binding;
    private String reportFlag;
    private String adKey;
    private String ownerAdKey;


    private void init(){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        reports = db.getReference(Constants.FB_REPORT_REFERENCE_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        adKey = bundle.getString("adKey");
        ownerAdKey = bundle.getString("ownerKey");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.reporter_flags));
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reportFlag = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnSendReport.setOnClickListener(view -> {
            createReport();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert_dialog_report_title));
            builder.setMessage(getString(R.string.alert_dialog_report_message));

            builder.setPositiveButton(getString(R.string.alert_report_positive_button),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            builder.create().show();
        });

    }


    private void createReport(){
        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();
        String description = binding.etReportDescription.getText().toString();
        Report report = new Report(timestamp, reportFlag, description, adKey, ownerAdKey,
                Auth.getCurrentUser().getKey());
        reports.push().setValue(report);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}