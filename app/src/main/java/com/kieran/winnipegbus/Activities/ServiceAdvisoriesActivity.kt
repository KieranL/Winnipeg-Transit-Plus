package com.kieran.winnipegbus.Activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import com.kieran.winnipegbus.Adapters.ServiceAdvisoriesAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.Views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisoriesParser
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisory
import com.kieran.winnipegbusbackend.TransitApiManager

import org.json.JSONObject

import java.util.ArrayList

class ServiceAdvisoriesActivity : BaseActivity(), TransitApiManager.OnJsonLoadResultReceiveListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private var adapter: ServiceAdvisoriesAdapter? = null
    private var swipeRefreshLayout: StyledSwipeRefresh? = null
    private var loading = false
    private var task: AsyncTask<*, *, *>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_advisories)

        val listView = findViewById<View>(R.id.service_advisories_listview) as ListView
        listView.onItemClickListener = this
        val advisories = ArrayList<ServiceAdvisory>()
        adapter = ServiceAdvisoriesAdapter(this, R.layout.listview_service_advisories_row, advisories)
        listView.adapter = adapter

        swipeRefreshLayout = findViewById<View>(R.id.service_advisories_swipe_refresh) as StyledSwipeRefresh
        swipeRefreshLayout!!.setOnRefreshListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_service_advisories, menu)

        onOptionsItemSelected(menu.findItem(R.id.action_refresh)) //manually click the refresh button, this is the only way the swipe refresh loading spinner works correctly on initial load. Not happy with this but it was the only way I could get it to work
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                refresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onDestroy() {
        super.onDestroy()
        loading = false
        if (task != null)
            task!!.cancel(true)
    }

    public override fun onPause() {
        super.onPause()
        loading = false
    }

    private fun refresh() {
        if (!loading) {
            if (isOnline) {
                loading = true

                adapter!!.loadTimeSetting()

                task = TransitApiManager.getJsonAsync(TransitApiManager.generateServiceAdvisoriesUrl(), this)
            } else {
                showLongToaster(R.string.network_error)
            }
        }
        swipeRefreshLayout!!.isRefreshing = loading
    }

    override fun OnReceive(result: LoadResult<JSONObject>) {
        adapter!!.clear()
        val advisories = ServiceAdvisoriesParser.parseAdvisories(result.result)
        adapter!!.addAll(advisories)

        adapter!!.notifyDataSetChanged()
        swipeRefreshLayout!!.isRefreshing = false
        loading = false
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val intent = Intent(this, ServiceAdvisoryActivity::class.java)
        intent.putExtra(SERVICE_ADVISORY, adapter!!.getItem(i))
        startActivity(intent)
    }

    override fun onRefresh() {
        refresh()
    }

    companion object {
        val SERVICE_ADVISORY = "service-advisory"
    }
}
