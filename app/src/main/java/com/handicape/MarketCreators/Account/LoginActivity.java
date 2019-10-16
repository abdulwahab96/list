package com.handicape.MarketCreators.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.handicape.MarketCreators.MainProductActivity;
import com.handicape.MarketCreators.R;

import java.net.InetAddress;

import static com.handicape.MarketCreators.Account.User.url_image;


public class LoginActivity extends AppCompatActivity {

    Button btnLog;
    EditText edEmail;
    TextInputEditText edPass;
    TextView txtForget, txtNewAcc;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
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

    private void getUrlImage(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String urlImage =  document.toObject(User.class).getUrl_image();
                                SessionSharedPreference.setUrlImage(urlImage,LoginActivity.this);
                                url_image = SessionSharedPreference.getUrlImage(LoginActivity.this);
                                Toast.makeText(LoginActivity.this, User.url_image , Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }
                });
    }

    // تحقق من صحة بيانات تسجيل الدخول من قاعدة البيانات
    private void validData(final String email, String pass) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            getUrlImage(email);
                            progressDialog.dismiss();
//                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        User.loginSuccess = true;
        User.email = user.getEmail();
        User.name = user.getDisplayName();
        SessionSharedPreference.setLoggedIn(getApplicationContext(), true, User.name, User.email);
    }






    public void forgetPasswordBtn(View view) {
        final String[] m_Text = {""};
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Input Email:");

        // Set up the input
        final EditText input = new EditText(LoginActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("sending email...");
                progressDialog.show();

                m_Text[0] = input.getText().toString(); // paypal email
                if (m_Text[0].length() > 0) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.sendPasswordResetEmail(m_Text[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Email sent.", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Email sent.");
                                progressDialog.setMessage("Email sent.");
                                Runnable progressRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                };

                                Handler pdCanceller = new Handler();
                                pdCanceller.postDelayed(progressRunnable, 1000);

                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                Log.d(TAG, task.getException().getMessage());
                                progressDialog.setMessage(task.getException().getMessage());
                                Runnable progressRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                };

                                Handler pdCanceller = new Handler();
                                pdCanceller.postDelayed(progressRunnable, 3000);
                            }

                        }
                    });
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
