package net.ichigotake.pyazing;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

final class Toasts {

    private Toasts() {}

    static void ignoreFile(Context context) {
        show(context, context.getString(R.string.app_upload_ignore_file));
    }

    static void startUploading(Context context) {
        show(context, context.getString(R.string.app_upload_progress_start));
    }

    static void completeUploading(Context context) {
        show(context, context.getString(R.string.app_upload_complete));
    }

    static void completeCopingText(Context context, String text) {
        show(context, context.getString(R.string.app_copy_to_clipboard_complete, text));
    }

    private static void show(final Context context, final String message) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
