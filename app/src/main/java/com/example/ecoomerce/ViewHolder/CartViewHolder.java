package com.example.ecoomerce.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoomerce.Interface.ItemClickListner;
import com.example.ecoomerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtpn,txtpp,txtpq;
    private ItemClickListner itemClickListner;

    public CartViewHolder(View itemView) {
        super(itemView);
        txtpn = itemView.findViewById(R.id.cartpn);
        txtpp = itemView.findViewById(R.id.cartpp);
        txtpq = itemView.findViewById(R.id.cartq);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner)
    {
        this.itemClickListner = itemClickListner;
    }
}
