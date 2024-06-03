package com.example.ytuobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StudentMenu extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageButton imageButtonStudentBilgi, imageButtonStudentDersgor, imageButtonOgrenciDersListele, imageButtonOgrenciRapor, imageButtonPoll, imageButtonOClassroom;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_menu);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        imageButtonStudentBilgi = findViewById(R.id.imageButtonStudentBilgi);
        imageButtonStudentDersgor = findViewById(R.id.imageButtonStudentDersgör);
        imageButtonOgrenciDersListele = findViewById(R.id.imageButtonOgrenciDersListele);
        imageButtonOgrenciRapor = findViewById(R.id.imageButtonOgrenciRapor);
        imageButtonPoll = findViewById(R.id.imageButtonPollQuestionar);
        imageButtonOClassroom = findViewById(R.id.imageButtonStudentClassroom);
        buttonLogout = findViewById(R.id.buttonLogout);

        imageButtonPoll.setOnClickListener(this);
        imageButtonStudentBilgi.setOnClickListener(this);
        imageButtonStudentDersgor.setOnClickListener(this);
        imageButtonOgrenciDersListele.setOnClickListener(this);
        imageButtonOgrenciRapor.setOnClickListener(this);
        imageButtonOClassroom.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();

        if (id == R.id.imageButtonStudentBilgi) {
            intent = new Intent(StudentMenu.this, Profile.class);
            intent.putExtra("currentUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.imageButtonOgrenciDersListele) {
            intent = new Intent(StudentMenu.this, CourseList.class);
            intent.putExtra("loginUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.imageButtonOgrenciRapor) {
            intent = new Intent(StudentMenu.this, Reporting.class);
            intent.putExtra("loginUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.imageButtonPollQuestionar) {
            intent = new Intent(StudentMenu.this, PollListActivity.class);
            startActivity(intent);
        } else if (id == R.id.imageButtonStudentClassroom) {
            //classroom modulu
            intent = new Intent(StudentMenu.this, ClassroomClasses.class);
            intent.putExtra("loginUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.buttonLogout) {
            // Logout işlemi
            mAuth.signOut();
            intent = new Intent(StudentMenu.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Bilinmeyen buton", Toast.LENGTH_SHORT).show();
        }
    }
}