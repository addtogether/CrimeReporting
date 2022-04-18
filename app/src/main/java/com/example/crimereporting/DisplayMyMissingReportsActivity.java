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
import com.example.crimereporting.Model.Missing;
import com.example.crimereporting.Prevalent.Prevalent;
import com.example.crimereporting.ViewHolder.FirViewHolder;
import com.example.crimereporting.ViewHolder.MissingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DisplayMyMissingReportsActivity extends AppCompatActivity {

    private DatabaseReference myMissingReportRef;
    private RecyclerView myMissingReportList;
    private String address ="", nearestStation = "", phone ="", relation ="", image ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_my_missing_reports);

        myMissingReportRef = FirebaseDatabase.getInstance().getReference().child("Missing");


        myMissingReportList = findViewById(R.id.myMissingReportList);
        myMissingReportList.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Missing> options =new FirebaseRecyclerOptions.Builder<Missing>()
                .setQuery(myMissingReportRef.orderByChild("userPhone").equalTo(Prevalent.currentOnlineUsers.getPhone()),Missing.class).build();

        FirebaseRecyclerAdapter<Missing, MissingViewHolder> adapter = new FirebaseRecyclerAdapter<Missing, MissingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MissingViewHolder holder, int position, @NonNull final Missing model) {


                holder.myReportMissingNum.setText("MISSING NO: "+model.getMissingNumber());
                holder.myReportMissingName.setText("Missing Person Name: "+model.getMissingPerName());
                holder.myReportMissingAddress.setText("Address: "+model.getMissingPerAddress()+", "+model.getMissingPerState()+", "+model.getMissingPerDistrict());
                holder.myReportMissingAge.setText("Age: "+model.getMissingPerAge());
                holder.myReportMissingNearestPoliceStation.setText("Police Station: "+model.getMissingPerNearestPoliceStation());
                holder.myReportMissingPhone.setText("Phone: "+model.getMissingPerPhone());
                holder.myReportMissingRelation.setText("Relation: "+model.getMissingPerRelation());
                holder.myReportMissingReport.setText("Report: "+model.getMissingPerReport());
                holder.myReportMissingFrom.setText("Missing from: "+model.getMissingPerDate());
                holder.myReportMissingDateTime.setText("Reported On: "+model.getMissingDate()+"  "+model.getMissingTime());
                holder.myReportMissingUserPhone.setText("User Phone: " + model.getUserPhone());
                holder.myReportMissingStatus.setText("STATUS: "+model.getMissingStatus());
                Picasso.get().load(model.getMissingPerImage()).into(holder.myReportMissingImage);


                phone = model.getMissingPerPhone();
                relation = model.getMissingPerRelation();
                image = model.getMissingPerImage();

                if(phone.equals("")){
                    holder.myReportMissingPhone.setText("Phone: Not Available");
                    holder.myReportMissingPhone.setTextColor(Color.parseColor("#DB4437"));
                }
                if(relation.equals("")){
                    holder.myReportMissingRelation.setText("Relation: Not Available");
                    holder.myReportMissingRelation.setTextColor(Color.parseColor("#DB4437"));
                }

            }

            @NonNull
            @Override
            public MissingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_missing_item, parent, false);
                MissingViewHolder holder = new MissingViewHolder(view);
                return holder;
            }
        };
        myMissingReportList.setAdapter(adapter);
        adapter.startListening();



    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}