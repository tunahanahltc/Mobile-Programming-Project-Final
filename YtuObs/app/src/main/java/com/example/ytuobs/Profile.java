package com.example.ytuobs;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    EditText name, telNo, educate, mail, studentId;
    TextView statu, studentIdTextView;
    Button saveButton, editButton;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageButton menu_button;
    FirebaseFirestore firestore;
    String currentUserMail;
    DocumentReference docRef;
    private ImageView profileImageView;
    private FirebaseStorage storage;
    ImageButton captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_inform);

        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        statu = findViewById(R.id.statu);
        studentIdTextView = findViewById(R.id.studentIdTextView);
        name = findViewById(R.id.editTextName);
        telNo = findViewById(R.id.editTextPhone);
        educate = findViewById(R.id.editTextEducation);
        mail = findViewById(R.id.editTextEmail);
        studentId = findViewById(R.id.editTextStudentId);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(); // FirebaseStorage'ı initialize etme
        telNo.setEnabled(false);
        educate.setEnabled(false);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        currentUserMail = (String) getIntent().getSerializableExtra("currentUserMail");

        profileImageView = findViewById(R.id.profileImageView);
        captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        if (!currentUserMail.equals(mUser.getEmail())) {
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            setInformationProfile(currentUserMail);
        } else {
            setInformationProfile(mUser.getEmail());
        }

        if (currentUserMail.endsWith("@std.yildiz.edu.tr")) {
            studentId.setVisibility(View.VISIBLE);
            studentIdTextView.setVisibility(View.VISIBLE);
        } else if (currentUserMail.endsWith("@yildiz.edu.tr")) {
            studentId.setVisibility(View.GONE);
            studentIdTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!currentUserMail.equals(mUser.getEmail())) {
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            setInformationProfile(currentUserMail);
        } else {
            setInformationProfile(mUser.getEmail());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editButton) {
            telNo.setEnabled(true);
            educate.setEnabled(true);
            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.saveButton) {
            telNo.setEnabled(false);
            educate.setEnabled(false);
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
            setFirebaseData(telNo.getText().toString(), educate.getText().toString());
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Kamera izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileImageView.setImageBitmap(imageBitmap);
            uploadImageToFirebase(imageBitmap);
        }
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Sıkıştırma oranını değiştirerek fotoğraf kalitesini ayarlayabiliriz
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // %100 sıkıştırma oranı
        byte[] data = baos.toByteArray();

        StorageReference storageRef = storage.getReference().child("profile_images/" + mUser.getEmail() + ".jpg");
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception ->
                Toast.makeText(Profile.this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show()
        ).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                docRef.update("profileImageUrl", downloadUrl);
            });
        });
    }

    public void setInformationProfile(String e_mail) {
        if (e_mail.contains("@std.yildiz.edu.tr")) {
            docRef = firestore.collection("Students").document(e_mail);
        } else if (e_mail.contains("@yildiz.edu.tr")) {
            docRef = firestore.collection("Instructors").document(e_mail);
        }

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                statu.setText(documentSnapshot.get("statu").toString());
                educate.setText(documentSnapshot.get("educateInform").toString());
                mail.setText(documentSnapshot.get("eMail").toString());
                telNo.setText(documentSnapshot.get("telNo").toString());
                name.setText(documentSnapshot.get("isim").toString());

                if (e_mail.contains("@std.yildiz.edu.tr")) {
                    studentId.setText(documentSnapshot.get("studentNo").toString());
                }

                String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    new ImageDownloaderTask(profileImageView).execute(profileImageUrl);
                }
            }
        });
    }

    public void setFirebaseData(String telno, String educateInform) {
        if (mail.getText().toString().endsWith("@std.yildiz.edu.tr")) {
            docRef = firestore.collection("Students").document(mail.getText().toString());
            docRef.update(
                            "telNo", telno,
                            "educateInform", educateInform)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Başarılı güncelleme işlemi
                            Log.d("Firestore", "Document successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Güncelleme işlemi başarısız oldu
                            Log.w("Firestore", "Error updating document", e);
                        }
                    });
        } else if (mail.getText().toString().endsWith("@yildiz.edu.tr")) {
            docRef = firestore.collection("Instructors").document(mail.getText().toString());
            docRef.update(
                            "telNo", telno,
                            "educateInform", educateInform)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Başarılı güncelleme işlemi
                            Log.d("Firestore", "Document successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Güncelleme işlemi başarısız oldu
                            Log.w("Firestore", "Error updating document", e);
                        }
                    });
        }
    }
}

class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;

    public ImageDownloaderTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlString = params[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            imageView.setImageBitmap(result);
        }
    }
}