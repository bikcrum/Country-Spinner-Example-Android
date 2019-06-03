package com.example.countryspinnerexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "countryspinnerexample";
    private JSONArray jsonCountryArray;
    private Spinner countrySpinner;
    private Spinner stateSpinner;
    private Spinner citySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countrySpinner = findViewById(R.id.spinner_country);
        stateSpinner = findViewById(R.id.spinner_state);
        citySpinner = findViewById(R.id.spinner_city);

        populateSpinner();
    }

    private void populateSpinner() {
        try {
            jsonCountryArray = new JSONObject(loadJSONFromAsset()).optJSONArray("country");

            ArrayList<String> countryList = new ArrayList<>();

            for (int i = 0; i < jsonCountryArray.length(); i++) {
                countryList.add(jsonCountryArray.optJSONObject(i).optString("name"));
            }

            ArrayAdapter<String> countryListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);

            countrySpinner.setAdapter(countryListAdapter);

            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> stateArray = new ArrayList<>();

                    final JSONArray jsonStateArray = jsonCountryArray.optJSONObject(position).optJSONArray("state");

                    for (int i = 0; i < jsonStateArray.length(); i++) {
                        stateArray.add(jsonStateArray.optJSONObject(i).optString("name"));
                    }

                    ArrayAdapter<String> stateListAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stateArray);

                    stateSpinner.setAdapter(stateListAdapter);

                    stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            final ArrayList<String> cityArray = new ArrayList<>();

                            final JSONArray jsonCityArray = jsonStateArray.optJSONObject(position).optJSONArray("city");

                            for (int i = 0; i < jsonCityArray.length(); i++) {
                                cityArray.add(jsonCityArray.optJSONObject(i).optString("name"));
                            }

                            ArrayAdapter<String> cityListAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, cityArray);

                            citySpinner.setAdapter(cityListAdapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error=" + e.getMessage());
        }
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void submit(View view) {
        int countryPosition = countrySpinner.getSelectedItemPosition();
        int statePosition = stateSpinner.getSelectedItemPosition();
        int cityPosition = citySpinner.getSelectedItemPosition();

        int countryId = jsonCountryArray
                .optJSONObject(countryPosition)
                .optInt("id");

        int stateId = jsonCountryArray
                .optJSONObject(countryPosition)
                .optJSONArray("state")
                .optJSONObject(statePosition)
                .optInt("id");

        int cityId = jsonCountryArray
                .optJSONObject(countryPosition)
                .optJSONArray("state")
                .optJSONObject(statePosition)
                .optJSONArray("city")
                .optJSONObject(cityPosition)
                .optInt("id");

        try {
            JSONObject result = new JSONObject()
                    .put("countryId", countryId)
                    .put("stateId", stateId)
                    .put("cityId", cityId);

            Log.d(TAG, "result=" + result);
        } catch (JSONException e) {
            Log.e(TAG, "error=" + e.getMessage());
            e.printStackTrace();
        }
    }
}
