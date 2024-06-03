package com.example.ytuobs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClassroomReply extends AppCompatActivity {

    private FirebaseUser mUser;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String email;
    private List<Reply> replyList;
    private ReplyAdapter mAdapter;
    private EditText replyEditText;
    private Button replyButton;
    private String courseId;
    private String announcementId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_reply);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        replyEditText = findViewById(R.id.replyEditText);
        replyButton = findViewById(R.id.replyButton);
        mRecyclerView = findViewById(R.id.repliesRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        email = mUser.getEmail();

        // Intent ile gelen courseId ve announcementId'yi al
        courseId = getIntent().getStringExtra("courseId");
        announcementId = getIntent().getStringExtra("announcementId");

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReply();
            }
        });

        // Yanıtları çek ve RecyclerView'e ekle
        fetchReplies();
    }

    private void addReply() {
        String replyText = replyEditText.getText().toString();
        if (replyText.isEmpty()) {
            Toast.makeText(ClassroomReply.this, "Yanıt metni boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        // Benzersiz ID'yi oluşturmak için tarih ve saat bilgisi alıyoruz
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String uniqueID = sdf.format(new Date());

        // Formatlanmış tarih ve saat bilgisi
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = displayFormat.format(new Date());

        Map<String, Object> replyData = new HashMap<>();
        replyData.put("replyText", replyText);
        replyData.put("replier", email);
        replyData.put("formattedDate", formattedDate);

        mFirestore.collection("Courses").document(courseId)
                .collection("Announcements").document(announcementId)
                .collection("Replies").document(uniqueID)
                .set(replyData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ClassroomReply.this, "Yanıt eklendi", Toast.LENGTH_SHORT).show();
                    replyEditText.setText(""); // Yanıt eklendikten sonra metin alanını temizle
                    fetchReplies(); // Yeni yanıtları çek
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ClassroomReply.this, "Yanıt eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchReplies() {
        replyList = new ArrayList<>();
        mFirestore.collection("Courses").document(courseId)
                .collection("Announcements").document(announcementId)
                .collection("Replies")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reply reply = document.toObject(Reply.class);
                            replyList.add(reply);
                        }
                        mAdapter = new ReplyAdapter(ClassroomReply.this, replyList);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Yanıtlar alınamadı.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}