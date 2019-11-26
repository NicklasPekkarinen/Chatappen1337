package com.autorave.chatapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TakePicture extends AppCompatActivity {

    // Deklarera variabler för imageview, sökväg och

    ImageView imageView;
    private String filepath; //För att hålla koll på sökväen
    private int REQUEST_PICTURE_CAPTURE = 1;
    Button camera_button;

    //Set content view och koppla samman Imageviewklassen med knappen samt kameraknappen med onclick.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.bild);
        camera_button = findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kolla att result code överenstämmer med den vi skickade

        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK){

                // Få Imageview dimensioner

                int imageViewWidth = imageView.getWidth();
                int imageViewHeight = imageView.getHeight();
                String log = "Imageview Width: " + imageViewWidth + " ImageView height: " + imageViewHeight;

                // Skapa Bitmap options så att de endast kan vara så stora som imageview

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filepath, options);

                // Beräkna bildens skala

                int scaleFactor = Math.min(options.outWidth / imageViewWidth, options.outHeight / imageViewHeight);
                String log3 = "options out. Width:: " + options.outWidth + "  height: " + options.outHeight;
                Log.d("TAG", log3);

                // Reset options to a new object and apply the scale

                options = new BitmapFactory.Options();
                options.inSampleSize = scaleFactor;

                // Decode the image

                Bitmap image = BitmapFactory.decodeFile(filepath, options);
                String log2 = "Image Width: " + image.getWidth() + " Image height: " + image.getHeight();
                Log.d("TAG", log2);

                // Set image to imageView

                imageView.setImageBitmap(image);
            }
        }
    }

    private void startCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo cannot be generated, pls try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }
    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddmmyyyyhhmmss", Locale.getDefault()).format(new Date());
        String pictureFile = "pic_" + timeStamp;

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(dir, "iths");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {

            }
        }
        File image = File.createTempFile( pictureFile, ".jpg", storageDir);

        filepath = image.getAbsolutePath();
        return image;
    }
}