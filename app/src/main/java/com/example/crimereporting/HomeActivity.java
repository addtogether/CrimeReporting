package com.example.crimereporting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.airbnb.lottie.LottieAnimationView;
import com.example.crimereporting.Prevalent.Prevalent;
import com.example.crimereporting.Service.ListenReports;
import com.example.crimereporting.Service.ListenUserFirReports;
import com.example.crimereporting.Service.ListenUserLiveReports;
import com.example.crimereporting.Service.ListenUserMissingReports;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import io.paperdb.Paper;

import static com.example.crimereporting.WelcomeActivity.isNetworkStatusAvailable;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvName;
    private LottieAnimationView imageView1, imageView2;
    private CardView firBtn, missingBtn, liveBtn, aboutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvName = findViewById(R.id.helloTv);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);

        firBtn = findViewById(R.id.firCv);
        missingBtn = findViewById(R.id.missingCv);
        aboutBtn = findViewById(R.id.aboutCv);
        liveBtn = findViewById(R.id.liveCv);



        Paper.init(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        Intent service = new Intent(HomeActivity.this, ListenUserFirReports.class);
        service.putExtra("uPhone", Prevalent.currentOnlineUsers.getPhone());
        startService(service);

        Intent service1 = new Intent(HomeActivity.this, ListenUserMissingReports.class);
        service1.putExtra("uPhone", Prevalent.currentOnlineUsers.getPhone());
        startService(service1);

        Intent service2 = new Intent(HomeActivity.this, ListenUserLiveReports.class);
        service2.putExtra("uPhone", Prevalent.currentOnlineUsers.getPhone());
        startService(service2);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyReportsActivity.class);
                startActivity(intent);
            }
        });

        firBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FirActivity.class);
                intent.putExtra("reportType","Fir");
                startActivity(intent);

            }
        });


        missingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MissingActivity.class);
                intent.putExtra("reportType","Missing");
                startActivity(intent);

            }
        });

        liveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LiveActivity.class);
                intent.putExtra("reportType","Live");
                startActivity(intent);

            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
                startActivity(intent);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UsersRef.child(Prevalent.currentOnlineUsers.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()) {

                    String uName = dataSnapshot.child("name").getValue().toString();
                    tvName.setText("Hello "+uName+" !!!");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_myReports) {

            Intent intent = new Intent(HomeActivity.this, MyReportsActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}

