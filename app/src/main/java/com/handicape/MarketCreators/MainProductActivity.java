package com.handicape.MarketCreators;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.handicape.MarketCreators.Account.LoginActivity;
import com.handicape.MarketCreators.Account.RegisterActivity;
import com.handicape.MarketCreators.Account.SessionSharedPreference;
import com.handicape.MarketCreators.Account.User;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static com.handicape.MarketCreators.Account.SessionSharedPreference.setImageSherPref;
import static com.handicape.MarketCreators.Account.User.email;
import static com.handicape.MarketCreators.Account.User.loginSuccess;
import static com.handicape.MarketCreators.Account.User.url_image;
import static com.handicape.MarketCreators.Account.User.name;

public class MainProductActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    View header;
    FloatingActionButton fab;
    static DrawerLayout drawer;
    static NavController navController;
    public static boolean FLAG_MUST_REFRESH_LIST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProductActivity.this, AddProductActivity.class);
                startActivity(intent);

            }
        });
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile,
                R.id.nav_about, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        checkSession(); // هل المستخدم مسجل دخول من قبل لجلب بياناته
    }

    // هل المستخدم مسجل دخول من قبل لجلب بياناته
    private void checkSession() {
        Menu menu = navigationView.getMenu();

        // Check if UserResponse is Already Logged In
        if (SessionSharedPreference.getLoggedStatus(getApplicationContext())) {
            // الزبون مسجل دخول مسبقا إجلب بياناته وإعرضها
            String n = SessionSharedPreference.getUserName(getApplicationContext());
            String e = SessionSharedPreference.getEmail(getApplicationContext());
            String p = SessionSharedPreference.getEPaypal(getApplicationContext());
            menu.findItem(R.id.nav_profile).setVisible(true);
            if (n.length() > 0 && e.length() > 0) {
                User user = new User(n, e, "", true);
                if (p.length() > 0)
                    user.setE_paypal(p);
                Log.d("paypal", p);
                setProfileData();
            }
        } else {
            menu.findItem(R.id.nav_profile).setVisible(false);
        }
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
//        Toast.makeText(MainProductActivity.this, url_image, Toast.LENGTH_LONG).show();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView login_btn = header.findViewById(R.id.login_btn);
        TextView reg_btn = header.findViewById(R.id.register_btn);
//        Toast.makeText(MainProductActivity.this,"onResume",Toast.LENGTH_LONG).show();
        if (login_btn.getVisibility() != View.GONE | reg_btn.getVisibility() == View.VISIBLE) {
            if (loginSuccess) {
//                Toast.makeText(MainProductActivity.this, loginSuccess + "onResume", Toast.LENGTH_LONG).show();
                setProfileData();     // أظهر بيانات المستخدم بعد تسجيل الدخول
                String p = SessionSharedPreference.getEPaypal(getApplicationContext());
                if (p.length() > 0)
                    User.setE_paypal(p);
                Log.d("paypal", p);
                /*MenuItem itemq = findViewById(R.id.action_delete);
                itemq.setVisible(true);*/
            }
        }

        if (FLAG_MUST_REFRESH_LIST) {
            setGraphView(R.id.nav_home);
            FLAG_MUST_REFRESH_LIST = false;
        }
    }


    // أظهر بيانات المستخدم بعد تسجيل الدخول
    private void setProfileData() {
        fab.show();

        header = navigationView.getHeaderView(0);
        TextView user_name_tv = header.findViewById(R.id.user_name_tv);
        TextView email_tv = header.findViewById(R.id.email_tv);

        final ImageView user_image_view = header.findViewById(R.id.profile_image);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (url_image != null) {
            url_image = SessionSharedPreference.getUrlImage(MainProductActivity.this);
        }
        storageRef.child("users_images/" + url_image)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.d("------+++++", uri.toString());
                Glide.with(MainProductActivity.this /* context */)
                        .asBitmap()
                        .load(uri.toString())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                user_image_view.setImageBitmap(resource);
                                setImageSherPref(getApplicationContext(), encodeTobase64(resource));
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
//                Log.d("-----", exception.getMessage());
            }
        });

        user_name_tv.setText(name);
        user_name_tv.setVisibility(View.VISIBLE);
        email_tv.setText(email);
        email_tv.setVisibility(View.VISIBLE);

        hideBtnLogReg();

        if (url_image != null)
            if (url_image.length() == 0) {
                String i = SessionSharedPreference.getImage(getApplicationContext());
                if (i.length() > 0) {
                    user_image_view.setImageBitmap(decodeBase64(i));
                }
            }

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setVisible(true);
        menu.findItem(R.id.nav_profile).setVisible(true);
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
       /* navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setCheckedItem(R.id.nav_profile);

        drawer.closeDrawer(Gravity.LEFT); //Edit Gravity.START need API 14

        this.getSupportFragmentManager().beginTransaction()
                .remove(new HomeFragment())
                .replace(HomeFragment.id, new ProfileFragment())
                .addToBackStack(null)
                .commit();*/

       /* FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(new ProfileFragment());
        transaction.commit();*/
        if (loginSuccess) {
            setGraphView(R.id.nav_profile);
        }

    }

    public static void setGraphView(int id) {
        NavGraph graph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
        graph.setStartDestination(id);
        navController.setGraph(graph);
        drawer.closeDrawer(Gravity.RIGHT);

    }


    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

//        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void logoutBtn(MenuItem item) {
        loginSuccess = false;
        SessionSharedPreference.setLoggedIn(getApplicationContext(), false, "", "");
        SessionSharedPreference.setImageSherPref(getApplicationContext(), "");
        SessionSharedPreference.setEPaypal("", getApplicationContext());
        SessionSharedPreference.setUrlImage("", getApplicationContext());
        User.setE_paypal("");

        fab.hide();
        header = navigationView.getHeaderView(0);

        TextView user_name_tv = header.findViewById(R.id.user_name_tv);
        TextView email_tv = header.findViewById(R.id.email_tv);

        user_name_tv.setVisibility(View.GONE);
        email_tv.setVisibility(View.GONE);

        TextView login_btn = header.findViewById(R.id.login_btn);
        TextView labeled_v = header.findViewById(R.id.labeled_v);
        TextView register_btn = header.findViewById(R.id.register_btn);

        login_btn.setVisibility(View.VISIBLE);
        labeled_v.setVisibility(View.VISIBLE);
        register_btn.setVisibility(View.VISIBLE);

        Toast.makeText(getApplicationContext(), getString(R.string.logout_done), Toast.LENGTH_SHORT).show();

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setVisible(false);
        menu.findItem(R.id.nav_profile).setVisible(false);

        final ImageView user_image_view = header.findViewById(R.id.profile_image);
        user_image_view.setImageResource(R.mipmap.ic_launcher_round);

        // if profile fragment open close it
        setGraphView(R.id.nav_home);

        /*MenuItem itemq = findViewById(R.id.action_delete);
        itemq.setVisible(false);*/
    }
}

