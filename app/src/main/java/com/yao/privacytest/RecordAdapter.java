package com.yao.privacytest;

import android.app.Activity;
import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter implements Adapter {
    //final static private String TAG = "RecordAdapter";
    static private Context context = null;

    static MediaRecorder mRecord;
    static MediaPlayer mPlayer;
    static File audioFile;

    public RecordAdapter(Context context) {
        RecordAdapter.context = context;
    }

    @Override
    protected void finalize(){
        RecordAdapter.context = null;
    }

    private static File createAudioFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String audioFileName = "AUDIO_" + timeStamp + "_";
        File storageDir = context.getFilesDir(); //context.getCacheDir();
        File audioFile = File.createTempFile(
                audioFileName,  /* prefix */
                ".3gp",         /* suffix */
                storageDir      /* directory */
        );

        return audioFile;
    }

    public static String[] RecordProcess(Button button) {
        List<String> list = new ArrayList<String>();
        String text = button.getText().toString();

        //start record
        if (text == context.getString(R.string.button_startrec)) {
            try {
                audioFile = createAudioFile();
            } catch (Exception e) {
                list.add("[Exception][createAudioFile()] " + e.getLocalizedMessage());
                return list.toArray(new String[list.size()]);
            }

            RecordAdapter.mRecord = new MediaRecorder();
            RecordAdapter.mRecord.reset();
            RecordAdapter.mRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
            RecordAdapter.mRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            RecordAdapter.mRecord.setOutputFile(audioFile.getAbsolutePath());
            RecordAdapter.mRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                RecordAdapter.mRecord.prepare();
            } catch (Exception e) {
                RecordAdapter.mRecord = null;

                //Log.e(TAG, "[Exception][RecordProcess()] " + e.getLocalizedMessage());
                list.add("[Exception][RecordProcess()] " + e.getLocalizedMessage());
                return list.toArray(new String[list.size()]);
            }
            RecordAdapter.mRecord.start();
            button.setText(R.string.button_stoprec);

            list.add("Start Recording ....");
        } else if (text == context.getString(R.string.button_stoprec)) {
            if (RecordAdapter.mRecord != null) {
                RecordAdapter.mRecord.stop();
                RecordAdapter.mRecord.release();
                RecordAdapter.mRecord = null;

                RecordAdapter.mPlayer = new MediaPlayer();
                try {
                    RecordAdapter.mPlayer.setDataSource(audioFile.getAbsolutePath());
                    RecordAdapter.mPlayer.prepare();
                    RecordAdapter.mPlayer.start();
                } catch (IOException e) {
                    RecordAdapter.mPlayer = null;
                    button.setText(R.string.button_startrec);

                    //Log.e(TAG, "[Exception][RecordProcess()] " + e.getLocalizedMessage());
                    list.add("[Exception][RecordProcess()] " + e.getLocalizedMessage());
                    return list.toArray(new String[list.size()]);
                }

                button.setText(R.string.button_stopplay);
                list.add("Stop Record. Start Playing ....");
            }
        } else if (text == context.getString(R.string.button_stopplay)) {
            RecordAdapter.mPlayer.release();
            RecordAdapter.mPlayer = null;

            button.setText(R.string.button_startrec);
            list.add("Stop Play. Finish.");
        }

        return list.toArray(new String[list.size()]);
    }

    public static String[] RecordDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to record");
        return error.toArray(new String[error.size()]);
    }
}
