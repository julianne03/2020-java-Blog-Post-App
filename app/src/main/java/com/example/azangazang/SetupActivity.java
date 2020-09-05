package com.example.azangazang;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {

    private Uri mainImageURI = null;
    private CircularImageView profile_image;

    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setup_progress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;   //사용자 이메일 가져와서 닉네임에 써주기 위해
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.show();
        ab.setTitle("계정 설정");
        ab.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        profile_image = findViewById(R.id.profile_image);
        setupName = findViewById(R.id.setup_name_text);
        setupBtn = findViewById(R.id.setup_btn);
        setup_progress = findViewById(R.id.setup_progress);

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_name = setupName.getText().toString();

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {
                    final String user_id = firebaseAuth.getCurrentUser().getUid();
                    setup_progress.setVisibility(View.VISIBLE);

                    final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                    UploadTask uploadTask = image_path.putFile(mainImageURI);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return image_path.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {

                                Uri download_uri = task.getResult();

                                Map<String, String> userMap = new HashMap<>();
                                userMap.put("name", user_name);
                                userMap.put("image", download_uri.toString());

                                firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(SetupActivity.this, "변경 내용이 성공적으로 업데이트 되었습니다.", Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(SetupActivity.this, "Firestore Error: " + error, Toast.LENGTH_LONG).show();
                                        }
                                        setup_progress.setVisibility(View.INVISIBLE);
                                    }
                                });
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "Image Error: " + error, Toast.LENGTH_LONG).show();
                                setup_progress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(SetupActivity.this, "권한이 거부되었습니다.", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });

    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                profile_image.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}