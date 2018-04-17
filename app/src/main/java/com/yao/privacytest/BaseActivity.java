package com.yao.privacytest;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;


public class BaseActivity extends AppCompatActivity {
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<Integer, Runnable>();
    private Map<Integer, Runnable> disallowblePermissionRunnables = new HashMap<Integer, Runnable>();


    protected void requestPermission(int requestId, String permission,
                                     Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }
        allowablePermissionRunnables.put(requestId, allowableRunnable);

        if (disallowableRunnable != null) {
            disallowblePermissionRunnables.put(requestId, disallowableRunnable);

        }

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //检查是否拥有权限
            int checkPermission = ContextCompat.checkSelfPermission(this, permission);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框请求授权
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, requestId);
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]  permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Runnable allowRun=allowablePermissionRunnables.get(requestCode);
            allowRun.run();
        }else {
            Runnable disallowRun = disallowblePermissionRunnables.get(requestCode);
            disallowRun.run();
        }
    }

}