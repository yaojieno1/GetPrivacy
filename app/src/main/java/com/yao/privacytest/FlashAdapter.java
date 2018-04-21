package com.yao.privacytest;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class FlashAdapter implements Adapter {

    //final static private String TAG = "FlashAdapter";
    static private Context context = null;
    static private Camera camera = null;

    public FlashAdapter(Context context) {
        FlashAdapter.context = context;
    }

    @Override
    protected void finalize(){
        FlashAdapter.context = null;
    }


    public static String[] FlashProcess(Button button) {
        List<String> list = new ArrayList<String>();
        String text = button.getText().toString();

        //start record
        if (text == context.getString(R.string.button_openflash)) {
            camera = Camera.open();
            Camera.Parameters parameter = camera.getParameters();
            try {
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameter);
            } catch (Exception e) {
                camera.release();
                camera = null;

                list.add("[Exception][parameter.setFlashMode()] " + e.getLocalizedMessage());
                return list.toArray(new String[list.size()]);
            } finally{
            }
            button.setText(R.string.button_closeflash);

            list.add("Open Flash ....");
        } else if (text == context.getString(R.string.button_closeflash)) {
            if (camera == null) {
                button.setText(R.string.button_openflash);

                list.add("[Exception] camera not open ");
                return list.toArray(new String[list.size()]);
            }
            Camera.Parameters parameter = camera.getParameters();
            try {
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameter);
            } catch (Exception e) {
                list.add("[Exception][parameter.setFlashMode()] " + e.getLocalizedMessage());
                return list.toArray(new String[list.size()]);
            } finally{
                camera.release();
                camera = null;
            }
            button.setText(R.string.button_openflash);
            list.add("Stop Flash ...");
        }

        return list.toArray(new String[list.size()]);
    }

    public static String[] FlashDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to open/close flash");
        return error.toArray(new String[error.size()]);
    }
}
