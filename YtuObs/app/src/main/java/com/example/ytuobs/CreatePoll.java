package com.example.ytuobs;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePoll extends AppCompatActivity implements View.OnClickListener {


    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Button buttonCreatePoll,buttonListAllPool;
    EditText editTextSurveyName,editTextQuestionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        editTextSurveyName = findViewById(R.id.editTextSurveyName);
        editTextQuestionCount = findViewById(R.id.editTextQuestionCount);
        buttonListAllPool = findViewById(R.id.buttonListAllPoll);
        buttonListAllPool.setOnClickListener(this);

        buttonCreatePoll = findViewById(R.id.buttonCreateSurvey);

        buttonCreatePoll.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();



    }


    public void createPoll(String pollName, int questCount) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> pollData = new HashMap<>();
        pollData.put("currentCount", 0);
        pollData.put("questCount", questCount);
        pollData.put("participantNumber", 0);

        // Directly add the poll data to the Polls collection
        db.collection("Polls").document(pollName).set(pollData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Poll added successfully
                        Toast.makeText(CreatePoll.this, "Anket Oluşturuldu", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error adding poll
                        System.err.println("Error adding document: " + e);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonCreateSurvey) {
            if (!editTextSurveyName.getText().toString().isEmpty() && !editTextQuestionCount.getText().toString().isEmpty())
            {
                createPoll(editTextSurveyName.getText().toString(), Integer.parseInt(editTextQuestionCount.getText().toString()));
            Intent intent = new Intent(CreatePoll.this,AddQuestion.class);
            intent.putExtra("PollName",editTextSurveyName.getText().toString());
            intent.putExtra("MaxQuestionCount",editTextQuestionCount.getText().toString());
            startActivity(intent);
        }
        } else if (v.getId() == R.id.buttonListAllPoll) {
            Intent intent = new Intent(CreatePoll.this,ListAllPollForResult.class);
            startActivity(intent);

        }
    }
}