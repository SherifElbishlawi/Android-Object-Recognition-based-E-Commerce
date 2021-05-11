package com.example.ecoomerce;//RecyclerAdapter


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.ecoomerce.Model.Products;
import com.example.ecoomerce.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    List<Products> listOfProducts = new ArrayList<Products>();

    Context context;
    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(Context c) {
        context = c;
    }


    void setProductList(List<Products> listOfProducts) {
        this.listOfProducts = listOfProducts;
        notifyDataSetChanged();
    }

    void addMoreData(List<Products> listOfProducts) {
        this.listOfProducts.addAll(listOfProducts);
        notifyDataSetChanged();
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ProductViewHolder vh = new ProductViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, final int position) {
        holder.txtpname.setText(listOfProducts.get(position).getCategory());
        holder.txtpprice.setText("Price =" + listOfProducts.get(position).getPrice() + "$");
        Picasso.get().load(listOfProducts.get(position).getImage()).into(holder.iv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProductDetailsActivity.class);
                intent.putExtra("pid",listOfProducts.get(position).getPid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfProducts.size();
    }
}

