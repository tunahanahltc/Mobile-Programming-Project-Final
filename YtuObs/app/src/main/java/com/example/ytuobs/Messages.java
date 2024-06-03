package com.example.ytuobs;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Messages extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private List<Record> mRecordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.message_recyclerWiev);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecordList = new ArrayList<>();
        mAdapter = new MessageAdapter(mRecordList);
        mRecyclerView.setAdapter(mAdapter);

        if (mUser != null) {
            loadMessages();
        } else {
            Toast.makeText(this, "Kullanıcı oturumu açmamış", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        String userEmail = mUser.getEmail();
        CollectionReference recordsRef = mFirestore.collection("Instructors").document(userEmail).collection("records");

        recordsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    mRecordList.clear();
                    List<Record> records = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Record record = document.toObject(Record.class);
                        records.add(record);
                    }
                    // Tarih ve saat öğelerini alarak belgeleri sırala
                    Collections.sort(records, (record1, record2) -> {
                        // Önce yıla göre sırala
                        int yearCompare = Integer.compare(record2.getYear(), record1.getYear());
                        if (yearCompare != 0) {
                            return yearCompare;
                        }
                        // Sonra aya göre sırala
                        int monthCompare = Integer.compare(record2.getMonth(), record1.getMonth());
                        if (monthCompare != 0) {
                            return monthCompare;
                        }
                        // Sonra güne göre sırala
                        int dayCompare = Integer.compare(record2.getDay(), record1.getDay());
                        if (dayCompare != 0) {
                            return dayCompare;
                        }
                        // Sonra saate göre sırala
                        int hourCompare = Integer.compare(record2.getHour(), record1.getHour());
                        if (hourCompare != 0) {
                            return hourCompare;
                        }
                        // Son olarak dakikaya göre sırala
                        int minuteCompare = Integer.compare(record2.getSecond(), record1.getSecond());
                        if (minuteCompare != 0) {
                            return minuteCompare;
                        }
                        // Son olarak saniyeye göre sırala
                        return Integer.compare(record2.getSecond(), record1.getSecond());
                    });
                    // Sıralanmış belgeleri mRecordList'e ekle
                    mRecordList.addAll(records);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(Messages.this, "Mesajlar yüklenemedi: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}