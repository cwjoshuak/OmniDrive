package com.example.omnidrive;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OneDriveFileList {
    @SerializedName("@odata.count")
    private int count;

    @SerializedName("value")
    public List<OneDriveFile> fileList;

    @Override
    public String toString() {
        return "OneDriveFileList{" +
                "count=" + count +
                ", fileList=" + fileList +
                '}';
    }
}


