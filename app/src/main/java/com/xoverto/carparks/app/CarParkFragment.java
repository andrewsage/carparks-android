package com.xoverto.carparks.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.xoverto.carparks.app.dummy.DummyContent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the Callbacks
 * interface.
 */
public class CarParkFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String TAG = "CARPARKS";
    private Handler handler = new Handler();

    private List mCarParkList;

    private OnFragmentInteractionListener mListener;


    private SimpleCursorAdapter mCursorAdapter;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CarParkAdapter mAdapter;

    public static CarParkFragment newInstance() {
        CarParkFragment fragment = new CarParkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CarParkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCarParkList = new ArrayList();
        mAdapter = new CarParkAdapter(getActivity(), mCarParkList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carpark, container, false);

        // Set the adapter
        mCursorAdapter = new CarParkSimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[] { CarParkProvider.KEY_NAME, CarParkProvider.KEY_UPDATED, CarParkProvider.KEY_CAR_PARK_ID, CarParkProvider.KEY_LOCATION_LAT, CarParkProvider.KEY_LOCATION_LNG},
                new int[] { android.R.id.text1, android.R.id.text2}, 0);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mCursorAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);
        refreshCarParks();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onAttach(Activity activity) {
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CarParkProvider.KEY_ID,
                CarParkProvider.KEY_NAME,
                CarParkProvider.KEY_CAR_PARK_ID,
                CarParkProvider.KEY_LOCATION_LAT,
                CarParkProvider.KEY_LOCATION_LNG,
                CarParkProvider.KEY_UPDATED
        };
        CursorLoader loader = new CursorLoader(getActivity(),
                CarParkProvider.CONTENT_URI,
                projection, null, null, null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = (Cursor)mCursorAdapter.getItem(position);
        String value = cursor.getString(cursor
                .getColumnIndex(CarParkProvider.KEY_NAME));
        double lat = cursor.getDouble(cursor.getColumnIndex(CarParkProvider.KEY_LOCATION_LAT));
        double lng = cursor.getDouble(cursor.getColumnIndex(CarParkProvider.KEY_LOCATION_LNG));

        LatLng carParkLatLng = new LatLng(lat, lng);


        Toast.makeText(parent.getContext(), value, Toast.LENGTH_SHORT).show();

        //CarPark carPark = (CarPark) mCarParkList.get(position);
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(carParkLatLng);
        }
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

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(LatLng latLng);
    }

    public void refreshCarParks() {

        getLoaderManager().restartLoader(0, null, CarParkFragment.this);
        getActivity().startService(new Intent(getActivity(), CarParkUpdateService.class));

    }

}
