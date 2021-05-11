package com.example.ecoomerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecoomerce.Model.Products;
import com.example.ecoomerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;


public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView price,name;
    private String pid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        pid = getIntent().getStringExtra("pid");

        addToCartBtn = (FloatingActionButton) findViewById(R.id.addtocart);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.ivimage);
        price = (TextView) findViewById(R.id.txtpp);
        name = (TextView) findViewById(R.id.txtpn);

        getProductDetails(pid);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });

    }

    private void addingToCartList() {
        String date,time;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        date = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        time = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartmap = new HashMap<>();
        cartmap.put("pid",pid);
        cartmap.put("pname",name.getText().toString());
        cartmap.put("pprice",price.getText().toString());
        cartmap.put("quantity",numberButton.getNumber());
        cartmap.put("time",time);
        cartmap.put("date",date);

        cartListRef.child("User View").child(Prevalent.CurrentOnlineUser.getPhone())
                .child("Products").child(pid).updateChildren(cartmap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    cartListRef.child("Admin View").child(Prevalent.CurrentOnlineUser.getPhone())
                            .child("Products").child(pid).updateChildren(cartmap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ProductDetailsActivity.this,"Added to Cart List", Toast.LENGTH_SHORT);
                                        Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void getProductDetails(String pid) {
        DatabaseReference pref = FirebaseDatabase.getInstance().getReference().child("Products");
        pref.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Products p = dataSnapshot.getValue(Products.class);
                    name.setText(p.getCategory());
                    price.setText(p.getPrice());
                    Picasso.get().load(p.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
