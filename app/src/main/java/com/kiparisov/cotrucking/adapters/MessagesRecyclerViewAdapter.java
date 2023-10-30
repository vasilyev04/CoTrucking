package com.kiparisov.cotrucking.adapters;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
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
import com.kiparisov.cotrucking.interfaces.OnAvatarClickListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.Message;
import com.kiparisov.cotrucking.model.User;

import java.util.ArrayList;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder> {
    private int COMPANION_MESSAGE = 0;
    private int MY_MESSAGE = 1;
    private User user;
    private User companionUser;
    private ArrayList<Message> messages;
    private OnAvatarClickListener listener;

    public MessagesRecyclerViewAdapter(ArrayList<Message> messages, User companionUser, OnAvatarClickListener l) {
        this.messages = messages;
        this.companionUser = companionUser;
        this.listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageAvatar;
        TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            text = itemView.findViewById(R.id.text);
        }

        void bind(String userKey){
            imageAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Auth.getCurrentUser().getKey().equals(userKey)){
                        listener.onAvatarClicked(userKey);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MessagesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == COMPANION_MESSAGE){
            v = inflater.inflate(R.layout.item_companion_message, parent, false);
        }else{
            v = inflater.inflate(R.layout.item_my_message, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesRecyclerViewAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message.getOwnerKey());
        if (!TextUtils.isEmpty(user.getAvatarUri())){
            Glide.with(holder.itemView.getContext())
                    .load(user.getAvatarUri())
                    .centerCrop()
                    .into(holder.imageAvatar);
        }else{
            holder.imageAvatar.setImageDrawable(holder
                    .itemView
                    .getContext()
                    .getDrawable(R.drawable.unnamed));
        }

        holder.text.setText(message.getText());
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getOwnerKey().equals(Auth.getCurrentUser().getKey())) {
            user = Auth.getCurrentUser();
            //image = imageCurrentUser;
            return MY_MESSAGE;
        }else {
            user = companionUser;
            //image = imageCompanionUser;
            return COMPANION_MESSAGE;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
