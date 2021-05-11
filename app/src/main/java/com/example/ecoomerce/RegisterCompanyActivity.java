package com.example.ecoomerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterCompanyActivity extends AppCompatActivity {

    private Button CreateAccount;
    private EditText name,phone,pass,age;
    private RadioButton male,female;
    private String gender;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);
        CreateAccount = (Button) findViewById(R.id.btncompreglogin);
        name = (EditText) findViewById(R.id.txtcompregname);
        phone = (EditText) findViewById(R.id.txtcompregphonenumber);
        pass = (EditText) findViewById(R.id.txtcompregpass);

        loadingbar = new ProgressDialog(this);


        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccountf();
            }
        });
    }
    private void CreateAccountf()
    {
        String id = name.getText().toString();
        String num = phone.getText().toString();
        String password = pass.getText().toString();
        String agen = "-1";

        if(TextUtils.isEmpty(id))
        {
            Toast.makeText(this,"Please write your name...",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(num))
        {
            Toast.makeText(this,"Please write your phone number...",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please write your password...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(agen))
        {
            Toast.makeText(this,"Please write your age...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please wait, while checking the credintials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidatePhoneNumber(id,num,password,agen,"no gender");
        }

    }

    private void ValidatePhoneNumber(String id, String num, String password,String agen,String gen)
    {

        final String number = num;
        final String pas = password;
        final String user = id;
        final String ag = agen;
        final String ge = gen;
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(number).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", number);
                    userdataMap.put("password", pas);
                    userdataMap.put("name", user);
                    userdataMap.put("age", ag);
                    userdataMap.put("gender", ge);


                    RootRef.child("Users").child(number).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterCompanyActivity.this, "Congratulation, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        Intent intent = new Intent( RegisterCompanyActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterCompanyActivity.this, "Network Error: please try again..", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }

                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });

                }
                else
                {
                    Toast.makeText(RegisterCompanyActivity.this, "This " + number+ " already exists.", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(RegisterCompanyActivity.this, "Please try again using another number", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent( RegisterCompanyActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
