package com.example.ytuobs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class ClassroomAnnouncementAdapter extends RecyclerView.Adapter<ClassroomAnnouncementAdapter.AnnouncementViewHolder> {

    private Context context;
    private List<Announcement> announcementList;
    private OnItemClickListener onItemClickListener;

    public ClassroomAnnouncementAdapter(Context context, List<Announcement> announcementList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.announcementList = announcementList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class_classroom, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.teacherNameText.setText(announcement.getTeacher());
        holder.announcementText.setText(announcement.getText());
        holder.formattedDateText.setText(announcement.getFormattedDate());

        holder.replyButton.setOnClickListener(v -> {
            String replyText = holder.replyEditText.getText().toString();
            onItemClickListener.onReplyClick(announcement, replyText);
        });

        holder.viewRepliesButton.setOnClickListener(v -> onItemClickListener.onViewRepliesClick(announcement));
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public interface OnItemClickListener {
        void onReplyClick(Announcement announcement, String reply);
        void onViewRepliesClick(Announcement announcement);
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView teacherNameText, announcementText, formattedDateText;
        EditText replyEditText;
        Button replyButton, viewRepliesButton;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherNameText = itemView.findViewById(R.id.teacherNameText);
            announcementText = itemView.findViewById(R.id.announcementText);
            formattedDateText = itemView.findViewById(R.id.formattedDateText);
            replyEditText = itemView.findViewById(R.id.replyEditText);
            replyButton = itemView.findViewById(R.id.replyButton);
            viewRepliesButton = itemView.findViewById(R.id.viewRepliesButton);
        }
    }
}