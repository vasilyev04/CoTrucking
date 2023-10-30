package com.kiparisov.cotrucking.adapters;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.interfaces.OnChatItemClickListener;
import com.kiparisov.cotrucking.interfaces.OnItemClickListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Chat> chats;
    private OnChatItemClickListener listener;
    private int ONE_DAY = 86400000;

    public ChatRecyclerViewAdapter(ArrayList<Chat> chats, OnChatItemClickListener l) {
        this.chats = chats;
        this.listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageAvatar;
        TextView userName, text, timestamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            userName = itemView.findViewById(R.id.tv_user_name);
            text = itemView.findViewById(R.id.tv_text);
            timestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        void bind(Chat chat, int position){
            itemView.setOnClickListener(view ->{
                listener.onItemClickListener(chat, position);
            });

        }
    }

    @NonNull
    @Override
    public ChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_chat_rv, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat, position);
        if (!TextUtils.isEmpty(chat.getUserAvatar())){
            Glide.with(holder.itemView.getContext())
                    .load(chat.getUserAvatar())
                    .centerCrop()
                    .into(holder.imageAvatar);
        }else{
            holder.imageAvatar.setImageDrawable(holder
                    .itemView
                    .getContext()
                    .getDrawable(R.drawable.unnamed));
        }
        if (!TextUtils.isEmpty(chat.getUserName())){
            holder.userName.setText(chat.getUserName());
        }else{
            holder.userName.setText(chat.getEmail());
        }
        if (chat.getText().length() > 30){
            holder.text.setText(chat.getText().substring(0, 30) + "...");
        }else{
            holder.text.setText(chat.getText());
        }

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        long timestamp = chat.getTimestamp();
        SimpleDateFormat format;

        if (currentTime - timestamp > ONE_DAY){
            format = new SimpleDateFormat("dd.MM");
            holder.timestamp.setText(format.format(timestamp));
        }else{
            format = new SimpleDateFormat("HH:mm");
            holder.timestamp.setText(holder.itemView.getContext().getString(R.string.today) + " " + format.format(timestamp));
        }



    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
