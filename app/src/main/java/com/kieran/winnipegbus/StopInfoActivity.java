package com.kieran.winnipegbus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbus.Adapters.StopFeaturesAdapter;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.StopFeatures;

import org.w3c.dom.Document;

public class StopInfoActivity extends BaseActivity {
    private StopFeatures stopFeatures;
    private StopFeaturesAdapter adapter;
    private AdView adView;
    private AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_info);
        adView = (AdView) findViewById(R.id.stopFeaturesAdView);
        stopFeatures = (StopFeatures) getIntent().getSerializableExtra(StopTimesActivity.STOP);
        setTitle("Stop " + stopFeatures.getStopNumber());
        ((TextView)findViewById(R.id.stop_features_title)).setText(stopFeatures.getStopName());

        ListView listView = (ListView) findViewById(R.id.listView_stop_features);
        adapter = new StopFeaturesAdapter(this, R.layout.listview_stop_features_row, stopFeatures.getStopFeatures());
        listView.setAdapter(adapter);

        ActivityUtilities.initializeAdsIfEnabled(this, adView);
        task = new LoadStopFeatures().execute(BusUtilities.generateStopFeaturesUrl(stopFeatures.getStopNumber()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        task.cancel(true);
        ActivityUtilities.destroyAdView(adView);
    }

    @Override
    public void onPause() {
        super.onPause();
        adView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadStopFeatures extends AsyncTask<String, Void, LoadResult> {

        @Override
        protected LoadResult doInBackground(String... urls) {
            return BusUtilities.getXML(urls[0]);
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                stopFeatures.loadFeatures((Document)result.getResult());
                adapter.notifyDataSetChanged();
            }else if(result.getException() != null) {
                ActivityUtilities.createLongToaster(getApplicationContext(), getText(R.string.network_error).toString());
            }

        }

    }
}
