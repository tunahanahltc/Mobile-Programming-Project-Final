package com.example.ytuobs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListAllPollForResult extends Activity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListView pollsListView;
    private List<String> pollNames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_poll_for_result);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        pollsListView = findViewById(R.id.allPollsListView);
        pollNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pollNames);
        pollsListView.setAdapter(adapter);

        loadPolls();

        pollsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPoll = pollNames.get(position);
                Intent intent = new Intent(ListAllPollForResult.this, ViewResultsActivity.class);
                intent.putExtra("pollName", selectedPoll);
                startActivity(intent);
            }
        });
    }

    private void loadPolls() {
        db.collection("Polls").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String pollId = document.getId();
                            pollNames.add(pollId);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error getting documents: ", e);
                    }
                });
    }
}
