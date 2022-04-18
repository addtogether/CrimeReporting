package com.example.crimereporting.ViewHolder;

import android.app.Application;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crimereporting.Interface.ItemClickListener;
import com.example.crimereporting.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class LiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView myReportLiveNum, myReportLiveName, myReportLiveAddress, myReportLivePhone, myReportLiveRelation, myReportLiveReport, myReportLiveDateTime, myReportLiveStatus, myReportLiveUserPhone;
    public SimpleExoPlayer exoPlayer;
    public PlayerView myReportLiveImage;
    public ItemClickListener listener;


    public LiveViewHolder(@NonNull View itemView) {
        super(itemView);

        myReportLiveNum=itemView.findViewById(R.id.myReportLiveNum);
        myReportLiveName=itemView.findViewById(R.id.myReportLiveName);
        myReportLiveAddress=itemView.findViewById(R.id.myReportLiveAddress);
        myReportLivePhone=itemView.findViewById(R.id.myReportLivePhone);
        myReportLiveRelation=itemView.findViewById(R.id.myReportLiveRelation);
        myReportLiveReport=itemView.findViewById(R.id.myReportLiveReport);
        myReportLiveDateTime=itemView.findViewById(R.id.myReportLiveDateTime);
        myReportLiveStatus=itemView.findViewById(R.id.myReportLiveStatus);

        myReportLiveImage=itemView.findViewById(R.id.myReportLiveImage);
        myReportLiveUserPhone=itemView.findViewById(R.id.myReportLiveUserPhone);



    }


    public void setExoplayer(Application application , String video){

        myReportLiveImage = itemView.findViewById(R.id.myReportLiveImage);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(application).build();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(application);
        Uri videoUri = Uri.parse(video);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,dataSourceFactory,extractorsFactory,null,null);
        myReportLiveImage.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(false);



    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener=listener;

    }
    @Override
    public void onClick(View v) {

    }
}
