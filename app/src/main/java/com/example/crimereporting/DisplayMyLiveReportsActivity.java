package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.crimereporting.Model.Fir;
import com.example.crimereporting.Model.Live;
import com.example.crimereporting.Prevalent.Prevalent;
import com.example.crimereporting.ViewHolder.FirViewHolder;
import com.example.crimereporting.ViewHolder.LiveViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DisplayMyLiveReportsActivity extends AppCompatActivity {

    private DatabaseReference myReportRef;
    private RecyclerView myLiveReportList;
    private String address ="", nearestStation = "", phone ="", relation ="", image ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_my_live_reports);


        myReportRef = FirebaseDatabase.getInstance().getReference().child("Live");


        myLiveReportList = findViewById(R.id.displayLiveReportsList);
        myLiveReportList.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Live> options =new FirebaseRecyclerOptions.Builder<Live>()
                .setQuery(myReportRef.orderByChild("userPhone").equalTo(Prevalent.currentOnlineUsers.getPhone()),Live.class).build();

        FirebaseRecyclerAdapter<Live, LiveViewHolder> adapter = new FirebaseRecyclerAdapter<Live, LiveViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LiveViewHolder holder, int position, @NonNull final Live model) {

                holder.myReportLiveNum.setText("LIVE CRIME NO: " + model.getLiveNumber());
                holder.myReportLiveName.setText("Name: " + model.getLiveUserName());
                holder.myReportLiveAddress.setText("Address: " + model.getLiveUserAddress());
                holder.myReportLivePhone.setText("Phone: " + model.getLiveUserPhone());
                holder.myReportLiveRelation.setText("Relation: " + model.getLiveUserRelation());
                holder.myReportLiveReport.setText("Report: " + model.getLiveUserReport());
                holder.myReportLiveDateTime.setText("Reported On: " + model.getLiveDate() + "  " + model.getLiveTime());
                holder.myReportLiveUserPhone.setText("User Phone: " + model.getUserPhone());
                holder.myReportLiveStatus.setText("STATUS: " + model.getLiveStatus());
                holder.setExoplayer(getApplication(),model.getLiveImage());

                if (relation.equals("")) {
                    holder.myReportLiveRelation.setText("Relation: Not Available");
                    holder.myReportLiveRelation.setTextColor(Color.parseColor("#DB4437"));
                }


            }

            @NonNull
            @Override
            public LiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_live_item, parent, false);
                LiveViewHolder holder = new LiveViewHolder(view);
                return holder;
            }
        };
        myLiveReportList.setAdapter(adapter);
        adapter.startListening();



    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}