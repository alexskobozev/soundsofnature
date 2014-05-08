package ru.eternalkaif.soundsofnature.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.activities.PlayerActivity;
import ru.eternalkaif.soundsofnature.adapters.SoundsListCursorAdapter;
import ru.eternalkaif.soundsofnature.db.SoundsDataBaseContract;
import ru.eternalkaif.soundsofnature.db.SoundsProvider;
import ru.eternalkaif.soundsofnature.listeners.OnFragmentInteractionListener;
import ru.eternalkaif.soundsofnature.service.MusicService;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link android.support.v7.internal.widget.ActivityChooserView.Callbacks}
 * interface.
 */
public class MainListFragment extends ListFragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] PROJECTION = new String[]{BaseColumns._ID,
            SoundsDataBaseContract.Sounds.NamesColoumns.SOUNDTITLE,
            SoundsDataBaseContract.Sounds.NamesColoumns.SOUNDMP3LINK};
    private static final int LOADER_ID = 1;

    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;


    // TODO: Rename and change types of parameters
    @Nullable
    private String mParam1;
    @Nullable
    private String mParam2;

    @Nullable
    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SoundsListCursorAdapter mAdapter;

    // TODO: Rename and change types of parameters
    @NotNull
    public static MainListFragment newInstance(String param1, String param2) {
        MainListFragment fragment = new MainListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainlistfragment, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        // mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(@NotNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(@NotNull AdapterView<?> parent, View view, int position, long id) {
        int pqosition = (int) parent.getSelectedItemId();
        Log.i("Position:", Integer.toString(pqosition));

        Log.d("ITEMCLICK", position + "");
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        Cursor cursor = mAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            if (MusicService.getInstance() != null)
                if (MusicService.getInstance().isPlaying()) {
                    MusicService.getInstance().stop();
                }
            Log.d("ITEMCLICK", cursor.toString() + "");
            Intent serviceIntent = new Intent(getActivity(), MusicService.class);
            serviceIntent.setAction(MusicService.ACTION_PLAY);
            serviceIntent.putExtra(MusicService.SONG_URL, cursor.getString(cursor
                    .getColumnIndexOrThrow(SoundsDataBaseContract
                            .Sounds.NamesColoumns.SOUNDMP3LINK)));
            serviceIntent.putExtra(MusicService.SONG_NAME, cursor.getString(cursor
                    .getColumnIndexOrThrow(SoundsDataBaseContract
                            .Sounds.NamesColoumns.SOUNDTITLE)));
            getActivity().startService(serviceIntent);


            intent.putExtra(MusicService.SONG_URL, cursor.getString(cursor
                    .getColumnIndexOrThrow(SoundsDataBaseContract
                            .Sounds.NamesColoumns.SOUNDMP3LINK)));
            intent.putExtra(MusicService.SONG_NAME, cursor.getString(cursor
                    .getColumnIndexOrThrow(SoundsDataBaseContract
                            .Sounds.NamesColoumns.SOUNDTITLE)));
        }
        startActivity(intent);

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), SoundsProvider.CONTENT_URI, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> cursorLoader, @NotNull Cursor cursor) {
        switch (cursorLoader.getId()) {
            case LOADER_ID:
                mAdapter = new SoundsListCursorAdapter(getActivity(), cursor, 0);
                mAdapter.swapCursor(cursor);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(this);
                break;

        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}
