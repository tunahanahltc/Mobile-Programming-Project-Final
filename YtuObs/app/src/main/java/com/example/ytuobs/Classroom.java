package com.example.ytuobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class Classroom extends AppCompatActivity implements ClassroomAnnouncementAdapter.OnItemClickListener {

    private FirebaseUser mUser;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String email;
    private List<Announcement> announcementList;
    private ClassroomAnnouncementAdapter mAdapter;
    private RelativeLayout classroomDuyuruLayout;
    private EditText announcementEditText;
    private Button announcementButton;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_classroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        classroomDuyuruLayout = findViewById(R.id.ClassroomDuyuru);
        announcementEditText = findViewById(R.id.editTextTextMultiLine2);
        announcementButton = findViewById(R.id.button);
        mRecyclerView = findViewById(R.id.recyclerView2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        email = mUser.getEmail();

        // Kullanıcı türüne göre duyuru görünürlüğünü ayarla
        checkUserTypeAndSetVisibility();

        // Intent ile gelen courseId'yi al
        courseId = getIntent().getStringExtra("courseId");

        announcementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAnnouncement();
            }
        });

        // Duyuruları çek ve RecyclerView'e ekle
        fetchAnnouncements();
    }

    private void checkUserTypeAndSetVisibility() {
        if (email.endsWith("@std.yildiz.edu.tr")) {
            // Öğrenci ise duyuru kısmını gizle
            classroomDuyuruLayout.setVisibility(View.GONE);
        } else {
            // Öğretmen ise duyuru kısmını göster
            classroomDuyuruLayout.setVisibility(View.VISIBLE);
        }
    }

    private void addAnnouncement() {
        String announcementText = announcementEditText.getText().toString();
        if (announcementText.isEmpty()) {
            Toast.makeText(Classroom.this, "Duyuru metni boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        // Benzersiz ID'yi oluşturmak için tarih ve saat bilgisi alıyoruz
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String uniqueID = sdf.format(new Date());

        // Formatlanmış tarih ve saat bilgisi
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = displayFormat.format(new Date());

        Announcement announcement = new Announcement(uniqueID, announcementText, email, formattedDate);

        mFirestore.collection("Courses").document(courseId).collection("Announcements").document(uniqueID)
                .set(announcement)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Classroom.this, "Duyuru eklendi", Toast.LENGTH_SHORT).show();
                    announcementEditText.setText(""); // Duyuru eklendikten sonra metin alanını temizle
                    fetchAnnouncements(); // Yeni duyuruları çek
                    // Duyuru yapıldığında bildirim gönder
                    sendNotificationToStudents(courseId, announcementText);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Classroom.this, "Duyuru eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchAnnouncements() {
        announcementList = new ArrayList<>();
        mFirestore.collection("Courses").document(courseId).collection("Announcements")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Announcement announcement = document.toObject(Announcement.class);
                            announcement.setId(document.getId()); // ID'yi ayarla
                            announcementList.add(announcement);
                        }
                        mAdapter = new ClassroomAnnouncementAdapter(Classroom.this, announcementList, Classroom.this);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Duyurular alınamadı.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNotificationToStudents(String courseId, String announcementText) {
        mFirestore.collection("Courses").document(courseId).collection("student-courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String studentEmail = document.getId();
                            mFirestore.collection("Students").document(studentEmail)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            String token = task1.getResult().getString("fcmToken");
                                            if (token != null) {
                                                sendNotification(token, "Yeni Duyuru", announcementText);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendNotification(String token, String title, String message) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                String json = "{"
                        + "\"to\":\"" + token + "\","
                        + "\"data\": {"
                        + "\"title\":\"" + title + "\","
                        + "\"message\":\"" + message + "\""
                        + "}"
                        + "}";

                RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(body)
                        .addHeader("Authorization", "key=YOUR_SERVER_KEY")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onReplyClick(Announcement announcement, String reply) {
        if (reply.isEmpty()) {
            Toast.makeText(this, "Yanıt boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        // Benzersiz ID'yi oluşturmak için tarih ve saat bilgisi alıyoruz
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String uniqueID = sdf.format(new Date());

        // Formatlanmış tarih ve saat bilgisi
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = displayFormat.format(new Date());

        Map<String, Object> replyData = new HashMap<>();
        replyData.put("replyText", reply);
        replyData.put("replier", email);
        replyData.put("formattedDate", formattedDate);

        mFirestore.collection("Courses").document(courseId)
                .collection("Announcements").document(announcement.getId())
                .collection("Replies").document(uniqueID)
                .set(replyData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Classroom.this, "Yanıt eklendi", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Classroom.this, "Yanıt eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onViewRepliesClick(Announcement announcement) {
        Intent intent = new Intent(this, ClassroomReply.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("announcementId", announcement.getId());
        startActivity(intent);
    }
}