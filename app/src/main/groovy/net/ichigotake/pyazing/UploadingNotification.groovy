package net.ichigotake.pyazing;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.dmitriy.tarasov.android.intents.IntentUtils;

final class UploadingNotification {

    private final String NOTIFICATION_TAG_UPLOAD_COMPLETE = "upload_complete";
    private final String NOTIFICATION_PROGRESS = "progress";
    private final Context context;

    UploadingNotification(Context context) {
        this.context = context;
    }

    void startProgress() {
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_upload_progress))
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(100, 0, true)
                .setOngoing(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_PROGRESS, R.string.app_name, notification);
    }

    void stopProgress() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_PROGRESS, R.string.app_name);
    }

    void notifyToSuccess(String url) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_upload_complete))
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false);
        // - クリップボードにコピーする導線は最優先で取っておきたい
        // - Kitkat以降ではOS標準で「共有」から「クリップボードにコピー」出来るので、自前でコピー機能を用意する必要無し
        // - 人と共有する前にアップロードしたものがプレビュー出来たら安心感があるのでプレビューも簡単にしたい
        // - TODO: ゆくゆくは largeIcon や RemoteViews を利用したプレビューに置き換えたい
        // - 今は実験的機能として Kitkat 以降の端末でのみブラウザを開けるようにする
        // - 通知上にボタンが3つ以上並ぶと窮屈感あるので、2つまでに厳選したい
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            builder.addAction(R.drawable.ic_action_web_site,
                    context.getString(R.string.app_open_browser),
                    createOpenBrowserIntent(url));
        } else {
            builder.addAction(R.drawable.ic_action_copy,
                    context.getString(R.string.app_copy_to_clipboard),
                    createCopyToClipboardIntent(url));
        }
        builder.addAction(R.drawable.ic_action_share,
                context.getString(R.string.app_share),
                createShareIntent(url));
        NotificationManager notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        notificationManager.notify(NOTIFICATION_TAG_UPLOAD_COMPLETE, R.string.app_name, builder.build());
        Toasts.completeUploading(context);
    }

    PendingIntent createOpenBrowserIntent(String url) {
        Intent openBrowserIntent = IntentUtils.openLink(url);
        return PendingIntent.getActivity(context, 0, openBrowserIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    PendingIntent createShareIntent(String url) {
        Intent shareIntent = IntentUtils.shareText("", url);
        return PendingIntent.getActivity(context, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    PendingIntent createCopyToClipboardIntent(String url) {
        Intent clipboardIntent = CopyToClipboardService.createIntent(
                context, url, context.getString(R.string.app_copy_to_clipboard));
        return PendingIntent.getService(context, 0, clipboardIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    void notifyToFailure(UploadMedia media) {
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_upload_failure))
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_launcher,
                        context.getString(R.string.app_upload_retry), createRetryUploadIntent(media))
                .build();
        NotificationManager notificationManager = (NotificationManager) as NotificationManager
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, notification);
    }

    PendingIntent createRetryUploadIntent(UploadMedia media) {
        Intent intent = UploadMediaService.createIntent(context, media.getData(), media.getMimeType());
        return PendingIntent.getService(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
