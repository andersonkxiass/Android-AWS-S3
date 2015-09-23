package br.com.acs.amazons3_example;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.squareup.okhttp.OkHttpClient;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import br.com.acs.amazons3_example.rest.services.AwsS3;
import br.com.acs.amazons3_example.utils.AWSOauth;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import static android.app.AlertDialog.*;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_CAMERA_IMAGE = 2;
    private static int RESULT_LOAD_GALLERY_IMAGE = 1;
    private String mCurrentPhotoPath;
    private File cameraImageFile;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.image);
        imageView.setOnClickListener(chooseImageListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String bucket = getString(R.string.s3_bucket);

                RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint("http://" + bucket + ".s3.amazonaws.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setClient(new OkClient(new OkHttpClient()));

                AwsS3 aws = builder.build().create(AwsS3.class);

                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z").withLocale(Locale.US);
                String ZONE = "GMT";
                DateTime dt = new DateTime();
                DateTime dateTime = dt.withZone(DateTimeZone.forID(ZONE)).plusHours(1);
                String formattedDate = dateTime.toString(fmt);

                try {

                    Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();

                    TypedInput body = new TypedByteArray("image/jpg", b);

                    String imageName = "files_" + System.currentTimeMillis();

                    String oauth = AWSOauth.getOAuthAWS(getApplicationContext(), imageName.trim());
                    String host = bucket + ".s3.amazonaws.com";
                    String mimeType = body.mimeType();
                    long length = body.length();

                    aws.upload(imageName.trim(), length, host, formattedDate, mimeType, oauth, body, new Callback<String>() {

                        @Override
                        public void success(String s, Response response) {

                            Log.d("tag","S = " + s);
                            Log.d("tag","getHeaders = " + response.getHeaders());
                            Log.d("tag","Status = " + response.getStatus());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("tag","error: S = " + error.getMessage());
                        }
                    });

                }catch (Exception e){

                }
            }
        });
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RESULT_LOAD_GALLERY_IMAGE && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);

                if(cursor != null  && cursor.moveToFirst()){

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mCurrentPhotoPath = cursor.getString(columnIndex);
                    cursor.close();
                }

            } else if (requestCode == RESULT_LOAD_CAMERA_IMAGE) {
                mCurrentPhotoPath = cameraImageFile.getAbsolutePath();
            }

            File image = new File(mCurrentPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

            imageView.setImageBitmap(bitmap);
        }
    }

    View.OnClickListener chooseImageListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogChooseFrom();
        }
    };


    private void dialogChooseFrom() {

        final CharSequence[] items = {"From Gallery", "From Camera"};

        Builder chooseDialog = new Builder(this);
        chooseDialog.setTitle("Pick your choice").setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (items[which].equals("From Gallery")) {

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_GALLERY_IMAGE);

                } else {

                    try {

                        File photoFile = createImageFile();
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, RESULT_LOAD_CAMERA_IMAGE);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        chooseDialog.show();
    }

    private File createImageFile () throws IOException {

        String imageFileName = "file_" + System.currentTimeMillis();

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File folder = new File(storageDir.getAbsolutePath() + "/tempFiles");

        if (!folder.exists()) {
            folder.mkdir();
        }

        cameraImageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                folder      /* directory */
        );

        return cameraImageFile;
    }
}