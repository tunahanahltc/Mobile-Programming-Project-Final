package com.example.ytuobs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordAdminAdapter extends RecyclerView.Adapter<RecordAdminAdapter.RecordAdminViewHolder> {

    private List<RecordAdmin> recordAdminList;

    public RecordAdminAdapter(List<RecordAdmin> recordAdminList) {
        this.recordAdminList = recordAdminList;
    }

    @NonNull
    @Override
    public RecordAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_admin, parent, false);
        return new RecordAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdminViewHolder holder, int position) {
        RecordAdmin recordAdmin = recordAdminList.get(position);
        holder.messageTextView.setText(recordAdmin.getMessage());
        holder.emailTextView.setText(recordAdmin.getEmail());
        holder.dateTextView.setText(
                String.format("%04d-%02d-%02d %02d:%02d:%02d",
                        recordAdmin.getYear(), recordAdmin.getMonth(), recordAdmin.getDay(),
                        recordAdmin.getHour(), recordAdmin.getMinute(), recordAdmin.getSecond()));
    }

    @Override
    public int getItemCount() {
        return recordAdminList.size();
    }

    public static class RecordAdminViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView emailTextView;
        public TextView dateTextView;

        public RecordAdminViewHolder(View view) {
            super(view);
            messageTextView = view.findViewById(R.id.messageTextView);
            emailTextView = view.findViewById(R.id.emailTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
        }
    }
}
