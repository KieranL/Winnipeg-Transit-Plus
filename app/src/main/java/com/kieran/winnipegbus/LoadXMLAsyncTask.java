package com.kieran.winnipegbus;

import android.os.AsyncTask;

import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;

public class LoadXMLAsyncTask extends AsyncTask<String, Void, LoadResult> {
    protected LoadResult doInBackground(String... urls) {
        return BusUtilities.getXML(urls[0]);
    }
}
