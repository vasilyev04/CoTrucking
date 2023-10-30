package com.kiparisov.cotrucking.ui.vpfragments.nestedAdFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kiparisov.cotrucking.interfaces.OnImageUploadedListener;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.databinding.FragmentTruckAdTypeBinding;
import com.kiparisov.cotrucking.model.TransporterAd;
import com.kiparisov.cotrucking.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class TruckAdTypeFragment extends Fragment {
    private FragmentTruckAdTypeBinding binding;

    private FirebaseDatabase db;
    private DatabaseReference transporterAds;
    private FirebaseStorage storage;
    private StorageReference adImages;

    private EditText etTruckMark, etLoadCapacity, etDescription,
            etStartLocation, etEndLocation, etUserName, etPhone;

    private int truckType;

    private ActivityResultLauncher<Intent> openCameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> openGalleryActivityResultLauncher;

    private Bitmap bitmap;

    private void init(){
        etTruckMark = binding.etTransporterTruckMark;
        etLoadCapacity = binding.etTransporterTruckLoadCapacity;
        etDescription = binding.etTransporterTruckDescription;
        etStartLocation = binding.etTransporterTruckStartLocation;
        etEndLocation = binding.etTransporterTruckEndLocation;
        etUserName = binding.etUserName;
        etPhone = binding.etUserContactNumber;

        truckType = TransporterAd.TRUCK_TYPE_AWNING;



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
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
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
                    }
                });

        //коллбек с результатом после закрытия галереи
        openGalleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent i = result.getData();

                            //конвертация картинки из галереи в формат Base64
                            try {
                                InputStream is = getActivity().getContentResolver().openInputStream(i.getData());
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerCallBacks();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTruckAdTypeBinding.inflate(getLayoutInflater());

        init();

        initExistingUserData();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rgTruckTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.truck_tent:
                        truckType = TransporterAd.TRUCK_TYPE_AWNING;
                        break;
                    case R.id.truck_closed:
                        truckType = TransporterAd.TRUCK_TYPE_CLOSED;
                        break;
                    default:
                        truckType = TransporterAd.TRUCK_TYPE_OPENED;
                }
            }
        });

        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTransporterTruckAd();
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

    private void createTransporterTruckAd(){
        String truckMark = etTruckMark.getText().toString();
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
        String phone = etPhone.getText().toString();
        if(!isFieldsEmpty(truckMark, loadCapacity, description,
                startLocation, endLocation, userName)){

            if (bitmap == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(R.string.alert_title_question);
                builder.setMessage(getString(R.string.alert_no_photo_message));

                final int finalLoadCapacity = loadCapacity;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case AlertDialog.BUTTON_POSITIVE:
                                Calendar calendar = Calendar.getInstance();
                                long createTime = calendar.getTimeInMillis();
                                int adTransporterType = TransporterAd.AD_TRUCK_TYPE;
                                TransporterAd ad = new TransporterAd(truckMark, adTransporterType,
                                        description, phone, userName, startLocation,
                                        endLocation, finalLoadCapacity, truckType, createTime);

                                ad.setOwnerKey(Auth.getCurrentUser().getKey());
                                DatabaseReference push = transporterAds.push();
                                ad.setKey(push.getKey());
                                push.setValue(ad);

                                Auth.getCurrentUser().getUserAds().add(push.getKey());

                                User user = Auth.getCurrentUser();
                                user.setName(userName);
                                if (!TextUtils.isEmpty(phone)){
                                    user.setPhone(phone);
                                }
                                Auth.updateUserInFireBase(user);

                                requireActivity().onBackPressed();
                                break;
                            case AlertDialog.BUTTON_NEGATIVE:
                                requireActivity().closeContextMenu();
                                binding.btnUploadPhoto.requestFocus();
                                break;
                        }
                    }
                };

                builder.setPositiveButton(getString(R.string.alert_positive_button), listener);
                builder.setNegativeButton(getString(R.string.alert_negative_button), listener);
                builder.show().create();
            }else{
                Calendar calendar = Calendar.getInstance();
                long createTime = calendar.getTimeInMillis();

                int adTransporterType = TransporterAd.AD_TRUCK_TYPE;
                TransporterAd ad = new TransporterAd(truckMark, adTransporterType,
                        description, phone, userName, startLocation,
                        endLocation, loadCapacity, truckType, createTime);

                ad.setOwnerKey(Auth.getCurrentUser().getKey());
                DatabaseReference push = transporterAds.push();
                ad.setKey(push.getKey());
                Bitmap extractedBitmap = ThumbnailUtils.extractThumbnail(bitmap, 600, 600);
                uploadImage(extractedBitmap,
                        new OnImageUploadedListener() {
                    @Override
                    public void onImageUploaded(Uri uri) {
                        ad.setImageUri(uri.toString());
                        push.setValue(ad);

                        Auth.getCurrentUser().getUserAds().add(push.getKey());

                        User user = Auth.getCurrentUser();
                        user.setName(userName);
                        if (!TextUtils.isEmpty(phone)){
                            user.setPhone(phone);
                        }
                        Auth.updateUserInFireBase(user);

                        requireActivity().onBackPressed();
                    }
                });

            }
        }

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

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

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
                requireActivity().closeContextMenu();
            }
        };

        builder.setNegativeButton(getString(R.string.alert_negative_button), listener);
        builder.setTitle(getString(R.string.alert_title_choice));

        builder.create().show();
    }


    private void uploadImage(Bitmap bitmap, OnImageUploadedListener l){
        ProgressDialog dialog = new ProgressDialog(requireContext());
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


    private boolean isFieldsEmpty(String truckMark, int loadCapacity, String description,
                                  String startLocation,
                                  String endLocation, String userName){
        boolean isFieldsEmpty = false;

        ArrayList<EditText> wrongFields = new ArrayList<>();

        binding.inputLayoutTransporterTruckMark.setErrorEnabled(false);
        binding.inputLayoutTransporterLoadCapacity.setErrorEnabled(false);
        binding.inputLayoutTransporterDescription.setErrorEnabled(false);
        binding.inputLayoutTransporterStartLocation.setErrorEnabled(false);
        binding.inputLayoutTransporterEndLocation.setErrorEnabled(false);
        binding.inputLayoutUserName.setErrorEnabled(false);
        binding.inputLayoutUserContactNumber.setErrorEnabled(false);

        if (TextUtils.isEmpty(truckMark)){
            binding.inputLayoutTransporterTruckMark.setErrorEnabled(true);
            binding.inputLayoutTransporterTruckMark.setError(getString(R.string.error_empty_field));

            wrongFields.add(etTruckMark);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}