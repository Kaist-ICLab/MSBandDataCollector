package kr.ac.kaist.kse.ic.msbanddatacollector;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.StringBuilderPrinter;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAltimeterEventListener;
import com.microsoft.band.sensors.BandAmbientLightEvent;
import com.microsoft.band.sensors.BandAmbientLightEventListener;
import com.microsoft.band.sensors.BandBarometerEvent;
import com.microsoft.band.sensors.BandBarometerEventListener;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
import com.microsoft.band.sensors.BandSensorManager;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandUVEventListener;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.SampleRate;

/**
 * Created by kimauk on 2016. 11. 14..
 */
public class SensingService extends Service {
    private BandAccelerometerEventListener bandAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent event) {
            infoBandData(event.getTimestamp(), "Accelerometer (x, y, z) = (%f, %f, %f)", event.getAccelerationX(), event.getAccelerationY(), event.getAccelerationZ());
        }
    };
    private BandAltimeterEventListener bandAltimeterEventListener = new BandAltimeterEventListener() {
        @Override
        public void onBandAltimeterChanged(BandAltimeterEvent event) {
            try {
                infoBandData(event.getTimestamp(), "Altimeter (flightsAscended, flightsDescended, rate, steppingGain, steppingLoss, stepsAscended, stepsDescended, totalGain, totalLoss, flightsAscendedToday, totalGainToday) = (%d, %d, %f, %d, %d, %d, %d, %d, %d, %d, %d)", event.getFlightsAscended(), event.getFlightsDescended(), event.getRate(), event.getSteppingGain(), event.getSteppingLoss(), event.getStepsAscended(), event.getStepsDescended(), event.getTotalGain(), event.getTotalLoss(), event.getFlightsAscendedToday(), event.getTotalGainToday());
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    };
    private BandAmbientLightEventListener bandAmbientLightEventListener = new BandAmbientLightEventListener() {
        @Override
        public void onBandAmbientLightChanged(final BandAmbientLightEvent event) {
            infoBandData(event.getTimestamp(), "AmbientLight = %d", event.getBrightness());

        }
    };
    private BandBarometerEventListener bandBarometerEventListener = new BandBarometerEventListener() {
        @Override
        public void onBandBarometerChanged(BandBarometerEvent event) {
            infoBandData(event.getTimestamp(), "Barometer (airPressure, temperature) = (%f, %f)", event.getAirPressure(), event.getTemperature());
        }
    };
    private BandCaloriesEventListener bandCaloriesEventListener = new BandCaloriesEventListener() {
        @Override
        public void onBandCaloriesChanged(BandCaloriesEvent event) {
            try {
                infoBandData(event.getTimestamp(), "Calories (calories, todayCalories) = (%d, %d)", event.getCalories(), event.getCaloriesToday());
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    };
    private BandDistanceEventListener bandDistanceEventListener = new BandDistanceEventListener() {
        @Override
        public void onBandDistanceChanged(BandDistanceEvent event) {
            try {
                infoBandData(event.getTimestamp(), "Distance (distanceToday, motionType, pace, speed, totalDistance) = (%d, %s, %f, %f, %d)", event.getDistanceToday(), event.getMotionType().toString(), event.getPace(), event.getSpeed(), event.getTotalDistance());
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    };
    private BandGsrEventListener bandGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(BandGsrEvent event) {
            infoBandData(event.getTimestamp(), "Gsr = %d", event.getResistance());
        }
    };
    private BandGyroscopeEventListener bandGyroscopeEventListener = new BandGyroscopeEventListener() {
        @Override
        public void onBandGyroscopeChanged(BandGyroscopeEvent event) {
            infoBandData(event.getTimestamp(), "Gyroscope (x, y, z, velocityX, velocityY, velocityZ) = (%f, %f, %f, %f, %f, %f)", event.getAccelerationX(), event.getAccelerationY(), event.getAccelerationZ(), event.getAngularVelocityX(), event.getAngularVelocityY(), event.getAngularVelocityZ());
        }
    };
    private BandHeartRateEventListener bandHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            infoBandData(event.getTimestamp(), "HeartRate (heartRate, quality) = (%d, %s)", event.getHeartRate(), event.getQuality());
        }
    };
    private BandPedometerEventListener bandPedometerEventListener = new BandPedometerEventListener() {
        @Override
        public void onBandPedometerChanged(BandPedometerEvent event) {
            try {
                infoBandData(event.getTimestamp(), "Pedometer = %d", event.getStepsToday());
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    };
    private BandSkinTemperatureEventListener bandSkinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event) {
            infoBandData(event.getTimestamp(), "Temperature = %f", event.getTemperature());
        }
    };
    private BandUVEventListener bandUVEventListener = new BandUVEventListener() {
        @Override
        public void onBandUVChanged(BandUVEvent event) {

            try {
                infoBandData(event.getTimestamp(), "UV(UVExposureToday, UVIndexLevel) = %d %s", event.getUVExposureToday(), event.getUVIndexLevel());
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    };
    private BandRRIntervalEventListener rrIntervalEventListener = new BandRRIntervalEventListener() {
        @Override
        public void onBandRRIntervalChanged(BandRRIntervalEvent event) {
            infoBandData(event.getTimestamp(), "RRInterval = %f", event.getInterval());
        }
    };

    private BandClient bandClient = null;

    private MSBandDataLogger MSBandDataLogger;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MSBandDataLogger = (MSBandDataLogger) getApplicationContext();
        try {
            regesterBandSensorListeners();
        } catch (BandException e) {
            String exceptionMessage = "";
            e.printStackTrace();
            switch (e.getErrorType()) {
                case UNSUPPORTED_SDK_VERSION_ERROR:
                    exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
                    break;
                case SERVICE_ERROR:
                    exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
                    break;
                default:
                    exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                    break;
            }
            closeApplicatonWithWarning(e, exceptionMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
            stopSelf();
        }
        return START_STICKY;
    }

    private void closeApplicatonWithWarning(Throwable throwable, String msg){
        getMSBandDataLoggerApplication().closeApplicationWithWarning(throwable, msg);
    }

    private MSBandDataLogger getMSBandDataLoggerApplication(){
        return MSBandDataLogger;
    }

    private void notifyToUser(String msg){
        getMSBandDataLoggerApplication().notify(msg);
    }

    private void infoBandData(long timestamp, String format, Object... args) {
        getMSBandDataLoggerApplication().i(timestamp, LoggerApplication .DOMAIN_SERVICE, format, args);
    }

    private void infoBandEvent(String format, Object... args) {
        getMSBandDataLoggerApplication().i(System.currentTimeMillis(), LoggerApplication .DOMAIN_SERVICE, format, args);
    }

    private void regesterBandSensorListeners() throws BandException, InterruptedException {
        if(!isBandConncted())
            stopSelf();

        SampleRate defaultSampleRate = SampleRate.MS128;

        getSensorManager().registerAccelerometerEventListener(bandAccelerometerEventListener, defaultSampleRate);
        infoBandEvent("Registered AccelerometerEventListener");


        getSensorManager().registerCaloriesEventListener(bandCaloriesEventListener);
        infoBandEvent("Registered CaloriesEventListener");

        getSensorManager().registerDistanceEventListener(bandDistanceEventListener);
        infoBandEvent("Registered DistanceEventListener");

        getSensorManager().registerGyroscopeEventListener(bandGyroscopeEventListener, defaultSampleRate);
        infoBandEvent("Registered GyroscopeEventListener");

        getSensorManager().registerPedometerEventListener(bandPedometerEventListener);
        infoBandEvent("Registered PedometerEventListener");

        getSensorManager().registerSkinTemperatureEventListener(bandSkinTemperatureEventListener);
        infoBandEvent("Registered SkinTemperatureEventListener");

        getSensorManager().registerUVEventListener(bandUVEventListener);
        infoBandEvent("Registered UVEventListener");

        getSensorManager().registerHeartRateEventListener(bandHeartRateEventListener);
        infoBandEvent("Registered HeartRateListener");

        StringBuilder builder = new StringBuilder("Data is collecting");

        try {
            getSensorManager().registerGsrEventListener(bandGsrEventListener, GsrSampleRate.MS200);
            infoBandEvent("Registered GsrEventListener");
            getSensorManager().registerAltimeterEventListener(bandAltimeterEventListener);
            infoBandEvent("Registered AltimeterEventListener");

            getSensorManager().registerAmbientLightEventListener(bandAmbientLightEventListener);
            infoBandEvent("Registered AmbientLightEventListener");

            getSensorManager().registerBarometerEventListener(bandBarometerEventListener);
            infoBandEvent("Registered BarometerEventListener");

            getSensorManager().registerRRIntervalEventListener(rrIntervalEventListener);
            infoBandEvent("Registered RRIntervalEventListener");
        } catch (InvalidBandVersionException e) {
            builder.append(", except Gsr, Altimeter, AmbientLight, Barometer and RRInterval. To collect Gsr, Altimeter, AmbientLight, Barometer and RRInterval, please use Mictosoft Band 2");
        }
        builder.append(".");

        notifyToUser(builder.toString());
    }

    private boolean isBandConncted() throws BandException, InterruptedException {
        if(getBandClient() == null){
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                getMSBandDataLoggerApplication().warn("Band isn't paired with your phone.");
                return false;
            }
            bandClient = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        }
        if (ConnectionState.CONNECTED == getBandClient().getConnectionState() || ConnectionState.CONNECTED == getBandClient().connect().await()) {
            return true;
        }
        return false;
    }

    public BandClient getBandClient(){
        return bandClient;
    }

    private BandSensorManager getSensorManager() {
        return getBandClient().getSensorManager();
    }

    @Override
    public void onDestroy() {
        try {
            if(isBandConncted()){
                getSensorManager().unregisterAllListeners();
                getBandClient().disconnect().await();
            }
        } catch (InterruptedException e) {
            // Do nothing as this is happening during destroy
        } catch (BandException e) {
            // Do nothing as this is happening during destroy
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
