package com.example.crimereporting.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crimereporting.Interface.ItemClickListener;
import com.example.crimereporting.R;

public class FirViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView myReportFirNum, myReportFirName, myReportFirAddress, myReportFirNearestPoliceStation, myReportFirPhone, myReportFirRelation, myReportFirReport, myReportFirDateTime, myReportFirStatus, myReportFirUserPhone;
    public ImageView myReportFirImage;
    public ItemClickListener listener;


    public FirViewHolder(@NonNull View itemView) {
        super(itemView);

        myReportFirNum=itemView.findViewById(R.id.myReportFirNum);
        myReportFirName=itemView.findViewById(R.id.myReportFirName);
        myReportFirAddress=itemView.findViewById(R.id.myReportFirAddress);
        myReportFirNearestPoliceStation=itemView.findViewById(R.id.myReportFirNearestPoliceStation);
        myReportFirPhone=itemView.findViewById(R.id.myReportFirPhone);
        myReportFirRelation=itemView.findViewById(R.id.myReportFirRelation);
        myReportFirReport=itemView.findViewById(R.id.myReportFirReport);
        myReportFirDateTime=itemView.findViewById(R.id.myReportFirDateTime);
        myReportFirStatus=itemView.findViewById(R.id.myReportFirStatus);
        myReportFirImage=itemView.findViewById(R.id.myReportFirImage);
        myReportFirUserPhone=itemView.findViewById(R.id.myReportFirUserPhone);




    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener=listener;

    }
    @Override
    public void onClick(View v) {

    }
}
