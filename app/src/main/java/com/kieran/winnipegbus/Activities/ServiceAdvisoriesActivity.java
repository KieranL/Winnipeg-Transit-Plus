package com.kieran.winnipegbus.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.kieran.winnipegbus.Adapters.ServiceAdvisoriesAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisoriesParser;
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisory;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdvisoriesActivity extends BaseActivity implements BusUtilities.OnLoadResultReceiveListener{
    private ServiceAdvisoriesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_advisories);

        ListView listView = (ListView) findViewById(R.id.service_advisories_listview);
        List<ServiceAdvisory> advisories = new ArrayList<>();
        adapter = new  ServiceAdvisoriesAdapter(this, R.layout.listview_service_advisories_row, advisories);
        listView.setAdapter(adapter);
        String path = BusUtilities.generateServiceAdvisoriesUrl();
        BusUtilities.getXMLAsync(path, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_advisories, menu);
        return true;
    }

    @Override
    public void OnReceive(LoadResult result) {
        List<ServiceAdvisory> advisories =  ServiceAdvisoriesParser.parseAdvisories((Document) result.getResult());
        adapter.addAll(advisories);

        adapter.notifyDataSetChanged();
    }
}
