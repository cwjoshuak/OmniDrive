package com.example.omnidrive;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class OneDriveFile {
    public String createdDateTime;
    public String name;

    public ParentReference parentReference;

    public long size;
    public Folder folder;
    public File file;

    public boolean isBackDirectory = false;

    public OneDriveFile parentFile;
    @Override
    public String toString() {
        return "OneDriveFile{" +
                "createdDateTime='" + createdDateTime + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", folder=" + folder +
                ", file=" + file +
                "}\n";
    }
    public String getFullPath(){
        return parentReference.path +"/"+ this.name;
    }
    public boolean isFile() {
        return file != null;
    }
    public class ParentReference {
        public String path;
    }
    class Folder {
    }
    class File {
        String mimeType;
    }
}