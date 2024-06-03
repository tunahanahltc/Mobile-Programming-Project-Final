package com.example.ytuobs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecordAdminListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecordAdminAdapter recordAdminAdapter;
    private List<RecordAdmin> recordAdminList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_admin_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordAdminList = new ArrayList<>();
        recordAdminAdapter = new RecordAdminAdapter(recordAdminList);
        recyclerView.setAdapter(recordAdminAdapter);

        firestore = FirebaseFirestore.getInstance();
        loadRecordAdmins();
    }

    private void loadRecordAdmins() {
        firestore.collection("Adminrecords")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RecordAdmin recordAdmin = document.toObject(RecordAdmin.class);
                                recordAdminList.add(recordAdmin);
                            }
                            recordAdminAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
