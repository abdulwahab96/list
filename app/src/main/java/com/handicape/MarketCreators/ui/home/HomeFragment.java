package com.handicape.MarketCreators.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.handicape.MarketCreators.DetailsActivity;
import com.handicape.MarketCreators.MainActivity;
import com.handicape.MarketCreators.Product;
import com.handicape.MarketCreators.ProductAdapter;
import com.handicape.MarketCreators.R;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements Serializable {

    private HomeViewModel homeViewModel;
    static ArrayList<Product> products;
    static ProductAdapter mAdapter;
    private ListView productListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        products = new ArrayList<Product>();
        productListView = (ListView) root.findViewById(R.id.list_view);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity().getBaseContext(), DetailsActivity.class);
                Product p = products.get(position);
                intent.putExtra("product_name", p.getName_product());
                intent.putExtra("product_details", p.getDetails_product());
                intent.putExtra("MyClass", (Serializable) products.get(position));
                startActivity(intent);
            }
        });

        mAdapter = new ProductAdapter(getActivity().getBaseContext(), products);
        productListView.setAdapter(mAdapter);

        initDatabsae();

        return root;
    }

    private void initDatabsae() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        ProgressBar progressBar = (ProgressBar)getView(). findViewById(R.id.loading_indicator);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showText() {
        hide_progress();
        TextView empty_text = (TextView) getView().findViewById(R.id.empty_view);
        empty_text.setVisibility(View.VISIBLE);
    }
}