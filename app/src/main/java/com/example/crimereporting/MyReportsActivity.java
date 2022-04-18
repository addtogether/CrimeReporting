package com.example.crimereporting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyReportsActivity extends AppCompatActivity {

    private CardView myFirReport, myMissingReport, myLiveCrimeReport, myCyberCrimeReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);


        myFirReport = findViewById(R.id.firMyReportsCv);
        myMissingReport = findViewById(R.id.missingMyReportsCv);
        myLiveCrimeReport = findViewById(R.id.liveCrimeMyReportsCv);
        myCyberCrimeReport = findViewById(R.id.cyberCrimeMyReportsCv);

        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        myFirReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyReportsActivity.this, DisplayMyFirReportsActivity.class);
                startActivity(intent);
            }
        });
        myMissingReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyReportsActivity.this, DisplayMyMissingReportsActivity.class);
                startActivity(intent);
            }
        });
        myLiveCrimeReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyReportsActivity.this, DisplayMyLiveReportsActivity.class);
                startActivity(intent);
            }
        });
//        myCyberCrimeReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MyReportsActivity.this, DisplayMyFirReportsActivity.class);
//                intent.putExtra("reportType","Cyber Crime");
//                startActivity(intent);
//            }
//        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}