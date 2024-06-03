package com.example.ytuobs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Record> mRecordList;

    public MessageAdapter(List<Record> recordList) {
        this.mRecordList = recordList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recyclerview, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Record record = mRecordList.get(position);
        holder.courseTextView.setText("Course: " + record.getCourse());
        holder.emailTextView.setText("Email: " + record.getEmail());
        holder.messageTextView.setText("Message: " + record.getMessage());
        holder.dateTextView.setText("Date: " + record.getDate()); // Tarih alanını doldur
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView courseTextView;
        TextView emailTextView;
        TextView messageTextView;
        TextView dateTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTextView = itemView.findViewById(R.id.courseTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView); // Tarih TextView'ini bul
        }
    }
}
