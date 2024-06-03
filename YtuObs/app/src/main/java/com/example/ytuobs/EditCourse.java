package com.example.ytuobs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class EditCourse extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_CSV_FILE = 1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    Button crtCourseButton, crtGrupButton, addStdButton, delStdButton, importCsvButton;
    EditText courseId1, courseId2, groupId1, groupId2, date, studentMail, instMail, courseId3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        crtCourseButton = findViewById(R.id.crtCourseButton);
        crtGrupButton = findViewById(R.id.crtGrupButton);
        addStdButton = findViewById(R.id.addStdButton);
        delStdButton = findViewById(R.id.delStdButton);
        importCsvButton = findViewById(R.id.uploadCsvButton);
        courseId2 = findViewById(R.id.courseId2);
        groupId1 = findViewById(R.id.groupId1);
        groupId2 = findViewById(R.id.groupId2);
        courseId3 = findViewById(R.id.courseId3);
        studentMail = findViewById(R.id.studentMail);
        instMail = findViewById(R.id.instMail);
        courseId1 = findViewById(R.id.courseId1);
        date = findViewById(R.id.date_editxt);

        crtCourseButton.setOnClickListener(this);
        crtGrupButton.setOnClickListener(this);
        addStdButton.setOnClickListener(this);
        delStdButton.setOnClickListener(this);
        importCsvButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.crtCourseButton) {
            createCourse();
        } else if (id == R.id.crtGrupButton) {
            createGroup();
        } else if (id == R.id.addStdButton) {
            addStudentToGroup(courseId3.getText().toString(), groupId2.getText().toString(), studentMail.getText().toString());
        } else if (id == R.id.delStdButton) {
            removeStudentFromGroup(courseId3.getText().toString(), groupId2.getText().toString(), studentMail.getText().toString());
        } else if (id == R.id.uploadCsvButton) {
            selectCsvFile();
        }
    }

    private void selectCsvFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_CSV_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    importCsv(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "CSV dosyası okunurken hata oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void importCsv(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] data = csvLine.split(",");
                String courseId = data[0].trim();
                String groupId = data[1].trim();
                String studentEmail = data[2].trim();
                addStudentToGroup(courseId, groupId, studentEmail);
            }
            Toast.makeText(this, "Öğrenciler CSV'den başarıyla yüklendi", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Log.e("EditCourse", ex.getMessage(), ex);
            Toast.makeText(this, "CSV dosyası okunurken hata oluştu", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e("EditCourse", e.getMessage(), e);
            }
        }
    }

    private void createCourse() {
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("CourseId", courseId1.getText().toString());
        courseData.put("Date", date.getText().toString());
        courseData.put("Creator", mUser.getEmail());

        db.collection("Courses").document(courseId1.getText().toString())
                .set(courseData)
                .addOnCompleteListener(EditCourse.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditCourse.this, "Kurs başarılı bir şekilde oluşturuldu", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditCourse.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createGroup() {
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("Akademisyen", instMail.getText().toString());

        db.collection("Courses").document(courseId2.getText().toString())
                .collection("Gruplar").document(groupId1.getText().toString())
                .set(groupData)
                .addOnCompleteListener(EditCourse.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditCourse.this, "Grup başarılı bir şekilde oluşturuldu", Toast.LENGTH_SHORT).show();
                            addCourseToInstructorCollection();
                        } else {
                            Toast.makeText(EditCourse.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addCourseToInstructorCollection() {
        String instructorEmail = instMail.getText().toString();
        String courseId = courseId2.getText().toString();

        DocumentReference instructorDocRef = db.collection("Instructors").document(instructorEmail).collection("Courses").document(courseId);

        instructorDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Map<String, Object> courseData = new HashMap<>();
                        courseData.put("courseId", courseId);
                        instructorDocRef.set(courseData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(EditCourse.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(EditCourse.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addStudentToGroup(String courseId, String groupId, String stdMail) {
        DocumentReference studentDocument = db.collection("Students").document(stdMail);

        studentDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> studentData = documentSnapshot.getData();

                    db.collection("Courses").document(courseId)
                            .collection("Gruplar").document(groupId)
                            .collection("Students").document(stdMail).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && !document.exists()) {
                                            db.collection("Courses").document(courseId)
                                                    .collection("Gruplar").document(groupId)
                                                    .collection("Students").document(stdMail).set(studentData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                updateStudentCourse(courseId, groupId, stdMail);
                                                                updateStudentCount(courseId);
                                                            } else {
                                                                Toast.makeText(EditCourse.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(EditCourse.this, "Öğrenci zaten kayıtlı", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(EditCourse.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(EditCourse.this, "Öğrenci bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateStudentCount(String courseId) {
        DocumentReference courseRef = db.collection("Courses").document(courseId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(courseRef);
            long currentStudentCount = snapshot.getLong("studentCount") != null ?
                    snapshot.getLong("studentCount") : 0;
            long newStudentCount = currentStudentCount + 1;
            transaction.update(courseRef, "studentCount", newStudentCount);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditCourse.this, "Öğrenci sayısı başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(EditCourse.this, "Öğrenci sayısı güncellenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateStudentCourse(String courseId, String groupId, String stdMail) {
        DocumentReference group = db.collection("Courses").document(courseId)
                .collection("Gruplar").document(groupId);

        group.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String instructorMail = documentSnapshot.getString("Akademisyen");
                    Map<String, Object> data = new HashMap<>();
                    data.put("instructor", instructorMail);

                    db.collection("Students").document(stdMail)
                            .collection("student-courses").document(courseId)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("Doküman başarıyla eklendi.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Doküman eklenirken hata oluştu: " + e.getMessage());
                                }
                            });
                } else {
                    System.out.println("Belirtilen grup bulunamadı.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Doküman alınamadı: " + e.getMessage());
            }
        });
    }

    private void removeStudentFromGroup(String courseId, String groupId, String stdMail) {
        db.collection("Courses").document(courseId)
                .collection("Gruplar").document(groupId)
                .collection("Students")
                .document(stdMail).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditCourse.this, "Öğrenci silindi", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditCourse.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("Students").document(stdMail)
                .collection("student-courses").document(courseId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Öğrenci dersten silindi");
                        } else {
                            Log.d("Firestore", "Öğrenci dersten silinirken hata oluştu");
                        }
                    }
                });
    }
}
