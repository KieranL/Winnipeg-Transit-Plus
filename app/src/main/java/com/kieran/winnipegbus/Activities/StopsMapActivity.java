package com.kieran.winnipegbus.Activities;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.Stop;

public class StopsMapActivity extends MapActivity {

    public static final float DEFAULT_ZOOM = 11f;
    public static final double DEFAULT_LATITUDE = 49.8954;
    public static final double DEFAULT_LONGITUDE = -97.1385;
    public static final int MAP_PADDING = 50;
    private CameraUpdate cameraUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        mapOnCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        if(SearchResultsActivity.searchResultsList != null) {
            for (FavouriteStop favouriteStop : SearchResultsActivity.searchResultsList) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(favouriteStop.getLatLng());
                markerOptions.title(Integer.toString(favouriteStop.getNumber()));
                markerOptions.snippet(favouriteStop.getName());
                map.addMarker(markerOptions);
            }
        }

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                openStopTimes(new Stop(marker.getSnippet(), Integer.parseInt(marker.getTitle())));
            }
        });
        onConnected();

        moveMap();
    }

    private void moveMap() {
        double maxLat = 0;
        double maxLng = 0;
        double minLat = 0;
        double minLng = 0;

        if(cameraUpdate == null) {
            for (FavouriteStop favouriteStop : SearchResultsActivity.searchResultsList) {
                if (maxLat == 0 || favouriteStop.getLatLng().latitude > maxLat)
                    maxLat = favouriteStop.getLatLng().latitude;
                if (maxLng == 0 || favouriteStop.getLatLng().longitude > maxLng)
                    maxLng = favouriteStop.getLatLng().longitude;
                if (minLat == 0 || favouriteStop.getLatLng().latitude < minLat)
                    minLat = favouriteStop.getLatLng().latitude;
                if (minLng == 0 || favouriteStop.getLatLng().longitude < minLng)
                    minLng = favouriteStop.getLatLng().longitude;
            }

            if (maxLat != 0 && minLat != 0 && maxLng != 0 && minLng != 0) {
                LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);
            } else {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), DEFAULT_ZOOM);
            }
            map.moveCamera(cameraUpdate);
        }
    }
}
