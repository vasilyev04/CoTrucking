package com.kiparisov.cotrucking.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityEditCarAdBinding;
import com.kiparisov.cotrucking.interfaces.OnDataAdReceivedListener;
import com.kiparisov.cotrucking.interfaces.OnImageUploadedListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.DataBaseHelper;
import com.kiparisov.cotrucking.model.TransporterAd;
import com.kiparisov.cotrucking.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class EditCarAdActivity extends AppCompatActivity {
    private ActivityEditCarAdBinding binding;

    private FirebaseDatabase db;
    private DatabaseReference transporterAds;

    private FirebaseStorage storage;
    private StorageReference adImages;

    private TransporterAd givenAd;

    private EditText etCarMark, etLoadCapacity, etDescription,
            etStartLocation, etEndLocation, etUserName, etPhone;

    private ActivityResultLauncher<Intent> openCameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> openGalleryActivityResultLauncher;

    private Bitmap bitmap;

    private void init(){
        etCarMark = binding.etTransporterCarMark;
        etLoadCapacity = binding.etTransporterCarLoadCapacity;
        etDescription = binding.etTransporterCarDescription;
        etStartLocation = binding.etTransporterCarStartLocation;
        etEndLocation = binding.etTransporterCarEndLocation;
        etUserName = binding.etUserName;
        etPhone = binding.etUserContactNumber;

        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        storage = FirebaseStorage.getInstance();
        adImages = storage.getReference(Constants.FB_STORAGE_ADS_REFERENCE_KEY);
        transporterAds = db.getReference()
                .child(Constants.FB_ADS_REFERENCE_KEY)
                .child(Constants.FB_TRANSPORTER_ADS_REFERENCE_KEY);
    }


    //регистрация коллбеков
    private void registerCallBacks(){

        //callback с результатом после закрытия камеры
        openCameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent i = result.getData();

                        bitmap = (Bitmap) i.getExtras().get("data");

                        //установка картинки в imageView
                        ViewGroup.LayoutParams frameLayoutParams = binding.layoutUploadPhoto.getLayoutParams();
                        frameLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

                        binding.btnUploadPhoto.setVisibility(View.GONE);
                        binding.ivAdImage.setVisibility(View.VISIBLE);
                        binding.ivAdImage.setImageBitmap(bitmap);
                        binding.btnDeleteImage.setVisibility(View.VISIBLE);

                    }
                });

        //коллбек с результатом после закрытия галереи
        openGalleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent i = result.getData();

                        //конвертация картинки из галереи в формат Base64
                        try {
                            InputStream is = getContentResolver().openInputStream(i.getData());
                            bitmap = BitmapFactory.decodeStream(is);

                            ViewGroup.LayoutParams frameLayoutParams = binding.layoutUploadPhoto.getLayoutParams();
                            frameLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

                            //установка картинки в imageView
                            binding.btnUploadPhoto.setVisibility(View.GONE);
                            binding.ivAdImage.setVisibility(View.VISIBLE);
                            binding.ivAdImage.setImageBitmap(bitmap);
                            binding.btnDeleteImage.setVisibility(View.VISIBLE);

                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                        }

                    }
                });
    }


    private void initExistingUserData(){
        User user = Auth.getCurrentUser();
        if (!TextUtils.isEmpty(user.getName())){
            etUserName.setText(user.getName());
        }
        if (!TextUtils.isEmpty(user.getPhone())){
            etPhone.setText(user.getPhone());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCarAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);

        registerCallBacks();
        init();
        initExistingUserData();

        Intent intent = getIntent();
        String key = intent.getStringExtra(Constants.INTENT_EXTRA_AD_KEY);
        initAd(key);




        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTransporterCarAd();
            }
        });


        binding.btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
            }
        });


        binding.btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePhoto();
            }
        });

    }

    private void fillViews(){
        etCarMark.setText(givenAd.getTitle());
        etLoadCapacity.setText(givenAd.getLoadCapacity() + "");
        etDescription.setText(givenAd.getDescription());
        if (!TextUtils.isEmpty(givenAd.getImageUri())){
            Glide.with(this)
                    .asBitmap()
                    .load(givenAd.getImageUri())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;
                            binding.ivAdImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

            //установка картинки в imageView
            ViewGroup.LayoutParams frameLayoutParams = binding.layoutUploadPhoto.getLayoutParams();
            frameLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

            binding.btnUploadPhoto.setVisibility(View.GONE);
            binding.ivAdImage.setVisibility(View.VISIBLE);
            binding.btnDeleteImage.setVisibility(View.VISIBLE);
        }
        etStartLocation.setText(givenAd.getStartLocation());
        etEndLocation.setText(givenAd.getEndLocation());
    }

    private void initAd(String key){
        binding.nestedScrollView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        DataBaseHelper.getTransporterAd(key, new OnDataAdReceivedListener() {
            @Override
            public void onAdReceived(Ad ad) {
                givenAd = (TransporterAd) ad;
                fillViews();
                binding.progressBar.setVisibility(View.GONE);
                binding.nestedScrollView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void removePhoto(){
        ViewGroup.LayoutParams frameLayoutParams = binding.layoutUploadPhoto.getLayoutParams();
        frameLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.layoutUploadPhoto.setLayoutParams(frameLayoutParams);

        binding.btnUploadPhoto.setVisibility(View.VISIBLE);

        binding.btnDeleteImage.setVisibility(View.GONE);
        binding.ivAdImage.setVisibility(View.GONE);

        bitmap = null;
    }
    private void addPhoto(){
        String[] option = {getString(R.string.alert_choice_take_photo),
                getString(R.string.alert_choice_open_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCarAdActivity.this);

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        openCameraActivityResultLauncher.launch(openCamera);
                        break;
                    default:
                        Intent openGallery = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        openGalleryActivityResultLauncher.launch(openGallery);
                        break;
                }
            }
        });

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                closeContextMenu();
            }
        };

        builder.setNegativeButton(getString(R.string.alert_negative_button), listener);
        builder.setTitle(getString(R.string.alert_title_choice));

        builder.create().show();
    }

    private void editTransporterCarAd(){
        String carMark = etCarMark.getText().toString();
        int loadCapacity;
        try{
            loadCapacity = Integer.parseInt(etLoadCapacity.getText().toString());
        }catch (Exception ex){
            loadCapacity = 0;
        }

        String description = etDescription.getText().toString();
        String startLocation = etStartLocation.getText().toString();
        String endLocation = etEndLocation.getText().toString();
        String userName = etUserName.getText().toString();
        String phone = etPhone.getText().toString() + "";
        if (!isFieldsEmpty(carMark, loadCapacity, description,
                startLocation, endLocation, userName)){

            if (bitmap == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCarAdActivity.this);
                builder.setTitle(getString(R.string.alert_title_question));
                builder.setMessage(getString(R.string.alert_no_photo_message));

                final int finalLoadCapacity = loadCapacity;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case AlertDialog.BUTTON_POSITIVE:
                                long createTime = givenAd.getCreateTime();
                                int adTransporterType = TransporterAd.AD_CAR_TYPE;
                                TransporterAd ad = new TransporterAd(carMark, adTransporterType,
                                        description, phone, userName, startLocation,
                                        endLocation, finalLoadCapacity, createTime);

                                if (!TextUtils.isEmpty(givenAd.getImageUri())) {
                                    storage.getReferenceFromUrl(givenAd.getImageUri()).delete();
                                }
                                ad.setImageUri("");
                                ad.setKey(givenAd.getKey());
                                ad.setOwnerKey(givenAd.getOwnerKey());
                                transporterAds.child(givenAd.getKey()).setValue(ad);

                                User user = Auth.getCurrentUser();
                                user.setName(userName);
                                if (!TextUtils.isEmpty(phone)){
                                    user.setPhone(phone);
                                }
                                Auth.updateUserInFireBase(user);


                                Toast.makeText(getApplicationContext(),getString(R.string.edit_successful), Toast.LENGTH_SHORT).show( );
                                setResult(RESULT_OK);
                                finish();
                                break;
                            case AlertDialog.BUTTON_NEGATIVE:
                                closeContextMenu();
                                binding.btnUploadPhoto.requestFocus();
                                break;
                        }
                    }
                };

                builder.setPositiveButton(getString(R.string.alert_positive_button), listener);
                builder.setNegativeButton(getString(R.string.alert_negative_button), listener);
                builder.show().create();
            }else{
                long createTime = givenAd.getCreateTime();
                int adTransporterType = TransporterAd.AD_CAR_TYPE;

                TransporterAd ad = new TransporterAd(carMark, adTransporterType,
                        description, phone, userName, startLocation,
                        endLocation, loadCapacity, createTime);

                ad.setKey(givenAd.getKey());
                ad.setOwnerKey(givenAd.getOwnerKey());
                Bitmap extractedBitmap = ThumbnailUtils.extractThumbnail(bitmap,600,600);
                uploadImage(extractedBitmap, new OnImageUploadedListener() {
                    @Override
                    public void onImageUploaded(Uri uri) {
                        if (!TextUtils.isEmpty(givenAd.getImageUri())) {
                            storage.getReferenceFromUrl(givenAd.getImageUri()).delete();
                        }
                        ad.setImageUri(uri.toString());

                        transporterAds.child(givenAd.getKey()).setValue(ad);

                        User user = Auth.getCurrentUser();
                        user.setName(userName);
                        if (!TextUtils.isEmpty(phone)){
                            user.setPhone(phone);
                        }
                        Auth.updateUserInFireBase(user);
                        Toast.makeText(getApplicationContext(),getString(R.string.edit_successful), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        }
    }


    private void uploadImage(Bitmap bitmap, OnImageUploadedListener l){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCancelable(false);
        dialog.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        StorageReference currentImage = adImages.child(System.currentTimeMillis() + "");
        UploadTask uploadTask = currentImage.putBytes(bytes);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return currentImage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                l.onImageUploaded(task.getResult());
                dialog.hide();
            }
        });
    }



    private boolean isFieldsEmpty(String carMark, int loadCapacity, String description,
                                  String startLocation,
                                  String endLocation, String userName){
        boolean isFieldsEmpty = false;

        ArrayList<EditText> wrongFields = new ArrayList<>();

        binding.inputLayoutTransporterCarMark.setErrorEnabled(false);
        binding.inputLayoutTransporterLoadCapacity.setErrorEnabled(false);
        binding.inputLayoutTransporterDescription.setErrorEnabled(false);
        binding.inputLayoutTransporterStartLocation.setErrorEnabled(false);
        binding.inputLayoutTransporterEndLocation.setErrorEnabled(false);
        binding.inputLayoutUserName.setErrorEnabled(false);
        binding.inputLayoutUserContactNumber.setErrorEnabled(false);

        if (TextUtils.isEmpty(carMark)){
            binding.inputLayoutTransporterCarMark.setErrorEnabled(true);
            binding.inputLayoutTransporterCarMark.setError(getString(R.string.error_empty_field));

            wrongFields.add(etCarMark);
            isFieldsEmpty = true;
        }

        if(loadCapacity == 0){
            binding.inputLayoutTransporterLoadCapacity.setErrorEnabled(true);
            binding.inputLayoutTransporterLoadCapacity.setError(getString(R.string.error_empty_field));

            wrongFields.add(etLoadCapacity);
            isFieldsEmpty = true;
        }

        if (TextUtils.isEmpty(description)){
            binding.inputLayoutTransporterDescription.setErrorEnabled(true);
            binding.inputLayoutTransporterDescription.setError(getString(R.string.error_empty_field));

            wrongFields.add(etDescription);
            isFieldsEmpty = true;
        }

        if (TextUtils.isEmpty(startLocation)){
            binding.inputLayoutTransporterStartLocation.setErrorEnabled(true);
            binding.inputLayoutTransporterStartLocation.setError(getString(R.string.error_empty_field));

            wrongFields.add(etStartLocation);
            isFieldsEmpty = true;
        }

        if (TextUtils.isEmpty(endLocation)){
            binding.inputLayoutTransporterEndLocation.setErrorEnabled(true);
            binding.inputLayoutTransporterEndLocation.setError(getString(R.string.error_empty_field));

            wrongFields.add(etEndLocation);
            isFieldsEmpty = true;
        }

        if (TextUtils.isEmpty(userName)){
            binding.inputLayoutUserName.setErrorEnabled(true);
            binding.inputLayoutUserName.setError(getString(R.string.error_empty_field));

            wrongFields.add(etUserName);
            isFieldsEmpty = true;
        }

        String phone = etPhone.getText().toString();
        if (phone.length() != 11 && phone.length() != 0){
            binding.inputLayoutUserContactNumber.setErrorEnabled(true);
            binding.inputLayoutUserContactNumber.setError(getString(R.string.error_incorrect_phone));

            wrongFields.add(etPhone);
            isFieldsEmpty = true;
        }

        //вызов фокуса для первого неверно-заполненного поля
        if (!wrongFields.isEmpty()){
            wrongFields.get(0).requestFocus();
        }


        return isFieldsEmpty;
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