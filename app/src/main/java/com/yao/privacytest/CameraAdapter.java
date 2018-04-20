package com.yao.privacytest;


import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraAdapter implements Adapter {

    //final static String TAG = "CameraAdapter";
    private static Context context = null;
    static private final int REQUEST_TAKE_PHOTO = 1;

    CameraAdapter(Context context) {
        CameraAdapter.context = context;
    }

    @Override
    protected void finalize() {
        CameraAdapter.context = null;
    }

    public static String[] startThumbnail() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        MainActivity.getInstance().startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        //Log.i(TAG, "==start Thumbnail==");
        return new String[]{};
    }

    static private String mCurrentPhotoPath = "";

    private static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getFilesDir(); //context.getCacheDir();
        //Log.i(TAG, "==TRY to create temp file " + storageDir.getAbsolutePath() + "/" + imageFileName + ".jpg==");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public static int getRequestCode() {
        return REQUEST_TAKE_PHOTO;
    }

    public static String[] startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //Log.e(TAG, "[dispatchTakePictureIntent.createImageFile] Exception " + ex.getLocalizedMessage());
                //Log.e(TAG, "[dispatchTakePictureIntent.createImageFile] " + ex.getCause());
                return new String[]{};
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.yaojie.privacytest.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                MainActivity.getInstance().setPhotoUri(photoFile.getAbsolutePath());
                MainActivity.getInstance().startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        return new String[]{};
    }

    public static String[] startCameraDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to take photo");
        return error.toArray(new String[error.size()]);
    }
}
