package io.sensable;

import io.sensable.model.Sensable;
import io.sensable.model.User;
import io.sensable.model.UserLogin;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
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

    @POST("/login")
    void login(@Body UserLogin userLogin, Callback<User> cb);

    @POST("/login")
    User login(@Body UserLogin userLogin);

    @GET("/user-settings/{username}")
    void settings(@Path("username") String username, Callback<User> cb);

    @GET("/user-settings/{username}")
    User settings(@Path("username") String username);
}
