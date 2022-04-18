package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

public class MissingActivity extends AppCompatActivity {

    private EditText missingName, missingAddress, missingAge, missingState, missingDistrict, missingNearestPoliceStation, missingPhone, missingRelation, missingReport, missingDate;
    private Button addMissingBtn;
    private TextView uploadMissingImageTv;
    private ImageView missingImage;
    private ProgressDialog progressDialog;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String saveCurrentDate, saveCurrentTime, serviceRandomKey,downloadImageUrl="", name="", reportType="";
    private int missingNumber;
    private StorageReference missingImageRef;
    private DatabaseReference missingReportRef;
    private DatePickerDialog datePickerDialog;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_LOCATION = 1;
    private Double userLatitude=0.0, userLongitude=0.0;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        reportType = getIntent().getStringExtra("reportType");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Missing Report");
        progressDialog.setMessage("Please wait while we are adding your report !!!");
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
        progressDialog.setIcon(R.drawable.logo);
        progressDialog.setCanceledOnTouchOutside(false);


        missingImageRef= FirebaseStorage.getInstance().getReference().child("Report Images").child(reportType);
        missingReportRef= FirebaseDatabase.getInstance().getReference().child(reportType);


        missingName = findViewById(R.id.MissingPersonName);
        missingAddress = findViewById(R.id.MissingPersonAddress);
        missingAge = findViewById(R.id.MissingPersonAge);
        missingState = findViewById(R.id.MissingPersonState);
        missingDistrict = findViewById(R.id.MissingPersonDistrict);
        missingNearestPoliceStation = findViewById(R.id.MissingNearestPoliceStation);
        missingPhone = findViewById(R.id.MissingPersonPhone);
        missingRelation = findViewById(R.id.MissingPersonRelation);
        missingReport = findViewById(R.id.MissingPersonReport);
        missingDate = findViewById(R.id.MissingPersonDate);

        addMissingBtn = findViewById(R.id.addMissingBtn);
        uploadMissingImageTv = findViewById(R.id.clickHereImgMissing);

        missingImage = findViewById(R.id.missingImage);


        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Missing Person Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        missingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        uploadMissingImageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        addMissingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               progressDialog.show();
               validateEnteredData();

            }
        });



        missingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MissingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                missingDate.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
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

        if(requestCode==GalleryPick && resultCode==RESULT_OK  && data!=null){
            ImageUri = data.getData();
            missingImage.setImageURI(ImageUri);

        }
    }

    private void validateEnteredData() {

        String missingPerName = missingName.getText().toString();
        String missingPerAddress = missingAddress.getText().toString();
        String missingPerAge = missingAge.getText().toString();
        String missingPerState = missingState.getText().toString();
        String missingPerDistrict = missingDistrict.getText().toString();
        String missingPerNearestPoliceStation = missingNearestPoliceStation.getText().toString();
        String missingPerPhone = missingPhone.getText().toString();
        String missingPerRelation = missingRelation.getText().toString();
        String missingPerReport = missingReport.getText().toString();
        String missingPerDate = missingDate.getText().toString();


        if(ImageUri==null)
        {
            progressDialog.dismiss();
            Toast.makeText(this,"Missing Person's Image Is Mandatory...",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(missingPerName) && TextUtils.isEmpty(missingPerAddress) && TextUtils.isEmpty(missingPerAge) && TextUtils.isEmpty(missingPerState) && TextUtils.isEmpty(missingPerDistrict) && TextUtils.isEmpty(missingPerNearestPoliceStation) && TextUtils.isEmpty(missingPerPhone) && TextUtils.isEmpty(missingPerRelation) && TextUtils.isEmpty(missingPerReport) && TextUtils.isEmpty(missingPerDate)){
            progressDialog.dismiss();
            Toast.makeText(this, "Please provide each and every details !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerName)){
            progressDialog.dismiss();
            missingName.setError("Please provide Missing Person's name");
            Toast.makeText(this, "Please provide Missing Person's name !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerAddress)){
            progressDialog.dismiss();
            missingAddress.setError("Please provide Missing Person's Address");
            Toast.makeText(this, "Please provide Missing Person's Address !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerAge)){
            progressDialog.dismiss();
            missingAge.setError("Please provide Missing Person's Age");
            Toast.makeText(this, "Please provide Missing Person's Age !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerState)){
            progressDialog.dismiss();
            missingState.setError("Please provide state name");
            Toast.makeText(this, "Please provide state name !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerDistrict)){
            progressDialog.dismiss();
            missingDistrict.setError("Please provide district name");
            Toast.makeText(this, "Please provide district name !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerNearestPoliceStation)){
            progressDialog.dismiss();
            missingNearestPoliceStation.setError("Please provide Nearest Police Station Name");
            Toast.makeText(this, "Please provide Nearest Police Station Name !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerReport)){
            progressDialog.dismiss();
            missingReport.setError("Please provide Missing Person's Details");
            Toast.makeText(this, "Please provide Missing Person's Details !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(missingPerDate)){
            progressDialog.dismiss();
            missingDate.setError("Please provide Missing Date");
            Toast.makeText(this, "Please provide Missing Date !!!", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.dismiss();
            CharSequence options[] = new CharSequence[]
                    {
                            "Yes",
                            "No"
                    };
            final AlertDialog.Builder builder = new AlertDialog.Builder(MissingActivity.this);
            builder.setTitle("Confirm Adding Missing Report ? ");

            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        progressDialog.show();
                        storeImage(missingPerName, missingPerAddress, missingPerAge, missingPerState, missingPerDistrict, missingPerNearestPoliceStation, missingPerPhone, missingPerRelation, missingPerReport, missingPerDate);
                    }
                    if (i == 1) {

                        dialogInterface.dismiss();
                    }

                }
            });
            builder.show();

        }
    }

    private void storeImage(String missingPerName, String missingPerAddress, String missingPerAge, String missingPerState, String missingPerDistrict, String missingPerNearestPoliceStation, String missingPerPhone, String missingPerRelation, String missingPerReport, String missingPerDate) {


            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat(" MMM dd ,yyyy ");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a ");
            saveCurrentTime = currentTime.format(calendar.getTime());

            serviceRandomKey= saveCurrentDate +saveCurrentTime;

            final StorageReference filePath= missingImageRef.child(ImageUri.getLastPathSegment()+".jpg");
            final UploadTask uploadTask = filePath.putFile(ImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    String message = e.toString();
                    Toast.makeText(MissingActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful())
                            {
                                throw task.getException();

                            }

                            downloadImageUrl= filePath.getDownloadUrl().toString();
                            name = ImageUri.getLastPathSegment();
                            return filePath.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {

                                downloadImageUrl=task.getResult().toString();
                                saveFirData(missingPerName, missingPerAddress, missingPerAge, missingPerState, missingPerDistrict, missingPerNearestPoliceStation, missingPerPhone, missingPerRelation, missingPerReport, missingPerDate);
                            }
                        }
                    });
                }
            });

    }

    private void saveFirData(String missingPerName, String missingPerAddress, String missingPerAge, String missingPerState, String missingPerDistrict, String missingPerNearestPoliceStation, String missingPerPhone, String missingPerRelation, String missingPerReport, String missingPerDate) {


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat(" MMM dd ,yyyy ");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a ");
        saveCurrentTime = currentTime.format(calendar.getTime());

        missingNumber = new Random().nextInt(1000000);

        HashMap<String,Object> missingMap = new HashMap<>();

        missingMap.put("missingNumber",String.valueOf(missingNumber));
        missingMap.put("missingPerName",missingPerName);
        missingMap.put("missingPerAddress",missingPerAddress);
        missingMap.put("missingPerAge",missingPerAge);
        missingMap.put("missingPerState",missingPerState);
        missingMap.put("missingPerDistrict",missingPerDistrict);
        missingMap.put("missingPerNearestPoliceStation",missingPerNearestPoliceStation);
        missingMap.put("missingPerPhone",missingPerPhone);
        missingMap.put("missingPerRelation",missingPerRelation);
        missingMap.put("missingPerReport",missingPerReport);
        missingMap.put("missingPerDate",missingPerDate);
        missingMap.put("missingPerImage", downloadImageUrl);
        missingMap.put("missingDate",saveCurrentDate);
        missingMap.put("missingTime",saveCurrentTime);
        missingMap.put("reportType",reportType);
        missingMap.put("missingStatus","PENDING");
        missingMap.put("userLatitude", String.valueOf(userLatitude));
        missingMap.put("userLongitude", String.valueOf(userLongitude));
        missingMap.put("userPhone", Prevalent.currentOnlineUsers.getPhone());
        missingMap.put("userAddress", Prevalent.currentOnlineUsers.getAddress());
        missingMap.put("userEmail", Prevalent.currentOnlineUsers.getEmail());
        missingMap.put("userName", Prevalent.currentOnlineUsers.getName());


        missingReportRef.child(String.valueOf(missingNumber)).updateChildren(missingMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            createNotificationChannel();
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            progressDialog.dismiss();
                            Toast.makeText(MissingActivity.this,"Report Added Successfully...",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MissingActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            progressDialog.dismiss();
                            String message =task.getException().toString();
                            Toast.makeText(MissingActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MissingActivity.this, "ENABLE LOCATION !!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MissingActivity.this, "ENABLE LOCATION !!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MissingActivity.this, "ENABLE LOCATION !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void createNotificationChannel() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MissingActivity. this,
                default_notification_channel_id )
                .setSmallIcon(R.drawable.logo )
                .setContentTitle( "Congratulations !!!" )
                .setContentText( "Your Report has been added" );
        Intent intent = new Intent(MissingActivity.this,MyReportsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MissingActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
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