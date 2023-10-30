package com.kiparisov.cotrucking.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivitySignUpBinding;
import com.kiparisov.cotrucking.interfaces.OnUserSignedListener;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.Message;
import com.kiparisov.cotrucking.model.User;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.signUpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.signUpToolbar.getNavigationIcon().setTint(getResources().getColor(R.color.black));


        //кнопка регистрации
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString();
                String passwd = binding.etPassword.getText().toString();
                String repeatPasswd = binding.etRepeatPassword.getText().toString();


                //если все поля не пустые
                if (!isFieldsEmpty(email, passwd, repeatPasswd)){
                    //если пароль валидный
                    if (validatePassword(passwd)) {
                        //если оба пароля совпадают
                        if (passwd.equals(repeatPasswd)){       //pass1 == pass2
                            binding.emailLayout.setErrorEnabled(false);
                            binding.passwordLayout.setErrorEnabled(false);
                            binding.passwordRepeatLayout.setErrorEnabled(false);

                            showLoading();
                            signUp(email,passwd);


                        } else{                                 //pass1 != pass2
                            Toast.makeText(SignUpActivity.this,
                                    getString(R.string.error_type_crown_passwords),
                                    Toast.LENGTH_SHORT).show();

                            binding.passwordLayout.setErrorEnabled(true);
                            binding.passwordRepeatLayout.setErrorEnabled(true);

                            binding.passwordLayout.setError(getString(R.string.error_type_crown_passwords));
                            binding.passwordRepeatLayout.setError(getString(R.string.error_type_crown_passwords));
                        }
                    } else {                                    //pass hasn't letters and nums
                        Toast.makeText(SignUpActivity.this,
                                getString(R.string.error_wake_password_NL),
                                Toast.LENGTH_SHORT).show();

                        binding.passwordLayout.setErrorEnabled(true);
                        binding.passwordLayout.setError(getString(R.string.error_wake_password_NL));
                    }
                }
            }
        });
    }



    //регистрация
    private void signUp(String email, String passwd) {
        Auth.auth.createUserWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {      //sign in successful!
                        Toast.makeText(getApplicationContext(), getString(R.string.sign_up_successful),
                                Toast.LENGTH_SHORT).show();


                        SharedPreferences sp = getSharedPreferences(Constants.SP_USER_SIGN_KEY, MODE_PRIVATE);
                        sp.edit().putString("UID", Auth.auth.getUid()).apply();
                        sp.edit().putString("email", email).apply();
                        sp.edit().putString("passwd", passwd).apply();

                        DatabaseReference push = Auth.users.push();
                        User user = new User(email, passwd, "",
                                Auth.auth.getUid(), push.getKey());
                        user.setUserIndicator(User.CLIENT_INDICATOR);
                        push.setValue(user);

                        DatabaseReference chats = FirebaseDatabase
                                .getInstance(Constants.FB_URL_CONNECTION)
                                .getReference(Constants.FB_CHATS_REFERENCE_KEY);

                        Message message = new Message(Calendar.getInstance().getTimeInMillis(),
                                Constants.FB_COTRUCKING_ACC_KEY,
                                getString(R.string.welcome_chat_message),
                                Constants.FB_COTRUCKING_ACC_KEY,
                                user.getKey());

                        chats.child(Constants.FB_COTRUCKING_ACC_KEY + user.getKey())
                                .push().setValue(message);

                        Auth.signIn(this, email, passwd, new OnUserSignedListener() {
                            @Override
                            public void onUserSigned(boolean isSigned) {
                                hideLoading();
                                Intent intent = new Intent(SignUpActivity.this,
                                        FragmentContainerActivity.class);
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NO_ANIMATION
                                                |Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                |Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });

                    }else{
                        hideLoading();
                        try{
                            throw task.getException();
                        }catch(FirebaseAuthInvalidCredentialsException ex){     //INVALID EMAIL
                            if (ex.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                binding.passwordLayout.setErrorEnabled(false);

                                Toast.makeText(SignUpActivity.this,
                                        getString(R.string.error_type_correct_email),
                                        Toast.LENGTH_SHORT).show();

                                binding.emailLayout.setErrorEnabled(true);
                                binding.emailLayout.setError(getString(R.string.error_type_correct_email));
                                binding.etEmail.requestFocus();

                            }else if (ex.getErrorCode().equals("ERROR_WEAK_PASSWORD")){     //INVALID PASSWORD
                                binding.emailLayout.setErrorEnabled(false);

                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.error_wake_password),
                                        Toast.LENGTH_SHORT).show();

                                binding.passwordLayout.setErrorEnabled(true);
                                binding.passwordLayout.setError(getString(R.string.error_wake_password));
                                binding.etPassword.requestFocus();
                            }
                        }
                        catch (FirebaseAuthUserCollisionException ex){      //User already exist
                            binding.passwordLayout.setErrorEnabled(false);

                            Toast.makeText(SignUpActivity.this,
                                    getString(R.string.error_email_exist), Toast.LENGTH_SHORT).show();

                            binding.emailLayout.setErrorEnabled(true);
                            binding.emailLayout.setError(getString(R.string.error_email_exist));
                            binding.etEmail.requestFocus();

                        }catch (Exception ex){      //another exception
                            Log.d("signUpException", ex.getMessage());
                        }
                    }

                });
    }



    //валидация пароля
    private boolean validatePassword(String passwd) {
        boolean hasNums = false;
        boolean hasLetters = false;

        for (int i = 0; i < passwd.length(); i++) {
            if ((passwd.charAt(i) >= 'A' && passwd.charAt(i) <= 'Z')
                    || (passwd.charAt(i) >= 'a' && passwd.charAt(i) <= 'z')) {
                hasLetters = true;
                break;
            }
        }

        for (int i = 0; i < passwd.length(); i++) {
            if (passwd.charAt(i) >= '0' && passwd.charAt(i) <= '9') {
                hasNums = true;
            }
        }

        return hasNums && hasLetters;
    }



    //проверка всех полей на пустоту
    private boolean isFieldsEmpty(String email, String passwd, String repeatPasswd){

        binding.emailLayout.setErrorEnabled(false);
        binding.passwordLayout.setErrorEnabled(false);
        binding.passwordRepeatLayout.setErrorEnabled(false);
        boolean isFieldsEmpty = false;

        if (TextUtils.isEmpty(email.trim())){
            binding.emailLayout.setErrorEnabled(true);
            binding.emailLayout.setError(getString(R.string.error_empty_field));
            isFieldsEmpty = true;
        }

        if (TextUtils.isEmpty(passwd.trim())){
            binding.passwordLayout.setErrorEnabled(true);
            binding.passwordLayout.setError(getString(R.string.error_empty_field));
            isFieldsEmpty = true;

        }
        if (TextUtils.isEmpty(repeatPasswd.trim())){
            binding.passwordRepeatLayout.setErrorEnabled(true);
            binding.passwordRepeatLayout.setError(getString(R.string.error_empty_field));
            isFieldsEmpty = true;
        }


        return  isFieldsEmpty;
    }

    private void showLoading(){
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void hideLoading(){
        dialog.cancel();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}