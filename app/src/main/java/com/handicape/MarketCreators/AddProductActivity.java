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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    ImageView img;
    Button upload;
    Uri photo_uri;
    String imageName;

    EditText Name;
    EditText p_Name;
    EditText address;
    EditText available;
    EditText price;
    EditText details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        Button upload = (Button) findViewById(R.id.upload);
        img = (ImageView) findViewById(R.id.gallery);

        // إختر صورة من الإستوديوا
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent
                        (Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(i, "Select Your Photo"), 1);
            }
        });
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
                img.setImageBitmap(selected_photo);
            } catch (FileNotFoundException FNFE) {
                Toast.makeText(AddProductActivity.this, FNFE.getMessage(), Toast.LENGTH_LONG).show();
            }

            //للحفاظ على مقاسات الصوؤة
            selected_photo = Bitmap.createScaledBitmap
                    (selected_photo, 200, 200, true);
            img.setImageBitmap(selected_photo);

            //لعدم دوران الصورة
            Matrix matrix = new Matrix();
            matrix.postRotate(0);
            Bitmap rotated_photo = Bitmap.createBitmap(selected_photo, 0, 0,
                    selected_photo.getWidth(), selected_photo.getHeight(), matrix, true);

        }
    }


    // الإنتقال إلى أكتيفيتي عرض قائمة المنتجات
    public void openListProduct(View view) {
        finish();
    }

    // إرفع البيانات إلى القاعدة
    private synchronized void uploadData() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if (photo_uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            imageName = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child("images/" + imageName);
            ref.putFile(photo_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Uploaded Done", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> product = new HashMap<>();
                            product.put("name", p_Name.getText().toString());
                            product.put("address_owner", address.getText().toString());
                            product.put("details_product", details.getText().toString());
                            product.put("number_of_pieces", available.getText().toString());
                            product.put("price", price.getText().toString());
                            product.put("name_owner", Name.getText().toString());
                            product.put("url_image", imageName);

                            // Add a new document with a generated ID
                            db.collection("products")
                                    .add(product)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                            Toast.makeText(AddProductActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error adding document", e);
                                        }
                                    });
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // عند الضعظ على زر إضافة المنتج قم بإضافنه على قاعد البيانات
        if (id == R.id.add_done) {
            img = (ImageView) findViewById(R.id.gallery);
            Name = (EditText) findViewById(R.id.Name);
            p_Name = (EditText) findViewById(R.id.p_Name);
            address = (EditText) findViewById(R.id.address);
            available = (EditText) findViewById(R.id.available);
            price = (EditText) findViewById(R.id.price);
            details = (EditText) findViewById(R.id.p_details);

            uploadData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

