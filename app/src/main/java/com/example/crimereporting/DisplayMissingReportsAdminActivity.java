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
import com.example.crimereporting.Model.Missing;
import com.example.crimereporting.Prevalent.Prevalent;
import com.example.crimereporting.ViewHolder.FirViewHolder;
import com.example.crimereporting.ViewHolder.MissingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DisplayMissingReportsAdminActivity extends AppCompatActivity {

    private DatabaseReference displayMissingReportAdminRef;
    private RecyclerView displayMissingReportsAdminList;
    private String reportType = "";
    private String address = "", nearestStation = "", phone = "", relation = "", image = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_missing_reports_admin);

        reportType = getIntent().getStringExtra("reportType");


        displayMissingReportAdminRef = FirebaseDatabase.getInstance().getReference().child(reportType);


        displayMissingReportsAdminList = findViewById(R.id.displayMissingReportsAdminList);
        displayMissingReportsAdminList.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Missing Reports");
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

        FirebaseRecyclerOptions<Missing> options = new FirebaseRecyclerOptions.Builder<Missing>()
                .setQuery(displayMissingReportAdminRef, Missing.class).build();

        FirebaseRecyclerAdapter<Missing, MissingViewHolder> adapter = new FirebaseRecyclerAdapter<Missing, MissingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MissingViewHolder holder, int position, @NonNull final Missing model) {


                holder.myReportMissingNum.setText("MISSING NO: " + model.getMissingNumber());
                holder.myReportMissingName.setText("Missing Person Name: " + model.getMissingPerName());
                holder.myReportMissingAddress.setText("Address: " + model.getMissingPerAddress() + ", " + model.getMissingPerState() + ", " + model.getMissingPerDistrict());
                holder.myReportMissingAge.setText("Age: " + model.getMissingPerAge());
                holder.myReportMissingNearestPoliceStation.setText("Police Station: " + model.getMissingPerNearestPoliceStation());
                holder.myReportMissingPhone.setText("Phone: " + model.getMissingPerPhone());
                holder.myReportMissingRelation.setText("Relation: " + model.getMissingPerRelation());
                holder.myReportMissingReport.setText("Report: " + model.getMissingPerReport());
                holder.myReportMissingFrom.setText("Missing from: " + model.getMissingPerDate());
                holder.myReportMissingDateTime.setText("Reported On: " + model.getMissingDate() + "  " + model.getMissingTime());
                holder.myReportMissingUserPhone.setText("User Phone: " + model.getUserPhone());
                holder.myReportMissingStatus.setText("STATUS: " + model.getMissingStatus());
                Picasso.get().load(model.getMissingPerImage()).into(holder.myReportMissingImage);


                phone = model.getMissingPerPhone();
                relation = model.getMissingPerRelation();
                image = model.getMissingPerImage();

                if (phone.equals("")) {
                    holder.myReportMissingPhone.setText("Phone: Not Available");
                    holder.myReportMissingPhone.setTextColor(Color.parseColor("#DB4437"));
                }
                if (relation.equals("")) {
                    holder.myReportMissingRelation.setText("Relation: Not Available");
                    holder.myReportMissingRelation.setTextColor(Color.parseColor("#DB4437"));
                }


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String missingNum = model.getMissingNumber();
                        String userLat = model.getUserLatitude();
                        String userLon = model.getUserLongitude();
                        String userName = model.getUserName();
                        String id = model.getMissingNumber();

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
                                    builder.setTitle("Are you Sure you want to change status to PENDING");

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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Missing");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("missingStatus", "PENDING");

                                                state.child(missingNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayMissingReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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
                                    builder.setTitle("Are you Sure you want to change status to ACCEPTED");

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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Missing");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("missingStatus", "ACCEPTED");

                                                state.child(missingNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayMissingReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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
                                    builder.setTitle("Are you Sure you want to change status to SOLVED");

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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Missing");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("missingStatus", "SOLVED");

                                                state.child(missingNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayMissingReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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
                                    builder.setTitle("Are you Sure you want to change status to REJECTED");

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

                                                DatabaseReference state = FirebaseDatabase.getInstance().getReference().child("Missing");
                                                HashMap<String, Object> statusMap = new HashMap<>();
                                                statusMap.put("missingStatus", "REJECTED");

                                                state.child(missingNum).updateChildren(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(DisplayMissingReportsAdminActivity.this, AdminHomeActivity.class));
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Status Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DisplayMissingReportsAdminActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
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

                                                Intent intent = new Intent(DisplayMissingReportsAdminActivity.this,MissingMapsActivity.class);
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
            public MissingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_missing_item, parent, false);
                MissingViewHolder holder = new MissingViewHolder(view);
                return holder;
            }
        };
        displayMissingReportsAdminList.setAdapter(adapter);
        adapter.startListening();

    }

    private void removeReport(String id) {


        DatabaseReference delReport = FirebaseDatabase.getInstance().getReference().child(reportType).child(id);

        delReport.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(DisplayMissingReportsAdminActivity.this, "Report Removed Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(DisplayMissingReportsAdminActivity.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}