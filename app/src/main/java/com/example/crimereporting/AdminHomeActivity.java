package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.crimereporting.Model.Fir;

import com.example.crimereporting.Prevalent.Prevalent;
import com.example.crimereporting.Service.ListenReports;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import io.paperdb.Paper;

public class AdminHomeActivity extends AppCompatActivity {

    private CardView myFirReportAdmin, myMissingReportAdmin, myLiveCrimeReportAdmin, myCyberCrimeReportAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        myFirReportAdmin = findViewById(R.id.firMyReportsCvAdmin);
        myMissingReportAdmin = findViewById(R.id.missingMyReportsCvAdmin);
        myLiveCrimeReportAdmin = findViewById(R.id.liveCrimeMyReportsCvAdmin);
        myCyberCrimeReportAdmin = findViewById(R.id.cyberCrimeMyReportsCvAdmin);




        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Police Home");

        Intent service = new Intent(AdminHomeActivity.this, ListenReports.class);
        startService(service);



        myFirReportAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, DisplayFirReportsAdminActivity.class);
                intent.putExtra("reportType","Fir");
                startActivity(intent);
            }
        });
        myMissingReportAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, DisplayMissingReportsAdminActivity.class);
                intent.putExtra("reportType","Missing");
                startActivity(intent);
            }
        });
        myLiveCrimeReportAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, DisplayLiveReportsAdminActivity.class);
                intent.putExtra("reportType","Live");
                startActivity(intent);
            }
        });



    }


    @Override
    public void onBackPressed() {

            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);

            alertDialog.setTitle("Confirm Exit");
            alertDialog.setIcon(R.drawable.logout);
            alertDialog.setMessage("Are you sure You want to Exit ?");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            alertDialog.show();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin_logout) {

            Paper.book().destroy();
            Intent intent = new Intent(AdminHomeActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}