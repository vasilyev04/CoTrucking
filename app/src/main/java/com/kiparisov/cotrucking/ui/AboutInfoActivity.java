package com.kiparisov.cotrucking.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityAboutInfoBinding;
import com.kiparisov.cotrucking.model.Constants;

/*
    информация о создателе
 */

public class AboutInfoActivity extends AppCompatActivity {
    private ActivityAboutInfoBinding binding;
    private int selectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_baseline_arrow_back_ios_24));
        getSupportActionBar().setTitle(getString(R.string.about));

        Intent inputIntent = getIntent();
        selectedItemId = (int) inputIntent.getExtras().get("selectedItemId");


        binding.telegramIcon.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MY_TELEGRAM_LINK));
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("selectedItemId", selectedItemId);
        setResult(RESULT_CANCELED, intent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("selectedItemId", selectedItemId);
        setResult(RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();
    }
}