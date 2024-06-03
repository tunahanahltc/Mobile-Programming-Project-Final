package com.example.ytuobs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class AddQuestion extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout linearLayoutAnswers;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Button buttonAddQuestion;
    EditText editTextQuestion,editTextAnswerCount;
    String pollName;
    int maxQuestionCount;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        linearLayoutAnswers = findViewById(R.id.linearLayoutAnswers);
        editTextQuestion = findViewById(R.id.editTextQuestion);
        editTextAnswerCount = findViewById(R.id.editTextAnswerCount);
        pollName = getIntent().getStringExtra("PollName");
        maxQuestionCount = Integer.parseInt(getIntent().getStringExtra("MaxQuestionCount"));


        buttonAddQuestion = findViewById(R.id.buttonAddQuestion);
        buttonAddQuestion.setOnClickListener(this);



        editTextAnswerCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Bu metod, metin değişmeden önce çağrılır
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Bu metod, metin değiştiği anda çağrılır
                addAnswerFields(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Bu metod, metin değiştikten sonra çağrılır
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();



    }

    private void addAnswerFields(String answerCountStr) {
        if (TextUtils.isEmpty(answerCountStr)) {
            linearLayoutAnswers.removeAllViews();
            return;
        }

        int answerCount = Integer.parseInt(answerCountStr);
        linearLayoutAnswers.removeAllViews();

        for (int i = 0; i < answerCount; i++) {
            EditText answerEditText = new EditText(this);
            answerEditText.setHint("Cevap " + (i + 1));
            linearLayoutAnswers.addView(answerEditText);
        }

        Toast.makeText(this, answerCount + " adet cevap alanı oluşturuldu.", Toast.LENGTH_SHORT).show();
    }

    public void addAnswer(String pollName, int counter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Question data
        Map<String, Object> questionData = new HashMap<>();
        questionData.put("questionText", editTextQuestion.getText().toString());

        Map<String, Object> answersData = new HashMap<>();

        // Get answers from dynamically created EditTexts
        for (int i = 0; i < linearLayoutAnswers.getChildCount(); i++) {
            View view = linearLayoutAnswers.getChildAt(i);
            if (view instanceof EditText) {
                EditText answerEditText = (EditText) view;
                String answerText = answerEditText.getText().toString();
                if (!TextUtils.isEmpty(answerText)) {
                    answersData.put(answerText, 0);
                }
            }
        }

        // Add the answers map to the question data
        questionData.put("answers", answersData);

        // Add the question document to the Questions collection with the dynamic ID
        db.collection("Polls").document(pollName)
                .collection("Questions").document(Integer.toString(counter)).set(questionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document added successfully
                        Log.d("Firestore", "Question document added with ID: " + counter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error adding document
                        Log.w("Firestore", "Error adding document", e);
                    }
                });

        db.collection("Polls").document(pollName).update("currentCount",counter+1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Firestore", "Current count updated " + counter);

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(counter<maxQuestionCount){
        addAnswer(pollName,counter);
        counter +=1;}
        else {
            Toast.makeText(AddQuestion.this,"Max sayiya ulasildi",Toast.LENGTH_SHORT).show();
        }
    }
}