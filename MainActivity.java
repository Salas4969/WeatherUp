public class WeatherActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView cityTextView;
    private TextView temperatureTextView;
    private ImageView weatherIconImageView;
    private TextView descriptionTextView;
    private TextView humidityTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private RecyclerView forecastRecyclerView;

    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityEditText = findViewById(R.id.cityEditText);
        cityTextView = findViewById(R.id.cityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windTextView = findViewById(R.id.windTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        forecastRecyclerView = findViewById(R.id.forecastRecyclerView);

        forecastAdapter = new ForecastAdapter();
        forecastRecyclerView.setAdapter(forecastAdapter);

        String apiKey = "04cfc26e70fc446a9dc26c29fb9e8eac";

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString();
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

                RequestQueue queue = Volley.newRequestQueue(WeatherActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject main = response.getJSONObject("main");
                                    JSONArray weatherArray = response.getJSONArray("weather");
                                    JSONObject weather = weatherArray.getJSONObject(0);
                                    JSONObject wind = response.getJSONObject("wind");

                                    String temperature = String.format(Locale.getDefault(), "%.0f°C", main.getDouble("temp") - 273.15);
                                    String description = weather.getString("description");
                                    String iconCode = weather.getString("icon");
                                    String humidity = String.format(Locale.getDefault(), "%d%%", main.getInt("humidity"));
                                    String windSpeed = String.format(Locale.getDefault(), "%.1f km/h", wind.getDouble("speed") * 3.6);
                                    String pressure = String.format(Locale.getDefault(), "%d hPa", main.getInt("pressure"));

                                    cityTextView.setText(city);
                                    temperatureTextView.setText(temperature);
                                    descriptionTextView.setText(description);
                                    humidityTextView.setText(humidity);
                                    windTextView.setText(windSpeed);
                                    pressureTextView.setText(pressure);

                                    int resourceId = getResources().getIdentifier("ic_" + iconCode, "drawable", getPackageName());
                                    weatherIconImageView.setImageResource(resourceId);

                                    // Fetch forecast data
                                    String forecastApiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey;
                                    JsonObjectRequest forecastJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, forecastApiUrl, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        JSONArray forecastList = response.getJSONArray("list");
                                                        List<Forecast> forecasts = new ArrayList<>();
                                                        for (int i = 0; i < forecastList.length(); i += 8) {
                                                            JSONObject forecastItem = forecastList.getJSONObject(i);
                                                            JSONObject main = forecastItem.getJSONObject("main");
                                                            JSONArray weatherArray = forecastItem.getJSONArray("weather");
                                                                                                                    JSONObject weather = weatherArray.getJSONObject(0);

                                                        String date = forecastItem.getString("dt_txt").substring(0, 10);
                                                        String temperature = String.format(Locale.getDefault(), "%.0f°C", main.getDouble("temp") - 273.15);
                                                        String description = weather.getString("description");
                                                        String iconCode = weather.getString("icon");

                                                        int resourceId = getResources().getIdentifier("ic_" + iconCode, "drawable", getPackageName());
                                                        Forecast forecast = new Forecast(date, temperature, description, resourceId);
                                                        forecasts.add(forecast);
                                                    }

                                                    forecastAdapter.setForecasts(forecasts);
                                                    forecastAdapter.notifyDataSetChanged();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(WeatherActivity.this, "Error fetching forecast data", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                queue.add(forecastJsonObjectRequest);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(WeatherActivity.this, "Error fetching weather data", Toast.LENGTH_SHORT).show();
                }
            });

            queue.add(jsonObjectRequest);
        }
    });
}


