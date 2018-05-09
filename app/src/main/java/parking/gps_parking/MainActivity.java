package parking.gps_parking;

import java.util.Date;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;

    public static TextView tvLocat2;
    public static TextView tvBetweenPoint;
    Button coordinate2;

    public static LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();

    public static double lat1;
    public static double lng1;
    public static double lng2;
    public static double lat2;
    public static Location locationField;

    public static Context context;

    private boolean checked = false;

    public int metters;
    public static boolean averaging;
    public static int counter;
    public static double[] array;
    public static double arraySumm;
    private boolean cycle_flag;
    private AsyncAveraging asyncObj = new AsyncAveraging();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);

        tvLocat2 = (TextView) findViewById(R.id.tvLocat2);
        tvBetweenPoint = (TextView) findViewById(R.id.tvBetweenPoint);
        coordinate2 = (Button) findViewById(R.id.coordinate2);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        averaging = true;
        cycle_flag = false;

        context = getApplicationContext();

    }





    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                300 * 10, 3, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 300 * 10, 3,
                locationListener);
        checkEnabled();             // обновляем информацию о включенности провайдеров
        checked = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
            if (!checked){
                lat1 = (double) location.getLatitude();
                lng1 = (double) location.getLongitude();
                checked = true;
            }

        }
        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }
        @SuppressLint("SetTextI18n")
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        locationField = location;
        return lat1 + " //// " + lng1;
    }

    @SuppressLint("SetTextI18n")
    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    public void onClickGeo2(View view){
        averaging();
    }

        public void averaging(){


            if (averaging){
                averaging = false;
            } else {
                averaging = true;
                arraySumm = 0;
                new AsyncAveraging().execute();
                tvLocat2.setText("Вычисляю");
                tvBetweenPoint.setText(" ");
            }
            }
        }
