package kr.ac.kaist.kse.ic.msbanddatacollector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.HeartRateConsentListener;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{


    private Switch aSwitch;

    private final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WeakReference<Activity> reference = new WeakReference<Activity>(this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
        aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
                return;
            }
        }
    }

    private BandClient client = null;

    private BandClient getBandClient(){
        return client;
    }

    private boolean isBandConnected() throws InterruptedException, BandException {
        if (getBandClient() == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                ((MSBandDataLogger) getApplication()).warn("MS Band isn't paired with your phone.");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == getBandClient().getConnectionState()) {
            return true;
        }
        return ConnectionState.CONNECTED == getBandClient().connect().await();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try {
            if(b) {
                if(isBandConnected()) {
                    new HeartRateConsentTask().execute();
                    startService(new Intent(this, SensingService.class));
                }else
                    compoundButton.setChecked(false);
            }else
                stopService(new Intent(this,SensingService.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BandException e) {
            e.printStackTrace();
        }
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void>{
        @Override
        protected Void doInBackground(WeakReference<Activity>... weakReferences) {
            Activity activity = weakReferences[0].get();
            if (activity != null) {
                getBandClient().getSensorManager().requestHeartRateConsent(activity, new HeartRateConsentListener() {
                    @Override
                    public void userAccepted(boolean b) {

                    }
                });
            }
            return null;
        }
    }
}
