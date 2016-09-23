package com.kieran.winnipegbus.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kieran.winnipegbus.Adapters.ServiceAdvisoriesAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbus.Views.StyledSwipeRefresh;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisoriesParser;
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisory;
import com.kieran.winnipegbusbackend.TransitApiManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdvisoriesActivity extends BaseActivity implements TransitApiManager.OnJsonLoadResultReceiveListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String SERVICE_ADVISORY = "service-advisory";
    private ServiceAdvisoriesAdapter adapter;
    private StyledSwipeRefresh swipeRefreshLayout;
    private boolean loading = false;
    private AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_advisories);

        ListView listView = (ListView) findViewById(R.id.service_advisories_listview);
        listView.setOnItemClickListener(this);
        List<ServiceAdvisory> advisories = new ArrayList<>();
        adapter = new  ServiceAdvisoriesAdapter(this, R.layout.listview_service_advisories_row, advisories);
        listView.setAdapter(adapter);

        swipeRefreshLayout = (StyledSwipeRefresh) findViewById(R.id.service_advisories_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_advisories, menu);

        onOptionsItemSelected(menu.findItem(R.id.action_refresh)); //manually click the refresh button, this is the only way the swipe refresh loading spinner works correctly on initial load. Not happy with this but it was the only way I could get it to work
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loading = false;
        if (task != null)
            task.cancel(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        loading = false;
    }

    private void refresh() {
        if (!loading) {
            if (isOnline()) {
                loading = true;

                adapter.loadTimeSetting();

                task = TransitApiManager.getJsonAsync(TransitApiManager.generateServiceAdvisoriesUrl(), this);
            } else {
                showLongToaster(R.string.network_error);
            }
        }
        swipeRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void OnReceive(LoadResult<JSONObject> result) {
        adapter.clear();
        List<ServiceAdvisory> advisories =  ServiceAdvisoriesParser.parseAdvisories(result.getResult());
        adapter.addAll(advisories);

        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        loading = false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ServiceAdvisoryActivity.class);
        intent.putExtra(SERVICE_ADVISORY, adapter.getItem(i));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
