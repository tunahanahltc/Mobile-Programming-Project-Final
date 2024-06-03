package com.example.ytuobs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private Context context;
    private List<Reply> replyList;

    public ReplyAdapter(Context context, List<Reply> replyList) {
        this.context = context;
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Reply reply = replyList.get(position);
        holder.replyText.setText(reply.getReplyText());
        holder.replierText.setText(reply.getReplier());
        holder.dateText.setText(reply.getFormattedDate());
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView replyText, replierText, dateText;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            replyText = itemView.findViewById(R.id.replyText);
            replierText = itemView.findViewById(R.id.replierText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}
