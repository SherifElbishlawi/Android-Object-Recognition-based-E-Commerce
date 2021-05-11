package com.example.ecoomerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoomerce.Model.Users;
import com.example.ecoomerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText number,password;
    private Button login;
    private ProgressDialog loadingbar;

    private CheckBox chbxRememberMe;

    private String parentDBName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        number = (EditText) findViewById(R.id.txtphonenumber);
        password = (EditText) findViewById(R.id.txtpass);
        login = (Button) findViewById(R.id.btnlogin);

        loadingbar = new ProgressDialog(this);




        chbxRememberMe = (CheckBox) findViewById(R.id.chbxrememberme);
        //save date to mobile memory to remember user id and password
        Paper.init(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
    }
    private void LoginUser()
    {

        String id = number.getText().toString();
        String pass = password.getText().toString();

        if(TextUtils.isEmpty(id))
        {
            Toast.makeText(this,"Please write your phone number...",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(this,"Please write your password...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Logging in");
            loadingbar.setMessage("Please wait, while checking the credintials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            AllowAccess(id,pass);
        }
    }
    private void AllowAccess(String id, String pass)
    {
        if(chbxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey,id);
            Paper.book().write(Prevalent.UserPassKey,pass);
        }

        if(id.equals("0") && pass.equals("0"))
        {
            //Intent intent = new Intent( LoginActivity.this,AddProductActivity.class);
            Intent intent = new Intent( LoginActivity.this,RegisterCompanyActivity.class);
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
                if(dataSnapshot.child(parentDBName).child(user).exists())
                {
                    Users usersData = dataSnapshot.child(parentDBName).child(user).getValue(Users.class);
                    if(usersData.getPhone().equals(user))
                    {
                        if(usersData.getPassword().equals(pas))
                        {

                            Toast.makeText(LoginActivity.this,"Logging in Successful",Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();




                            Prevalent.CurrentOnlineUser = usersData;

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            if(usersData.getAge().equals("-1"))
                            {
                                intent = new Intent(LoginActivity.this, AddProductActivity.class);
                            }
                            startActivity(intent);



                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Incorrect password!",Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }

                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Account with this " + user + " number doesn't exist",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
