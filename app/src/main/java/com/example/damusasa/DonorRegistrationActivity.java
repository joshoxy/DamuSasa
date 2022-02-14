package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damusasa.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorRegistrationActivity extends AppCompatActivity {

    private TextView backButton;
    private CircleImageView profile_image;
    private TextInputEditText reg_fullname, reg_IdNum,reg_phone,reg_Email, reg_pass;
    private Spinner bloodGroupsSpinner;
    private Button registerButton;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    //Tutorial part
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonorRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        profile_image = findViewById(R.id.profile_image);
        reg_fullname = findViewById(R.id.reg_fullname);
        reg_IdNum = findViewById(R.id.reg_IdNum);
        reg_phone = findViewById(R.id.reg_phone);
        reg_Email = findViewById(R.id.reg_Email);
        reg_pass = findViewById(R.id.reg_pass);
        bloodGroupsSpinner = findViewById(R.id.bloodGroupsSpinner);
        registerButton = findViewById(R.id.registerButton);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        //Tutorial part
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Allow user to pick profile photo from gallery
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //Validate user's input
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email =  reg_Email.getText().toString().trim();
                final String fullName =  reg_fullname.getText().toString().trim();
                final String IdNum =  reg_IdNum.getText().toString().trim();
                final String phoneNumber =  reg_phone.getText().toString().trim();
                final String password =  reg_pass.getText().toString().trim();
                final String bloodGroup = bloodGroupsSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(email)){
                    reg_Email.setError("Email Address is required!");
                    return;
                }
                if(TextUtils.isEmpty(fullName)){
                    reg_fullname.setError("Full Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(IdNum)){
                    reg_IdNum.setError("Id Number is required!");
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    reg_phone.setError("Phone Number is required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    reg_pass.setError("Password is required!");
                    return;
                }
                if(bloodGroup.equals("Select your blood group")){
                    Toast.makeText(DonorRegistrationActivity.this, "Select a blood group", Toast.LENGTH_SHORT).show();
                    return;
                }

                else{
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(DonorRegistrationActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserId);
                                userInfo.put("name",fullName);
                                userInfo.put("email",email);
                                userInfo.put("idNumber",IdNum);
                                userInfo.put("phoneNumber",phoneNumber);
                                userInfo.put("bloodGroup",bloodGroup);
                                userInfo.put("type","donor");
                                userInfo.put("search","donor"+bloodGroup);


                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(DonorRegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(DonorRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        finish();
                                        //loader.dismiss();
                                    }
                                });


                                //Upload Image to firebase
                                if (resultUri != null){
                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                                            .child("profile images").child(currentUserId);
                                    Bitmap bitmap = null;

                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);

                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filePath.putBytes(data);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonorRegistrationActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null){
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        Map newImageMap = new HashMap();
                                                        newImageMap.put("profilepictureurl", imageUrl);
                                                        userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(DonorRegistrationActivity.this, "Image url added to db successfully", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    Toast.makeText(DonorRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                        finish();
                                                    }
                                                });

                                                Intent intent = new Intent(DonorRegistrationActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                                loader.dismiss();

                                            }
                                        }
                                    });
                                }
                                //end of upload image code
                            }
                        }
                    });

                }
            }
        });
    }

   //Original On ActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            resultUri = data.getData();

            profile_image.setImageURI(resultUri);

            //Tutorial method for picture
            /*uploadPicture();*/
        }
    }

    //Upload picture method from tutorial
  /*  private void uploadPicture() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        // Create a reference to "mountains.jpg"
        //His filePath is my resultUri
        FirebaseStorage imgstorage = FirebaseStorage.getInstance();
        StorageReference imageRef = imgstorage.getReference("images/" +new Random().nextInt(50));

        imageRef.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                User obj = new User(uri.toString());
                               userDatabaseRef.setValue(uri.toString());
//                                profile_image.setImageResource(R.drawable.ic_profile);

                            }
                        });
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Image Failed to Upload", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });

    }*/
}