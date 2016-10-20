package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String TAG = "PlaceholderFragment";
        private View rootView;
        private ListView lv_forecase;
        private String[] forecastArray = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        private List<String> weekForecase = new ArrayList<>(Arrays.asList(forecastArray));

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            initView();
            getData();
            return rootView;
        }

        private void getData() {
            new Thread(new Runnable() {
                @Override
                public void run() {


                    HttpURLConnection httpURLConnection = null;
                    BufferedReader bufferedReader = null;
                    String forecaseJsonStr = null;
                    try {
                        String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                        String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;


                        URL url = new URL(baseUrl.concat(apiKey));


                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.connect();

                        InputStream inputStream = httpURLConnection.getInputStream();
                        StringBuffer stringBuffer = new StringBuffer();
                        if (inputStream == null) {
                            forecaseJsonStr = null;
                        }
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuffer.append(line + "\n");
                        }
                        if (stringBuffer.length() == 0) {
                            forecaseJsonStr = null;
                        }
                        forecaseJsonStr = stringBuffer.toString();
                        Log.i(TAG, "getData: forecaseJsonStr=" + forecaseJsonStr);
                    }
//            catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
                    catch (IOException e) {
                        Log.e(TAG, "getData() Error", e);
                        forecaseJsonStr = null;
//                e.printStackTrace();
                    } finally {
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                Log.e(TAG, "getData: Error closing stream", e);
//                        e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }

        private void initView() {
            lv_forecase = (ListView) rootView.findViewById(R.id.lv_forecase);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_list_forecase, R.id.item_list_forecast_textview, weekForecase);
            lv_forecase.setAdapter(adapter);
        }
    }
}
