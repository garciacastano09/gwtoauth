package jgc.asai.gwtoauth.shared;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import java.util.ArrayList;
import java.util.List;

public class GoogleDriveFile {
    private String kind;
    private String id;
    private String name;
    private String mimeType;

    public GoogleDriveFile(String kind, String id, String name, String mimeType){
        this.kind = kind;
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
    }

    public GoogleDriveFile(){}

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static ArrayList<GoogleDriveFile> getFileListFromJSONString(JSONObject j){
        ArrayList<GoogleDriveFile> r = new ArrayList<>();
        JSONValue files = j.get("files");
        if (files != null && files.isArray().size() > 0){
            for (int i = 0; i < files.isArray().size(); i++) {
                JSONValue file = files.isArray().get(i);
                GoogleDriveFile item = new GoogleDriveFile();
                if (file != null && file.isObject() != null){
                    if (file.isObject().get("kind") != null)
                        item.setKind(String.valueOf(file.isObject().get("kind")));
                    if (file.isObject().get("id") != null)
                        item.setId(String.valueOf(file.isObject().get("id")));
                    if (file.isObject().get("name") != null)
                        item.setName(String.valueOf(file.isObject().get("name")));
                    if (file.isObject().get("mimeType") != null)
                        item.setMimeType(String.valueOf(file.isObject().get("mimeType")));
                }
                r.add(item);
            }
        }
        return r;
    }
}
