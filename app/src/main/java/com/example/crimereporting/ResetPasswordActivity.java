package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crimereporting.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextView titleQuestion;
    private EditText phoneNumber,question1,question2;
    private Button verifyBtn;
    private ProgressDialog progressDialog,progressDialog1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        titleQuestion=findViewById(R.id.title_question);
        phoneNumber=findViewById(R.id.phone_number);
        question1=findViewById(R.id.question1);
        question2=findViewById(R.id.question2);
        verifyBtn=findViewById(R.id.verify_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog1= new ProgressDialog(this);


        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
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

            phoneNumber.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();

                }
            });

    }

    private void verifyUser() {
        progressDialog.setTitle("Verifying User");
        progressDialog.setMessage("Please wait while we are verifying the user !!!");
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
        progressDialog.setIcon(R.drawable.logo);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if(!phone.equals("") && !answer1.equals("") && !answer2.equals("")) {

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        String ans1 = dataSnapshot.child("answer1").getValue().toString();
                        String ans2 = dataSnapshot.child("answer2").getValue().toString();

                        if (!ans1.equals(answer1)) {
                            progressDialog.dismiss();
                            Toast.makeText(ResetPasswordActivity.this, "Your 1st Answer is Incorrect", Toast.LENGTH_SHORT).show();
                        } else if (!ans2.equals(answer2)) {
                            progressDialog.dismiss();
                            Toast.makeText(ResetPasswordActivity.this, "Your 2nd Answer is Incorrect", Toast.LENGTH_SHORT).show();
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                            builder.setTitle("New Password");

                            final EditText newPassword = new EditText(ResetPasswordActivity.this);
                            newPassword.setHint("Enter new Password here");
                            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            newPassword.setMaxLines(1);
                            builder.setView(newPassword);

                            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!newPassword.getText().toString().equals("")) {
                                        ref.child("password").setValue(newPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            progressDialog.dismiss();
                                                            Toast.makeText(ResetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }

                                                    }
                                                });
                                    }else{
                                        Toast.makeText(ResetPasswordActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    progressDialog.dismiss();

                                }
                            });
                            builder.show();
                        }

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(ResetPasswordActivity.this, "User with this phone number doesn't exists", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            progressDialog.dismiss();
            Toast.makeText(this, "Please fill every details", Toast.LENGTH_SHORT).show();
        }


    }
}