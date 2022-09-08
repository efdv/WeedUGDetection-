package com.example.jeappmaleza;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.*;

public class MainActivity extends AppCompatActivity {

    Button btnCap;
    Button btnUpLoad;
    Button btnVideo;
    Button btnInfo;
    private static final int CAPTURE_IMAGE = 1;
    private static final int GARELLY_REQUEST_CODE = 100;
    private static final int REAL_TIME = 0;
    private ContentValues values;
    private Uri imageUri;
    private Bitmap thumbnail;
    String imageurl;

    public static final int MULTIPLE_PERMISSIONS = 10;



    String[] permissions = new String[] {
            permission.WRITE_EXTERNAL_STORAGE,
            permission.READ_EXTERNAL_STORAGE,
            permission.CAMERA
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkPermissions())
        {
            //permissions granted
        }
        else
        {
            //show dialog informing them that we lack certain permissions
        }

        //BotonCapturar
        btnCap = (Button)findViewById(R.id.btnCap);
        btnCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,CAPTURE_IMAGE);


            }
        });

        //BotonCargar
        btnUpLoad = (Button)findViewById(R.id.btnUpLoad);
        btnUpLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg","image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                startActivityForResult(intent,GARELLY_REQUEST_CODE);
            }
        });

        //BotonTiempoReal
        btnVideo = (Button)findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iVideo = new Intent(v.getContext(), show_camera.class);
                startActivityForResult(iVideo,REAL_TIME);

            }
        });

        btnInfo = (Button)findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Uri url = Uri.parse("https://jonathancn16.wixsite.com/profile/copyright");
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode)
            {
                case CAPTURE_IMAGE:
                    //Bitmap image = (Bitmap)data.getExtras().get("data");
                    //Intent icap = new Intent(this,viewcapture.class);
                    //icap.putExtra("image",image);
                    //startActivity(icap);
                    try
                    {
                        thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        imageurl = getRealPathFromURI(imageUri);
                        Intent iCapture = new Intent(this,viewcapture.class);
                        Bundle b = new Bundle();
                        b.putString("image", imageurl);
                        iCapture.putExtras(b);
                        startActivity(iCapture);
                        break;

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();

                    }

                    break;

                case GARELLY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null,null,null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    Intent iGallery = new Intent(this,viewcapture.class);
                    Bundle b = new Bundle();
                    b.putString("image", imgDecodableString);
                    iGallery.putExtras(b);
                    startActivity(iGallery);
                    break;


            }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean checkPermissions()
    {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(String p:permissions)
        {
            result = ContextCompat.checkSelfPermission(this,p);
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                listPermissionsNeeded.add(p);
            }
        }

        if(!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return  false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case MULTIPLE_PERMISSIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permissions granted
                }
                else
                {
                    //no permissions granted
                }
                return;
        }
    }


}
