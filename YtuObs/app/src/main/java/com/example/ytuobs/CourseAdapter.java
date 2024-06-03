package com.example.ytuobs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mCourseList;

    public CourseAdapter(Context context, ArrayList<String> courseList) {
        mContext = context;
        mCourseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String courseName = mCourseList.get(position);
        holder.textCourseName.setText(courseName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCourseData(courseName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCourseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseName = itemView.findViewById(R.id.textCourseName);
        }
    }

    private void showCourseDialog(String courseName, String courseDate, int studentCount) {
        // LayoutInflater'ı kullanarak kendi XML dosyanızı yükleyin
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.dialog_course_info, null);

        // View bileşenlerine verileri atayın
        TextView textCourseName = dialogView.findViewById(R.id.custom_dialog_course_name);
        TextView textCourseDate = dialogView.findViewById(R.id.custom_dialog_course_date);
        TextView textStudentCount = dialogView.findViewById(R.id.custom_dialog_student_count);
//        ImageView imageView = dialogView.findViewById(R.id.custom_dialog_image);
        Button buttonOk = dialogView.findViewById(R.id.dialog_button);

        textCourseName.setText(courseName);
        textCourseDate.setText("Course Date: " + courseDate);
        textStudentCount.setText("Student Count: " + studentCount);

        // Bilgi temalı fotoğrafı ayarlayın (Örnek olarak, R.drawable.info_image yerine kendi kaynak dosyanızı kullanın)
//        imageView.setImageResource(R.drawable.info_image);

        // Ok düğmesine tıklama işlemini tanımlayın


        // AlertDialog'ı özelleştirilmiş builder ile oluşturun
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);

        // Dialog'u oluşturun ve gösterin
        AlertDialog dialog = builder.create();
        dialog.show();
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dialog'u kapat
                dialog.dismiss();
            }
        });
    }



    public void getCourseData(String courseId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference courseRef = db.collection("Courses").document(courseId);

        // Dokümanı al
        courseRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Doküman varsa verileri al
                    String courseId = documentSnapshot.getString("CourseId");
                    String date = documentSnapshot.getString("Date");
                    Long tmp = documentSnapshot.getLong("studentCount");
                    int studentCount = tmp != null ? tmp.intValue() : 0;
                    showCourseDialog(courseId,date,studentCount);
                }
            }
        });
    }

}
