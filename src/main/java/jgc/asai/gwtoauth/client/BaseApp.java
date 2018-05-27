package jgc.asai.gwtoauth.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import jgc.asai.gwtoauth.shared.*;
import com.google.gwt.core.client.JsonUtils;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SingleSelectionModel;

import static jgc.asai.gwtoauth.client.Utils.LINKEDIN;
import static jgc.asai.gwtoauth.client.Utils.GOOGLE;
import static jgc.asai.gwtoauth.shared.UrlResources.CALLBACK_URL;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BaseApp implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private final Logger logger = java.util.logging.Logger.getLogger("BaseApp");

  private final GoogleAuthServiceAsync googleAuthService = GWT.create(GoogleAuthService.class);
  private final LinkedInAuthServiceAsync linkedInAuthServer = GWT.create(LinkedInAuthService.class);

  private final ListBox googleApiList = new ListBox();
  private final ListBox linkedInApiList = new ListBox();
  private final Button goBackButton = new Button("Go Back");
  private final Button googleButton = new Button("Login Google");
  private final Button toggleResponseRawNice = new Button();
  private final Button getGoogleResourceButton = new Button("Get Google Resource");
  private final Button getLinkedInResourceButton = new Button("Get LinkedIn Resource");
  private final Button linkedInButton = new Button("Login LinkedIn");
  private final Label errorLabel = new Label();
  private final VerticalPanel rawResponsePanel = new VerticalPanel();
  private final VerticalPanel niceResponsePanel = new VerticalPanel();

  private final FlexTable googleDriveFlexTable = new FlexTable();
  private final FlexTable googlePlusFlexTable = new FlexTable();
  private final FlexTable linkedInFlexTable = new FlexTable();

  private final CellTable<GoogleDriveFile> cellTableOfGoogleDriveFile = new CellTable<>();
  private final CellTable<GooglePlusIdentity> cellTableOfGooglePlusProfile = new CellTable<>();
  private final CellTable<LinkedInProfile> cellTableOfLinkedIn = new CellTable<>();

  private JSONObject response;
  private Boolean niceOutput = false;

  private static BaseApp singleton;

  public static BaseApp get(){
    return singleton;
  }

  public void onModuleLoad() {
    singleton = this;

    rawResponsePanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
    rawResponsePanel.setVisible(false);

    niceResponsePanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
    niceResponsePanel.setVisible(false);

    goBackButton.addClickHandler(new GoBackHandler());
    googleButton.setVisible(false);

    googleButton.addClickHandler(new GoogleHandler());
    googleButton.setVisible(true);

    linkedInButton.addClickHandler(new LinkedInHandler());
    linkedInButton.setVisible(true);

    getGoogleResourceButton.addClickHandler(new GoogleGetResourceHandler());
    getGoogleResourceButton.setVisible(false);

    toggleResponseRawNice.addClickHandler(new ToggleResponseRawNiceHandler());
    toggleResponseRawNice.setVisible(false);

    getLinkedInResourceButton.addClickHandler(new LinkedInGetResourceHandler());
    getLinkedInResourceButton.setVisible(false);

    googleApiList.addItem("Google Plus");
    googleApiList.addItem("Google Drive");
    googleApiList.setVisible(false);

    linkedInApiList.addItem("LinkedIn");
    linkedInApiList.setVisible(false);

    createGoogleDriveFilesTable();
    createGooglePlusProfileTable();
    createLinkedInTable();

    RootPanel.get("toggleResponseRawNice").add(toggleResponseRawNice);
    RootPanel.get("rawResponsePanel").add(rawResponsePanel);
    RootPanel.get("googleApiList").add(googleApiList);
    RootPanel.get("googleButtonContainer").add(googleButton);
    RootPanel.get("getGoogleResourceButton").add(getGoogleResourceButton);
    RootPanel.get("linkedInButtonContainer").add(linkedInButton);
    RootPanel.get("getLinkedInResourceButton").add(getLinkedInResourceButton);
    RootPanel.get("errorLabelContainer").add(errorLabel);
    RootPanel.get("niceResponsePanel").add(niceResponsePanel);
    RootPanel.get("goBack").add(goBackButton);

    handleRedirect();
  }

  private void handleRedirect(){
    logger.info("handleRedirect");
    if (Utils.redirected()){
      if (!Utils.alreadyLoggedIn()){
        String authProvider = Utils.getAuthProviderNameFromCookie();
        logger.info("handleRedirect authProvider: "+authProvider);
        if(authProvider.equals(GOOGLE)){
          String code = Window.Location.getParameter("code");
          String state = Window.Location.getParameter("state");
          logger.info("code="+code);
          logger.info("state="+state);
          try {
            googleAuthService.googleAccessToken(code, state, new AsyncCallback<String>() {
              @Override
              public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
              }

              @Override
              public void onSuccess(String s) {
                logger.info("Access Token="+s);
                googleButton.setVisible(false);
                linkedInButton.setVisible(false);
                getGoogleResourceButton.setVisible(true);
                googleApiList.setVisible(true);
                linkedInApiList.setVisible(false);
                goBackButton.setVisible(true);
              }
            });
          } catch (Exception e) {e.printStackTrace();}
        }
        if(authProvider.equals(LINKEDIN)){
          String code = Window.Location.getParameter("code");
          String state = Window.Location.getParameter("state");
          logger.info("code="+code);
          logger.info("state="+state);
          try {
            linkedInAuthServer.linkedInAccessToken(code, state, new AsyncCallback<String>() {
              @Override
              public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
              }

              @Override
              public void onSuccess(String s) {
                logger.info("Access Token="+s);
                googleButton.setVisible(false);
                linkedInButton.setVisible(false);
                getLinkedInResourceButton.setVisible(true);
                linkedInApiList.setVisible(true);
                goBackButton.setVisible(true);
              }
            });
          } catch (Exception e) {e.printStackTrace();}
        }
      } else {logger.info("Redirected, no logged in..");}
    } else {logger.info("No redirection.."); goBackButton.setVisible(false);}
  }

  public Boolean getNiceOutput() {
    return niceOutput;
  }

  public void setNiceOutput(Boolean niceOutput) {
    this.niceOutput = niceOutput;
    niceResponsePanel.setVisible(niceOutput);
    rawResponsePanel.setVisible(!niceOutput);
    toggleResponseRawNice.setVisible(true);
    if (niceOutput) toggleResponseRawNice.setHTML("See Raw");
    else toggleResponseRawNice.setHTML("See Nice");
  }

  public JSONObject getResponse() {
    return response;
  }

  public void setResponse(JSONObject response) {
    this.response = response;
  }

  public void updateResponseUI(String r, String apiName){
    logger.info("updateResponseUI: "+apiName);
    this.setResponse(new JSONObject(JsonUtils.safeEval(r)));
    rawResponsePanel.clear();
    rawResponsePanel.add(new HTML(JsonUtils.escapeValue(r)));
    this.setNiceOutput(true);
    switch (apiName){
      case Utils.GOOGLE_DRIVE:
        populateGoogleDriveTable(GoogleDriveFile.getFileListFromJSONString(this.getResponse()));
        break;
      case Utils.GOOGLE_PLUS:
        populateGooglePlusTable(GooglePlusIdentity.getProfileFromJSONString(this.getResponse()));
        break;
      case Utils.LINKEDIN:
        populateLinkedInTable(LinkedInProfile.getProfileFromJSONString(this.getResponse()));
        break;
    }
  }

  class GoogleHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
      BaseApp.get().getAuthorizationUrl(GOOGLE);
    }
  }

  class LinkedInHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
      BaseApp.get().getAuthorizationUrl(LINKEDIN);
    }
  }

  class GoBackHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
      Utils.redirect(CALLBACK_URL);
    }
  }

  class ToggleResponseRawNiceHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
//    Toggle from nice to raw
      if(BaseApp.get().getNiceOutput()){
        BaseApp.get().setNiceOutput(false);
      }
//    Toggle from raw to nice
      else {
        BaseApp.get().setNiceOutput(true);
      }
    }
  }

  class GoogleGetResourceHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
      try {
        String apiName = Utils.getApiName(googleApiList.getSelectedItemText());
        googleAuthService.googleGetResource(apiName, new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable throwable) {
            throwable.printStackTrace();
          }
          @Override
          public void onSuccess(String s) {
            logger.info("resource="+s);
            updateResponseUI(s, apiName);
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  class LinkedInGetResourceHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
      try {
        String apiName = Utils.getApiName(linkedInApiList.getSelectedItemText());
        linkedInAuthServer.linkedInGetResource(apiName, new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable throwable) {
            throwable.printStackTrace();
          }
          @Override
          public void onSuccess(String s) {
            logger.info("resource="+s);
            updateResponseUI(s, apiName);
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void getAuthorizationUrl(final String authProvider){
    logger.info("Getting authorization url");

    final Credential credential = new Credential();
    credential.setRedirectUrl(CALLBACK_URL);
    credential.setAuthProvider(authProvider);

    if(authProvider.equals(GOOGLE)){
      try {
        googleAuthService.getGoogleAuthorizationUrl(credential, new AsyncCallback<String>(){
          @Override
          public void onSuccess(String authorizationUrl){
            logger.info("Authorization url: " + authorizationUrl);
            Utils.clearCookies();
            Utils.saveAuthProvider(GOOGLE);
            Utils.saveRediretUrl(CALLBACK_URL);
            Utils.redirect(authorizationUrl);
          }
          @Override
          public void onFailure(Throwable caught){
            caught.printStackTrace();
          }
        });
      } catch (JGCException e) {
        e.printStackTrace();
      }
    }
    if(authProvider.equals(LINKEDIN)){
      try {
        linkedInAuthServer.getLinkedInAuthorizationUrl(credential, new AsyncCallback<String>(){
          @Override
          public void onSuccess(String authorizationUrl){
            logger.info("Authorization url: " + authorizationUrl);
            Utils.clearCookies();
            Utils.saveAuthProvider(LINKEDIN);
            Utils.saveRediretUrl(CALLBACK_URL);
            Utils.redirect(authorizationUrl);
          }
          @Override
          public void onFailure(Throwable caught){
            caught.printStackTrace();
          }
        });
      } catch (JGCException e) {
        e.printStackTrace();
      }
    }
  }

  private void createGoogleDriveFilesTable(){
    // The policy that determines how keyboard selection will work. Keyboard
    // selection is enabled.
    cellTableOfGoogleDriveFile.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

    // Add a text columns to show the details.
    TextColumn<GoogleDriveFile> columnFirstLine = new TextColumn<GoogleDriveFile>() {
      @Override
      public String getValue(GoogleDriveFile object) {
        return object.getId();
      }
    };
    cellTableOfGoogleDriveFile.addColumn(columnFirstLine, "ID");

    TextColumn<GoogleDriveFile> columnSecondLine = new TextColumn<GoogleDriveFile>() {
      @Override
      public String getValue(GoogleDriveFile object) {
        return object.getName();
      }
    };
    cellTableOfGoogleDriveFile.addColumn(columnSecondLine, "Name");

    TextColumn<GoogleDriveFile> townColumn = new TextColumn<GoogleDriveFile>() {
      @Override
      public String getValue(GoogleDriveFile object) {
        return object.getKind();
      }
    };
    cellTableOfGoogleDriveFile.addColumn(townColumn, "Kind");

    TextColumn<GoogleDriveFile> countryColumn = new TextColumn<GoogleDriveFile>() {
      @Override
      public String getValue(GoogleDriveFile object) {
        return object.getMimeType();
      }
    };
    cellTableOfGoogleDriveFile.addColumn(countryColumn, "MimeType");

    final SingleSelectionModel<GoogleDriveFile> selectionModel = new SingleSelectionModel<>();
    cellTableOfGoogleDriveFile.setSelectionModel(selectionModel);
  }

  private void populateGoogleDriveTable(ArrayList<GoogleDriveFile> files){
    cellTableOfGoogleDriveFile.setRowCount(files.size(), true);
    cellTableOfGoogleDriveFile.setRowData(0, files);
    niceResponsePanel.clear();
    niceResponsePanel.add(googleDriveFlexTable);
    niceResponsePanel.add(cellTableOfGoogleDriveFile);
  }

  private void createGooglePlusProfileTable(){
    // The policy that determines how keyboard selection will work. Keyboard
    // selection is enabled.
    cellTableOfGooglePlusProfile.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

    // Add a text columns to show the details.
    TextColumn<GooglePlusIdentity> columnFirstLine = new TextColumn<GooglePlusIdentity>() {
      @Override
      public String getValue(GooglePlusIdentity object) {
        return object.getId();
      }
    };
    cellTableOfGooglePlusProfile.addColumn(columnFirstLine, "ID");

    TextColumn<GooglePlusIdentity> columnSecondLine = new TextColumn<GooglePlusIdentity>() {
      @Override
      public String getValue(GooglePlusIdentity object) {
        return object.getGivenName();
      }
    };
    cellTableOfGooglePlusProfile.addColumn(columnSecondLine, "Given Name");

    TextColumn<GooglePlusIdentity> townColumn = new TextColumn<GooglePlusIdentity>() {
      @Override
      public String getValue(GooglePlusIdentity object) {
        return object.getFamilyName();
      }
    };
    cellTableOfGooglePlusProfile.addColumn(townColumn, "Family Name");

    TextColumn<GooglePlusIdentity> countryColumn = new TextColumn<GooglePlusIdentity>() {
      @Override
      public String getValue(GooglePlusIdentity object) {
        return object.getUrl();
      }
    };
    cellTableOfGooglePlusProfile.addColumn(countryColumn, "URL");

    TextColumn<GooglePlusIdentity> imageURL = new TextColumn<GooglePlusIdentity>() {
      @Override
      public String getValue(GooglePlusIdentity object) {
        return object.getImageUrl();
      }
    };
    cellTableOfGooglePlusProfile.addColumn(imageURL, "ImageURL");

    TextColumn<GooglePlusIdentity> kind = new TextColumn<GooglePlusIdentity>() {
      @Override
      public String getValue(GooglePlusIdentity object) {
        return object.getImageUrl();
      }
    };
    cellTableOfGooglePlusProfile.addColumn(kind, "Kind");

    final SingleSelectionModel<GooglePlusIdentity> selectionModel = new SingleSelectionModel<>();
    cellTableOfGooglePlusProfile.setSelectionModel(selectionModel);
  }

  private void populateGooglePlusTable(GooglePlusIdentity profile){
    ArrayList<GooglePlusIdentity> p = new ArrayList<>();
    p.add(profile);
    cellTableOfGooglePlusProfile.setRowCount(1, true);
    cellTableOfGooglePlusProfile.setRowData(0, p);
    niceResponsePanel.clear();
    niceResponsePanel.add(googlePlusFlexTable);
    niceResponsePanel.add(cellTableOfGooglePlusProfile);
  }

  private void createLinkedInTable(){
    // The policy that determines how keyboard selection will work. Keyboard
    // selection is enabled.
    cellTableOfLinkedIn.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

    // Add a text columns to show the details.
    TextColumn<LinkedInProfile> firstName = new TextColumn<LinkedInProfile>() {
      @Override
      public String getValue(LinkedInProfile object) {
        return object.getFirstName();
      }
    };
    cellTableOfLinkedIn.addColumn(firstName, "First Name");

    TextColumn<LinkedInProfile> lastName = new TextColumn<LinkedInProfile>() {
      @Override
      public String getValue(LinkedInProfile object) {
        return object.getLastName();
      }
    };
    cellTableOfLinkedIn.addColumn(lastName, "Last Name");

    TextColumn<LinkedInProfile> emailAddress = new TextColumn<LinkedInProfile>() {
      @Override
      public String getValue(LinkedInProfile object) {
        return object.getEmailAddress();
      }
    };
    cellTableOfLinkedIn.addColumn(emailAddress, "Email Address");

    TextColumn<LinkedInProfile> industry = new TextColumn<LinkedInProfile>() {
      @Override
      public String getValue(LinkedInProfile object) {
        return object.getIndustry();
      }
    };
    cellTableOfLinkedIn.addColumn(industry, "Industry");

    TextColumn<LinkedInProfile> currentPositionCompany = new TextColumn<LinkedInProfile>() {
      @Override
      public String getValue(LinkedInProfile object) {
        return object.getCurrentPositionCompany();
      }
    };
    cellTableOfLinkedIn.addColumn(currentPositionCompany, "Current Company");

    TextColumn<LinkedInProfile> pictureUrl = new TextColumn<LinkedInProfile>() {
      @Override
      public String getValue(LinkedInProfile object) {
        return object.getPictureUrl();
      }
    };
    cellTableOfLinkedIn.addColumn(pictureUrl, "Picture URL");

    final SingleSelectionModel<LinkedInProfile> selectionModel = new SingleSelectionModel<>();
    cellTableOfLinkedIn.setSelectionModel(selectionModel);
  }

  private void populateLinkedInTable(LinkedInProfile profile){
    ArrayList<LinkedInProfile> p = new ArrayList<>();
    p.add(profile);
    cellTableOfLinkedIn.setRowCount(1, true);
    cellTableOfLinkedIn.setRowData(0, p);
    niceResponsePanel.clear();
    niceResponsePanel.add(linkedInFlexTable);
    niceResponsePanel.add(cellTableOfLinkedIn);
  }
}