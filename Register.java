package com.example.simpleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mPhone, mStudentID;
    Button mRegisterButton;
    TextView mLoginTextButton;
    ProgressBar mProgressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ImageButton back_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName=findViewById(R.id.fullName);
        mEmail=findViewById(R.id.Email);
        mPassword=findViewById(R.id.Password);
        mPhone=findViewById(R.id.Phone);
        mStudentID=findViewById(R.id.StudentID);
        back_button = findViewById(R.id.back_button);

        mRegisterButton=findViewById(R.id.registerButton);
        mLoginTextButton=findViewById(R.id.loginText);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        mProgressBar=findViewById(R.id.registerProgressBar);

        if(fAuth.getCurrentUser() !=null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                String studentID=mStudentID.getText().toString().trim();
                String fullName = mFullName.getText().toString();
                String phone = mPhone.getText().toString();

                String split_second = email.substring(email.indexOf("@")+1);

                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }

                if((!"student.just.edu.bd".equals(split_second)) && (!"just.edu.bd".equals(split_second))) {
                    mEmail.setError("Enter university email");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Set Password");
                    return;
                }

                if(password.length()<8) {
                    mPassword.setError("Password length at least 8");
                    return;
                }

                if(TextUtils.isEmpty(studentID)) {
                    mStudentID.setError("Student ID is missing");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                //register the user in Firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {


                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();

                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fullName",fullName);
                            user.put("email",email);
                            user.put("studentID",studentID);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","onSuccess : user profile is created for " + userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else {
                            Toast.makeText(Register.this, "Error!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility((View.GONE));
                        }
                    }
                });
            }
        });

        mLoginTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });




        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}