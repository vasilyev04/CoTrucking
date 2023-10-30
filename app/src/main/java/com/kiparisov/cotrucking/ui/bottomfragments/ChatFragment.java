package com.kiparisov.cotrucking.ui.bottomfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.adapters.ChatRecyclerViewAdapter;
import com.kiparisov.cotrucking.databinding.FragmentChatBinding;
import com.kiparisov.cotrucking.interfaces.OnChatItemClickListener;
import com.kiparisov.cotrucking.interfaces.OnDataUserReceivedListener;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.Chat;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.Message;
import com.kiparisov.cotrucking.model.User;
import com.kiparisov.cotrucking.ui.ChatActivity;
import java.util.ArrayList;
import java.util.List;

/*
                    Task task = chats.child(ds.getKey()).get();
                    Tasks.whenAllSuccess(task).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            if (isCurrentUserChat[0]){
                                Message finalLastMessage = lastMessage[0];
                                Auth.getUserByKey(companionKey[0], new OnDataUserReceivedListener() {
                                    @Override
                                    public void onUserReceived(User user) {
                                        companionUser = user;
                                        Chat chat = new Chat(companionUser.getName(),
                                                companionUser.getAvatarBase64(),
                                                finalLastMessage.getTimeStamp(), finalLastMessage.getText(),
                                                companionUser.getEmail(),
                                                companionUser.getKey());
                                        chatsArr.add(chat);
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        }
                    });*/

public class ChatFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference chats;
    private FragmentChatBinding binding;
    private User companionUser;
    private ArrayList<Chat> chatsArr;
    private ChatRecyclerViewAdapter adapter;
    private ValueEventListener listener;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(getLayoutInflater());

        showProgressBar();

        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        chats = db.getReference(Constants.FB_CHATS_REFERENCE_KEY);
        chatsArr = new ArrayList<>();
        adapter = new ChatRecyclerViewAdapter(chatsArr, new OnChatItemClickListener() {
            @Override
            public void onItemClickListener(Chat chat, int position) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("companionKey", chat.getCompanionKey());
                requireContext().startActivity(intent);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        binding.chatRecyclerView.setLayoutManager(manager);
        binding.chatRecyclerView.setAdapter(adapter);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsArr.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d("roomID", ds.getKey());

                    final boolean[] isCurrentUserChat = {false};
                    final String[] companionKey = {""};
                    final Message[] lastMessage = {new Message()};
                    lastMessage[0].setTimeStamp(Integer.MIN_VALUE);

                    chats.child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Message message = ds.getValue(Message.class);
                                if (message.getUserKey1().equals(Auth.getCurrentUser().getKey())){
                                    companionKey[0] = message.getUserKey2();
                                    if (message.getTimeStamp() > lastMessage[0].getTimeStamp()){
                                        lastMessage[0] = message;
                                    }
                                    isCurrentUserChat[0] = true;

                                }else if (message.getUserKey2().equals(Auth.getCurrentUser().getKey())){
                                    companionKey[0] = message.getUserKey1();
                                    if (message.getTimeStamp() > lastMessage[0].getTimeStamp()){
                                        lastMessage[0] = message;
                                    }
                                    isCurrentUserChat[0] = true;
                                }else{
                                    isCurrentUserChat[0] = false;
                                    companionKey[0] = "";
                                    lastMessage[0] = new Message();
                                    lastMessage[0].setTimeStamp(Integer.MIN_VALUE);
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    Task task = chats.child(ds.getKey()).get();
                    Tasks.whenAllSuccess(task).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                         public void onSuccess(List<Object> objects) {
                            if (isCurrentUserChat[0]){
                                Auth.getUserByKey(companionKey[0], new OnDataUserReceivedListener() {
                                    @Override
                                    public void onUserReceived(User user) {
                                        companionUser = user;
                                        Chat chat = new Chat(companionUser.getName(),
                                                companionUser.getAvatarUri(),
                                                lastMessage[0].getTimeStamp(),
                                                lastMessage[0].getText(),
                                                companionUser.getEmail(),
                                                companionUser.getKey());

                                        chatsArr.add(chat);
                                        hideProgressBar();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };




        return binding.getRoot();
    }

    private void showProgressBar(){
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        binding.progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_chat));


    }


    @Override
    public void onPause() {
        super.onPause();
        chats.removeEventListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        chats.addValueEventListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chats.removeEventListener(listener);
    }
}