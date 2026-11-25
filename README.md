# Listen & Shadow – APK + widget

1. Upload toàn bộ nội dung thư mục này lên repo GitHub (nhánh `main`).
2. Vào tab **Actions** → workflow **Build APK** tự chạy (hoặc bấm *Run workflow*).
3. Chạy xong, mở lần chạy đó → mục **Artifacts** → tải `ListenShadow-debug-apk` → giải nén ra `app-debug.apk` → cài lên điện thoại.
4. Thêm widget: giữ lâu màn hình chính → Widget → **Listen & Shadow**.

Widget tròn hiện "Ngày N" + vòng tiến trình của ngày đang học, cập nhật mỗi khi bạn học trong app.
Audio KHÔNG cần đưa vào repo: app tự nạp audio trong máy qua nút "+ Nạp audio".

## Nhúng sẵn audio vào app
Bỏ file mp3 vào `www/audio/` theo tên `L1_1.mp3`, `L1_3.mp3`, `L1_5.mp3`... (xem `www/audio/README.txt`), rồi push lên GitHub. Workflow tự nhúng vào APK.
