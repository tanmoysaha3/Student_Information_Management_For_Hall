package com.example.simpleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText mProfileName, mProfilePhone, mProfileAddr, mProfileDepartment, mProfileStudentId, mProfileYear;
    ImageView mProfileImageV;
    Button mUpdateProfileButton;
    ImageButton back_button;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        String fullName = data.getStringExtra("fullName");
        String phone = data.getStringExtra("phone");
        String address = data.getStringExtra("address");
        String department = data.getStringExtra("department");
        String studentid = data.getStringExtra("studentid");
        String year = data.getStringExtra("year");

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        user=fAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();

        mProfileName=findViewById(R.id.changeName);
        mProfilePhone=findViewById(R.id.changePhone);
        mProfileAddr=findViewById(R.id.changeAddress);
        mProfileDepartment=findViewById(R.id.changeDepartment);
        mProfileStudentId=findViewById(R.id.changeStudentId);
        mProfileYear=findViewById(R.id.changeYear);
        mProfileImageV=findViewById(R.id.changePImage);
        back_button=findViewById(R.id.back_button);
        mUpdateProfileButton=findViewById(R.id.updateProfileButton);

        StorageReference profileRef=storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfileImageV);
            }
        });

        mProfileImageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
                Toast.makeText(EditProfile.this, "Profile Image clicked", Toast.LENGTH_SHORT).show();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mUpdateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProfileName.getText().toString().isEmpty() || mProfilePhone.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "One or more fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docRef=fStore.collection("users").document(user.getUid());
                Map<String,Object> edited =new HashMap<>();
                edited.put("phone",mProfilePhone.getText().toString());
                edited.put("fullName",mProfileName.getText().toString());
                edited.put("address",mProfileAddr.getText().toString());
                edited.put("department",mProfileDepartment.getText().toString());
                edited.put("studentid",mProfileStudentId.getText().toString());
                edited.put("year",mProfileYear.getText().toString());
                docRef.update((edited)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        finish();
                    }
                });
            }
        });

        mProfileName.setText(fullName);
        mProfilePhone.setText(phone);
        mProfileAddr.setText(address);
        mProfileDepartment.setText(department);
        mProfileStudentId.setText(studentid);
        mProfileYear.setText(year);


        Log.d(TAG,"onCreate: " + fullName + " " + phone);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1000){
            if(resultCode== Activity.RESULT_OK){
                Uri imageUri=data.getData();
                mProfileImageV.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef=storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mProfileImageV);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}