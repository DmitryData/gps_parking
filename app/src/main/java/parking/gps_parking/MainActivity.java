package parking.gps_parking;

/*  Программа собирает gps и net координаты,
во время запуска приложения, и сравнивает их
с другой координатой, которая получается
после нажатия на кнопку */

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

public class MainActivity extends Activity {

    private AsyncAveraging asyncObj = new AsyncAveraging();

    public static LocationManager locationManager; // объект который делает магию gps

    private TextView tvLocationGPS; // координаты gps, для точки 1
    private TextView tvLocationNet; // координаты net, для точки 1
    private TextView tvStatusNet;   // код состояния net
    private TextView tvStatusGPS;   // код состояния gps
    private TextView tvEnabledGPS;  // включен ли gps
    private TextView tvEnabledNet;  // включен ли net

    public static boolean averaging;               // если averaging == true, то кнопка точки 2,
                                                   // была нажата. Поэтому для того чтоб
                                                   // запустился asincTask
                                                   // надо чтоб checked == false
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvLocat2;               // static координаты, для точки 2
    @SuppressLint("StaticFieldLeak")
    public static TextView tvBetweenPoint;         // расстояние между двумя точками
                                                   // находится на отличной от точки 1 месте)
    public static Location locationField;
    public static boolean checked = false;         // если false, то проверяет текущее местоположение
                                                   // при запуске, если true, то уже проверил
    public static double lat1;                     // широта точки 1
    public static double lat2;                     // широта точки 2
    public static double lng1;                     // долгота точки 1
    public static double lng2;                     // долгота точки 2

    public static int counter;                     // счетчик
    public static double arraySumm;                // усредненная дистанция

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
    };  // обработка разных сценариев для gps

    // задает координаты в поля текста
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

    // возвращает в поле текста координаты
    @SuppressLint("DefaultLocale")
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        locationField = location;
        return lat1 + " / " +  lng1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocat2 = (TextView) findViewById(R.id.tvLocat2);
        tvBetweenPoint = (TextView) findViewById(R.id.tvBetweenPoint);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        averaging = true;
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,300, 1, locationListener);      // настройка сбора данных
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300, 1, locationListener); // настройка сбора данных
        checked = false;
        checkEnabled();                            // обновляем информацию о включенности провайдеров
    }
    @SuppressLint("SetTextI18n")
    private void checkEnabled() {
        tvEnabledGPS.setText("Состояние: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Состояние: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    // запуск Activity настроек
    public void onClickLocationSettings(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    // запуск AsyncTask c установкой точки 2
    public void averaging(View view){
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
