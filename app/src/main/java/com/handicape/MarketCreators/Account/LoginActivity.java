package com.handicape.MarketCreators.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.handicape.MarketCreators.R;

import java.net.InetAddress;


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
    private void validData(final String email, String pass) {

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
                                Log.d("-----", task.toString() + " ,-----------");

                                if (user.getName().length()>0){
                                    User.loginSuccess = true;
                                }else {
                                    User.loginSuccess = false;
                                }
                            }
                            progressDialog.dismiss();
                            if (User.loginSuccess) {
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                                // Set Logged In statue to 'true'
                                SessionSharedPreference.setLoggedIn(getApplicationContext(), true, User.name, User.email);
                                finish();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Wrong email or password..", Toast.LENGTH_LONG).show();
                                Log.d("-----", task.toString() + " ,++---------");

                            }

                        } else {
                            Log.d("-----", "Error getting documents: ", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Faild", Toast.LENGTH_LONG).show();
                    }
                })
        .addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Login Faild", Toast.LENGTH_LONG).show();

            }
        });
    }
}
