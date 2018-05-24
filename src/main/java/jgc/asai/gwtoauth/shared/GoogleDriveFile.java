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
        for (int i = 0; i < files.isArray().size(); i++) {
            JSONValue file = files.isArray().get(i);
            r.add(new GoogleDriveFile(
                file.isObject().get("kind").toString(),
                file.isObject().get("id").toString(),
                file.isObject().get("name").toString(),
                file.isObject().get("mimeType").toString()));
        }
        return r;
    }
}
