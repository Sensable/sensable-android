package io.sensable.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import io.sensable.SensableService;
import io.sensable.model.User;
import io.sensable.model.UserLogin;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by simonmadine on 12/07/2014.
 */
public class SensableUser {

    private static final String TAG = SensableUser.class.getSimpleName();

    private User mUser;
    public boolean loggedIn = false;
    public boolean hasAccessToken = false;

    private SensableService service;
    private SharedPreferences sharedPreferences;
    private Context context;

    public SensableUser(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;

        mUser = new User();
        loggedIn = readUserFromPreferences();

        //TODO: Remove this once API doesn't use cookies
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://sensable.io")
                .build();
        service = restAdapter.create(SensableService.class);

    }

    private boolean readUserFromPreferences() {
        String username = sharedPreferences.getString(context.getString(R.string.saved_username), "");
        if (username != "") {
            Log.d(TAG, "Username:" + username);
            // User is logged in
            mUser.setUsername(username);

            String email = sharedPreferences.getString(context.getString(R.string.saved_email), "");
            mUser.setEmail(email);

            String accessToken = sharedPreferences.getString(context.getString(R.string.saved_access_token), "");
            Log.d(TAG, "Access Token:" + accessToken);
            if (accessToken != "") {
                mUser.setAccessToken(accessToken);
                hasAccessToken = true;
            }
            return true;
        } else {
            return false;
        }
    }

    public void login(UserLogin userLogin, final MainActivity.CallbackInterface cb) {

        service.login(userLogin, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "Login Callback Success:" + user.getUsername());

                mUser.setUsername(user.getUsername());

                loggedIn = true;

                //TODO: Get rid of this once login returns user details by default
                userSettings();

                saveUserToPreferences();

                cb.loginStatusUpdate(loggedIn);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    public void userSettings() {

        service.settings(mUser.getUsername(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "Login Callback Success:" + user.getUsername());

                mUser.setUsername(user.getUsername());
                mUser.setEmail(user.getEmail());
                Log.d(TAG, "Login Callback Success:" + user.getEmail());

                loggedIn = true;

                String accessToken = user.getAccessToken();
                Log.d(TAG, "Login Callback Success:" + accessToken);
                if (accessToken != null) {
                    mUser.setAccessToken(accessToken);
                    hasAccessToken = true;
                }
                saveUserToPreferences();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    private void saveUserToPreferences() {
        Log.d(TAG, "Saving: " + mUser.getUsername() + ", " + mUser.getEmail() + ", " + mUser.getAccessToken());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.saved_username), mUser.getUsername());
        editor.putString(context.getString(R.string.saved_email), mUser.getEmail());
        editor.putString(context.getString(R.string.saved_access_token), mUser.getAccessToken());
        editor.commit();
    }

    public void deleteSavedUser(final MainActivity.CallbackInterface cb) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(context.getString(R.string.saved_username));
        editor.remove(context.getString(R.string.saved_email));
        editor.remove(context.getString(R.string.saved_access_token));
        editor.commit();
        mUser = new User();
        loggedIn = false;
        hasAccessToken = false;
        cb.loginStatusUpdate(loggedIn);
    }

}
