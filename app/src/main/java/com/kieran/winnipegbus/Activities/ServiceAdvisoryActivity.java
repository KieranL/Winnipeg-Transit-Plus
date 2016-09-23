package com.kieran.winnipegbus.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.kieran.winnipegbus.Adapters.ReroutesAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisory;

public class ServiceAdvisoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_advisory);

        ServiceAdvisory advisory = (ServiceAdvisory) getIntent().getSerializableExtra(ServiceAdvisoriesActivity.SERVICE_ADVISORY);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.service_advisory_reroutes_list);
        expandableListView.setAdapter(new ReroutesAdapter(this, advisory.getReroutes()));
        View header = getLayoutInflater().inflate(R.layout.expandablelistview_service_advisory_header, null);
        expandableListView.addHeaderView(header);

        ListView listView = (ListView) findViewById(R.id.service_advisory_affected_stops);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.listview_affected_stop_row, advisory.getAffectedStops()));

        setTextViewText(R.id.service_advisory_header, advisory.getHeader());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_advisory, menu);
        return true;
    }
}
