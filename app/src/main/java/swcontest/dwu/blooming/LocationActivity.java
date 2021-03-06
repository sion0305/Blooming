package swcontest.dwu.blooming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import swcontest.dwu.blooming.db.LocationDBManager;
import swcontest.dwu.blooming.dto.LocationDto;
import swcontest.dwu.blooming.service.FetchAddressIntentService;
import swcontest.dwu.blooming.service.LocationService;

import static swcontest.dwu.blooming.MainActivity.period;

public class LocationActivity extends AppCompatActivity {

    public static final String TAG = "LocationActivity";

    LocationActivityThread thread;
    handler mHandler;
    private AddressResultReceiver addressResultReceiver;
    double latitude, longitude;
    String class_name = LocationService.class.getName();

    private MapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private Marker centerMarker;
    private PolylineOptions pOptions;
    private ArrayList<LatLng> arrayPoints = null;

    TextView tvAddress, tvGuide;
    LocationDBManager dbManager;
    ArrayList<LocationDto> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        addressResultReceiver = new AddressResultReceiver(new Handler());
        tvAddress = findViewById(R.id.tvAddress);

        tvGuide = findViewById(R.id.tvGuide);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

        pOptions = new PolylineOptions();
        pOptions.color(Color.rgb(255, 102, 0));
        pOptions.width(5);

        dbManager = new LocationDBManager(this);
        list = new ArrayList<LocationDto>();
        arrayPoints = new ArrayList<LatLng>();

        if (isServiceRunning(class_name)) {
            tvGuide.setText("??? ?????? ????????? ????????? ??? " + period + "????????? ???????????????.");

            mHandler = new handler();
            thread = new LocationActivityThread(mHandler);
            thread.start();
        } else {
            tvGuide.setText("??? ?????? ????????? ????????? ????????????.");
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLocationMemo :
                Intent intent = new Intent(LocationActivity.this, LocationListActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.btnHome :
                Intent hIntent = new Intent(LocationActivity.this, MainActivity.class);
                startActivity(hIntent);
                finish();
                break;
        }
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            long now = System.currentTimeMillis();
            Date date = new Date(now);

            SimpleDateFormat sdf_day = new SimpleDateFormat("yyyy??? M??? dd???");
            String getDay = sdf_day.format(date);

            list.clear();
            list.addAll(dbManager.getLocationByDate(getDay));

            if (list.size() != 0) {
                latitude = list.get(list.size() - 1).getLatitude();
                longitude = list.get(list.size() - 1).getLongitude();

                LatLng location = new LatLng(latitude, longitude);
                Log.v(TAG, "?????? : " + latitude + ", ?????? : " + longitude);

                startAddressService();

                MarkerOptions options = new MarkerOptions();
                options.position(location);
                options.title("?????? ??????");
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                centerMarker = mGoogleMap.addMarker(options);
                centerMarker.showInfoWindow();

                for (int i = 0; i < list.size(); i++) {
                    LatLng latLng = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
                    arrayPoints.add(latLng);
                }
                pOptions.addAll(arrayPoints);
                mGoogleMap.addPolyline(pOptions);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
            } else {
                if (!isServiceRunning(class_name)) {
                    Toast.makeText(getApplicationContext(), "?????? ????????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    /* ??????/?????? ??? ?????? ?????? IntentService ?????? */
    private void startAddressService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LocationConstants.RECEIVER, addressResultReceiver);
        intent.putExtra(LocationConstants.LAT_DATA_EXTRA, latitude);
        intent.putExtra(LocationConstants.LNG_DATA_EXTRA, longitude);
        startService(intent);
    }

    /* ??????/?????? ??? ?????? ?????? ResultReceiver */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String addressOutput = null;

            if (resultCode == LocationConstants.SUCCESS_RESULT) {
                if (resultData == null) return;
                addressOutput = resultData.getString(LocationConstants.RESULT_DATA_KEY);
                if (addressOutput == null) addressOutput = "";
                tvAddress.setText(addressOutput);
            } else {
                tvAddress.setText("??? ????????? ?????? ??? ????????????.");
            }
        }
    }

    // ????????? ?????? ?????? ??????
    public boolean isServiceRunning(String class_name) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (class_name.equals(service.service.getClassName())) {
                Log.d("LocationService", "?????? ???????????? ???????????? " + service.service.getClassName());
                return true;
            }
        }
        return false;
    }

    class handler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mGoogleMap.clear();
            Log.d("Location", "mapReadyCallBack");
            mapFragment.getMapAsync(mapReadyCallBack);
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            thread.stopForever();
            thread = null; // ????????? ??? ???????????? ????????? ??????????????? null ?????? ?????????.
        }

        super.onDestroy();
        Log.d(TAG, "LocationActivity : onDestroy() ??????");
    }
}
