package com.kieran.winnipegbus.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kieran.winnipegbus.Adapters.StopFeaturesAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.StopFeatures;
import com.kieran.winnipegbusbackend.TransitApiManager;

import org.json.JSONObject;

import java.util.Locale;

public class StopInfoActivity extends MapActivity implements TransitApiManager.OnJsonLoadResultReceiveListener {
    public static final String ACTIONBAR_TEXT = "Stop %d";
    public static final String STOP_FEATURES = "stop_features";
    private StopFeatures stopFeatures;
    private StopFeaturesAdapter adapter;
    private AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_info);
        stopFeatures = (StopFeatures)getIntent().getSerializableExtra(STOP_FEATURES);

        setTitle(String.format(Locale.CANADA, ACTIONBAR_TEXT, stopFeatures.getNumber()));
        setTextViewText(R.id.stop_features_title, stopFeatures.getName());

        ListView listView = (ListView) findViewById(R.id.listView_stop_features);
        adapter = new StopFeaturesAdapter(this, R.layout.listview_stop_features_row, stopFeatures.getStopFeatures());
        listView.setAdapter(adapter);

        task = TransitApiManager.getJsonAsync(TransitApiManager.generateStopFeaturesUrl(stopFeatures.getNumber()), this);
        mapOnCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_info, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(stopFeatures.getLatLng(), 17);
        map.moveCamera(cameraUpdate);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(stopFeatures.getLatLng());
        map.addMarker(markerOptions);
        map.setTrafficEnabled(true);

        onConnected();
    }

    private void showStopFeatures() {
        if(stopFeatures.numberOfFeatures() > 0) {
            adapter.notifyDataSetChanged();
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.stop_features_group);
            layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnReceive(LoadResult<JSONObject> result) {
        if (result.getResult() != null) {
            stopFeatures.loadFeatures(result.getResult());
            showStopFeatures();
        }else if(result.getException() != null) {
            handleException(result.getException());
        }
    }
}
