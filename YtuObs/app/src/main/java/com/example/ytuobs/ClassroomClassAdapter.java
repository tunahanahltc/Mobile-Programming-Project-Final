package com.example.ytuobs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
public class ClassroomClassAdapter extends RecyclerView.Adapter<ClassroomClassAdapter.ClassroomClassViewHolder> {

    private List<String> courseList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ClassroomClassAdapter(Context context, List<String> courseList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.courseList = courseList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ClassroomClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.classroom_class_item, parent, false);
        return new ClassroomClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomClassViewHolder holder, int position) {
        String courseId = courseList.get(position);
        holder.courseIdText.setText(courseId);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(courseId));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class ClassroomClassViewHolder extends RecyclerView.ViewHolder {
        TextView courseIdText;

        public ClassroomClassViewHolder(@NonNull View itemView) {
            super(itemView);
            courseIdText = itemView.findViewById(R.id.courseIdText);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String courseId);
    }
}