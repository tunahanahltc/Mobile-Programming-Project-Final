package com.example.ytuobs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Login extends Activity implements View.OnClickListener {

    Button login_button,reset_pass_button;
    TextView register_textView, change_password_TextView,goToLoginPage;
    private EditText e_mail, student_no, name, password;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.e_mail = findViewById(R.id.e_mail_adress);
        this.password = findViewById(R.id.password);
        register_textView = findViewById(R.id.register_text_view);
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);
        goToLoginPage = findViewById(R.id.go_to_login_page);
        goToLoginPage.setOnClickListener(this);
        reset_pass_button = findViewById(R.id.change_password_button);
        reset_pass_button.setOnClickListener(this);
        change_password_TextView = findViewById(R.id.chance_password_text_view);
        change_password_TextView.setOnClickListener(this);
        register_textView.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.login_button) {
            if (!TextUtils.isEmpty(e_mail.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {
                mAuth.signInWithEmailAndPassword(e_mail.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                mUser = mAuth.getCurrentUser();
//                                if(mUser.isEmailVerified())
                                if(true)//HESAP ONAYLIMI KONTROLUNU SAGLA
                                {
                                    Toast.makeText(Login.this,"Giriş başarılı",Toast.LENGTH_SHORT).show();

                                    if (e_mail.getText().toString().endsWith("@std.yildiz.edu.tr")) {
                                        // Öğrenci ekranına git
                                        Intent intent = new Intent(Login.this, StudentMenu.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if (mUser.getEmail().equals("admin@yildiz.edu.tr")) {
                                        Toast.makeText(Login.this, "ADMIN", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, AdminMenu.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    } else if (e_mail.getText().toString().endsWith("@yildiz.edu.tr")) {
                                        // Öğretmen ekranına git
                                        //Toast.makeText(Login.this, "Ogretmen", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, TeacherMenu.class);
                                        startActivity(intent);
                                        finish();


                                }//ONAYLI OLMAMA IHTIMALI VAR O YUZDEN IKI TARAFA DA KOYDUM AYNI IFI
                                //DUZENLENMESI GEREK BURANIN
                                else if (mUser.getEmail().equals("admin@yildiz.edu.tr")) {
                                    Toast.makeText(Login.this, "ADMIN", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, AdminMenu.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(Login.this,"Hesabınız Onaylanmadı",Toast.LENGTH_SHORT).show();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Bilgiler hatalı",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }else if(v.getId() == R.id.chance_password_text_view){
                password.setVisibility(View.GONE);
                login_button.setVisibility(View.GONE);
                change_password_TextView.setVisibility(View.GONE);
                register_textView.setVisibility(View.GONE);
                reset_pass_button.setVisibility(View.VISIBLE);
                goToLoginPage.setVisibility(View.VISIBLE);


        } else if (v.getId() == R.id.change_password_button) {

            String email = e_mail.getText().toString();
            if(!email.isEmpty()) {
                sendPasswordReset(email);
            }
            else {
                Toast.makeText(Login.this,"Mail Adresi Giriniz",Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.register_text_view) {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.go_to_login_page) {
            password.setVisibility(View.VISIBLE);
            login_button.setVisibility(View.VISIBLE);
            change_password_TextView.setVisibility(View.VISIBLE);
            register_textView.setVisibility(View.VISIBLE);
            reset_pass_button.setVisibility(View.GONE);
            goToLoginPage.setVisibility(View.GONE);
        }


    }
    public void sendPasswordReset(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(Login.this, "Sıfırlama maili gönderilemedi: " + errorMessage, Toast.LENGTH_SHORT).show();


                        }
                        else {
                            Toast.makeText(Login.this,"Sıfırlama maili gönderildi",Toast.LENGTH_SHORT).show();
                            password.setVisibility(View.VISIBLE);
                            login_button.setVisibility(View.VISIBLE);
                            change_password_TextView.setVisibility(View.VISIBLE);
                            register_textView.setVisibility(View.VISIBLE);
                            reset_pass_button.setVisibility(View.GONE);
                            goToLoginPage.setVisibility(View.GONE);
                         }
                    }
                });
    }

}