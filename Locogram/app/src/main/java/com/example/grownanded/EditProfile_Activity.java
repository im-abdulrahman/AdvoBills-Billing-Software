package com.example.grownanded;

import static com.google.firebase.FirebaseApp.getInstance;
import static com.google.firebase.FirebaseApp.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

public class EditProfile_Activity extends AppCompatActivity {

    TextView editPage_display_name,editPage_display_email, editPage_display_bio;
    ImageView editPage_display_image, btn_close_edit_profile_activity;
    Button btn_update_image, btn_update_name, btn_update_bio;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private Dialog loading_dialog;

    //permissions
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;
    // array of permission to be request
    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;
    String ProfilePhoto;
    //storage
    StorageReference storageReference ;
    String storagePath = "Users_Profile_images/";

    Dialog dialog;
    Dialog dialog2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


//***********************************************************************************************************

        // todo : EditProfile activity Set and init

        btn_close_edit_profile_activity = findViewById(R.id.toolbar_btn_back_edit_profile);
        editPage_display_email = findViewById(R.id.editPage_display_email);
        btn_update_image = findViewById(R.id.btn_update_image);
        btn_update_name = findViewById(R.id.btn_update_name);
        btn_update_bio = findViewById(R.id.btn_update_bio);

        // loading dialog
        loading_dialog = new Dialog(this);
        loading_dialog.setContentView(R.layout.layout_loading_dialog);
        loading_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // init array of permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


//***********************************************************************************************************

        // todo: Profile data set from firebase

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        editPage_display_image = findViewById(R.id.editPage_display_image);
        editPage_display_name = findViewById(R.id.editPage_display_name);
        editPage_display_bio = findViewById(R.id.editPage_display_bio);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    String bio = "" + ds.child("bio").getValue();
                    String image = ""+ds.child("image").getValue();
                    String name = ""+ds.child("name").getValue();

                    editPage_display_bio.setText(bio);
                    editPage_display_name.setText(name);

                    Picasso.get().load(image).into(editPage_display_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Todo : Show user profile data

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null){

            String Email=account.getEmail();

            editPage_display_email.setText(Email);

        }

        // Todo : Back btn click

        btn_close_edit_profile_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile_Activity.this,Settings_Activity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
        });

        // todo : Update image btn

        btn_update_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilePhoto = "image";
                ShowImagePicDialog();
            }
        });

        // todo : Update name btn

        btn_update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ShowNameUpdateDialog("name");
            }
        });

        // todo : Update name btn

        btn_update_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowBioUpdateDialog("bio");
            }
        });
    }

    private void ShowNameUpdateDialog(String name) {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_edittext_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Button btn_confirm_changes = dialog.findViewById(R.id.btn_save_changes);
        Button btn_cancel_changes = dialog.findViewById(R.id.btn_cancel_changes);
        TextView textView_title = dialog.findViewById(R.id.TV_EditAlertBox);
        TextInputLayout TextInputLayout_edit_profile = dialog.findViewById(R.id.TextInputLayout_edit_profile);
        EditText inputText_edit_profile = dialog.findViewById(R.id.TextInputEditText_edit_profile);

        TextInputLayout_edit_profile.setHint("Edit " + name);
        TextInputLayout_edit_profile.setCounterMaxLength(15);
        textView_title.setText("Update " + name);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    String old_name = ""+ds.child("name").getValue();
                    inputText_edit_profile.setText(old_name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_cancel_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = inputText_edit_profile.getText().toString().trim();
                if (inputText_edit_profile.length()<=15 ) {
                    if (inputText_edit_profile.length()>0){
                        if (!TextUtils.isEmpty(value)){
                            loading_dialog.show();

                            HashMap<String,Object> result = new HashMap<>();
                            result.put(name, value);

                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            loading_dialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(EditProfile_Activity.this,name + " Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loading_dialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(EditProfile_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        } else {
                            Toast.makeText(EditProfile_Activity.this, "please enter " + name, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        TextInputLayout_edit_profile.setHelperText("Please Enter Name");

                    }

                }else {
                    TextInputLayout_edit_profile.setHelperText("Your name is to long !");

                }

            }
        });

        dialog.show();

    }

    private void ShowBioUpdateDialog(String bio) {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_edittext_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Button btn_confirm_changes = dialog.findViewById(R.id.btn_save_changes);
        Button btn_cancel_changes = dialog.findViewById(R.id.btn_cancel_changes);
        TextView textView_title = dialog.findViewById(R.id.TV_EditAlertBox);
        TextInputLayout TextInputLayout_edit_profile = dialog.findViewById(R.id.TextInputLayout_edit_profile);
        TextInputEditText inputText_edit_profile = dialog.findViewById(R.id.TextInputEditText_edit_profile);

        TextInputLayout_edit_profile.setHint("Edit " + bio);
        TextInputLayout_edit_profile.setCounterMaxLength(60);
        textView_title.setText("Update " + bio);


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    String old_bio = ""+ds.child("bio").getValue();
                    inputText_edit_profile.setText(old_bio);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    btn_cancel_changes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    });

        btn_confirm_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = inputText_edit_profile.getText().toString().trim();
                if (inputText_edit_profile.length()<=60){
                    if (inputText_edit_profile.length()>0){
                        if (!TextUtils.isEmpty(value)){
                            loading_dialog.show();
                            HashMap<String,Object> result = new HashMap<>();
                            result.put(bio, value);

                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            loading_dialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(EditProfile_Activity.this,bio + " Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loading_dialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(EditProfile_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        } else {
                            Toast.makeText(EditProfile_Activity.this, "please enter " + bio, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        TextInputLayout_edit_profile.setHelperText("Please Enter Bio");

                    }


                } else {
                    TextInputLayout_edit_profile.setHelperText("Your bio is to long !");
                }
                         }
        });

        dialog.show();

    }

    // todo : EditProfile activity permissions

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(EditProfile_Activity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requestStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
        }
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(EditProfile_Activity.this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(EditProfile_Activity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
        }
    }


//***********************************************************************************************************

    // todo : Custom Methode ( update image dialog )

    private void ShowImagePicDialog() {
        dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.layout_2_buttons_dialog);
        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView alert = dialog2.findViewById(R.id.TV_EditAlertBox_2btn_dialog);
        TextView option1 = dialog2.findViewById(R.id.TV_option1_2btn_dialog);
        TextView option2 = dialog2.findViewById(R.id.TV_option2_2btn_dialog);
        LinearLayout cameraBtn = dialog2.findViewById(R.id.btn1_2btn_dialog);
        LinearLayout galleryBtn = dialog2.findViewById(R.id.btn2_2btn_dialog);

        alert.setText("Pick Image From");
        option1.setText("Camera");
        option2.setText("Gallery");

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                if (!checkCameraPermission()){
                    requestCameraPermission();
                } else {
                    PickFromCamera();
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                if (!checkStoragePermission()){
                    requestStoragePermission();
                } else {
                    PickFromGallery();
                }
            }
        });

        dialog2.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //todo : permission Enabled
                        PickFromCamera();
                    }
                    else {
                        //todo : permission denied
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //todo : permission Enabled
                        PickFromGallery();
                    }
                    else {
                        //todo : permission denied
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();

                    }
                }
            } break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                image_uri = data.getData();
                UploadProfileImagePhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){

                UploadProfileImagePhoto(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadProfileImagePhoto(Uri uri) {
        loading_dialog.show();
        String filePathAndName = storagePath+""+ProfilePhoto+""+ user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()){
                            HashMap<String,Object> results = new HashMap<>();
                            results.put(ProfilePhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            loading_dialog.dismiss();
                                            Toast.makeText(EditProfile_Activity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loading_dialog.dismiss();
                                            Toast.makeText(EditProfile_Activity.this, "error ! try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(EditProfile_Activity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
               .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading_dialog.dismiss();
                        Toast.makeText(EditProfile_Activity.this, "Error ! try again" , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void PickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);
    }

    private void PickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditProfile_Activity.this,Settings_Activity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }

}