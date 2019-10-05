package com.abdulwahab.listclickditails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Serializable {

    private static ArrayList<Product> products;
    private static ProductAdapter mAdapter;
    private ListView productListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        products = new ArrayList<Product>();
        productListView = (ListView) findViewById(R.id.list_view);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Product p = products.get(position);
                intent.putExtra("product_name", p.getName_product());
                intent.putExtra("product_details", p.getDetails_product());
                intent.putExtra("MyClass", (Serializable) products.get(position));
                startActivity(intent);
            }
        });

       /* Product product1 = new Product("cake", "25$", "7",
                "khaled", "syria, homs", "it's very nice!");

        Product product2 = new Product("cake", "25$", "7",
                "khaled", "syria, homs", "it's very nice!");
        Product product3 = new Product("cake", "25$", "7",
                "khaled", "syria, homs", "it's very nice!");
        Product product4 = new Product("cake", "25$", "7",
                "khaled", "syria, homs", "it's very nice!");
        Product product5 = new Product("cake", "25$", "7",
                "khaled", "syria, homs", "it's very nice!");

        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);*/

        mAdapter = new ProductAdapter(this, products);
        productListView.setAdapter(mAdapter);

        initDatabsae();
    }

    private void initDatabsae() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
      /*  Map<String, Object> product = new HashMap<>();
        product.put("name", "cak");
        product.put("address_owner", "syria");
        product.put("details_product", "it's very nice!!");
        product.put("number_of_pieces", "150");
        product.put("price", "2$");
        product.put("name_owner", "Abdulwahab");

        // Add a new document with a generated ID
        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });*/


        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());

                                Product pojo = new Product(document.getString("name"),
                                        document.getString("price"),
                                        document.getString("number_of_pieces"),
                                        document.getString("name_owner"),
                                        document.getString("address_owner"),
                                        document.getString("details_product"),
                                        document.getString("url_image")
                                );
                                products.add(pojo);
                                mAdapter.notifyDataSetChanged();
                                hide_progress();
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                            showText();

                        }
                    }

                });

    }

    private void hide_progress() {
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.loading_indicator);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showText() {
        hide_progress();
        TextView empty_text = (TextView)findViewById(R.id.empty_view);
        empty_text.setVisibility(View.VISIBLE);
    }
}
