package com.trendmicro.materialdesign_note.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trendmicro.materialdesign_note.R;
import com.trendmicro.materialdesign_note.act.EditActivity;

import java.util.List;

/**
 * Created by zheng_liu on 2018/1/30.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>  {
    List<Message> messages;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View View;
        TextView tv_title,tv_info,tv_date;

        public ViewHolder(View itemView) {
            super(itemView);
            View = itemView;
            tv_title=itemView.findViewById(R.id.item_title);
            tv_info=itemView.findViewById(R.id.item_info);
            tv_date=itemView.findViewById(R.id.main_item_date);
        }
    }


    public MsgAdapter(List<Message> message) {
        messages=message;
    }
    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_password_item, parent, false);
        final MsgAdapter.ViewHolder holder = new MsgAdapter.ViewHolder(view);
        holder.View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Message message = messages.get(position);
                message.setPosition(position);
                Toast.makeText(v.getContext(), "you clicked " + message.getTitle(), Toast.LENGTH_SHORT).show();
                EditActivity.startEditActivityForResult((Activity)v.getContext(),message);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MsgAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
//        SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
  //      String tv_day=day.format(message.getTime());
            holder.tv_info.setText(message.getMemoInfo());
            holder.tv_title.setText(message.getTitle());
            holder.tv_date.setText(message.getTime().substring(0,14));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }



}
