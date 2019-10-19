package com.handicape.MarketCreators;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.handicape.MarketCreators.Account.PaypalActivity;
import com.handicape.MarketCreators.Account.SessionSharedPreference;
import com.handicape.MarketCreators.Account.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity implements Serializable {

    static Toolbar toolbar;
    Product p;
    boolean imagexists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        final CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);

        TextView titleTv = findViewById(R.id.title);
        titleTv.setText(getIntent().getStringExtra("product_name"));

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        p = (Product) getIntent().getSerializableExtra("MyClass");

        if (p.getEmail_owner() != null && User.email != null && User.loginSuccess) {
            if (p.getEmail_owner().equalsIgnoreCase(User.email))
                viewButtonOwner();
            else
                viewButtonOthers();
        } else {
            viewButtonOthers();
        }

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


        TextView product_details = findViewById(R.id.product_details);

        String fullDetail = "\n" + p.getDetails_product() + "\n\n"
                + getString(R.string.price_view) + " " + p.getPrice_product() + "\n\n"
                + getString(R.string.count_available) + " " + p.getNumber_of_product() + "\n\n"
                + getString(R.string.by) + " " + p.getName_owner_product() + "\n\n"
                + getString(R.string.address_view) + " " + p.getAddress_owner_product() + "\n\n";
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

    private void viewButtonOwner() {
        Button deleteBtn = findViewById(R.id.delete_btn);
        Button buyBtn = findViewById(R.id.buy_btn);
        Button donateBtn = findViewById(R.id.donate_btn);

        deleteBtn.setVisibility(View.VISIBLE);
        buyBtn.setVisibility(View.VISIBLE);
        donateBtn.setVisibility(View.GONE);
    }

    private void viewButtonOthers() {
        Button deleteBtn = findViewById(R.id.delete_btn);
        Button buyBtn = findViewById(R.id.buy_btn);
        Button donateBtn = findViewById(R.id.donate_btn);

        deleteBtn.setVisibility(View.GONE);
        buyBtn.setVisibility(View.VISIBLE);
        donateBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
    /*    if (User.loginSuccess)
            menu.findItem(R.id.action_delete).setVisible(true);
        else
            menu.findItem(R.id.action_delete).setVisible(false);*/
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
            intent.putExtra("MyClass", p);
            startActivity(intent);
        }
    }

    public void donate(View view) {
        Intent intent = new Intent(DetailsActivity.this, PaypalActivity.class);
        startActivity(intent);
    }

    public void buyProduct(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);

        // Set up the input
        final TextView tv = new TextView(DetailsActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        tv.setText("نتشرف بزيارتك لنا على العنوان التالي:" + "\n" + p.getAddress_owner_product());
        tv.setGravity(Gravity.RIGHT);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(16, 16, 16, 16);
        builder.setView(tv);

        builder.setNegativeButton(getResources().getString(R.string.alright), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void deleteProductByOwner(View view) {
        final AlertDialog diaBox = AskOption();
        diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                diaBox.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            }
        });
        diaBox.show();
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle(getResources().getString(R.string.action_delete))
                .setMessage("\n" + getResources().getString(R.string.confirm_delete_prod))
                .setIcon(R.drawable.confirm_delete)

                .setPositiveButton(getResources().getString(R.string.action_delete), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteProductFromDatabase();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    private void deleteProductFromDatabase() {

        final ProgressDialog progressDialog = new ProgressDialog(DetailsActivity.this);
        progressDialog.setMessage(getString(R.string.progress_delete));
        progressDialog.show();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products").document(p.getId_product())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DetailsActivity", "DocumentSnapshot successfully deleted!");
                        Toast.makeText(DetailsActivity.this, getResources().getString(R.string.delete_done), Toast.LENGTH_SHORT);

                        // Create a storage reference from our app
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        // Create a reference to the file to delete
                        String t = p.getUrl_image_product();
                        if (t.length() > 0)
                            t = t.substring(t.indexOf("images%2F") + 9, t.indexOf("?alt"));
                        Log.d("DetailsActivity", t);

                        StorageReference desertRef = storageRef.child("images/" + t);
                        // Delete the file
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully

                                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.success_delete_prod), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                MainProductActivity.FLAG_MUST_REFRESH_LIST = true;
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Toast.makeText(DetailsActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DetailsActivity", "Error deleting document", e);
                        Toast.makeText(DetailsActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

}
