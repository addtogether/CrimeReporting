package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private String name="", address="", email="", password="", phone="", ques1="", ques2="";
    private PinView pinFromUser;
    private String codeBySystem;
    private TextView phoneNumDisplayTv;
    private Button verifyOtpBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        ques1 = getIntent().getStringExtra("ques1");
        ques2 = getIntent().getStringExtra("ques2");

        pinFromUser = findViewById(R.id.pin_view);
        phoneNumDisplayTv = findViewById(R.id.otp_phone_num_tv);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);

        phoneNumDisplayTv.setText(phone);

        sendVerificationCodeToUser(phone);

        verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = pinFromUser.getText().toString();
                if(!code.isEmpty()){
                    progressDialog.setTitle("Verifying OTP");
                    progressDialog.setMessage("Please wait while we are verifying OTP !!!");
                    progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                    progressDialog.setIcon(R.drawable.logo);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    verifyCode(code);
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(OtpActivity.this, "Please enter OTP !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCodeToUser(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,// Activity (for callback binding)
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinFromUser.setText(code);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(OtpActivity.this, "Something Went Wrong !!! Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    progressDialog.dismiss();
                    Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }


            };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final DatabaseReference RootRef;
                            RootRef = FirebaseDatabase.getInstance().getReference();
                            final String phone1 = phone.substring(3);

                            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!(dataSnapshot.child("Users").child(phone1).exists())) {
                                        HashMap<String, Object> userdataMap = new HashMap<>();
                                        userdataMap.put("phone", phone1);
                                        userdataMap.put("password", password);
                                        userdataMap.put("name", name);
                                        userdataMap.put("address",address);
                                        userdataMap.put("email", email);
                                        userdataMap.put("answer1",ques1);
                                        userdataMap.put("answer2",ques2);

                                        RootRef.child("Users").child(phone1).updateChildren(userdataMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            mAuth.createUserWithEmailAndPassword(email,phone1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                                    if (task.isSuccessful()) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(OtpActivity.this, "Congratulations, your account has been created", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                        startActivity(intent);
                                                                        finish();
//
                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(OtpActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(OtpActivity.this, "Network Issue, Please try again later.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(OtpActivity.this, "This " + phone + "already exists.", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(OtpActivity.this, "Please try again with another Phone Number.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });

                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                progressDialog.dismiss();
                                Toast.makeText(OtpActivity.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(OtpActivity.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


}