package parking.gps_parking;

import android.os.AsyncTask;
import android.widget.Toast;

import static parking.gps_parking.MainActivity.array;
import static parking.gps_parking.MainActivity.arraySumm;
import static parking.gps_parking.MainActivity.averaging;
import static parking.gps_parking.MainActivity.context;
import static parking.gps_parking.MainActivity.counter;
import static parking.gps_parking.MainActivity.lat1;
import static parking.gps_parking.MainActivity.lat2;
import static parking.gps_parking.MainActivity.lng1;
import static parking.gps_parking.MainActivity.lng2;
import static parking.gps_parking.MainActivity.locationField;
import static parking.gps_parking.MainActivity.tvBetweenPoint;
import static parking.gps_parking.MainActivity.tvLocat2;

public class AsyncAveraging extends AsyncTask< Void,Void,String> {




    @Override
    protected String doInBackground(Void... voids) {

        while (averaging) {

            lat2 = (double) locationField.getLatitude();
            lng2 = (double) locationField.getLongitude();

            double distance = distFrom(lat1, lng1, lat2, lng2);

            distance = distance * 100;

            String formattedDouble = String.format("%.2f", distance);



            arraySumm = arraySumm + distance;

            try {
                Thread.sleep(300 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            counter++;


            this.publishProgress();



            if (counter >= 3) {
                counter = 0;
                averaging = false;
                this.cancel(false);

                break;
            }


        }
            return null;
        }


        private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return   earthRadius * c;
    }



        @Override
        protected void onProgressUpdate (Void...values){
            super.onProgressUpdate(values);
            if (counter == 2) {
                tvBetweenPoint.setText("Метры " + arraySumm / 3 + " ");
                tvLocat2.setText("");
            }
            Toast toast = Toast.makeText(context, "lat2 " + lat2 + "lng2 " + lng2, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

