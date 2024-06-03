package com.example.ytuobs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Reporting extends AppCompatActivity {

    private Spinner spinnerWhich;
    private Spinner spinnerCourse;
    private LinearLayout courseNameLayout;
    private EditText reportMessage;
    private Button sendButton;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String userMail, teacherMail, course, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting);

        spinnerWhich = findViewById(R.id.spinner_which);
        spinnerCourse = findViewById(R.id.spinner_Course);
        courseNameLayout = findViewById(R.id.courseNameLayout);
        reportMessage = findViewById(R.id.report_message);
        sendButton = findViewById(R.id.send_button);
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        String[] whichArray = {"Uygulama Hatası", "Kurs Hatası"};
        ArrayList<String> courseList = new ArrayList<>();

        ArrayAdapter<String> whichAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, whichArray);
        whichAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWhich.setAdapter(whichAdapter);

        mFirestore.collection("Students").document(mUser.getEmail()).collection("student-courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                courseList.add(document.getId());
                            }
                            ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(Reporting.this,
                                    android.R.layout.simple_spinner_item, courseList);
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCourse.setAdapter(courseAdapter);
                        } else {
                            Toast.makeText(getApplicationContext(), "Hata oluştu, kurslar alınamadı.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        spinnerWhich.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedTopic = parentView.getItemAtPosition(position).toString();
                courseNameLayout.setVisibility(selectedTopic.equals("Uygulama Hatası") ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        sendButton.setOnClickListener(v -> {
            String topic = spinnerWhich.getSelectedItem().toString();
            course = spinnerCourse.getSelectedItem() != null ? spinnerCourse.getSelectedItem().toString() : "";
            message = reportMessage.getText().toString();
            userMail = mUser.getEmail();

            // Anlık tarihi al
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Ay değeri 0'dan başlar, bu yüzden +1 ekliyoruz
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND); // Saniye bilgisi

            Log.d("Reporting", "topic: " + topic);
            Log.d("Reporting", "course: " + course);
            Log.d("Reporting", "message: " + message);
            Log.d("Reporting", "userMail: " + userMail);
            Log.d("Reporting", "year: " + year);
            Log.d("Reporting", "month: " + month);
            Log.d("Reporting", "dayOfMonth: " + dayOfMonth);
            Log.d("Reporting", "hourOfDay: " + hourOfDay);
            Log.d("Reporting", "minute: " + minute);
            Log.d("Reporting", "second: " + second);

            if (topic.equals("Uygulama Hatası")) {
                addReportToAdminCollection(course, message, userMail, year, month, dayOfMonth, hourOfDay, minute, second);
            } else {
                mFirestore.collection("Students")
                        .document(mUser.getEmail())
                        .collection("student-courses")
                        .document(course)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // Belge başarılı bir şekilde alındı, belgenin içeriğini kontrol edin
                                        Log.d("Document", "Document data: " + document.getData());
                                        // Doğru alan adını kullanarak öğretmen e-postasını alın
                                        teacherMail = document.getString("instructor");
                                        if (teacherMail != null) {
                                            // Raporu Firestore koleksiyonuna ekleyin
                                            addReportToCollection(teacherMail, course, message, userMail, year, month, dayOfMonth, hourOfDay, minute, second);
                                        } else {
                                            // Öğretmen e-postası null olduğunda yapılacaklar
                                            Toast.makeText(Reporting.this, "Öğretmen e-postası bulunamadı", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Belge yok, "records" dokümanını oluşturun ve raporu ekleyin
                                        createRecordsDocument(course, message, userMail, year, month, dayOfMonth, hourOfDay, minute, second);
                                    }
                                } else {
                                    // Hata oluştu
                                    Toast.makeText(Reporting.this, "Belge alınamadı: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void createRecordsDocument(String course, String message, String userMail, int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        Map<String, Object> report = new HashMap<>();
        report.put("course", course);
        report.put("message", message);
        report.put("email", userMail);
        report.put("year", year);
        report.put("month", month);
        report.put("day", dayOfMonth);
        report.put("hour", hourOfDay);
        report.put("minute", minute);
        report.put("second", second); // Saniye bilgisi

        // Tarih bilgisini kullanarak belge ID'si oluştur
        String recordId = String.format(Locale.getDefault(), "%04d%02d%02d%02d%02d%02d", year, month, dayOfMonth, hourOfDay, minute, second);

        mFirestore.collection("Instructors")
                .document(teacherMail)
                .collection("records")
                .document(recordId) // Belgeyi tarih formatında oluşturulan ID ile ekleyin
                .set(report) // Belgeyi ekleyin
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Reporting.this, "Rapor başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Reporting.this, "Rapor eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addReportToCollection(String teacherMail, String course, String message, String userMail, int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        Map<String, Object> report = new HashMap<>();
        report.put("course", course);
        report.put("message", message);
        report.put("email", userMail);
        report.put("year", year);
        report.put("month", month);
        report.put("day", dayOfMonth);
        report.put("hour", hourOfDay);
        report.put("minute", minute);
        report.put("second", second); // Saniye bilgisi

        // Tarih bilgilerini kullanarak benzersiz bir ID oluşturun
        String recordId = String.format(Locale.getDefault(), "%04d%02d%02d%02d%02d%02d", year, month, dayOfMonth, hourOfDay, minute, second);

        // Oluşturulan raporu belirtilen öğretmenin kayıtları koleksiyonuna ekleyin
        mFirestore.collection("Instructors")
                .document(teacherMail)
                .collection("records")
                .document(recordId) // Belgeyi tarih formatında oluşturulan ID ile ekleyin
                .set(report) // Belgeyi ekleyin
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Reporting.this, "Rapor başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Reporting.this, "Rapor eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addReportToAdminCollection(String course, String message, String userMail, int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        Map<String, Object> report = new HashMap<>();
        report.put("course", course);
        report.put("message", message);
        report.put("email", userMail);
        report.put("year", year);
        report.put("month", month);
        report.put("day", dayOfMonth);
        report.put("hour", hourOfDay);
        report.put("minute", minute);
        report.put("second", second); // Saniye bilgisi

        // Tarih bilgilerini kullanarak benzersiz bir ID oluşturun
        String recordId = String.format(Locale.getDefault(), "%04d%02d%02d%02d%02d%02d", year, month, dayOfMonth, hourOfDay, minute, second);

        // Oluşturulan raporu Adminrecords koleksiyonuna ekleyin
        mFirestore.collection("Adminrecords")
                .document(recordId) // Belgeyi tarih formatında oluşturulan ID ile ekleyin
                .set(report) // Belgeyi ekleyin
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Reporting.this, "Rapor başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Reporting.this, "Rapor eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
