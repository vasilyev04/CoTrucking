package com.kiparisov.cotrucking.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kiparisov.cotrucking.interfaces.OnUserSignedListener;
import com.kiparisov.cotrucking.ui.SignInActivity;
import com.kiparisov.cotrucking.interfaces.OnDataUserReceivedListener;

public class Auth{
    public static FirebaseDatabase db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
    public static DatabaseReference users = db.getReference(Constants.FB_USERS_REFERENCE_KEY);
    public static User currentUser;
    public static FirebaseAuth auth = FirebaseAuth.getInstance();


    public static User getCurrentUser() {
        return currentUser;
    }

    public static void getDatabaseCurrentUser(OnDataUserReceivedListener listener) {
        FirebaseUser userFBAuth = auth.getCurrentUser();

        Log.d("userFBAuth", userFBAuth.getUid());
        if (userFBAuth != null ) {
            users.orderByChild("uid").equalTo(userFBAuth.getUid()).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User us = ds.getValue(User.class);
                                Log.d("currentUser", "currentUser: " + us.getKey());
                                listener.onUserReceived(us);
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public static FirebaseUser getCurrentUserFBAuth() {
        return auth.getCurrentUser();
    }


    public static void signOutInSharedPref(Context context) {
        SharedPreferences sp = context.
                getSharedPreferences(Constants.SP_USER_SIGN_KEY, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
    }


    public static void signIn(Context context, String email, String passwd, OnUserSignedListener l) {
        auth.signInWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Auth.getDatabaseCurrentUser(new OnDataUserReceivedListener() {
                                @Override
                                public void onUserReceived(User user) {
                                    Log.d("inSignIn", "onUserReceived: " + user.getKey());
                                    SharedPreferences sp = context.getSharedPreferences(Constants.SP_USER_SIGN_KEY,
                                            Context.MODE_PRIVATE);
                                    sp.edit().putString("KEY", user.getKey()).apply();
                                    currentUser = user;
                                    l.onUserSigned(true);
                                }
                            });

                        }else{
                            try {
                                throw task.getException();
                            } catch (Exception ex) {
                                l.onUserSigned(false);
                                Intent intent = new Intent(context, SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        }
                    }
                });
    }

    public static void getUserByKey(String key, OnDataUserReceivedListener listener) {
        users.orderByChild("key").equalTo(key).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            listener.onUserReceived(user);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    public static void updateUserInFireBase(User user){
        users.child(user.getKey()).setValue(user);
    }
}
