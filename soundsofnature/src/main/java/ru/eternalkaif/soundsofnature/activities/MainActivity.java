package ru.eternalkaif.soundsofnature.activities;

import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import ru.eternalkaif.soundsofnature.BaseActivity;
import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.adapters.SoundsListCursorAdapter;
import ru.eternalkaif.soundsofnature.fragments.DownloadedListFragment;
import ru.eternalkaif.soundsofnature.fragments.MainListFragment;
import ru.eternalkaif.soundsofnature.handler.BaseCommand;
import ru.eternalkaif.soundsofnature.handler.GetSongListCommand;
import ru.eternalkaif.soundsofnature.listeners.OnFragmentInteractionListener;

public class MainActivity extends BaseActivity implements
        ActionBar.OnNavigationListener, LoaderManager.LoaderCallbacks<Cursor>,
        OnFragmentInteractionListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final String PROGRESS_DIALOG = "progress-dialog";

    private int requestId = -1;


    public static final int LOADER_ID = 0;
    private Uri mDataUrl;
    private String[] mProjection;
    private SoundsListCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.title_section_songlist),
                                getString(R.string.title_section_downloaded),
                        }),
                this);

       retreiveSongList();

    }


    private void retreiveSongList(){
        //TODO:TEST METHOOD
        ProgressDialogFragment progress = new ProgressDialogFragment();
        progress.show(getSupportFragmentManager(), PROGRESS_DIALOG);

        requestId = getServiceHelper().getSongListAction();
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getServiceHelper().cancelCommand(requestId);
            }
        });

        return progressDialog;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (requestId != -1 && !getServiceHelper().isPending(requestId)) {
            dismissProgressDialog();
        }
    }

    @Override
    public void onServiceCallBack(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallBack(requestId, requestIntent, resultCode, resultData);

        if (getServiceHelper().check(requestIntent, GetSongListCommand.class)) {
            if (resultCode == GetSongListCommand.RESPONSE_SUCCESS) {
                Toast.makeText(this, resultData.getString("data"), Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            } else if (resultCode == GetSongListCommand.RESPONSE_PROGRESS) {
                upodateProgressDialog(resultData.getInt(BaseCommand.EXTRA_PROGRESS, -1));
            } else {
                Toast.makeText(this, resultData.getString("error"), Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            }
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Result: 0%");

            return progressDialog;
        }

        public void setProgress(int progress) {
            ((ProgressDialog) getDialog()).setMessage("Result: " + progress + "%");
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            ((MainActivity) getActivity()).cancelCommand();
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_reload){
            retreiveSongList();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.

        switch (position) {
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MainListFragment.newInstance("", "")).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, DownloadedListFragment.newInstance("", "")).commit();

        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(
                        this,
                        mDataUrl,
                        mProjection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
                mAdapter.swapCursor(data);
                break;
        }


    }
    public void cancelCommand() {
        getServiceHelper().cancelCommand(requestId);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public void onFragmentInteraction(String id) {

    }

    private void dismissProgressDialog() {
        ProgressDialogFragment progress = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(
                PROGRESS_DIALOG);
        if (progress != null) {
            progress.dismiss();
        }
    }

    private void upodateProgressDialog(int progress) {
        ProgressDialogFragment progressDialog = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(
                PROGRESS_DIALOG);
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }
}
