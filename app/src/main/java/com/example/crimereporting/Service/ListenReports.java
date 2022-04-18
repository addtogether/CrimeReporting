package com.example.crimereporting.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.crimereporting.DisplayFirReportsAdminActivity;
import com.example.crimereporting.Model.Fir;
import com.example.crimereporting.Prevalent.Prevalent;
import com.example.crimereporting.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListenReports extends Service implements ChildEventListener {

    FirebaseDatabase firebaseDatabase;
//    String uName="";
    DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Fir");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public ListenReports() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Fir newReport = dataSnapshot.getValue(Fir.class);

            showNotification(dataSnapshot.getKey(),newReport);

    }

    private void showNotification(String key, Fir newReport) {

//        Intent intent = new Intent(getBaseContext(), AdminHomeActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),
                "default" )
                .setSmallIcon(R.drawable.logo )
                .setContentTitle( "Hey Police !!!" )
                .setContentText( "You have a new FIR report from " + newReport.getUserName());
        Intent intent = new Intent(getBaseContext(), DisplayFirReportsAdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("reportType","Fir");
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {

            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new
                    NotificationChannel( "10001" , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            notificationChannel.enableVibration( true ) ;
            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
            mBuilder.setChannelId( "10001" ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;

        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
