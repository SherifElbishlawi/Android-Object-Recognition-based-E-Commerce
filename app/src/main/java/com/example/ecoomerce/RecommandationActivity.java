package com.example.ecoomerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecoomerce.Model.Items;
import com.example.ecoomerce.Model.Products;
import com.example.ecoomerce.Prevalent.Prevalent;
import com.example.ecoomerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

public class RecommandationActivity extends AppCompatActivity {

    private DatabaseReference recommref,personalref,productref;
    private RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    RecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommandation);


        productref = FirebaseDatabase.getInstance().getReference().child("Products");

        recommref = FirebaseDatabase.getInstance().getReference().child("Recommendations");

        personalref = FirebaseDatabase.getInstance().getReference().child("PersonalRecom");



        recyclerView = findViewById(R.id.recom_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        items = new HashMap<Integer,Items>();

        adapter = new RecyclerAdapter(RecommandationActivity.this);
        recyclerView.setAdapter(adapter);

    }

    void GetRecommandations()
    {
        TreeMap<Integer,Items> tm=new TreeMap<Integer,Items>(items);
        Iterator itr=tm.keySet().iterator();
        while(itr.hasNext())
        {
            int key=(int)itr.next();
            Items v = items.get(key);
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", "0");
            productMap.put("date", "0");
            productMap.put("time", "0");
            productMap.put("image", "0");
            productMap.put("category", "0");
            productMap.put("price","0");

            productref.child("0").updateChildren(productMap);
            SearchForProduct(v.getName());
        }
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        int age = Integer.parseInt(Prevalent.CurrentOnlineUser.getAge());
        age = age/10;
        age = age*10;
        String gender = Prevalent.CurrentOnlineUser.getGender();

        DatabaseReference r = recommref.child(String.valueOf(age)).child(gender);
        SearchForAddedItem(r,false);

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", "1");
        productMap.put("name", "");
        productMap.put("checked", 0);

        recommref.child(String.valueOf(age))
                .child(gender).child("1").updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });
        r = personalref.child(Prevalent.CurrentOnlineUser.getPhone());
        SearchForAddedItem(r,true);
        personalref.child(Prevalent.CurrentOnlineUser.getPhone())
                .child("1").updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });

    }

    protected void SearchForProduct(final String productname)
    {
        final List<Products> products = new ArrayList<Products>();
        Query query = productref;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int counter = 0;
                boolean entered = false;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(counter < 2) {

                        Products p = ds.getValue(Products.class);
                        if(p.getPid() == "0")
                            continue;
                        else
                        {
                            if(p.getCategory().startsWith(productname)) {
                                products.add(p);
                                counter += 1;
                            }
                        }
                        entered = true;

                    }
                    else {

                        break;
                    }
                }
                if(entered) {
                    productref.child("0").removeValue();
                    AddProductsToAdapter(products);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    void AddProductsToAdapter(List<Products> products)
    {
        this.adapter.addMoreData(products);
    }
    boolean exist = false;
    HashMap<Integer,Items> items;
    protected void SearchForAddedItem(final DatabaseReference ref, final boolean personal) {

        Query query = ref.orderByChild("checked");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int counter = 0;
                boolean entered = false;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(counter < 2) {

                        Items i = ds.getValue(Items.class);
                        if(i.getChecked() == 0)
                            continue;
                        if(personal)
                            items.put((int)(i.getChecked()*Math.pow(2,i.getChecked())*-1),i);
                        else
                            items.put(i.getChecked()*-1,i);
                        counter += 1;
                        entered = true;

                    }
                    else {

                        break;
                    }
                }
                if(entered) {
                    ref.child("1").removeValue();
                    if (personal)
                        GetRecommandations();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
