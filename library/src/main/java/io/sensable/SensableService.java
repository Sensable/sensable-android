package io.sensable;

import io.sensable.model.*;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import java.util.List;

/**
 * Acts as the interface between the application and the REST API
 */
public interface SensableService {
    @GET("/sensable")
    void listSensables(Callback<List<Sensable>> cb);

    @GET("/sensable")
    List<Sensable> listSensables();

    @POST("/sensable")
    void createSensable(@Body Sensable sensable, Callback<SampleResponse> cb);

    @POST("/sensable")
    SampleResponse createSensable(@Body Sensable sensable);

    @POST("/sensed/{id}")
    void saveSample(@Path("id") String id, @Body SampleSender sampleSender, Callback<SampleResponse> cb);

    @POST("/sensed/{id}")
    SampleResponse saveSample(@Path("id") String id, @Body SampleSender sampleSender);

    @GET("/sensed/{id}")
    void getSensorData(@Path("id") String id, Callback<Sensable> cb);

    @GET("/sensed/{id}")
    Sensable getSensorData(@Path("id") String id);

    @POST("/login")
    void login(@Body UserLogin userLogin, Callback<User> cb);

    @POST("/login")
    User login(@Body UserLogin userLogin);

    @GET("/user-settings/{username}")
    void settings(@Path("username") String username, Callback<User> cb);

    @GET("/user-settings/{username}")
    User settings(@Path("username") String username);

    @GET("/statistics")
    void getStatistics(Callback<Statistics> cb);

    @GET("/statistics")
    Statistics getStatistics();
}