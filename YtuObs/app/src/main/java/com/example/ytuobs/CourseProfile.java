package com.example.ytuobs;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CourseProfile extends AppCompatActivity {

    String name,date,creator,details;
    TextView nametxt,datetxt,creatortxt,detailstxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_profile);

        nametxt = findViewById(R.id.course_id_txt);
        datetxt = findViewById(R.id.course_date_txt);
        creatortxt = findViewById(R.id.course_creator_txt);
        detailstxt = findViewById(R.id.details_course_txt);



        name = (String) getIntent().getSerializableExtra("courseName");
        date = (String) getIntent().getSerializableExtra("date");
        creator = (String) getIntent().getSerializableExtra("courseCreator");
        details = (String) getIntent().getSerializableExtra("details");

        nametxt.setText(name);
        datetxt.setText(date);
        creatortxt.setText(creator);
        detailstxt.setText(details);

    }
}