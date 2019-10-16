package com.handicape.MarketCreators.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.handicape.MarketCreators.R;

public class PaypalActivity extends AppCompatActivity {

    EditText editText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        editText = findViewById(R.id.editText);
        editText.setText(SessionSharedPreference.getEPaypal(PaypalActivity.this));
    }

    public void Pay_pal(View view) {

    }
}
