package com.handicape.MarketCreators.ui.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.handicape.MarketCreators.Account.RegisterActivity;
import com.handicape.MarketCreators.Account.SessionSharedPreference;
import com.handicape.MarketCreators.MainProductActivity;
import com.handicape.MarketCreators.R;
import com.handicape.MarketCreators.Account.User;

import java.util.HashMap;
import java.util.Map;

import static com.handicape.MarketCreators.Account.User.loginSuccess;
import static com.handicape.MarketCreators.Account.User.url_image;
import static com.handicape.MarketCreators.MainProductActivity.decodeBase64;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final ImageView proImage = root.findViewById(R.id.pro_image);
        final TextView proName = root.findViewById(R.id.pro_name);
        final TextView proEmail = root.findViewById(R.id.pro_email);
        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
                if (loginSuccess) {
                    proName.setText(User.name);
                    proEmail.setText(User.email);
                    paypalView(User.getE_paypal(), root);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    storageRef.child("users_images/" + User.url_image)
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            Log.d(" uri.toString", uri.toString());
                            Glide.with(getActivity().getApplicationContext())
                                    .asBitmap()
                                    .load(uri.toString())
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            proImage.setImageBitmap(resource);
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
                            Log.d(" uri.toString", exception.getMessage());
                        }
                    });
                }
            }
        });

        if (url_image != null)
            if (url_image.length() == 0) {
                String i = SessionSharedPreference.getImage(getActivity().getApplicationContext());
                if (i.length() > 0) {
                    proImage.setImageBitmap(decodeBase64(i));
                }
            }
        Button addEPaypalBtn = (Button) root.findViewById(R.id.add_paypal_btn);
        addEPaypalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailPaypal(root);
            }
        });
        return root;
    }

    public void addEmailPaypal(final View root) {
        final String[] m_Text = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Input E-Paypal:");

        // Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text[0] = input.getText().toString(); // paypal email
                if (m_Text[0].length() > 0) {
                    addToDatabase(m_Text[0], root);
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

    private void addToDatabase(final String s, final View root) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Add paypal email...");
        progressDialog.show();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String, Object> map = new HashMap<>();
        map.put("e_paypal", s);

        // Add a new document with a generated ID
        db.collection("users")
                .whereEqualTo("email", User.email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    db.collection("users").document(document.getId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Add paypal email done!", Toast.LENGTH_SHORT).show();
                                            // افة إلى بيانات الجلسة أوفلاين
                                            SessionSharedPreference.setEPaypal(s, getContext());
                                            User.setE_paypal(s);
                                            // عرض إيميل البايبال
                                            paypalView(s, root);
                                            progressDialog.dismiss();
                                        }
                                    });

                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Add paypal email faild", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    void paypalView(String e_paypal, final View root) {
        LinearLayout linearLayout = root.findViewById(R.id.paypal_view_Linear);
        Button add_paypal_btn = root.findViewById(R.id.add_paypal_btn);

        if (e_paypal != null)
            if (e_paypal.length() > 0) {
                linearLayout.setVisibility(View.VISIBLE);
                add_paypal_btn.setVisibility(View.GONE);

                TextView tvEPaypal = root.findViewById(R.id.e_paypal);
                tvEPaypal.setText(e_paypal);
            } else {
                linearLayout.setVisibility(View.GONE);
                add_paypal_btn.setVisibility(View.VISIBLE);
            }
    }
}