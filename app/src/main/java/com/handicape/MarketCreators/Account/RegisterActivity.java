package com.handicape.MarketCreators.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.handicape.MarketCreators.MainProductActivity;
import com.handicape.MarketCreators.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    Button registration_button;
    EditText ed_user_name, ed_user_email;
    TextInputEditText ed_user_pass;
    TextView txt_have_acc;
    ImageView user_img;
    Uri photo_uri;
    String imageName,
            user_name,
            user_pass,
            user_email;
    private FirebaseAuth mAuth;
    private static final String TAG = "RegisterActivity";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init(); // Initializing

        // عند الضغط على لدي حساب بالفعل
        txt_have_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void init() {
        // Initializing
        registration_button = findViewById(R.id.registration_button);
        ed_user_name = findViewById(R.id.user_registration_name);
        ed_user_pass = findViewById(R.id.user_registration_password);
        ed_user_email = findViewById(R.id.user_registration_email);
        txt_have_acc = findViewById(R.id.have_account);
        user_img = (ImageView) findViewById(R.id.user_image);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    // عند الضعط على إنشاء حساب
    public void createAccoutn(View view) {

        user_name = ed_user_name.getText().toString();
        user_pass = ed_user_pass.getText().toString();
        user_email = ed_user_email.getText().toString();

        Log.d(TAG, "createAccount:" + user_email);
        if (!validateForm()) {
            return;
        }

        progressDialog.setMessage("Signing up...");
        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(user_email, user_pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user_name).build();
                            user.updateProfile(profileUpdates);

//                            Toast.makeText(RegisterActivity.this, "Create user success",
//                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                            uploadData();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String e = task.getException() + "";
                            if (e.contains("use by another")) {
                                e = e.substring(61, e.length());
//                                Toast.makeText(RegisterActivity.this, e ,
//                                        Toast.LENGTH_LONG).show();
                                ed_user_email.setError(e);
                            } else if (e.contains("password")) {
                                e = e.substring(61, e.length());
                                ed_user_pass.setError(e);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed." + e,
                                        Toast.LENGTH_LONG).show();
                            }
//                            updateUI(null);
                            hideProgressDialog();
                        }
                    }
                });
    }

    private void updateUI() {
        User.loginSuccess = true;
        User.email = user_email;
        User.name = user_name;
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private void showProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    //إرفع البيانات إلى القاعدة
    private void uploadData() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (photo_uri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            imageName = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("users_images/" + imageName);
            ref.putFile(photo_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(RegisterActivity.this, "Uploaded Done", Toast.LENGTH_SHORT).show();
                            User.url_image = imageName;
                            SessionSharedPreference.setUrlImage(imageName,RegisterActivity.this);
                            uploadInfo(db, true);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG,e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            uploadInfo(db, false);
            hideProgressDialog();
            finish();
        }

    }

    void uploadInfo(FirebaseFirestore db, final boolean imageExists) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", user_name);
        product.put("pass", user_pass);
        product.put("email", user_email);
        product.put("url_image", imageName);

        // Add a new document with a generated ID
        db.collection("users")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(RegisterActivity.this, "Create user success!", Toast.LENGTH_SHORT).show();

                        updateUI();
                        SessionSharedPreference.setLoggedIn(getApplicationContext(), true, User.name, User.email);

                        hideProgressDialog();
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_faild), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // إختر صورة من الإستوديوا
    public void addImage(View view) {
        Intent i = new Intent
                (Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Select Your Photo"), 1);
    }

    // نتيجة إختيار الصورة من الإستوديوا
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if(resultCode == RESULT_OK) تعني ان كان قد تم الحصول على البيانات بدون مشاكل
        if (requestCode == 1 && resultCode == RESULT_OK) {
            photo_uri = data.getData();
            Bitmap selected_photo = null;
            try {
                InputStream imagestream = getContentResolver().openInputStream(photo_uri);
                selected_photo = BitmapFactory.decodeStream(imagestream);
                user_img.setImageBitmap(selected_photo);
            } catch (FileNotFoundException FNFE) {
                Toast.makeText(RegisterActivity.this, FNFE.getMessage(), Toast.LENGTH_LONG).show();
            }

            //للحفاظ على مقاسات الصوؤة
            selected_photo = Bitmap.createScaledBitmap
                    (selected_photo, 200, 200, true);
            user_img.setImageBitmap(selected_photo);

            //لعدم دوران الصورة
            Matrix matrix = new Matrix();
            matrix.postRotate(0);
            Bitmap rotated_photo = Bitmap.createBitmap(selected_photo, 0, 0,
                    selected_photo.getWidth(), selected_photo.getHeight(), matrix, true);

        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = ed_user_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            ed_user_email.setError("Required.");
            valid = false;
        } else {
            ed_user_email.setError(null);
        }

        String password = ed_user_pass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ed_user_pass.setError("Required.");
            valid = false;
        } else {
            ed_user_pass.setError(null);
        }

        String userName = ed_user_name.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            ed_user_name.setError("Required.");
            valid = false;
        } else {
            ed_user_name.setError(null);
        }

        return valid;
    }
}
