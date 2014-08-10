package io.sensable.client;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import io.sensable.SensableService;
import io.sensable.model.Statistics;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class AboutActivity extends Activity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    private TextView statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        statistics = (TextView) findViewById(R.id.about_statistics);


        TextView view = (TextView)findViewById(R.id.about_text);
        String formattedText = getString(R.string.about_text);
        Spanned result = Html.fromHtml(formattedText);
        view.setText(result);

        loadStatistics();

    }

    private void loadStatistics() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.getStatistics(new Callback<Statistics>() {
            @Override
            public void success(Statistics statisticsResponse, Response response) {
                Log.d(TAG, "Statistics callback Success: " + statisticsResponse.getCount());

                statistics.setText(NumberFormat.getInstance().format(statisticsResponse.getCount()) + " samples on sensable.io");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
                statistics.setVisibility(View.GONE);
            }
        });
    }

}
