package com.kieran.winnipegbus.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.kieran.winnipegbus.Adapters.StopListAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;

public class FavouritesActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnItemLongClickListener {
    private StopListAdapter adapter;

    @Override
    protected void onRestart() {
        super.onRestart();
        FavouriteStopsList.sort(getSortPreference());
        reloadList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FavouriteStopsList.sort(getSortPreference());
        reloadList();
    }

    private void reloadList() {
        FavouriteStopsList.isLoadNeeded = true;
        getFavouritesList();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adViewResId = R.id.stopsListAdView;

        setContentView(R.layout.activity_favourite_stops);

        ListView listView = (ListView) findViewById(R.id.stops_listView);

        initializeAdsIfEnabled();
        getFavouritesList();

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        adapter = new StopListAdapter(this, R.layout.listview_stops_row);
        listView.setAdapter(adapter);
    }

    private void getFavouritesList() {
        FavouriteStopsList.loadFavourites();
        StopListAdapter.sortPreference =  getSortPreference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites, menu);
        return true;
    }

    private void openStopTimesAndUse(FavouriteStop favouriteStop) {
        favouriteStop.use();
        FavouriteStopsList.saveFavouriteStops();

        openStopTimes(favouriteStop);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openStopTimesAndUse(adapter.getItem(position));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Context context = this;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setMessage("Edit this Favourite?");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int which) {
                FavouriteStopsList.remove(adapter.getItem(position).getNumber());
                reloadList();
            }
        });

        alertDialog.setNeutralButton("Rename", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int which) {
                AlertDialog.Builder renameDialog = new AlertDialog.Builder(context);
                final EditText editText = new EditText(context);
                final FavouriteStop favouriteStop = FavouriteStopsList.get(position);
                editText.setText(favouriteStop.getDisplayName());
                renameDialog.setView(editText);

                renameDialog.setNeutralButton("Default", null);

                renameDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavouriteStopsList.get(position).setAlias(editText.getText().toString());
                        FavouriteStopsList.saveFavouriteStops();
                        reloadList();
                    }
                });
                renameDialog.setNegativeButton("Cancel", null);

                Button button = renameDialog.show().getButton(DialogInterface.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(favouriteStop.getName());
                    }
                });
            }
        });

        alertDialog.setNegativeButton("Cancel", null);
        alertDialog.create().show();

        return true;
    }
}
