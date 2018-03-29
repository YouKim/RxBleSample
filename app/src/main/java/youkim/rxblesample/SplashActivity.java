package youkim.rxblesample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import youkim.rxblesample.databinding.ActivitySplashBinding;


public class SplashActivity extends Activity {

    private static final long SPLASH_TIME = 1;
    private Disposable mStartScan;

    //example how to use databinding.
    private ActivitySplashBinding mBind;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_splash);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposeStartScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRequiredPermissions();
        //startActivityDelayed();
    }

    private synchronized void startActivityDelayed() {
        if (mStartScan == null || mStartScan.isDisposed()) {
            mStartScan = Observable.timer(SPLASH_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(time -> startScanActivity());
        }
    }

    private synchronized void startScanActivity() {
        Intent intent = new Intent(getApplicationContext(), DeviceScanActivity.class);
        startActivity(intent);
    }


    private synchronized void disposeStartScan() {
        if (mStartScan != null && !mStartScan.isDisposed()) {
            mStartScan.dispose();
        }
    }

    private synchronized void checkRequiredPermissions() {
        if (Build.VERSION.SDK_INT > 22) {
            requestRequiredPermissions();
        }
    }

    private void requestRequiredPermissions() {
        List<String> permissionsNeeded = Arrays.asList(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        );

        final List<String> permissionsList = new ArrayList<String>();
        final List<String> showList = new ArrayList<String>();

        boolean shouldShow = false;

        for (String perm : permissionsNeeded) {
            if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(perm);
                // Check for Rationale Option
                if (shouldShowRequestPermissionRationale(perm)) {
                    showList.add(perm);
                }
            }
        }

        if (permissionsList.size() > 0) {
            final String[] list = permissionsList.toArray(new String[0]);

            if (showList.size() > 0) {
                // Need Rationale
                String message = "App need access to " + showList.get(0);

                for (int i = 1; i < showList.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }

                showMessageOKCancel(message, (dialog, which) ->
                    requestPermissions(list, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
                );
            } else {
                requestPermissions(list, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        } else {
            startActivityDelayed();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
        case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            checkRequiredPermissions();
            break;
        }
    }
}
