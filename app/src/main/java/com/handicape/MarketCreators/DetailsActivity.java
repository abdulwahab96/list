package com.handicape.MarketCreators;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.collection.LLRBNode;
import com.handicape.MarketCreators.Account.PaypalActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;

public class DetailsActivity extends AppCompatActivity implements Serializable {

    static Toolbar toolbar;
    Product p;
    boolean imagexists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        final CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
       // toolbarLayout.setTitle("");
        TextView titleTv = (TextView)findViewById(R.id.title);
        titleTv.setText(getIntent().getStringExtra("product_name"));

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        p = (Product) getIntent().getSerializableExtra("MyClass");

        Glide.with(this /* context */)
                .asBitmap()
                .load(p.getUrl_image_product())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        toolbarLayout.setBackground(new BitmapDrawable(getResources(), resource));
                        if (toolbarLayout.getBackground() != null) {
                            imagexists = true;
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });


        TextView product_details = (TextView) findViewById(R.id.product_details);

        String fullDetail = p.getDetails_product() + "\n"
                + getString(R.string.price_view) + " " +p.getPrice_product() + "\n"
                + getString(R.string.count_available) + " " + p.getNumber_of_product() + "\n"
                + getString(R.string.by) + " " + p.getName_owner_product() + "\n"
                + getString(R.string.address_view) + " " + p.getAddress_owner_product() +"\n";
        product_details.setText(fullDetail);// getIntent().getStringExtra("product_details")

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openImage(View view) {
        if (imagexists) {
            Intent intent = new Intent(DetailsActivity.this, OpenImageActivity.class);
            intent.putExtra("MyClass", (Serializable) p);
            startActivity(intent);
        }
    }

    public void donate(View view) {
        Intent intent = new Intent(DetailsActivity.this, PaypalActivity.class);
        startActivity(intent);
    }

    public void buyProduct(View view) {
        // نتشرف بزيارتك لنا على العنوان التالي:
        final String[] m_Text = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);

        // Set up the input
        final TextView tv = new TextView(DetailsActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        tv.setText("نتشرف بزيارتك لنا على العنوان التالي:" + "\n" + p.getAddress_owner_product());
        tv.setGravity(Gravity.RIGHT);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(16,16,16,16);
        builder.setView(tv);

        builder.setNegativeButton(getResources().getString(R.string.alright), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
