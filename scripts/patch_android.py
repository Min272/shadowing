"""Chạy sau `npx cap add android`: chép widget vào project và khai báo trong manifest."""
import os, re, shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
AND = os.path.join(ROOT, "android")
OV = os.path.join(ROOT, "android-overlay")
PKG_DIR = os.path.join(AND, "app/src/main/java/com/listenshadow/app")
RES = os.path.join(AND, "app/src/main/res")

os.makedirs(PKG_DIR, exist_ok=True)
for f in os.listdir(os.path.join(OV, "java")):
    shutil.copy(os.path.join(OV, "java", f), os.path.join(PKG_DIR, f))

for sub in ("xml", "layout", "values"):
    os.makedirs(os.path.join(RES, sub), exist_ok=True)
    for f in os.listdir(os.path.join(OV, "res", sub)):
        shutil.copy(os.path.join(OV, "res", sub, f), os.path.join(RES, sub, f))

mf = os.path.join(AND, "app/src/main/AndroidManifest.xml")
s = open(mf, encoding="utf-8").read()
if "ProgressWidget" not in s:
    receiver = '''
        <receiver android:name=".ProgressWidget" android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data android:name="android.appwidget.provider"
                android:resource="@xml/progress_widget_info" />
        </receiver>
'''
    s = s.replace("</application>", receiver + "    </application>")
    open(mf, "w", encoding="utf-8").write(s)
print("Patched android project OK")
