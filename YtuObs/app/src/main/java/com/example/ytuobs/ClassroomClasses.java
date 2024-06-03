package com.example.ytuobs;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ClassroomClasses extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private FirebaseUser mUser;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String email;
    private List<String> courseList;
    private ClassroomClassAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_classes);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            Toast.makeText(this, "Kullanıcı oturumu açık değil", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        email = mUser.getEmail();
        courseList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.ClassroomClassesRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ClassroomClassAdapter(this, courseList, this::onCourseItemClick);
        mRecyclerView.setAdapter(mAdapter);

        // Bildirim iznini kontrol et ve iste
        checkNotificationPermission();

        fetchCourses();
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // İzin verilmemişse, kullanıcıdan izin iste
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, bildirim gönderebilirsiniz
                Toast.makeText(this, "Bildirim izni verildi", Toast.LENGTH_SHORT).show();
            } else {
                // İzin reddedildi, kullanıcıya bilgi ver
                Toast.makeText(this, "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchCourses() {
        if (email.endsWith("@std.yildiz.edu.tr")) {
            mFirestore.collection("Students").document(email).collection("student-courses")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    courseList.add(document.getId());
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Hata oluştu, kurslar alınamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            mFirestore.collection("Instructors").document(email).collection("Courses")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    courseList.add(document.getId());
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Hata oluştu, kurslar alınamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void onCourseItemClick(String courseId) {
        Intent intent = new Intent(ClassroomClasses.this, Classroom.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }
}