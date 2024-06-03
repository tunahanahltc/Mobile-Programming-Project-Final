package com.example.ytuobs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String pollName;
    private List<DocumentSnapshot> questions;
    private int currentQuestionIndex = 0;

    private TextView questionTextView;
    private RadioGroup answersRadioGroup;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        pollName = getIntent().getStringExtra("pollName");

        questionTextView = findViewById(R.id.questionTextView);
        answersRadioGroup = findViewById(R.id.answersRadioGroup);
        nextButton = findViewById(R.id.nextButton);

        loadQuestions();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer();
            }
        });
    }

    private void loadQuestions() {
        db.collection("Polls").document(pollName).collection("Questions")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        questions = queryDocumentSnapshots.getDocuments();
                        showQuestion();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error getting documents: ", e);
                    }
                });
    }
    private void addParticipant() {
       DocumentReference docref =  db.collection("Polls").document(pollName);
       docref.update("participantNumber",FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void unused) {
               Log.w("Firestore", "FireStore participant counter updeted: ");

           }

       });
        String userId = mUser.getUid();

        Map<String, Object> emptyMap = new HashMap<>();

        docref.collection("Participants").document(userId).set(emptyMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Firestore participant added with UID as document ID: " + userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding participant", e);
                    }
                });

    }
    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            DocumentSnapshot questionSnapshot = questions.get(currentQuestionIndex);
            String questionText = questionSnapshot.getString("questionText");
            Map<String, Long> answers = (Map<String, Long>) questionSnapshot.get("answers");

            questionTextView.setText(questionText);
            answersRadioGroup.removeAllViews();

            for (String answer : answers.keySet()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(answer);
                radioButton.setTextSize(20);
                answersRadioGroup.addView(radioButton);
                clearSelection();

            }
        } else {
           addParticipant();
            Toast.makeText(this, "Anket tamamlandı.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void submitAnswer() {
        int selectedId = answersRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Lütfen bir cevap seçin.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedAnswer = selectedRadioButton.getText().toString();

        // Update Firestore
        DocumentSnapshot questionSnapshot = questions.get(currentQuestionIndex);
        String questionId = questionSnapshot.getId();
        DocumentReference questionRef = db.collection("Polls").document(pollName)
                .collection("Questions").document(questionId);

        questionRef.update("answers." + selectedAnswer, FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Move to the next question
                        currentQuestionIndex++;
                        showQuestion();
                        clearSelection();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error updating document", e);
                    }
                });
    }

    private void clearSelection() {
        answersRadioGroup.clearCheck();
    }


}
