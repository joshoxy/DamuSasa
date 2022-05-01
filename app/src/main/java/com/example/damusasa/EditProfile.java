package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextInputEditText reg_fullname, reg_IdNum,reg_phone,reg_Email;
    private Button saveButton;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profile_image = findViewById(R.id.profile_image);
        reg_fullname = findViewById(R.id.reg_fullname);
        reg_IdNum = findViewById(R.id.reg_IdNum);
        reg_phone = findViewById(R.id.reg_phone);
        reg_Email = findViewById(R.id.reg_Email);
        saveButton = findViewById(R.id.saveButton);
        loader = new ProgressDialog(this);
        userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Display Existing values to fields
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reg_fullname.setText(snapshot.child("name").getValue().toString());
                reg_IdNum.setText(snapshot.child("idNumber").getValue().toString());
                reg_phone.setText(snapshot.child("phoneNumber").getValue().toString());
                reg_Email.setText(snapshot.child("email").getValue().toString());

                if (snapshot.hasChild("profilepictureurl")){
                    //Fetch image from firebase using Glider dependency
                    Glide.with(getApplicationContext()).load(snapshot.child("profilepictureurl").getValue().toString()).into(profile_image);
                }else{
                    profile_image.setImageResource(R.drawable.profile_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = reg_fullname.getText().toString().trim();
                String idNumber = reg_IdNum.getText().toString().trim();
                String phoneNumber = reg_phone.getText().toString().trim();
                String email = reg_Email.getText().toString().trim();

                loader.setMessage("Please wait...");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                userDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap saveInfo = new HashMap();
                        saveInfo.put("name", name);
                        saveInfo.put("idNumber", idNumber);
                        saveInfo.put("phoneNumber", phoneNumber);
                        saveInfo.put("email", email);

                        //Profile picture
                        if (resultUri != null){
                            final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                                    .child("profile images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                                    Toast.makeText(EditProfile.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
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
                                                            Toast.makeText(EditProfile.this, "Profile photo set successfully", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(EditProfile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                                finish();
                                            }
                                        });

                                    }
                                }
                            });
                        }

                        userDatabaseRef.updateChildren(saveInfo).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(EditProfile.this, "Successfully changed", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(EditProfile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }

                                finish();
                                //loader.dismiss();
                            }
                        });

                        //Add Intent here
                        Intent intent2 = new Intent(EditProfile.this, ProfileActivity.class);
                        startActivity(intent2);
                        finish();
                        loader.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //end of onClick
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);
        }
    }
}