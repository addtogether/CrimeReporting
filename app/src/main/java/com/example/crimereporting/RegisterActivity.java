package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText InputName, InputAddress, InputEmail, InputPassword, InputPhone, InputSecQues1, InputSecQues2;
    private ProgressDialog progressDialog;
    private TextView alreadyUser;
    private CountryCodePicker countryCodePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);

        InputEmail = findViewById(R.id.RegisterEmail);
        InputPassword = findViewById(R.id.RegisterPassword);
        InputName = findViewById(R.id.RegisterName);
        InputAddress = findViewById(R.id.RegisterAddress);
        InputPhone = findViewById(R.id.RegisterPhone);
        InputSecQues1 = findViewById(R.id.secQues1);
        InputSecQues2 = findViewById(R.id.secQues2);





        countryCodePicker = findViewById(R.id.country_code_picker);
        registerBtn = findViewById(R.id.create_button);
        alreadyUser = findViewById(R.id.alreadyUser);
        progressDialog = new ProgressDialog(this);


        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = InputName.getText().toString().trim();
                String address = InputAddress.getText().toString().trim();
                String email = InputEmail.getText().toString();
                String pass = InputPassword.getText().toString();
                String ques1 = InputSecQues1.getText().toString().toLowerCase();
                String ques2 = InputSecQues2.getText().toString().toLowerCase();
                String passwordVal = "^" +
                        //"(?=.*[0-9])" +         //at least 1 digit
                        //"(?=.*[a-z])" +         //at least 1 lower case letter
                        //"(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        "(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{4,}" +               //at least 4 characters
                        "$";
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                String phone = InputPhone.getText().toString().trim();
                //Remove first zero if entered!
//                if (phone.charAt(0) == '0') {
//                    phone = phone.substring(1);
//                }else{
//
//                }
                //Complete phone number
                final String phoneNum = "+" + countryCodePicker.getFullNumber() + phone;

                if(TextUtils.isEmpty(phone) && TextUtils.isEmpty(email) && TextUtils.isEmpty(name) && TextUtils.isEmpty(address) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(ques1) && TextUtils.isEmpty(ques2)){
                    Toast.makeText(RegisterActivity.this, "Please fill every details...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phone)){
                    InputPhone.setError("Phone Required");
                }
                else if(phone.length()!=10){
                    InputPhone.setError("Phone Number must be of 10 digits");
                }
                else if(TextUtils.isEmpty(name)){
                    InputName.setError("Name Required");
                }
                else if(TextUtils.isEmpty(address)){
                    InputAddress.setError("Address Required");
                }
                else if(TextUtils.isEmpty(email)){
                    InputEmail.setError("Email Required");
                }
                else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(RegisterActivity.this, "Password Required", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(ques1)){
                    InputSecQues1.setError("Answer Required");
                }
                else if(TextUtils.isEmpty(ques2)){
                    InputSecQues2.setError("Answer Required");
                }
                else if (!pass.matches(passwordVal)) {
                    Toast.makeText(RegisterActivity.this, "Password is too weak, must contain a special character", Toast.LENGTH_SHORT).show();
                }
                else if (!email.matches(emailPattern)) {
                    InputEmail.setError("Please enter a valid Email Id");
                }
                else{
                    progressDialog.setTitle("Verifying Credentials");
                    progressDialog.setMessage("Please wait while we are verifying credentials !!!");
                    progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                    progressDialog.setIcon(R.drawable.logo);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    createUser(name, address, email, phone, pass, ques1, ques2);

                }
            }
        });
    }

    private void createUser(String name, String address, String email, String phone, String pass, String ques1, String ques2) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists())) {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", pass);
                    userdataMap.put("name", name);
                    userdataMap.put("address",address);
                    userdataMap.put("email", email);
                    userdataMap.put("answer1",ques1);
                    userdataMap.put("answer2",ques2);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Issue, Please try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "This " + phone + "already exists.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, "Please try again with another Phone Number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}