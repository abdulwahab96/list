package com.handicape.MarketCreators;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    Button btnLog;
    EditText edEmail, edPass;
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

    private void validData(String email, String pass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("pass", pass)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Log.d("-----", queryDocumentSnapshots.toString());
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Faild", Toast.LENGTH_LONG).show();
                        }
//                        Toast.makeText(LoginActivity.this, queryDocumentSnapshots.isEmpty()+ " ", Toast.LENGTH_LONG).show();
                    }

                });
    }
}
