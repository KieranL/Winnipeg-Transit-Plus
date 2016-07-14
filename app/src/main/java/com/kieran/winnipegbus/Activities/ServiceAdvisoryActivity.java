package com.kieran.winnipegbus.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.kieran.winnipegbus.Adapters.ReroutesAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisory;

public class ServiceAdvisoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_advisory);

        ServiceAdvisory advisory = (ServiceAdvisory) getIntent().getSerializableExtra(ServiceAdvisoriesActivity.SERVICE_ADVISORY);

        ((TextView)findViewById(R.id.service_advisory_title1)).setText(advisory.getTitle());
        ((TextView)findViewById(R.id.service_advisory_header)).setText(advisory.getHeader());

        if(advisory.getAffectedStops().size() > 0)
            ((TextView)findViewById(R.id.service_advisory_affected_stops)).setText(advisory.getAffectedStops().toString());

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.service_advisory_reroutes_list);
        expandableListView.setAdapter(new ReroutesAdapter(this, advisory.getReroutes()));

        //((TextView)findViewById(R.id.service_advisory_reroutes)).setText(advisory.getReroutes().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_advisory, menu);
        return true;
    }
}
