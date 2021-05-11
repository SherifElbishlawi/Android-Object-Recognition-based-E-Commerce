package com.example.ecoomerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class AddProductActivity extends AppCompatActivity {

    private String category,cost,pname, saveCurrentDate, saveCurrentTime;
    private Button AddProduct;
    private Button OpenCamera;
    private Button Logout;
    private EditText name,price;
    private ImageView productImage;
    private static final int GalleryPick = 1;
    private Uri image;
    private String randomkey;
    private StorageReference ProductImages;
    private DatabaseReference Productref;
    private String downloadimageurl;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        loadingbar = new ProgressDialog(this);

        name = (EditText) findViewById(R.id.txtproductname);
        price = (EditText) findViewById(R.id.txtproductprice);

        productImage = (ImageView) findViewById(R.id.iv);

        ProductImages = FirebaseStorage.getInstance().getReference().child("Product Images");
        Productref = FirebaseDatabase.getInstance().getReference().child("Products");

        AddProduct = (Button) findViewById(R.id.btnaddproduct);

        Logout = (Button) findViewById(R.id.btnadminlogout);


        Paper.init(this);

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent( AddProductActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        OpenCamera = (Button) findViewById(R.id.btnaddproductphoto);

        OpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateData();
            }
        });
    }

    private void ValidateData() {
        cost = price.getText().toString();
        pname = name.getText().toString();

        if(image==null)
        {
            Toast.makeText(this,"Image is required", Toast.LENGTH_SHORT);

        }
        else if(TextUtils.isEmpty(cost))
        {
            Toast.makeText(this,"Price is required", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(pname))
        {
            Toast.makeText(this,"Product name is required", Toast.LENGTH_SHORT);
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingbar.setTitle("Adding New Product");
        loadingbar.setMessage("Please wait, while adding new product");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        randomkey= saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = ProductImages.child(image.getLastPathSegment()+randomkey +".jpg");
        final UploadTask uploadTask = filepath.putFile(image);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(AddProductActivity.this,"Error: " + msg, Toast.LENGTH_SHORT);
                loadingbar.dismiss();
            }
        })
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProductActivity.this,"Product Image Uploaded Successfully", Toast.LENGTH_SHORT);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }
                        downloadimageurl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            downloadimageurl = task.getResult().toString();
                            Toast.makeText(AddProductActivity.this, "Got image url successfully", Toast.LENGTH_SHORT);
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", randomkey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("image", downloadimageurl);
        productMap.put("category", pname);
        productMap.put("price",cost);

        Productref.child(randomkey).updateChildren(productMap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    loadingbar.dismiss();
                    Toast.makeText(AddProductActivity.this,"Product is added successfully",Toast.LENGTH_SHORT);
                }
                else
                {
                    loadingbar.dismiss();
                    String msg = task.getException().toString();
                    Toast.makeText(AddProductActivity.this,"Error: " + msg,Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/");
        startActivityForResult(galleryIntent,GalleryPick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!= null)
        {
            image = data.getData();
            productImage.setImageURI(image);

        }
    }
}
