package com.handicape.MarketCreators;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    }

    // عند الضعط على إنشاء حساب
    public void createAccoutn(View view) {

        user_name = ed_user_name.getText().toString();
        user_pass = ed_user_pass.getText().toString();
        user_email = ed_user_email.getText().toString();

        if (user_email.matches("^(.+)@(.+)$")) {
            if (true) {//validEmailOnline(user_email)
                if (user_name.matches("[a-zA-Z0-9\\._\\-]{3,}")) {
                    if (!user_pass.isEmpty()) {

                        uploadData(); //إرفع البيانات إلى القاعدة
                    }
               /* if (user_pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {

                } else {
                    Toast.makeText(RegisterActivity.this, "8 char password!", Toast.LENGTH_SHORT).show();
                }*/
                } else {
                    Toast.makeText(RegisterActivity.this, "Invalid Username!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
        }

    }

    // تحقق من أن الإيميل لم يسجل من قبل
    private boolean validEmailOnline(String user_email) {

        final boolean[] notExist = new boolean[1];
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", user_email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "This email is already registered", Toast.LENGTH_LONG).show();
                            notExist[0] = false;

                        } else {
                            notExist[0] = true;

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "error net", Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(RegisterActivity.this, notExist[0] + "", Toast.LENGTH_SHORT).show();
        return notExist[0];
    }

    //إرفع البيانات إلى القاعدة
    private void uploadData() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if (photo_uri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Signing up...");
            progressDialog.show();

            imageName = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child("users_images/" + imageName);
            ref.putFile(photo_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
//                            Toast.makeText(RegisterActivity.this, "Uploaded Done", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                            Toast.makeText(RegisterActivity.this, "Register done!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error adding document", e);
                                        }
                                    });
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing up...");
            progressDialog.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                            progressDialog.dismiss();
                            Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(RegisterActivity.this, "Register done!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Register Faild", Toast.LENGTH_LONG).show();
                        }
                    });
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    // إختر صورة من الإستوديوا
    public void addImage(View view) {
        Intent i = new Intent
                (Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Select Your Photo"), 1);
    }

    // عند إختيار الصورة من الإستوديوا
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
}
