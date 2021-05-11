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

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccount;
    private EditText name,phone,pass,age;
    private RadioButton male,female;
    private String gender;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CreateAccount = (Button) findViewById(R.id.btnreglogin);
        name = (EditText) findViewById(R.id.txtregname);
        phone = (EditText) findViewById(R.id.txtregphonenumber);
        pass = (EditText) findViewById(R.id.txtregpass);
        age = (EditText) findViewById(R.id.txtregage);
        male = (RadioButton) findViewById(R.id.rdmale);
        female = (RadioButton) findViewById(R.id.rdfemale);

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
        String agen = age.getText().toString();

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
            if(male.isChecked())
                gender = "male";
            if(female.isChecked())
                gender = "female";

            if(num.length() < 10)
            {
                Toast.makeText(this,"Please enter your real phone number...",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Double I = Double.parseDouble(num);
            } catch (NumberFormatException nfe) {
                Toast.makeText(this,"Phone number must be a number...",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                if(Integer.parseInt(agen) < 1)
                {
                    Toast.makeText(this,"Please enter your real age...",Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException nfe) {
                Toast.makeText(this,"Age must be a number...",Toast.LENGTH_SHORT).show();
                return;
            }


            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please wait, while checking the credintials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidatePhoneNumber(id,num,password,agen,gender);
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
                                        Toast.makeText(RegisterActivity.this, "Congratulation, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        Intent intent = new Intent( RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this, "Network Error: please try again..", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegisterActivity.this, "This " + number+ " already exists.", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another number", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent( RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
