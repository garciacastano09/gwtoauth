package jgc.asai.gwtoauth.shared;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import java.util.ArrayList;

public class GooglePlusIdentity {
    private String kind;
    private String etag;
    private String objectType;
    private String id;
    private String displayName;
    private String familyName;
    private String givenName;
    private String url;
    private String imageUrl;
    private String isPlusUser;
    private String language;
    private String circledByCount;
    private String verified;

    public GooglePlusIdentity(String kind, String etag, String objectType, String id, String displayName, String familyName, String givenName, String url, String imageUrl, String isPlusUser, String language, String circledByCount, String verified) {
        this.kind = kind;
        this.etag = etag;
        this.objectType = objectType;
        this.id = id;
        this.displayName = displayName;
        this.familyName = familyName;
        this.givenName = givenName;
        this.url = url;
        this.imageUrl = imageUrl;
        this.isPlusUser = isPlusUser;
        this.language = language;
        this.circledByCount = circledByCount;
        this.verified = verified;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIsPlusUser() {
        return isPlusUser;
    }

    public void setIsPlusUser(String isPlusUser) {
        this.isPlusUser = isPlusUser;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCircledByCount() {
        return circledByCount;
    }

    public void setCircledByCount(String circledByCount) {
        this.circledByCount = circledByCount;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public static GooglePlusIdentity getProfileFromJSONString(JSONObject j){
        return new GooglePlusIdentity(
            String.valueOf(j.get("kind")),
            String.valueOf(j.get("etag")),
            String.valueOf(j.get("objectType")),
            String.valueOf(j.get("id")),
            String.valueOf(j.get("displayName")),
            String.valueOf(j.get("name").isObject().get("familyName")),
            String.valueOf(j.get("name").isObject().get("givenName")),
            String.valueOf(j.get("url")),
            String.valueOf(j.get("image").isObject().get("url")),
            String.valueOf(j.get("isPlusUser")),
            String.valueOf(j.get("language")),
            String.valueOf(j.get("circledByCount")),
            String.valueOf(j.get("verified"))
        );
    }

}
