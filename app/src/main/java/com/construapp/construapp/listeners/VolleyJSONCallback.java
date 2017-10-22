package com.construapp.construapp.listeners;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by ESTEBANFML on 21-10-2017.
 */

public interface VolleyJSONCallback {
    void onSuccess(JSONObject result);
    void onErrorResponse(VolleyError result);
}
