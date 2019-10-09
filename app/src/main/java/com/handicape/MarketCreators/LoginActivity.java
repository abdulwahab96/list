package com.handicape.MarketCreators;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class LoginActivity extends AppCompatActivity {

    Button btnLog;
    EditText edEmail;
    TextInputEditText edPass;
    TextView txtForget, txtNewAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init(); // Initializing

        // إذا لم يكن لديك حساب إنتقل إلى واجهة التسجيل
        txtNewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

    }

    // Initializing
    private void init() {
        btnLog = findViewById(R.id.log_btn);
        edEmail = findViewById(R.id.log_email);
        edPass = findViewById(R.id.log_pass);
        txtForget = findViewById(R.id.forget_pass);
        txtNewAcc = findViewById(R.id.new_acc);
    }

    // عند الضغط على تسجيل الدخول
    public void logInClick(View view) {
        String email = edEmail.getText().toString();
        String pass = edPass.getText().toString();
        if (!(email.isEmpty() && pass.isEmpty())) {
            validData(email, pass);
        } else {
            Toast.makeText(LoginActivity.this, "Email or Password is empty!", Toast.LENGTH_LONG).show();
        }
    }

    // تحقق من صحة بيانات تسجيل الدخول من قاعدة البيانات
    private void validData(String email, String pass) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("pass", pass)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                document.getId();
                                User user = document.toObject(User.class);
//                                Log.d("-----", document.getId() + " => " + user.getName());
                                User.loginSuccess=true;
                            }
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                            finish();

                        } else {
                            Log.d("-----", "Error getting documents: ", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Faild", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                /*.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Log.d("-----", queryDocumentSnapshots.toString());
                        if (!queryDocumentSnapshots.isEmpty()) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            setProfileData();
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Faild", Toast.LENGTH_LONG).show();
                        }
//                        Toast.makeText(LoginActivity.this, queryDocumentSnapshots.isEmpty()+ " ", Toast.LENGTH_LONG).show();
                    }

                })*/
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Faild", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
