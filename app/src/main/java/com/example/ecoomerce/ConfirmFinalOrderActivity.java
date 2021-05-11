package com.example.ecoomerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecoomerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText name,phone,address,city;
    String totalprice = "";
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        name = findViewById(R.id.shippment_name);
        address = findViewById(R.id.shippment_address);
        phone = findViewById(R.id.shippment_phone_number);
        city = findViewById(R.id.shippment_city);

        btn = findViewById(R.id.confirm_final_order_btn);

        totalprice = getIntent().getStringExtra("totalprice");
        Toast.makeText(this,"Total price = " + totalprice,Toast.LENGTH_SHORT);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }


        });

    }
    private void Check() {

        if(TextUtils.isEmpty(name.getText().toString()))
        {
            Toast.makeText(this,"Please provide you name", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(address.getText().toString()))
        {
            Toast.makeText(this,"Please provide you address", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(city.getText().toString()))
        {
            Toast.makeText(this,"Please provide you city", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(phone.getText().toString()))
        {
            Toast.makeText(this,"Please provide you phone", Toast.LENGTH_SHORT);
        }
        else
        {
            ConfirmOrder();

        }


    }
    private void ConfirmOrder() {
        String date,time;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        date = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        time = currentTime.format(calForDate.getTime());

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.CurrentOnlineUser.getPhone());
        HashMap<String,Object> ordersMap = new HashMap<>();
        ordersMap.put("totalprice",totalprice);
        ordersMap.put("name",name.getText().toString());
        ordersMap.put("phone",phone.getText().toString());
        ordersMap.put("city",city.getText().toString());
        ordersMap.put("address",address.getText().toString());
        ordersMap.put("date",date);
        ordersMap.put("time",time);
        ordersMap.put("state","not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                    .child("User View")
                    .child(Prevalent.CurrentOnlineUser.getPhone())
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ConfirmFinalOrderActivity.this,"your final order is successfully!",Toast.LENGTH_SHORT);
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });

                }
            }
        });




    }
}
