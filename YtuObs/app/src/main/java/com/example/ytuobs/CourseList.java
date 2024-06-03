package com.example.ytuobs;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CourseList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CourseAdapter mAdapter;
    private ArrayList<String> mCourseList;
    FirebaseFirestore firestore;
    DatabaseReference databaseCourse;
    ImageButton menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        // RecyclerView ve adaptörü ayarlama
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCourseList = new ArrayList<>();
        mAdapter = new CourseAdapter(this, mCourseList);
        mRecyclerView.setAdapter(mAdapter);



    }

    @Override
    protected void onStop() {
        super.onStop();
        mCourseList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCourseList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toList();

    }
    public void toList(){
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String courseId = document.getId();
                        mCourseList.add(courseId);

                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });



/*
        databaseCourse = FirebaseDatabase.getInstance().getReference("Courses");
        databaseCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snp: snapshot.getChildren()){
                    mCourseList.add(snp.getKey().toString());

                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


    }
}