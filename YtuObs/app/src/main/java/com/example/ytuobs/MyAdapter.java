package com.example.ytuobs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

 Context context;
 ArrayList<Member> list;
 String loginUserMail;

    public MyAdapter(Context context, ArrayList<Member> list,String loginUserMail) {
        this.context = context;
        this.list = list;
        this.loginUserMail = loginUserMail;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,eMail,statu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameTxt);
            eMail = itemView.findViewById(R.id.e_mailTxt);
            statu = itemView.findViewById(R.id.statuTxt);

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Member member = list.get(position);
        holder.name.setText(member.getName());
        holder.eMail.setText(member.getE_mail());
        holder.statu.setText(member.getStatu());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Profile.class);
                intent.putExtra("currentUserMail",member.getE_mail());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
