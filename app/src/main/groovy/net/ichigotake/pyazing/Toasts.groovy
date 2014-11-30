package net.ichigotake.pyazing;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

final class Toasts {

    private Toasts() {}

    static def ignoreFile(Context context) {
        show(context, context.getString(R.string.app_upload_ignore_file));
    }

    static def startUploading(Context context) {
        show(context, context.getString(R.string.app_upload_progress_start));
    }

    static def completeUploading(Context context) {
        show(context, context.getString(R.string.app_upload_complete));
    }

    static def completeCopingText(Context context, String text) {
        show(context, context.getString(R.string.app_copy_to_clipboard_complete, text));
    }

    private static def show(final Context context, final String message) {
        new Handler(Looper.getMainLooper()).post() {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
