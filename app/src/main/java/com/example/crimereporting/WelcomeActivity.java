package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crimereporting.Model.Users;
import com.example.crimereporting.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class WelcomeActivity extends AppCompatActivity {

    private TextView tv1,tv2,tv3,tv4,tv5;
    private Button login;
    private Animation topAnim,middleAnim,bottomAnim;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        middleAnim= AnimationUtils.loadAnimation(this,R.anim.middle_animation);
        progressDialog=new ProgressDialog(this);

        tv1=findViewById(R.id.tvName1);
        tv2=findViewById(R.id.tvName2);
        tv3=findViewById(R.id.tvName3);
        tv4=findViewById(R.id.tvName4);
        tv5=findViewById(R.id.tvName5);
        login=findViewById(R.id.login);

        tv1.setAnimation(middleAnim);
        tv2.setAnimation(middleAnim);
        tv3.setAnimation(middleAnim);
        login.setAnimation(middleAnim);
        tv4.setAnimation(bottomAnim);
        tv5.setAnimation(bottomAnim);

        Paper.init(this);

        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WelcomeActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WelcomeActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setEnabled(false);
                Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });


        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey!="" && UserPasswordKey!=""){
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){

                AllowAccess(UserPhoneKey,UserPasswordKey);

                progressDialog.setTitle("  Already Logged in");
                progressDialog.setMessage("Please wait...");
                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                progressDialog.setIcon(R.drawable.logo);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        login.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setMessage("GPS NOT ENABLED")
                    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }
                    })
                    .show();
        }



        if(isNetworkStatusAvailable (getApplicationContext())) {


        } else {
            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);

            alertDialog.setTitle("Internet Connection");
            alertDialog.setIcon(R.drawable.about);
            alertDialog.setMessage("Please make sure that you are connected to Internet");
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
            alertDialog.show();

        }
    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);

        alertDialog.setTitle("Exit");
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

    private void AllowAccess(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(phone).exists()){
                    Users userData= dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone)){
                        if(userData.getPassword().equals(password)){
//                            Toast.makeText(WelcomeActivity.this, "Logged in Successfully",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent=new Intent(WelcomeActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUsers=userData;
                            startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(WelcomeActivity.this, "Invalid Password",Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        progressDialog.dismiss();
                        Toast.makeText(WelcomeActivity.this, "Invalid Phone Number",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(dataSnapshot.child("Admins").child(phone).exists()){
                    Users userData= dataSnapshot.child("Admins").child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone)){
                        if(userData.getPassword().equals(password)){
//                            Toast.makeText(WelcomeActivity.this, "Admin Logged in Successfully",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent=new Intent(WelcomeActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(WelcomeActivity.this, "Invalid Password",Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        progressDialog.dismiss();
                        Toast.makeText(WelcomeActivity.this, "Invalid Phone Number",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(WelcomeActivity.this, "Account with this " + phone + " do not exists",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static boolean isNetworkStatusAvailable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }
}