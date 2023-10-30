package com.kiparisov.cotrucking.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityEditProfileBinding;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private FirebaseStorage storage;
    private StorageReference profileImages;
    private int userIndicator;
    private Bitmap bitmap;
    private ActivityResultLauncher<Intent> openGalleryResult;
    private boolean imageDeleted;
    private static final int RESULT_AVATAR_DELETED = 3;


    private void init(){
        storage = FirebaseStorage.getInstance();
        profileImages = storage.getReference(Constants.FB_STORAGE_PROFILE_REFERENCE_KEY);
    }

    private void registerCallBack(){
        openGalleryResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    imageDeleted = false;
                    Intent i = result.getData();
                    try {
                        InputStream is = getContentResolver().openInputStream(i.getData());
                        bitmap = BitmapFactory.decodeStream(is);
                        binding.imageAvatar.setImageBitmap(bitmap);
                    } catch (FileNotFoundException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        registerCallBack();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.profile_fragment_status_bar));
        }

        fillViews();


        binding.rgUserIndicator.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.rb_client:
                        userIndicator = User.CLIENT_INDICATOR;
                        break;
                    default:
                        userIndicator = User.TRANSPORTER_INDICATOR;
                }
            }
        });

        binding.btnSaveChange.setOnClickListener(view -> {
            String phone = binding.etPhone.getText().toString();
            if (phone.length() == 11 || phone.length() == 0){
                binding.layoutPhone.setErrorEnabled(false);
                acceptChange();

                binding.scrollView.scrollTo(0, binding.scrollView.getTop());
            }else{
                binding.layoutPhone.setErrorEnabled(true);
                binding.layoutPhone.setError(getString(R.string.error_incorrect_phone));
            }
        });

        binding.imageAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            openGalleryResult.launch(intent);
        });

        binding.openGalleryIcon.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            openGalleryResult.launch(intent);
        });


        binding.deleteAvatarIcon.setOnClickListener(view -> {
            if (bitmap != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.alert_title_question));
                builder.setMessage(getString(R.string.alert_delete_photo));

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == AlertDialog.BUTTON_POSITIVE){
                            bitmap = null;
                            imageDeleted = true;
                            binding.imageAvatar.setImageDrawable(getDrawable(R.drawable.unnamed));
                        }else{
                            closeContextMenu();
                        }
                    }
                };

                builder.setPositiveButton(getString(R.string.alert_positive_button), listener);
                builder.setNegativeButton(getString(R.string.alert_negative_button), listener);

                builder.create().show();
            }
        });

        binding.btnBack.setOnClickListener(view -> finish());
    }

    private void fillViews(){
        User user = Auth.getCurrentUser();
        if (!TextUtils.isEmpty(user.getAvatarUri())){
            Glide.with(this)
                    .asBitmap()
                    .load(user.getAvatarUri())
                    .centerCrop()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;
                            binding.imageAvatar.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
        if (user.getUserIndicator() == User.TRANSPORTER_INDICATOR){
            binding.rbTransporter.setChecked(true);
        }
        userIndicator = user.getUserIndicator();
        if (!TextUtils.isEmpty(user.getName())){
            binding.etName.setText(user.getName());
        }
        if (user.getAge() != 0){
            binding.etAge.setText(user.getAge() + "");
        }
        if(!TextUtils.isEmpty(user.getProfileDescription())){
            binding.etAboutMe.setText(user.getProfileDescription());
        }
        if (!TextUtils.isEmpty(user.getPhone())){
            binding.etPhone.setText(user.getPhone());
        }

        if(!user.isEmailPrivate()){
            binding.switchHideEmail.setChecked(false);
        }
        if (!user.isPhonePrivate()){
            binding.switchHidePhone.setChecked(false);
        }
    }


    private void acceptChange(){
        User user = Auth.getCurrentUser();

        user.setUserIndicator(userIndicator);
        user.setName(binding.etName.getText().toString());
        try {
            user.setAge(Integer.parseInt(binding.etAge.getText().toString()));
        }catch (NumberFormatException ex){
            user.setAge(0);
        }
        user.setProfileDescription(binding.etAboutMe.getText().toString());
        user.setPhone(binding.etPhone.getText().toString());
        user.setEmailPrivate(binding.switchHideEmail.isChecked());
        user.setPhonePrivate(binding.switchHidePhone.isChecked());

        if (bitmap != null){
            uploadImage(ThumbnailUtils.extractThumbnail(bitmap, 500,500));
        }else{
            if (!TextUtils.isEmpty(Auth.getCurrentUser().getAvatarUri())){
                deleteImage();
            }else{
                Auth.updateUserInFireBase(Auth.getCurrentUser());
                Toast.makeText(this, getString(R.string.toast_changes_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    private void deleteImage(){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.show();
        storage.getReferenceFromUrl(Auth.getCurrentUser().getAvatarUri()).delete().addOnCompleteListener(
                new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Auth.getCurrentUser().setAvatarUri("");
                Auth.updateUserInFireBase(Auth.getCurrentUser());
                dialog.hide();
                finish();
            }
        });
    }

    private void uploadImage(Bitmap bitmap){

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCancelable(false);
        dialog.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        StorageReference currentImage = profileImages.child(System.currentTimeMillis() + "");
        UploadTask uploadTask = currentImage.putBytes(bytes);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return currentImage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (!TextUtils.isEmpty(Auth.getCurrentUser().getAvatarUri())){
                    storage.getReferenceFromUrl(Auth.getCurrentUser().getAvatarUri()).delete();
                }
                Auth.getCurrentUser().setAvatarUri(task.getResult().toString());
                Auth.updateUserInFireBase(Auth.getCurrentUser());
                dialog.hide();
                Toast.makeText(EditProfileActivity.this, getString(R.string.toast_changes_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
    }



}