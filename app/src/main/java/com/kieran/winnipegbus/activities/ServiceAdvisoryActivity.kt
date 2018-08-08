package com.kieran.winnipegbus.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import android.widget.ListView

import com.kieran.winnipegbus.adapters.ReroutesAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.ServiceAdvisories.AffectedStop
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisory

class ServiceAdvisoryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_advisory)

        val advisory = intent.getSerializableExtra(ServiceAdvisoriesActivity.SERVICE_ADVISORY) as ServiceAdvisory

        val expandableListView = findViewById<View>(R.id.service_advisory_reroutes_list) as ExpandableListView
        expandableListView.setAdapter(ReroutesAdapter(this, advisory.reroutes))
        val header = layoutInflater.inflate(R.layout.expandablelistview_service_advisory_header, null)
        expandableListView.addHeaderView(header)

        val listView = findViewById<View>(R.id.service_advisory_affected_stops) as ListView
        listView.adapter = ArrayAdapter<AffectedStop>(this, R.layout.listview_affected_stop_row, advisory.affectedStops)

        setTextViewText(R.id.service_advisory_header, advisory.header)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_service_advisory, menu)
        return true
    }
}
