package com.handicape.MarketCreators.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
        Intent Pay_pal =new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/sa/home"));
        startActivities(new Intent[]{Pay_pal});

    }
}
