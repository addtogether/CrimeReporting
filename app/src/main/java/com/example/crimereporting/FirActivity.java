package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crimereporting.Prevalent.Prevalent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class FirActivity extends AppCompatActivity {

    private EditText firName, firAddress, firState, firDistrict, firNearestPoliceStation, firPhone, firRelation, firReport;
    private Button addFirBtn;
    private TextView uploadFirImageTv;
    private ImageView firImage;
    private ProgressDialog progressDialog;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String saveCurrentDate, saveCurrentTime, serviceRandomKey, downloadImageUrl = "", name = "", reportType = "";
    private int firNumber;
    private StorageReference firImageRef;
    private DatabaseReference firReportRef;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_LOCATION = 1;
    private Double userLatitude=0.0, userLongitude=0.0;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fir);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        reportType = getIntent().getStringExtra("reportType");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding FIR");
        progressDialog.setMessage("Please wait while we are adding your report !!!");
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
        progressDialog.setIcon(R.drawable.logo);
        progressDialog.setCanceledOnTouchOutside(false);


        firImageRef = FirebaseStorage.getInstance().getReference().child("Report Images").child(reportType);
        firReportRef = FirebaseDatabase.getInstance().getReference().child(reportType);


        firName = findViewById(R.id.FirAccusedName);
        firAddress = findViewById(R.id.FirAccusedAddress);
        firState = findViewById(R.id.FirAccusedState);
        firDistrict = findViewById(R.id.FirAccusedDistrict);
        firNearestPoliceStation = findViewById(R.id.FirNearestPoliceStation);
        firPhone = findViewById(R.id.FirAccusedPhone);
        firRelation = findViewById(R.id.FirAccusedRelation);
        firReport = findViewById(R.id.FirAccusedReport);

        addFirBtn = findViewById(R.id.addFirBtn);
        uploadFirImageTv = findViewById(R.id.clickHereImgFir);

        firImage = findViewById(R.id.firImage);


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fir Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        uploadFirImageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        addFirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                validateEnteredData();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            firImage.setImageURI(ImageUri);

        }
    }


    private void validateEnteredData() {

        String firAccName = firName.getText().toString();
        String firAccAddress = firAddress.getText().toString();
        String firAccState = firState.getText().toString();
        String firAccDistrict = firDistrict.getText().toString();
        String firAccNearestPoliceStation = firNearestPoliceStation.getText().toString();
        String firAccPhone = firPhone.getText().toString();
        String firAccRelation = firRelation.getText().toString();
        String firAccReport = firReport.getText().toString();


        if (TextUtils.isEmpty(firAccName) && TextUtils.isEmpty(firAccAddress) && TextUtils.isEmpty(firAccState) && TextUtils.isEmpty(firAccDistrict) && TextUtils.isEmpty(firAccNearestPoliceStation) && TextUtils.isEmpty(firAccPhone) && TextUtils.isEmpty(firAccRelation) && TextUtils.isEmpty(firAccReport)) {
            progressDialog.dismiss();
            Toast.makeText(this, "At least you have to provide accused name and report", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(firAccName)) {
            progressDialog.dismiss();
            firName.setError("Please provide accused name");
            Toast.makeText(this, "Please provide accused name !!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(firAccReport)) {
            progressDialog.dismiss();
            firReport.setError("Please provide report details");
            Toast.makeText(this, "Please enter what happened with you in report !!!", Toast.LENGTH_SHORT).show();
        } else if (ImageUri == null) {
            progressDialog.dismiss();


            CharSequence options[] = new CharSequence[]
                    {
                            "Yes",
                            "No"
                    };
            final AlertDialog.Builder builder = new AlertDialog.Builder(FirActivity.this);
            builder.setTitle("Confirm Adding FIR Report ? ");

            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        progressDialog.show();
                        saveFirData(firAccName, firAccAddress, firAccState, firAccDistrict, firAccNearestPoliceStation, firAccPhone, firAccRelation, firAccReport);
                    }
                    if (i == 1) {

                        dialogInterface.dismiss();
                    }

                }
            });
            builder.show();


        } else {
            progressDialog.dismiss();

            CharSequence options[] = new CharSequence[]
                    {
                            "Yes",
                            "No"
                    };
            final AlertDialog.Builder builder = new AlertDialog.Builder(FirActivity.this);
            builder.setTitle("Confirm Adding FIR Report ? ");

            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        progressDialog.show();
                        storeImage(firAccName, firAccAddress, firAccState, firAccDistrict, firAccNearestPoliceStation, firAccPhone, firAccRelation, firAccReport);
                    }
                    if (i == 1) {

                        dialogInterface.dismiss();
                    }

                }
            });
            builder.show();


        }
    }

    private void storeImage(String firAccName, String firAccAddress, String firAccState, String firAccDistrict, String firAccNearestPoliceStation, String firAccPhone, String firAccRelation, String firAccReport) {


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat(" MMM dd ,yyyy ");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a ");
        saveCurrentTime = currentTime.format(calendar.getTime());

        serviceRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = firImageRef.child(ImageUri.getLastPathSegment() + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                String message = e.toString();
                Toast.makeText(FirActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        name = ImageUri.getLastPathSegment();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();
                            saveFirData(firAccName, firAccAddress, firAccState, firAccDistrict, firAccNearestPoliceStation, firAccPhone, firAccRelation, firAccReport);
                        }
                    }
                });
            }
        });


    }

    private void saveFirData(String firAccName, String firAccAddress, String firAccState, String firAccDistrict, String firAccNearestPoliceStation, String firAccPhone, String firAccRelation, String firAccReport) {


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat(" MMM dd ,yyyy ");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a ");
        saveCurrentTime = currentTime.format(calendar.getTime());

        firNumber = new Random().nextInt(1000000);


        HashMap<String, Object> firMap = new HashMap<>();

        firMap.put("firNumber", String.valueOf(firNumber));
        firMap.put("firAccName", firAccName);
        firMap.put("firAccAddress", firAccAddress);
        firMap.put("firAccState", firAccState);
        firMap.put("firAccDistrict", firAccDistrict);
        firMap.put("firAccNearestPoliceStation", firAccNearestPoliceStation);
        firMap.put("firAccPhone", firAccPhone);
        firMap.put("firAccRelation", firAccRelation);
        firMap.put("firAccReport", firAccReport);
        firMap.put("firImage", downloadImageUrl);
        firMap.put("firDate", saveCurrentDate);
        firMap.put("firTime", saveCurrentTime);
        firMap.put("reportType", reportType);
        firMap.put("firStatus", "PENDING");
        firMap.put("userLatitude", String.valueOf(userLatitude));
        firMap.put("userLongitude", String.valueOf(userLongitude));
        firMap.put("userPhone", Prevalent.currentOnlineUsers.getPhone());
        firMap.put("userAddress", Prevalent.currentOnlineUsers.getAddress());
        firMap.put("userEmail", Prevalent.currentOnlineUsers.getEmail());
        firMap.put("userName", Prevalent.currentOnlineUsers.getName());


        firReportRef.child(String.valueOf(firNumber)).updateChildren(firMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            createNotificationChannel();
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);

                            progressDialog.dismiss();
                            Toast.makeText(FirActivity.this, "Report Added Successfully...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FirActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(FirActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null){
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(FirActivity.this, "ENABLE LOCATION !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null){
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(FirActivity.this, "ENABLE LOCATION !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null){
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(FirActivity.this, "ENABLE LOCATION !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void createNotificationChannel() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(FirActivity. this,
                default_notification_channel_id )
                .setSmallIcon(R.drawable.logo )
                .setContentTitle( "Congratulations !!!" )
                .setContentText( "Your Report has been added" );
        Intent intent = new Intent(FirActivity.this,MyReportsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(FirActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {

            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new
                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            notificationChannel.enableVibration( true ) ;
            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;

        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
    }

}