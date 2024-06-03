package com.example.ytuobs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PollListActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListView pollsListView;
    private List<String> pollNames;
    private ArrayAdapter<String> adapter;
    Button listPollButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        listPollButton = findViewById(R.id.listPollButton);
        listPollButton.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        pollsListView = findViewById(R.id.pollsListView);
        pollNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pollNames);
        pollsListView.setAdapter(adapter);

        loadPolls();

        pollsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPoll = pollNames.get(position);
                Intent intent = new Intent(PollListActivity.this, SurveyActivity.class);
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
                            checkParticipant(pollId);
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

    private void checkParticipant(final String pollId) {
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("Polls").document(pollId).collection("Participants").document(currentUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            pollNames.add(pollId);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error checking participant: ", e);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.listPollButton){
            Intent intent = new Intent(PollListActivity.this,ListAllPollForResult.class);
            startActivity(intent);
        }
    }
}
