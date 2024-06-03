package com.example.ytuobs;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Accounts_list extends AppCompatActivity {

        RecyclerView recyclerView;
        DatabaseReference databaseStudent,databaseInstruction;
          FirebaseFirestore firestore;
          FirebaseAuth auth;
          FirebaseUser user;
          DocumentReference docRef;
          CollectionReference collectionReference;
        MyAdapter adapter;
        ArrayList<Member> list;
        ImageButton menu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.usersList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        String loginUserMail = user.getEmail();
        adapter = new MyAdapter(this,list,loginUserMail);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStop() {
        super.onStop();
        list.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        toList();

    }

    public void toList(){
        collectionReference = firestore.collection("Students");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("isim");
                        String eMail = document.getString("eMail");
                        String studentNo = document.getString("studentNo");
                        String educate = document.getString("educateInform");
                        String statu = document.getString("statu");
                        String telNo = document.getString("telNo");

                        Member member = new Student(eMail, name, studentNo);
                        member.setStatu(statu);
                        member.setEducateInform(educate);
                        member.setTel_no(telNo);
                        list.add(member);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    // Task başarısız olursa ne yapılacağını belirtin
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            }
        });
        collectionReference = firestore.collection("Instructors");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("isim");
                        String eMail = document.getString("eMail");
                        String educate = document.getString("educateInform");
                        String statu = document.getString("statu");
                        String telNo = document.getString("telNo");

                        Member member = new Instructor(eMail, name);
                        member.setStatu(statu);
                        member.setEducateInform(educate);
                        member.setTel_no(telNo);

                        list.add(member);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    // Task başarısız olursa ne yapılacağını belirtin
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            }
        });
    }


}