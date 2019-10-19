package com.handicape.MarketCreators.ui.home;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.handicape.MarketCreators.DetailsActivity;
import com.handicape.MarketCreators.MainProductActivity;
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
    public static int id;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        id = container.getId();
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

        initDatabase(root);

        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainProductActivity.setGraphView(R.id.nav_home); // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                hideText(root);

            }
        });

        return root;
    }

    private void initDatabase(final View root) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());

                                Product pojo = new Product(document.getId(),
                                        document.getString("name"),
                                        document.getString("price"),
                                        document.getString("number_of_pieces"),
                                        document.getString("name_owner"),
                                        document.getString("address_owner"),
                                        document.getString("details_product"),
                                        document.getString("url_image"),
                                        document.getString("email_owner")
                                );
                                products.add(pojo);
                                mAdapter.notifyDataSetChanged();
                                hide_progress(root);
                            }
                            if (products.isEmpty()) {
                                showText(root);
                                hide_progress(root);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                            hide_progress(root);
                            if (products.isEmpty()) {
                                showText(root);
                            }
                        }
                    }

                });
    }


    private void hide_progress(View root) {
        try {
            ProgressBar progressBar = root.findViewById(R.id.loading_indicator);
            progressBar.setVisibility(View.INVISIBLE);
        } catch (NullPointerException e) {

        }
    }

    private void showText(View root) {
        TextView empty_text = root.findViewById(R.id.empty_view);
        empty_text.setVisibility(View.VISIBLE);
    }

    private void hideText(View root) {
        TextView empty_text = root.findViewById(R.id.empty_view);
        empty_text.setVisibility(View.GONE);
    }

}