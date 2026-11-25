package com.listenshadow.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

public class WidgetBridge {
    private final Context ctx;

    public WidgetBridge(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    @JavascriptInterface
    public void update(String json) {
        SharedPreferences sp = ctx.getSharedPreferences(ProgressWidget.PREFS, Context.MODE_PRIVATE);
        sp.edit().putString("data", json).apply();
        ProgressWidget.refreshAll(ctx);
    }
}
