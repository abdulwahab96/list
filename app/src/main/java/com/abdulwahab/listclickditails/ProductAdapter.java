package com.abdulwahab.listclickditails;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    Context context;

    public ProductAdapter(Context context, ArrayList<Product> Products) {
        super(context, 0, Products);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        final Product currentProduct = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.name_product);
        titleTextView.setText(currentProduct.getName_product());

        TextView detailTextView = (TextView) listItemView.findViewById(R.id.detail_product);
        detailTextView.setText(currentProduct.getDetails_product());

        final ImageView productImageView = (ImageView) listItemView.findViewById(R.id.image_product);
        Glide.with(context /* context */)
                .asBitmap()
                .load(currentProduct.getUrl_image_product())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        productImageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });


        return listItemView;
    }
}
