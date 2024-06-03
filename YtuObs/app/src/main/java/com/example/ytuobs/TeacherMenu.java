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
public class TeacherMenu extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageButton imageButtonBilgilerim,
            imageButtonDersGor, imageButtonOgrenciIslemleri, imageButtonDersListele, imageButtonRaporGor,
            imagePollCreateButton, imageButtonClassroom;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_menu);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        imageButtonBilgilerim = findViewById(R.id.ImageButtonBilgilerimOgretmen);
        imageButtonDersGor = findViewById(R.id.imageButtonDersEkleSilOgretmen);
        imageButtonOgrenciIslemleri = findViewById(R.id.imageButtonOgrenciOgretmen);
        imageButtonDersListele = findViewById(R.id.imageButtonDersListeleOgretmen);
        imageButtonRaporGor = findViewById(R.id.imageButtonRaporOgretmen);
        imagePollCreateButton = findViewById(R.id.imageButtonPollQuestionar);
        imageButtonClassroom = findViewById(R.id.imageButtonStudentClassroom);
        buttonLogout = findViewById(R.id.buttonLogout);

        imagePollCreateButton.setOnClickListener(this);
        imageButtonBilgilerim.setOnClickListener(this);
        imageButtonDersGor.setOnClickListener(this);
        imageButtonOgrenciIslemleri.setOnClickListener(this);
        imageButtonDersListele.setOnClickListener(this);
        imageButtonRaporGor.setOnClickListener(this);
        imageButtonClassroom.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();

        if (id == R.id.ImageButtonBilgilerimOgretmen) {
            intent = new Intent(TeacherMenu.this, Profile.class);
            intent.putExtra("currentUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.imageButtonDersEkleSilOgretmen) {
            // Ders ekle/sil/güncelle butonuna tıklanınca yapılacak işlemler
            intent = new Intent(TeacherMenu.this, EditCourse.class);
            intent.putExtra("loginUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.imageButtonOgrenciOgretmen) {
            // Öğrenci işlemleri butonuna tıklanınca yapılacak işlemler
            // Örneğin:
            // bunu rastgele ekledim
            intent = new Intent(TeacherMenu.this, Accounts_list.class);
            intent.putExtra("loginUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.imageButtonDersListeleOgretmen) {
            // Ders Listele butonuna tıklanınca yapılacak işlemler
            // Örneğin:
            intent = new Intent(TeacherMenu.this, CourseList.class);
            startActivity(intent);
        } else if (id == R.id.imageButtonRaporOgretmen) {
            // Rapor görüntüleme butonuna tıklanınca yapılacak işlemler
            // Örneğin:
            intent = new Intent(TeacherMenu.this, Messages.class);
            intent.putExtra("loginUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.imageButtonPollQuestionar) {
            // Rapor görüntüleme butonuna tıklanınca yapılacak işlemler
            // Örneğin:
            intent = new Intent(TeacherMenu.this, CreatePoll.class);
            startActivity(intent);
        } else if (id == R.id.imageButtonStudentClassroom) {
            //classroom modulu
            intent = new Intent(TeacherMenu.this, ClassroomClasses.class);
            intent.putExtra("loginUserMail", mUser.getEmail());
            startActivity(intent);
        } else if (id == R.id.buttonLogout) {
            // Logout işlemi
            mAuth.signOut();
            intent = new Intent(TeacherMenu.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Bilinmeyen buton", Toast.LENGTH_SHORT).show();
        }
    }
}