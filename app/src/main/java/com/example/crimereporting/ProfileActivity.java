package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.crimereporting.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileActivity extends AppCompatActivity {

    private EditText name,phone,address,password,email,ques1,ques2;
    private Button updateBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressDialog = new ProgressDialog(this);

        name = findViewById(R.id.ProfileRegisterName);
        phone = findViewById(R.id.ProfileRegisterPhone);
        password = findViewById(R.id.ProfileRegisterPassword);
        phone.setEnabled(false);
        address = findViewById(R.id.ProfileRegisterAddress);
        email = findViewById(R.id.ProfileRegisterEmail);
        ques1 = findViewById(R.id.ProfileSecQues1);
        ques2 = findViewById(R.id.ProfileSecQues2);
        updateBtn = findViewById(R.id.updateBtn);


        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]=new CharSequence[]
                        {
                                "Yes",
                                "No"
                        };
                final AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Are you sure, you want to update the Profile? ");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i==0){

                            String userName = name.getText().toString();
                            String userAddress = address.getText().toString();
                            String userPassword = password.getText().toString();
                            String userEmail = email.getText().toString();
                            String answer1 = ques1.getText().toString().toLowerCase();
                            String answer2 = ques2.getText().toString().toLowerCase();

                            if(TextUtils.isEmpty(userName) && TextUtils.isEmpty(userAddress) && TextUtils.isEmpty(userPassword) && TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(answer1) && TextUtils.isEmpty(answer2)){
                                Toast.makeText(ProfileActivity.this, "Please provide each details !!!", Toast.LENGTH_SHORT).show();
                            }else if(TextUtils.isEmpty(userName)){
                                Toast.makeText(ProfileActivity.this, "Please provide User Name", Toast.LENGTH_SHORT).show();
                            }else if(TextUtils.isEmpty(userAddress)){
                                Toast.makeText(ProfileActivity.this, "Please provide User Address", Toast.LENGTH_SHORT).show();
                            }else if(TextUtils.isEmpty(userPassword)){
                                Toast.makeText(ProfileActivity.this, "Please provide Password", Toast.LENGTH_SHORT).show();
                            }else if(TextUtils.isEmpty(userEmail)){
                                Toast.makeText(ProfileActivity.this, "Please provide Email Id", Toast.LENGTH_SHORT).show();
                            }else if(TextUtils.isEmpty(answer1)){
                                Toast.makeText(ProfileActivity.this, "Please provide Security Question 1", Toast.LENGTH_SHORT).show();
                            }else if(TextUtils.isEmpty(answer2)){
                                Toast.makeText(ProfileActivity.this, "Please provide Security Question 2", Toast.LENGTH_SHORT).show();
                            }else{
                                progressDialog.setTitle("  Updating Account Info");
                                progressDialog.setMessage("Dear User please wait, while we are updating your profile...");
                                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                                progressDialog.setIcon(R.drawable.logo);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                updateOnlyUserInfo(userName,userAddress,userPassword,userEmail,answer1,answer2);
                            }

                        }
                        if(i==1){
                            dialogInterface.dismiss();
                        }

                    }
                });
                builder.show();

            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateOnlyUserInfo(String userName, String userAddress, String userPassword, String userEmail, String answer1, String answer2) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", userName);
        userMap. put("address", userAddress);
        userMap. put("password", userPassword);
        userMap. put("email", userEmail);
        userMap. put("answer1", answer1);
        userMap. put("answer2", answer2);

        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    Toast.makeText(ProfileActivity.this, "Profile Info Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String uPhone = dataSnapshot.child("phone").getValue().toString();
                    String uName = dataSnapshot.child("name").getValue().toString();
                    String uPassword = dataSnapshot.child("password").getValue().toString();
                    String uAddress = dataSnapshot.child("address").getValue().toString();
                    String uEmail = dataSnapshot.child("email").getValue().toString();
                    String uAnswer1 = dataSnapshot.child("answer1").getValue().toString();
                    String uAnswer2 = dataSnapshot.child("answer2").getValue().toString();

                    phone.setText(uPhone);
                    name.setText(uName);
                    address.setText(uAddress);
                    password.setText(uPassword);
                    email.setText(uEmail);
                    ques1.setText(uAnswer1);
                    ques2.setText(uAnswer2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}