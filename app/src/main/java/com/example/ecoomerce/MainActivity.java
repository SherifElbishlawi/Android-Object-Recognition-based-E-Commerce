package com.example.ecoomerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecoomerce.Model.Users;
import com.example.ecoomerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button join,login;
    private ProgressDialog loadingbar;
    private String parentDBName = "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        join = (Button)findViewById(R.id.btnjoin);
        login = (Button)findViewById(R.id.btngologin);

        loadingbar = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        Paper.init(this);

        //check if data is saved on phone memory then go to home page
        String id = Paper.book().read(Prevalent.UserPhoneKey);
        String pass = Paper.book().read(Prevalent.UserPassKey);

        if(id != "" && pass != "")
        {
            if(!TextUtils.isEmpty(id) && !TextUtils.isEmpty(pass))
            {
                loadingbar.setTitle("Already Logged in");
                loadingbar.setMessage("Please wait...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                AllowAccess(id,pass);
            }
        }
    }
    private void AllowAccess(String id,String pass)
    {
        if(id.equals("0") && pass.equals("0"))
        {
            Intent intent = new Intent( MainActivity.this,AddProductActivity.class);
            startActivity(intent);
            return;
        }
        final String pas = pass;
        final String user = id;
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(user).exists()) {
                    Users usersData = dataSnapshot.child(parentDBName).child(user).getValue(Users.class);
                    if (usersData.getPhone().equals(user)) {
                        if (usersData.getPassword().equals(pas)) {

                            Toast.makeText(MainActivity.this, "Logging in Successful", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();

                            Prevalent.CurrentOnlineUser = usersData;

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Account with this " + user + " number doesn't exist", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
