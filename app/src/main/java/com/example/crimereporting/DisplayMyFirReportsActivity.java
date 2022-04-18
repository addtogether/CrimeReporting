package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Placeholder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.crimereporting.Model.Fir;
import com.example.crimereporting.Prevalent.Prevalent;
import com.example.crimereporting.ViewHolder.FirViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DisplayMyFirReportsActivity extends AppCompatActivity {

    private DatabaseReference myReportRef;
    private RecyclerView myFirReportList;
    private String address ="", nearestStation = "", phone ="", relation ="", image ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_my_fir_reports);


        myReportRef = FirebaseDatabase.getInstance().getReference().child("Fir");


        myFirReportList = findViewById(R.id.myFirReportList);
        myFirReportList.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Fir> options =new FirebaseRecyclerOptions.Builder<Fir>()
                .setQuery(myReportRef.orderByChild("userPhone").equalTo(Prevalent.currentOnlineUsers.getPhone()),Fir.class).build();

        FirebaseRecyclerAdapter<Fir, FirViewHolder> adapter = new FirebaseRecyclerAdapter<Fir, FirViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull FirViewHolder holder, int position, @NonNull final Fir model) {


                holder.myReportFirNum.setText("FIR NO: "+model.getFirNumber());
                holder.myReportFirName.setText("Accused Name: "+model.getFirAccName());
                holder.myReportFirAddress.setText("Address: "+model.getFirAccAddress()+", "+model.getFirAccDistrict()+", "+model.getFirAccState());
                holder.myReportFirNearestPoliceStation.setText("Police Station: "+model.getFirAccNearestPoliceStation());
                holder.myReportFirPhone.setText("Phone: "+model.getFirAccPhone());
                holder.myReportFirRelation.setText("Relation: "+model.getFirAccRelation());
                holder.myReportFirReport.setText("Report: "+model.getFirAccReport());
                holder.myReportFirDateTime.setText("Reported On: "+model.getFirDate()+"  "+model.getFirTime());
                holder.myReportFirUserPhone.setText("User Phone: " + model.getUserPhone());
                holder.myReportFirStatus.setText("STATUS: "+model.getFirStatus());

                address = model.getFirAccAddress();
                nearestStation = model.getFirAccNearestPoliceStation();
                phone = model.getFirAccPhone();
                relation = model.getFirAccRelation();
                image = model.getFirImage();

                if(address.equals("")){
                    holder.myReportFirAddress.setText("Address: Not Available");
                    holder.myReportFirAddress.setTextColor(Color.parseColor("#DB4437"));
                }
                if(nearestStation.equals("")){
                    holder.myReportFirNearestPoliceStation.setText("Police Station: Not Available");
                    holder.myReportFirNearestPoliceStation.setTextColor(Color.parseColor("#DB4437"));
                }
                if(phone.equals("")){
                    holder.myReportFirPhone.setText("Phone: Not Available");
                    holder.myReportFirPhone.setTextColor(Color.parseColor("#DB4437"));
                }
                if(relation.equals("")){
                    holder.myReportFirRelation.setText("Relation: Not Available");
                    holder.myReportFirRelation.setTextColor(Color.parseColor("#DB4437"));
                }
                if(image.equals("")){
                    Picasso.get().load(R.drawable.user_profile).into(holder.myReportFirImage);
                    holder.myReportFirImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

                }else{
                    Picasso.get().load(model.getFirImage()).into(holder.myReportFirImage);
                    holder.myReportFirImage.setScaleType(ImageView.ScaleType.FIT_XY);

                }



            }

            @NonNull
            @Override
            public FirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_fir_item, parent, false);
                FirViewHolder holder = new FirViewHolder(view);
                return holder;
            }
        };
        myFirReportList.setAdapter(adapter);
        adapter.startListening();



    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}