package com.construapp.construapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.construapp.construapp.models.Lesson;

import java.io.File;
import java.io.IOException;

public class LessonActivity extends AppCompatActivity {

    private static final String USERNAME = "username";
    private static final String DESCRIPTION = "description";
    private TextView lessonName;
    private TextView  lessonDescription;

    private Lesson lesson = new Lesson();
    private ImageButton imageButtonView;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGallery;

    private static final int WRITE_EXTERNAL_REEQUEST = 1886;
    private static final int CAMERA_REQUEST = 1888;
    private static  final int CAMERA_REQUEST_PICTURE = 1887;
    private static final int SELECT_IMAGE = 1885;

    private String mPath;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lessonName = (TextView) findViewById(R.id.lesson_name);
        lessonDescription = (TextView) findViewById(R.id.lesson_description);
        imageButtonView = (ImageButton) findViewById(R.id.image_button_view);
        setLesson();

        lessonName.setText(lesson.getName());
        lessonDescription.setText(lesson.getDescription());

        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);

        mLayout = findViewById(R.id.lesson_layout);

        setFabCameraOnClickListener();
        setFabGalleryOnClickListener();

    }

    public void setFabGalleryOnClickListener() {
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Acceso a galería", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });
    }

    public void setFabCameraOnClickListener() {
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(LessonActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.i("PERMISSION", "Storage Permission");
                    getStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(LessonActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    Log.i("PERMISSION", "Camera Permission");
                    getCameraPermissions();
                }
                else {
                    Log.i("PERMISSION", "Granted");
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {

        String APP_DIRECTORY = "MyConceptsApp";

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), APP_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdir();
        }
        Long timestamp = System.currentTimeMillis() / 1000;
        String imageName = timestamp.toString() + ".jpg";

        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                File.separator +
                APP_DIRECTORY + File.separator + imageName;
        File newFile = new File(file, imageName);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(LessonActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                        dispatchTakePictureIntent();
                    }
                }   else {
                    Toast.makeText(this, "Hasta que no entregues acceso a a tu almacenamiento, " +
                            "no la podemos mostrar", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case WRITE_EXTERNAL_REEQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCameraPermissions();
                } else {
                    Toast.makeText(this, "Hasta que no entregues permiso a tu almacienamiento",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case CAMERA_REQUEST_PICTURE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "scanned"+path+":");
                                    Log.i("ExternalStorage", "-> Uri"+uri);
                                }
                            });
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    //imageButtonView.setImageBitmap(bitmap);
                    imageButtonView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80,80));


                case SELECT_IMAGE:

                    if (data != null)
                    {
                        mPath = getRealPathFromURI_API19(getApplicationContext(),data.getData());
                        Bitmap bitmapGallery = BitmapFactory.decodeFile(mPath);
                        imageButtonView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmapGallery, 80,80));

                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Toast.makeText(LessonActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public void setLesson() {
        lesson.setName(getIntent().getStringExtra(USERNAME));
        lesson.setDescription(getIntent().getStringExtra(DESCRIPTION));
    }

    public static Intent getIntent(Context context, String name, String description) {
        Intent intent = new Intent(context,LessonActivity.class);
        intent.putExtra(USERNAME,name);
        intent.putExtra(DESCRIPTION,description);
        //intent.putExtra(KEY_CHAT_ROOM_UUID,chatRoomUuid);

        return intent;
    }

    public void getStoragePermissions() {
        if (ContextCompat.checkSelfPermission(LessonActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos guardar las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_REEQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_REEQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getCameraPermissions() {
        if (ContextCompat.checkSelfPermission(LessonActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos tomar las fotos, necesitamos acceso a su cámara.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}