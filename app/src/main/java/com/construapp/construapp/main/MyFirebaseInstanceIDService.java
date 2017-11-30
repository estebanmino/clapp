package com.construapp.construapp.main;

/**
 * Created by jofre on 19-11-17.
 */

//package com.google.firebase.quickstart.fcm;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.construapp.construapp.api.VolleyGetThreads;
import com.construapp.construapp.api.VolleyPostDeviceToken;
import com.construapp.construapp.lessons.LessonFormActivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.microblog.NewThreadActivity;
import com.construapp.construapp.models.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private SessionManager sessionManager;

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sessionManager = new SessionManager(MyFirebaseInstanceIDService.this);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sessionManager.setFCMToken(refreshedToken);
        //sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        VolleyPostDeviceToken.volleyPostDeviceToken(new VolleyStringCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(MyFirebaseInstanceIDService.this, "Enviado token a api", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onErrorResponse(VolleyError result) {
            }
        },MyFirebaseInstanceIDService.this, token);
    }
}