package jgc.asai.gwtoauth.shared;

import com.google.gwt.json.client.JSONObject;

public class LinkedInProfile {
    private String emailAddress;
    private String firstName;
    private String industry;
    private String lastName;
    private String pictureUrl;
    private String currentPositionCompany;

    public LinkedInProfile(String emailAddress, String firstName, String industry, String lastName, String pictureUrl, String currentPositionCompany) {
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.industry = industry;
        this.lastName = lastName;
        this.pictureUrl = pictureUrl;
        this.currentPositionCompany = currentPositionCompany;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        }

    public String getCurrentPositionCompany() {
        return currentPositionCompany;
    }

    public void setCurrentPositionCompany(String currentPositionCompany) {
        this.currentPositionCompany = currentPositionCompany;
    }

    public static LinkedInProfile getProfileFromJSONString(JSONObject j){
        return new LinkedInProfile(
            String.valueOf(j.get("emailAddress")),
            String.valueOf(j.get("firstName")),
            String.valueOf(j.get("industry")),
            String.valueOf(j.get("lastName")),
            String.valueOf(j.get("pictureUrl")),
            String.valueOf(j.get("positions").isObject().get("values").isArray().get(0).isObject().get("company").isObject().get("name"))
        );

    }
}