package net.ichigotake.pyazing;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

class UploadParameter implements Parcelable {

    private final UploadMode uploadMode;
    private final Uri content;

    public UploadParameter(UploadMode uploadMode, Uri content) {
        this.uploadMode = uploadMode;
        this.content = content;
    }

    UploadMode getUploadMode() {
        return uploadMode;
    }

    Uri getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uploadMode == null ? -1 : this.uploadMode.ordinal());
        dest.writeParcelable(this.content, 0);
    }

    private UploadParameter(Parcel in) {
        int tmpUploadMode = in.readInt();
        this.uploadMode = tmpUploadMode == -1 ? null : UploadMode.values()[tmpUploadMode];
        this.content = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<UploadParameter> CREATOR = new Creator<UploadParameter>() {
        public UploadParameter createFromParcel(Parcel source) {
            return new UploadParameter(source);
        }

        public UploadParameter[] newArray(int size) {
            return new UploadParameter[size];
        }
    };
}
