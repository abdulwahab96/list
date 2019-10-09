package com.handicape.MarketCreators;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.handicape.MarketCreators.ui.profile.ProfileFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import static com.handicape.MarketCreators.User.email;
import static com.handicape.MarketCreators.User.loginSuccess;
import static com.handicape.MarketCreators.User.url_image;
import static com.handicape.MarketCreators.User.name;

public class MainProductActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    View header;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProductActivity.this, AddProductActivity.class);
                startActivity(intent);

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_product, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(MainProductActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void openRegisterActivity(View view) {
        Intent intent = new Intent(MainProductActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView login_btn = header.findViewById(R.id.login_btn);
//        Toast.makeText(MainProductActivity.this,"onResume",Toast.LENGTH_LONG).show();
        if (login_btn.getVisibility() != View.GONE) {
            if (loginSuccess) {
                setProfileData();
            }
        }
    }

    private void setProfileData() {
        fab.show();

        header = navigationView.getHeaderView(0);
        TextView user_name_tv = header.findViewById(R.id.user_name_tv);
        TextView email_tv = header.findViewById(R.id.email_tv);

        final ImageView user_image_view = header.findViewById(R.id.profile_image);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child("users_images/" + url_image)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.d("------", uri.toString());
                Glide.with(MainProductActivity.this /* context */)
                        .asBitmap()
                        .load(uri.toString())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                user_image_view.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("-----", exception.getMessage());
            }
        });

        user_name_tv.setText(name);
        user_name_tv.setVisibility(View.VISIBLE);
        email_tv.setText(email);
        email_tv.setVisibility(View.VISIBLE);

        hideBtnLogReg();
    }

    private void hideBtnLogReg() {
        TextView login_btn = header.findViewById(R.id.login_btn);
        TextView labeled_v = header.findViewById(R.id.labeled_v);
        TextView register_btn = header.findViewById(R.id.register_btn);

        login_btn.setVisibility(View.GONE);
        labeled_v.setVisibility(View.GONE);
        register_btn.setVisibility(View.GONE);

    }

    public void openProfileFragment(View view) {
       /* FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, new ProfileFragment());
        transaction.commit();*/
    }
}
