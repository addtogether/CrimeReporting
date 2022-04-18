package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.crimereporting.Model.Fir;
import com.example.crimereporting.Model.Live;
import com.example.crimereporting.ViewHolder.FirViewHolder;
import com.example.crimereporting.ViewHolder.LiveViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DisplayLiveReportsAdminActivity extends AppCompatActivity {

    private DatabaseReference displayReportAdminRef;
    private RecyclerView displayReportsAdminList;
    private String reportType = "";
    private String address = "", nearestStation = "", phone = "", relation = "", image = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_live_reports_admin);

        reportType = getIntent().getStringExtra("reportType");


        displayReportAdminRef = FirebaseDatabase.getInstance().getReference().child(reportType);


        displayReportsAdminList = findViewById(R.id.displayLiveReportsAdminList);
        displayReportsAdminList.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Live Crime Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Live> options = new FirebaseRecyclerOptions.Builder<Live>()
                .setQuery(displayReportAdminRef, Live.class).build();

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

                relation = model.getLiveUserRelation();
                image = model.getLiveImage();

                if (relation.equals("")) {
                    holder.myReportLiveRelation.setText("Relation: Not Available");
                    holder.myReportLiveRelation.setTextColor(Color.parseColor("#DB4437"));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String liveNum = model.getLiveNumber();
                        String userLat = model.getUserLatitude();
                        String userLon = model.getUserLongitude();
                        String userName = model.getUserName();
                        String id = model.getLiveNumber();

                        CharSequence options[] = new CharSequence[]
                                {
                                        "Status - PENDING",
                                        "Status - ACCEPTED",
                                        "Status - SOLVED",
                                        "Status - REJECTED",
                                        "TRACK - LOCATION",
                                        "DELETE - REPORT",
                                        "Cancel"
                                };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Select Option : ");


                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are you Sure you want to change status to PENDING ?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                progressDialog = new ProgressDialog(view.getContext());
                                                progressDialog.setTitle("  Changing Status");
                                                progressDialog.setMessage("Please wait, while we are changing status...");
                                                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                                                progressDialog.setIcon(R.drawable.logo);
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child(reportType);
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("liveStatus", "PENDING");

                                                state.child(liveNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayLiveReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                            if (i == 1) {
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();

                                }
                                if (i == 1) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are you Sure you want to change status to ACCEPTED ?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                progressDialog = new ProgressDialog(view.getContext());
                                                progressDialog.setTitle("  Changing Status");
                                                progressDialog.setMessage("Please wait, while we are changing status...");
                                                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                                                progressDialog.setIcon(R.drawable.logo);
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child(reportType);
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("liveStatus", "ACCEPTED");

                                                state.child(liveNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayLiveReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                            if (i == 1) {
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();

                                }
                                if (i == 2) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are you Sure you want to change status to SOLVED ?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                progressDialog = new ProgressDialog(view.getContext());
                                                progressDialog.setTitle("  Changing Status");
                                                progressDialog.setMessage("Please wait, while we are changing status...");
                                                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                                                progressDialog.setIcon(R.drawable.logo);
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child(reportType);
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("liveStatus", "SOLVED");

                                                state.child(liveNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayLiveReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                            if (i == 1) {
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();


                                }
                                if (i == 3) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are you Sure you want to change status to REJECTED ?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                progressDialog = new ProgressDialog(view.getContext());
                                                progressDialog.setTitle("  Changing Status");
                                                progressDialog.setMessage("Please wait, while we are changing status...");
                                                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                                                progressDialog.setIcon(R.drawable.logo);
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child(reportType);
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("liveStatus", "REJECTED");

                                                state.child(liveNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayLiveReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayLiveReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                            if (i == 1) {
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();


                                }
                                if (i == 4) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are you Sure you want to track location of user ?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {

                                                Intent intent = new Intent(DisplayLiveReportsAdminActivity.this,FirMapsActivity.class);
                                                intent.putExtra("latitude",userLat);
                                                intent.putExtra("longitude",userLon);
                                                intent.putExtra("userName",userName);
                                                startActivity(intent);

                                            }
                                            if (i == 1) {
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();


                                }
                                if (i == 5) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are you Sure you want to Delete this Report ?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {


                                                progressDialog = new ProgressDialog(view.getContext());
                                                progressDialog.setTitle("  Deleting Report");
                                                progressDialog.setMessage("Please wait, while we are deleting report...");
                                                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                                                progressDialog.setIcon(R.drawable.logo);
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                removeReport(id);
                                            }
                                            if (i == 1) {
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();

                                }

                                if (i == 6) {
                                    dialogInterface.dismiss();

                                }

                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public LiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_live_item, parent, false);
                LiveViewHolder holder = new LiveViewHolder(view);
                return holder;
            }
        };
        displayReportsAdminList.setAdapter(adapter);
        adapter.startListening();

    }

    private void removeReport(String id) {


        DatabaseReference delReport = FirebaseDatabase.getInstance().getReference().child(reportType).child(id);

        delReport.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(DisplayLiveReportsAdminActivity.this, "Report Removed Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(DisplayLiveReportsAdminActivity.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}