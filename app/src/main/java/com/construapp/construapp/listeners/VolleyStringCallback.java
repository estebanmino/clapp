package com.construapp.construapp.listeners;

import com.android.volley.VolleyError;

/**
 * Created by ESTEBANFML on 21-10-2017.
 */

public interface VolleyStringCallback {
    void onSuccess(String result);
    void onErrorResponse(VolleyError result);
}
