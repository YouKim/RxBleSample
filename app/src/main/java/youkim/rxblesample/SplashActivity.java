package youkim.rxblesample;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import youkim.rxblesample.databinding.ActivitySplashBinding;


public class SplashActivity extends Activity {

    private static final long SPLASH_TIME = 5;
    private Disposable mStartScan;

    //example how to use databinding.
    private ActivitySplashBinding mBind;


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
        startActivityDelayed();
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
}
