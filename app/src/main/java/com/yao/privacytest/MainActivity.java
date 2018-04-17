package com.yao.privacytest;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends BaseActivity {
    ListView ContactsLv;
    Adapter adapter;
    static MainActivity instance;
    static String TAG = "MainActivity";
    List<Bitmap> imageList = new ArrayList<Bitmap>();
    LocationAdapter la;
    CameraAdapter   ca ;
    ImeiAdapter     ia ;
    CallLogAdapter cla ;
    ContactsAdapter csa ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContactsLv = (ListView) findViewById(R.id.list_view_everything);
        instance = this;
        la = new LocationAdapter(this);
        ca = new CameraAdapter(this);
        ia = new ImeiAdapter(this);
        cla = new CallLogAdapter(this);
        csa = new ContactsAdapter(this);
    }

    public void click_getCallLog(View view) {
        adapter = new CallLogAdapter(this);
        requestPermission(readCallLog, CALLLOG_PERMISSION, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  CallLogAdapter.getCallLogList()));
            }
        }, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  CallLogAdapter.getCallLogDenied()));
            }
        });
    }

    public void click_getContacts(View view) {
        adapter = new ContactsAdapter(this);
        requestPermission(readContactRequest, CONTACT_PERMISSION, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  ContactsAdapter.getContactsList()));
            }
        }, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  ContactsAdapter.getContactsDenied()));
            }
        });
    }

    public void click_getImei(View view) {
        adapter = new ImeiAdapter(this);
        requestPermission(readImei, IMEIID_PERMISSION, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  ImeiAdapter.getDeviceId()));
            }
        }, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  ImeiAdapter.getDeviceIdDenied()));
            }
        });
    }

    public void click_startThumbnail(View view) {
        adapter = new CameraAdapter(this);
        requestPermission(startThumbnail, CAMERA_PERMISSION, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  CameraAdapter.startThumbnail()));
            }
        }, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  CameraAdapter.startCameraDenied()));
            }
        });
    }

    public void click_takePhoto(View view) {
        adapter = new CameraAdapter(this);
        requestPermission(startCamera, CAMERA_PERMISSION, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  CameraAdapter.startCamera()));
            }
        }, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  CameraAdapter.startCameraDenied()));
            }
        });
    }

    public void click_getLocation(View view) {
        adapter = new LocationAdapter(this);
        requestPermission(getLocation, LOCATION_PERMISSION, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  LocationAdapter.startLocate()));
            }
        }, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  LocationAdapter.LocateDenied()));
            }
        });
    }

    static final int REQUEST_CODE_PICK_IMAGE = 5;
    public void click_getAlbum(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }


    public void click_getSms(View view) {
        adapter = new SmsAdapter(this);
        requestPermission(getSms, SMS_PERMISSION, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,  SmsAdapter.getSmsList()));
            }
        }, new Runnable() {
            @Override
            public void run() {
                ContactsLv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, SmsAdapter.getSmsDenied()));
            }
        });
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        imageList = null;
        la = null;
        ca = null;
        ia = null;
        cla = null;
        csa = null;
        ContactsLv.setAdapter(null);
    }

    private static String photoPath;
    public void setPhotoUri (String photoPath)
    {
        MainActivity.photoPath = photoPath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE
                && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        imageList.clear();
                        imageList.add(bitmap);
                        ContactsLv.setAdapter(new MyImageListAdapter());
                    } catch (Exception ex) {
                        Log.e(TAG, "[Exception]" + ex.getLocalizedMessage());
                        Log.e(TAG, "[Exception]" + ex.getCause());
                    }
                }
            }
            return;
        }

        if (requestCode != CameraAdapter.getRequestCode()
                && resultCode != RESULT_OK) {
            Log.e(TAG, "==Take Camera Failed!==");
            return;
        }

        Log.i(TAG, "==requestCode " + requestCode + " resultCode " + resultCode + " RESULT_OK " + RESULT_OK + "==") ;

        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                //Bitmap photo = extras.getParcelable("data");
                Bitmap bmp = (Bitmap) extras.getParcelable("data");
                Log.i(TAG, "== width " + bmp.getWidth() + " height " + bmp.getHeight() + "==");
                imageList.clear();
                imageList.add(bmp);
                ContactsLv.setAdapter(new MyImageListAdapter());
            }
        } else {
            Bitmap bmp = BitmapFactory.decodeFile(MainActivity.photoPath);
            Log.i(TAG, "== width " + bmp.getWidth() + " height " + bmp.getHeight() + "==");
            imageList.clear();
            imageList.add(bmp);
            ContactsLv.setAdapter(new MyImageListAdapter());
        }
    }

    class MyImageListAdapter extends BaseAdapter {
        // 返回集合数据的数量
        @Override
        public int getCount() {
            return imageList.size();
        }

        //返回指定下标对应的数据对象
        @Override
        public Object getItem(int position) {
            return imageList.get(0);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.image_view, null);
            }

            Bitmap bitmap = imageList.get(position);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            imageView.setImageBitmap(bitmap);
            convertView.measure(0,0);
            return convertView;
        }
    }

    public static final String CONTACT_PERMISSION = android.Manifest.permission.READ_CONTACTS;
    public static final String CALLLOG_PERMISSION = Manifest.permission.READ_CALL_LOG;
    public static final String IMEIID_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String SMS_PERMISSION = Manifest.permission.READ_SMS;
    public static final int readContactRequest = 1;
    public static final int readCallLog = 1;
    public static final int readImei = 1;
    public static final int startCamera = 1;
    public static final int startThumbnail = 1;
    public static final int getLocation = 1;
    public static final int getSms = 1;

}
