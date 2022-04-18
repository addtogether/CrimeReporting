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
import com.example.crimereporting.ViewHolder.FirViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DisplayFirReportsAdminActivity extends AppCompatActivity {

    private DatabaseReference displayReportAdminRef;
    private RecyclerView displayReportsAdminList;
    private String reportType = "";
    private String address = "", nearestStation = "", phone = "", relation = "", image = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_fir_reports_admin);

        reportType = getIntent().getStringExtra("reportType");


        displayReportAdminRef = FirebaseDatabase.getInstance().getReference().child(reportType);


        displayReportsAdminList = findViewById(R.id.displayReportsAdminList);
        displayReportsAdminList.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fir Reports");
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

        FirebaseRecyclerOptions<Fir> options = new FirebaseRecyclerOptions.Builder<Fir>()
                .setQuery(displayReportAdminRef, Fir.class).build();

        FirebaseRecyclerAdapter<Fir, FirViewHolder> adapter = new FirebaseRecyclerAdapter<Fir, FirViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirViewHolder holder, int position, @NonNull final Fir model) {


                holder.myReportFirNum.setText("FIR NO: " + model.getFirNumber());
                holder.myReportFirName.setText("Accused Name: " + model.getFirAccName());
                holder.myReportFirAddress.setText("Address: " + model.getFirAccAddress() + ", " + model.getFirAccDistrict() + ", " + model.getFirAccState());
                holder.myReportFirNearestPoliceStation.setText("Police Station: " + model.getFirAccNearestPoliceStation());
                holder.myReportFirPhone.setText("Phone: " + model.getFirAccPhone());
                holder.myReportFirRelation.setText("Relation: " + model.getFirAccRelation());
                holder.myReportFirReport.setText("Report: " + model.getFirAccReport());
                holder.myReportFirDateTime.setText("Reported On: " + model.getFirDate() + "  " + model.getFirTime());
                holder.myReportFirUserPhone.setText("User Phone: " + model.getUserPhone());
                holder.myReportFirStatus.setText("STATUS: " + model.getFirStatus());

                address = model.getFirAccAddress();
                nearestStation = model.getFirAccNearestPoliceStation();
                phone = model.getFirAccPhone();
                relation = model.getFirAccRelation();
                image = model.getFirImage();

                if (address.equals("")) {
                    holder.myReportFirAddress.setText("Address: Not Available");
                    holder.myReportFirAddress.setTextColor(Color.parseColor("#DB4437"));
                }
                if (nearestStation.equals("")) {
                    holder.myReportFirNearestPoliceStation.setText("Police Station: Not Available");
                    holder.myReportFirNearestPoliceStation.setTextColor(Color.parseColor("#DB4437"));
                }
                if (phone.equals("")) {
                    holder.myReportFirPhone.setText("Phone: Not Available");
                    holder.myReportFirPhone.setTextColor(Color.parseColor("#DB4437"));
                }
                if (relation.equals("")) {
                    holder.myReportFirRelation.setText("Relation: Not Available");
                    holder.myReportFirRelation.setTextColor(Color.parseColor("#DB4437"));
                }
                if (image.equals("")) {
                    Picasso.get().load(R.drawable.user_profile).into(holder.myReportFirImage);
                    holder.myReportFirImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

                } else {
                    Picasso.get().load(model.getFirImage()).into(holder.myReportFirImage);
                    holder.myReportFirImage.setScaleType(ImageView.ScaleType.FIT_XY);

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String firNum = model.getFirNumber();
                        String userLat = model.getUserLatitude();
                        String userLon = model.getUserLongitude();
                        String userName = model.getUserName();
                        String id = model.getFirNumber();

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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Fir");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("firStatus", "PENDING");

                                                state.child(firNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayFirReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Fir");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("firStatus", "ACCEPTED");

                                                state.child(firNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayFirReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Fir");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("firStatus", "SOLVED");

                                                state.child(firNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayFirReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Fir");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("firStatus", "REJECTED");

                                                state.child(firNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayFirReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayFirReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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

                                                Intent intent = new Intent(DisplayFirReportsAdminActivity.this,FirMapsActivity.class);
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


                                }
                                if(i == 6){
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
            public FirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_fir_item, parent, false);
                FirViewHolder holder = new FirViewHolder(view);
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
                    Toast.makeText(DisplayFirReportsAdminActivity.this, "Report Removed Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(DisplayFirReportsAdminActivity.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}