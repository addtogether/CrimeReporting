package com.example.crimereporting.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crimereporting.Interface.ItemClickListener;
import com.example.crimereporting.R;

public class MissingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView myReportMissingNum, myReportMissingName, myReportMissingAddress, myReportMissingAge, myReportMissingNearestPoliceStation, myReportMissingPhone, myReportMissingRelation, myReportMissingReport, myReportMissingDateTime, myReportMissingStatus, myReportMissingFrom, myReportMissingUserPhone;
    public ImageView myReportMissingImage;
    public ItemClickListener listener;


    public MissingViewHolder(@NonNull View itemView) {
        super(itemView);

        myReportMissingNum=itemView.findViewById(R.id.myReportMissingNum);
        myReportMissingName=itemView.findViewById(R.id.myReportMissingName);
        myReportMissingAddress=itemView.findViewById(R.id.myReportMissingAddress);
        myReportMissingAge=itemView.findViewById(R.id.myReportMissingAge);
        myReportMissingNearestPoliceStation=itemView.findViewById(R.id.myReportMissingNearestPoliceStation);
        myReportMissingPhone=itemView.findViewById(R.id.myReportMissingPhone);
        myReportMissingRelation=itemView.findViewById(R.id.myReportMissingRelation);
        myReportMissingReport=itemView.findViewById(R.id.myReportMissingReport);
        myReportMissingDateTime=itemView.findViewById(R.id.myReportMissingDateTime);
        myReportMissingStatus=itemView.findViewById(R.id.myReportMissingStatus);
        myReportMissingFrom=itemView.findViewById(R.id.myReportMissingFrom);
        myReportMissingImage=itemView.findViewById(R.id.myReportMissingImage);
        myReportMissingUserPhone=itemView.findViewById(R.id.myReportMissingUserPhone);



    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener=listener;

    }
    @Override
    public void onClick(View v) {

    }
}
