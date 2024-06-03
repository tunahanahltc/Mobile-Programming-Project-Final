package com.example.ytuobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollResults extends AppCompatActivity {
    private static String pollName;

    public static void setPollName(String name) {
        pollName = name;
    }

    public interface OnResultsListener {
        void onResults(List<Map<String, Integer>> results, List<String> questionTexts);
        void onError(Exception e);
    }

    public static void getResults(final OnResultsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference questionsRef = db.collection("Polls").document(pollName).collection("Questions");
        questionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String, Integer>> allResults = new ArrayList<>();
                    List<String> questionTexts = new ArrayList<>();  // Add a list to hold question texts
                    for (DocumentSnapshot document : task.getResult()) {
                        String questionText = document.getString("questionText");  // Assume the question text field is named "questionText"
                        questionTexts.add(questionText);  // Store the question text

                        Map<String, Object> answers = (Map<String, Object>) document.get("answers");
                        Map<String, Integer> results = new HashMap<>();
                        for (Map.Entry<String, Object> entry : answers.entrySet()) {
                            results.put(entry.getKey(), ((Long) entry.getValue()).intValue());
                        }
                        allResults.add(results);
                    }
                    listener.onResults(allResults, questionTexts);  // Pass both results and question texts to the listener
                } else {
                    listener.onError(task.getException());
                }
            }
        });
    }
}
