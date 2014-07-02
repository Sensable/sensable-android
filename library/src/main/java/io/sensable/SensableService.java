package io.sensable;

import io.sensable.model.Sensable;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.List;

/**
 * Created by madine on 01/07/14.
 */
public interface SensableService {
    @GET("/sensable")
    void listSensables(Callback<List<Sensable>> cb);

    @GET("/sensable")
    List<Sensable> listSensables();

    @GET("/sensed/{id}")
    void getSensorData(@Path("id") String id, Callback<Sensable> cb);

    @GET("/sensed/{id}")
    Sensable getSensorData(@Path("id") String id);
}
