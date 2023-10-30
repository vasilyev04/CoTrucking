package com.kiparisov.cotrucking.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityResetPasswordBinding;
import com.kiparisov.cotrucking.model.Auth;

public class ResetPasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.signUpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.btnResetPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    binding.emailLayout.setErrorEnabled(true);
                    binding.emailLayout.setError(getString(R.string.error_empty_field));
                }else{
                    binding.emailLayout.setErrorEnabled(false);
                    resetPasswdWithEmail(email);
                }
            }
        });

    }


    //сброс пароля пользователем
    private void resetPasswdWithEmail(String email){
        Auth.auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                binding.emailLayout.setErrorEnabled(false);
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.message_successful_sent),
                        Toast.LENGTH_LONG).show();
                finish();
            }else{
                try {
                    throw task.getException();
                }catch(FirebaseAuthInvalidUserException ex){
                    Toast.makeText(ResetPasswordActivity.this,
                            getString(R.string.error_user_not_found),
                            Toast.LENGTH_SHORT).show();

                    binding.emailLayout.setErrorEnabled(true);
                    binding.emailLayout.setError(getString(R.string.error_user_not_found));
                }catch(FirebaseAuthInvalidCredentialsException ex){     //INVALID EMAIL

                    Toast.makeText(ResetPasswordActivity.this,
                             getString(R.string.error_type_correct_email),
                             Toast.LENGTH_SHORT).show();

                    binding.emailLayout.setErrorEnabled(true);
                    binding.emailLayout.setError(getString(R.string.error_type_correct_email));
                    binding.etEmail.requestFocus();

                }catch (Exception ex){
                    Log.d("ResetPasswordException", ex.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}