package com.example.ecoomerce;

import androidx.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    String productname;
    private DatabaseReference productref,recommref,personalref;
    private RecyclerView recyclerView;
    Button searchbtn;

    EditText input;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        input = findViewById(R.id.txtkeyword);
        searchbtn = findViewById(R.id.btnsearch);

        productref = FirebaseDatabase.getInstance().getReference().child("Products");

        recommref = FirebaseDatabase.getInstance().getReference().child("Recommendations");

        personalref = FirebaseDatabase.getInstance().getReference().child("PersonalRecom");


        productname = getIntent().getStringExtra("pname");
        input.setText(productname);

        recyclerView = findViewById(R.id.search_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productname = input.getText().toString();

                AddToSearchListOfAgeAndGender();
                SearchForProduct();
            }
        });
    }

    private void AddToSearchListOfAgeAndGender() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String randomkey= saveCurrentDate + saveCurrentTime;

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", randomkey);
        productMap.put("name", productname);
        productMap.put("checked", 0);

        int age = Integer.parseInt(Prevalent.CurrentOnlineUser.getAge());
        age = age/10;
        age = age*10;
        String gender = Prevalent.CurrentOnlineUser.getGender();

        DatabaseReference r = recommref.child(String.valueOf(age)).child(gender);

        SearchForAddedItem(r);

        if(!exist) {

            recommref.child(String.valueOf(age))
                    .child(gender).child(randomkey).updateChildren(productMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SearchActivity.this, "Product is added successfully", Toast.LENGTH_SHORT);
                            }
                        }
                    });

        }
        final HashMap<String, Object> personalmap = new HashMap<>();
        personalmap.put("pid",randomkey);
        personalmap.put("name",productname);
        personalmap.put("checked", 0);

        exist = false;
        r = personalref.child(Prevalent.CurrentOnlineUser.getPhone());
        SearchForAddedItem(r);
        if(!exist) {
            personalref.child(Prevalent.CurrentOnlineUser.getPhone())
                    .child(randomkey).updateChildren(personalmap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SearchForProduct();
    }

    protected void SearchForProduct()
    {
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productref.orderByChild("category").startAt(productname),Products.class).build();
        FirebaseRecyclerAdapter<Products,ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                //if(!products.getCategory().contains(productname))
                //    return;
                productViewHolder.txtpname.setText(products.getCategory());
                productViewHolder.txtpprice.setText("Price =" + products.getPrice() + "$");
                Picasso.get().load(products.getImage()).into(productViewHolder.iv);
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchActivity.this,ProductDetailsActivity.class);
                        intent.putExtra("pid",products.getPid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    boolean exist = false;
    protected void SearchForAddedItem(final DatabaseReference ref) {

        Query query = ref.orderByChild("name").equalTo(productname);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean startDeleting = false;

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(!startDeleting) {
                        Integer checked = ds.child("checked").getValue(Integer.class);
                        Items i = ds.getValue(Items.class);
                        int c = checked + 1;
                        //i.setChecked(String.valueOf(c));
                        i.setChecked(c);

                        ref.child(ds.child("pid").getValue(String.class)).setValue(i);
                        startDeleting = true;
                    }
                    else
                    {
                        ref.child(ds.child("pid").getValue(String.class)).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
