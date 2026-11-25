package com.listenshadow.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.widget.RemoteViews;

import org.json.JSONObject;

public class ProgressWidget extends AppWidgetProvider {
    public static final String PREFS = "widget_prefs";

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) render(ctx, mgr, id);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context ctx, AppWidgetManager mgr, int id, android.os.Bundle opts) {
        render(ctx, mgr, id);
    }

    public static void refreshAll(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, ProgressWidget.class));
        for (int id : ids) render(ctx, mgr, id);
    }

    private static void render(Context ctx, AppWidgetManager mgr, int widgetId) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int day = 1, pct = 0, done = 0, total = 0, lessonPct = 0;
        boolean rest = false, all = false;
        String lesson = "";
        try {
            String s = sp.getString("data", null);
            if (s != null) {
                JSONObject o = new JSONObject(s);
                day = o.optInt("day", 1);
                pct = o.optInt("dayPct", 0);
                done = o.optInt("done", 0);
                total = o.optInt("total", 0);
                lessonPct = o.optInt("lessonPct", 0);
                rest = o.optBoolean("rest", false);
                all = o.optBoolean("allDone", false);
                lesson = o.optString("lesson", "");
            }
        } catch (Exception ignored) { }

        boolean dark = (ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;

        int size = 400;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        float cx = size / 2f, cy = size / 2f;
        float stroke = 34f;
        float r = size / 2f - stroke / 2f - 8f;

        int bg = dark ? 0xFF1B2030 : 0xFFFFFFFF;
        int track = dark ? 0xFF2C3348 : 0xFFE4DFD2;
        int accent = dark ? 0xFFF2B61F : 0xFFE7A400;
        int ok = dark ? 0xFF4CC58A : 0xFF1B8F58;
        int ink = dark ? 0xFFDFE6FA : 0xFF1D2A44;
        int mu = dark ? 0xFF9AA1B3 : 0xFF6A7080;

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setColor(bg);
        c.drawCircle(cx, cy, size / 2f - 2f, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(stroke);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setColor(track);
        RectF rect = new RectF(cx - r, cy - r, cx + r, cy + r);
        c.drawCircle(cx, cy, r, p);

        int prog = rest ? (all ? 100 : 0) : pct;
        if (prog > 0) {
            p.setColor(prog >= 100 ? ok : accent);
            c.drawArc(rect, -90f, 360f * prog / 100f, false, p);
        }

        Paint t = new Paint(Paint.ANTI_ALIAS_FLAG);
        t.setTextAlign(Paint.Align.CENTER);

        if (rest) {
            t.setColor(ink);
            t.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            t.setTextSize(64f);
            c.drawText("Ngày " + day, cx, cy - 6f, t);
            t.setColor(mu);
            t.setTypeface(Typeface.DEFAULT);
            t.setTextSize(44f);
            c.drawText(all ? "Hoàn thành" : "Nghỉ", cx, cy + 56f, t);
        } else {
            t.setColor(mu);
            t.setTypeface(Typeface.DEFAULT);
            t.setTextSize(40f);
            c.drawText("Ngày", cx, cy - 56f, t);
            t.setColor(ink);
            t.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            t.setTextSize(124f);
            c.drawText(String.valueOf(day), cx, cy + 56f, t);
            t.setColor(mu);
            t.setTypeface(Typeface.DEFAULT);
            t.setTextSize(38f);
            String sub = total > 0 ? (done + "/" + total + " bước") : (pct + "%");
            c.drawText(sub, cx, cy + 112f, t);
        }

        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_progress);
        rv.setImageViewBitmap(R.id.w_img, bmp);
        rv.setContentDescription(R.id.w_img, "Ngày " + day + ", tiến trình " + prog + "%");
        rv.setTextViewText(R.id.w_lesson, lesson.isEmpty() ? "Listen & Shadow" : lesson + " · " + lessonPct + "%");
        rv.setTextColor(R.id.w_lesson, mu);

        Intent open = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        if (open != null) {
            PendingIntent pi = PendingIntent.getActivity(ctx, 0, open,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            rv.setOnClickPendingIntent(R.id.w_root, pi);
        }
        mgr.updateAppWidget(widgetId, rv);
    }
}
