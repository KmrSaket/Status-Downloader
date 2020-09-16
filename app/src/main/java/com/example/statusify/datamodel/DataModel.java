package com.example.statusify.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class DataModel implements Parcelable {
    public static final Creator<DataModel> CREATOR = new Creator<DataModel>() {
        public DataModel createFromParcel(Parcel parcel) {
            return new DataModel(parcel);
        }

        public DataModel[] newArray(int i) {
            return new DataModel[i];
        }
    };
    private String downPath,favPath;
    private String filename;
    private String filepath;
    private Boolean isDownloaded;
    private Boolean isFavourite;
    private Boolean isVideo;

    public int describeContents() {
        return 0;
    }

    public DataModel(String str, String str2) {
        this.filepath = str;
        this.filename = str2;
    }
    public DataModel(String str, String str2, Boolean isDownloaded, Boolean isFavourite, Boolean isVideo) {
        this.filepath = str;
        this.filename = str2;
        this.isDownloaded = isDownloaded;
        this.isFavourite = isFavourite;
        this.isVideo = isVideo;
    }

    public DataModel(String str, String str2, Boolean isDownloaded, Boolean isFavourite, Boolean isVideo, String downPath, String favPath) {
        this.filepath = str;
        this.filename = str2;
        this.isDownloaded = isDownloaded;
        this.isFavourite = isFavourite;
        this.isVideo = isVideo;
        this.downPath = downPath;
        this.favPath = favPath;
    }

    protected DataModel(Parcel parcel) {
        this.filename = parcel.readString();
        this.filepath = parcel.readString();
    }

//    public String getFileHeight(){ return }

    public String getFileName() {
        return this.filename;
    }

    public String getFilePath() {
        return this.filepath;
    }

    public Boolean getIsDownloaded() {
        return this.isDownloaded;
    }

    public Boolean getIsVideo() {
        return this.isVideo;
    }

    public Boolean getIsFavourite() {
        return this.isFavourite;
    }

    public String getDownPath(){ return this.downPath; }

    public String getFavPath(){ return this.favPath; }

    public void setDownloaded(Boolean downloaded) {
        this.isDownloaded = downloaded;
    }

    public void setFavourite(Boolean favourite) {
        this.isFavourite = favourite;
    }

    public void setFavPath(String favPath){
        this.favPath = favPath;
    }

    public void setDownPath(String downPath){
        this.downPath = downPath;
    }

    public void setFileName(String str) {
        this.filename = str;
    }

    public void setFilePath(String str) {
        this.filepath = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.filename);
        parcel.writeString(this.filepath);
    }
}