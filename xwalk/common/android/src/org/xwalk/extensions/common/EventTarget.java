// Copyright (c) 2015 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.extensions.common;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventTarget extends BindingObject {
    private String TAG = "EventTarget";
    private Map<String, FunctionInfo> mEvents;

    public EventTarget() {
        mEvents = new HashMap<String, FunctionInfo>();
        mHandler.register("addEventListener", this);
        mHandler.register("removeEventListener", this);
    }

    public void startEvent(String type) {}

    public void stopEvent(String type) {}

    public boolean isEventActive(String type) {
        return mEvents.containsKey(type);
    }

    public void dispatchEvent(String type) {
        dispatchEvent(type, null);
    }

    public void dispatchEvent(String type, JSONObject data) {
        try {
            if (!mEvents.containsKey(type)) {
                Log.w(TAG, "Attempt to dispatch to non-existing event :\n" + type);
                return;
            }

            FunctionInfo info = mEvents.get(type);
            JSONArray args = new JSONArray();
            if (data != null)
                args.put(0, data);
            info.postResult(args);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void onAddEventListener(FunctionInfo info) {
        try {
            String type = info.getArgs().getString(0);
            if (mEvents.containsKey(type)) {
                Log.w(TAG, "Trying to re-add the event :\n" + type);
                return;
            }

            mEvents.put(type, info);
            startEvent(type);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void onRemoveEventListener(FunctionInfo info) {
        try {
            String type = info.getArgs().getString(0);
            if (!mEvents.containsKey(type)) {
                Log.w(TAG, "Attempt to remove non-existing event :\n" + type);
                return;
            }

            stopEvent(type);
            mEvents.remove(type);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }
}
