package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crimereporting.Model.Users;
import com.example.crimereporting.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText inputPhone, inputPassword;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;
    private String parentDbName = "Users";
    private TextView text1, text2, newUser, adminPanel, notAdminPanel, forgetPass;
    private Animation topAnim, middleAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_button);
        inputPhone = findViewById(R.id.InputPhone);
        inputPassword = findViewById(R.id.InputPassword);
        checkBox = findViewById(R.id.rememberMe);
        forgetPass = findViewById(R.id.forgetPass);
        adminPanel = findViewById(R.id.adminPanel);
        notAdminPanel = findViewById(R.id.notAdminPanel);
        progressDialog = new ProgressDialog(this);
        Paper.init(this);

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        newUser = findViewById(R.id.newUser);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        text1.setAnimation(topAnim);
        text2.setAnimation(middleAnim);
        newUser.setAnimation(bottomAnim);
        checkBox.setAnimation(middleAnim);
        loginBtn.setAnimation(middleAnim);
        adminPanel.setAnimation(bottomAnim);

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(i);
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);

            }
        });

        adminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Police Login");
                adminPanel.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                notAdminPanel.setVisibility(View.VISIBLE);
                forgetPass.setVisibility(View.INVISIBLE);
                notAdminPanel.setAnimation(bottomAnim);
                parentDbName = "Admins";
                inputPhone.setText("");
                inputPassword.setText("");
            }
        });

        notAdminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login");
                adminPanel.setVisibility(View.VISIBLE);
                notAdminPanel.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                forgetPass.setVisibility(View.VISIBLE);
                adminPanel.setAnimation(bottomAnim);
                parentDbName = "Users";
                inputPhone.setText("");
                inputPassword.setText("");
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = inputPhone.getText().toString();
                String password = inputPassword.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    inputPhone.setText("");
                    inputPhone.setError("Phone Number Required");
                    Toast.makeText(LoginActivity.this, "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(password)) {
                    inputPassword.setText("");
                    Toast.makeText(LoginActivity.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter valid credentials !!!", Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog.setTitle("Logging into your Account");
                    progressDialog.setMessage("Please wait while we are verifying credentials !!!");
                    progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
                    progressDialog.setIcon(R.drawable.logo);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    AllowAccessToAccount(phone, password);

                }

            }
        });
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if (checkBox.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                    Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone)) {
                        if (userData.getPassword().equals(password)) {
                            if (parentDbName.equals("Admins")) {

                                progressDialog.dismiss();
                                Intent i = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(i);
                            } else if (parentDbName.equals("Users")) {

                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUsers = userData;
                                startActivity(intent);


                            }
                        } else {
                            progressDialog.dismiss();
                            inputPassword.setText("");
                            Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        inputPhone.setText("");
                        Toast.makeText(LoginActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    inputPassword.setText("");
                    inputPhone.setText("");
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + " do not exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}