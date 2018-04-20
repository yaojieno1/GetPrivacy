package com.yao.privacytest;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;


public class ImeiAdapter implements Adapter {

    //final private static String TAG = "ImeiAdapter";
    private static Context context = null;
    private static TelephonyManager tm ;
    private List<CallLogAdapter.CallLogBean> callLogs;

    ImeiAdapter(Context context) {
        ImeiAdapter.context = context;
        ImeiAdapter.tm = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
    }

    private static String getIMEI() {
        return tm.getDeviceId();
    }

    private static String getAndroidId() {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    private static String getSimSerialNumber() {
        return tm.getSimSerialNumber();
    }

    private static String getMSISDN() {
        return tm.getLine1Number();
    }

    private static String getModel() {
        return Build.MODEL;
    }

    private static String getSDKVersion() {
        return Build.VERSION.SDK;
    }

    private static String getFirmVersion() {
        return Build.VERSION.RELEASE;
    }

    private static String getCarriar() {
        return tm.getNetworkOperatorName();
    }

    private static String getSerialNumber(){
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String)get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    public static String[] getDeviceId() {
        List<String> list = new ArrayList<String>();
        try {
            //getDoubleSimInfo();
            list.add("IMEI             :" + getIMEI());
            list.add("MODEL            :" + getModel());
            list.add("ICCID            :" + getSimSerialNumber());
            list.add("MSISDN           :" + getMSISDN());
            list.add("Carriar          :" + getCarriar());
            list.add("Device Id        :" + getAndroidId());
            list.add("SDK Version      :" + getSDKVersion());
            list.add("Serial Number    :" + getSerialNumber());
            list.add("Firmware Version :" + getFirmVersion());
        } catch (Exception e) {
            //Log.e(TAG, "[Exception][getDeviceId()] " + e.getMessage());
            String x = e.getLocalizedMessage();
            if (x.lastIndexOf("requires") > 0)
                x = x.substring(x.lastIndexOf("requires"));
            list.add(x);
        }
        return list.toArray(new String[list.size()]);
    }

    public static String[] getDeviceIdDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to read device id");
        return error.toArray(new String[error.size()]);
    }

    private static boolean initQualcommDoubleSim() {
        try {
            Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");

            Method md = cx.getMethod("getDeviceId", int.class);
            Method ms = cx.getMethod("getSubscriberId", int.class);

            Object mtm = cx.getMethod("getDefault", new Class[]{}).invoke(cx, new Object[]{});
            String Imei_1 = (String) md.invoke(mtm,0);
            String Imei_2 = (String) md.invoke(mtm,1);
            String Imsi_1 = (String) ms.invoke(mtm,0);
            String Imsi_2 = (String) ms.invoke(mtm,1);
            //Log.w(TAG, "Imei 1 " + Imei_1 + " Imei 2 " + Imei_2 + " Imsi 1 " + Imsi_1 + " Imsi 2 " + Imsi_2 );
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "[initQualcommDoubleSim()] Exception: " + e.getCause());
            return false;
        }
        return true;
    }

    private static boolean initMtkDoubleSim() {
        int sim_id_1 = 0;
        int sim_id_2 = 0;
        String Imsi_1 = "";
        String Imsi_2 = "";
        String Imei_1 = "";
        String Imei_2 = "";
        int PhoneType_1 = 0;
        int PhoneType_2 = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            sim_id_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            sim_id_2 = (Integer) fields2.get(null);
            Method m = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
            Imsi_1 = (String) m.invoke(tm,0);
            Imsi_2 = (String) m.invoke(tm,1);

            Method m1 = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", int.class);
            Imei_1 = (String) m1.invoke(tm,0);
            Imei_2 = (String) m1.invoke(tm,1);

            Method mx = TelephonyManager.class.getDeclaredMethod(
                    "getPhoneTypeGemini", int.class);
            PhoneType_1 = (Integer) mx.invoke(tm,0);
            PhoneType_2 = (Integer) mx.invoke(tm,1);

        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "[initMtkDoubleSim()] Exception: " + e.getCause());
            return false;
        }

        return true;
    }


    private static  void isDoubleSim() {
        boolean isGaoTongCpu = initQualcommDoubleSim();
        boolean isMtkCpu = initMtkDoubleSim();
        if (isGaoTongCpu) {
            // 高通芯片双卡
            //Log.w(TAG, "GaoTong Two Sim Card");
        } else if (isMtkCpu) {
            // MTK芯片双卡
            //Log.w(TAG, "MTK Two Sim Card");
        } else {
            //普通单卡手机
            //Log.w(TAG, "Normal One Sim Card");
        }
    }

    private static void getDoubleSimInfo() {
        isDoubleSim();
    }

    protected void finalize(){
        ImeiAdapter.context = null;
        ImeiAdapter.tm = null;
    }

}
