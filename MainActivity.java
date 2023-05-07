import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "04cfc26e70fc446a9dc26c29fb9e8eac";
    private static final String CITY_NAME = "New York";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s";

    private TextView mTodayTempTextView;
    private TextView mTodayWeatherDescTextView;
    private TextView mTodayDateTextView;
    private TextView mTomorrowTempTextView;
    private TextView mTomorrowWeatherDescTextView;
    private TextView mTomorrowDateTextView;
    private TextView mFiveDayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTodayTempTextView = findViewById(R.id.today_temp_textview);
        mTodayWeatherDescTextView = findViewById(R.id.today_weather_desc_textview);
        mTodayDateTextView = findViewById(R.id.today_date_textview);
        mTomorrowTempTextView = findViewById(R.id.tomorrow_temp_textview);
        mTomorrowWeatherDescTextView = findViewById(R.id.tomorrow_weather_desc_textview);
        mTomorrowDateTextView = findViewById(R.id.tomorrow_date_textview);
        mFiveDayTextView = findViewById(R.id.five_day_textview);

        getCurrentWeather();
        getFiveDayForecast();
    }

    private void getCurrentWeather() {
        String url = String.format(API_URL, CITY_NAME, API_KEY);

        HttpHandler httpHandler = new HttpHandler();
        String jsonStr = httpHandler.makeServiceCall(url);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                JSONArray weatherArr = jsonObj.getJSONArray("list");

                // Get today's weather data
                JSONObject todayWeatherObj = weatherArr.getJSONObject(0);
                JSONObject todayMainObj = todayWeatherObj.getJSONObject("main");
                JSONArray todayWeatherJsonArr = todayWeatherObj.getJSONArray("weather");
                JSONObject todayWeatherJsonObj = todayWeatherJsonArr.getJSONObject(0);

                // Display today's weather data
                double todayTemp = todayMainObj.getDouble("temp");
                String todayTempStr = String.format("%.0f\u00B0", todayTemp);
                mTodayTempTextView.setText(todayTempStr);

                String todayWeatherDesc = todayWeatherJsonObj.getString("description");
                mTodayWeatherDescTextView.setText(todayWeatherDesc);

                String todayDateStr = getFormattedDate(todayWeatherObj.getLong("dt"));
                mTodayDateTextView.setText(todayDateStr);

                // Get tomorrow's weather data
                JSONObject tomorrowWeatherObj = weatherArr.getJSONObject(8);
                JSONObject tomorrowMainObj = tomorrowWeatherObj.getJSONObject("main");
                JSONArray tomorrowWeatherJsonArr = tomorrowWeatherObj.getJSONArray("weather");
                JSONObject tomorrowWeatherJsonObj = tomorrowWeatherJsonArr.getJSONObject(0);

                // Display tomorrow's weather data
                double tomorrowTemp = tomorrowMainObj.getDouble("temp");
                String tomorrowTempStr = String.format("%.0f\u00B0", tomorrowTemp);
                mTomorrowTempTextView.setText(tomorrowTempStr);

                String tomorrowWeatherDesc = tomorrowWeatherJsonObj.getString("description");
                mTomorrowWeatherDescTextView.setText(tomorrowWeatherDesc);

                String tomorrowDateStr = getFormattedDate(tomorrowWeatherObj.getLong("dt"));
                mTomorrowDateTextView.setText(tomorrowDateStr);

            } catch (final JSONException e) {
                e.printStackTrace();
