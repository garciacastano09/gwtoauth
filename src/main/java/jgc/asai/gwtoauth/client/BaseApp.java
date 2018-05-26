package jgc.asai.gwtoauth.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import jgc.asai.gwtoauth.shared.Credential;
import jgc.asai.gwtoauth.shared.GoogleDriveFile;
import jgc.asai.gwtoauth.shared.GooglePlusIdentity;
import jgc.asai.gwtoauth.shared.JGCException;
import com.google.gwt.core.client.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

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
  private static final String SERVER_ERROR = "An error occurred while "
          + "attempting to contact the server. Please check your network "
          + "connection and try again.";

  private final GoogleAuthServiceAsync googleAuthService = GWT.create(GoogleAuthService.class);
  private final FacebookAuthServiceAsync facebookAuthServer = GWT.create(FacebookAuthService.class);

  private final ListBox googleApiList = new ListBox();
  private final HorizontalPanel loginPanel = new HorizontalPanel();
  private final Anchor signInLink = new Anchor("");
  private final Image loginImage = new Image();
  private final TextBox nameField = new TextBox();
  private final Button googleButton = new Button("Login Google");
  private final Button toggleResponseRawNice = new Button();
  private final Button getGoogleResourceButton = new Button("Get Google Resource");
  private final Button facebookButton = new Button("Facebook");
  private final Label errorLabel = new Label();
  private final DialogBox dialogBox = new DialogBox();
  private final VerticalPanel rawResponsePanel = new VerticalPanel();
  private final VerticalPanel niceResponsePanel = new VerticalPanel();
  private final Button closeDialogButton = new Button("Close");

//  private final VerticalPanel flexTableVerticalPanel = new VerticalPanel();

  private final FlexTable googleDriveFlexTable = new FlexTable();
  private final FlexTable googlePlusFlexTable = new FlexTable();

  private final CellTable<GoogleDriveFile> cellTableOfGoogleDriveFile = new CellTable<>();
  private final CellTable<GooglePlusIdentity> cellTableOfGooglePlusProfile = new CellTable<>();

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

//    signInLink.getElement().setClassName("login-area");
//    signInLink.setTitle("sign out");
//    loginImage.getElement().setClassName("login-area");
//    loginPanel.add(signInLink);

    googleButton.addClickHandler(new GoogleHandler());
    googleButton.setVisible(true);

    getGoogleResourceButton.addClickHandler(new GoogleGetResourceHandler());
    getGoogleResourceButton.setVisible(false);

    toggleResponseRawNice.addClickHandler(new ToggleResponseRawNiceHandler());
    toggleResponseRawNice.setVisible(false);

    facebookButton.addClickHandler(new FacebookHandler());
    facebookButton.setVisible(false);

    googleApiList.addItem("Google Plus");
    googleApiList.addItem("Google Drive");
    googleApiList.setVisible(false);

    createGoogleDriveFilesTable();
    createGooglePlusProfileTable();

    RootPanel.get("toggleResponseRawNice").add(toggleResponseRawNice);
    RootPanel.get("rawResponsePanel").add(rawResponsePanel);
    RootPanel.get("googleApiList").add(googleApiList);
    RootPanel.get("googleButtonContainer").add(googleButton);
    RootPanel.get("getGoogleResourceButton").add(getGoogleResourceButton);
    RootPanel.get("facebookButtonContainer").add(facebookButton);
    RootPanel.get("errorLabelContainer").add(errorLabel);
    RootPanel.get("loginPanelContainer").add(loginPanel);
    RootPanel.get("niceResponsePanel").add(niceResponsePanel);

    handleRedirect();
  }

  private void handleRedirect(){
    logger.info("handleRedirect");
    if (Utils.redirected()){
      if (!Utils.alreadyLoggedIn()){
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
//              logger.info("Access Token="+s);
//              rawResponsePanel.clear();
//              rawResponsePanel.add(new HTML(s));
//              rawResponsePanel.setVisible(true);
              googleButton.setVisible(false);
              getGoogleResourceButton.setVisible(true);
              googleApiList.setVisible(true);
            }
          });
        } catch (Exception e) {e.printStackTrace();}
      } else {logger.info("Redirected, no logged in..");}
    } else {logger.info("No redirection..");}
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
    this.setResponse(new JSONObject(JsonUtils.safeEval(r)));
    rawResponsePanel.clear();
    rawResponsePanel.add(new HTML(JsonUtils.escapeValue(r)));
    switch (apiName){
      case Utils.GOOGLE_DRIVE:
        populateGoogleDriveTable(GoogleDriveFile.getFileListFromJSONString(this.getResponse()));
      case Utils.GOOGLE_PLUS:
        populateGooglePlusTable(GooglePlusIdentity.getProfileFromJSONString(this.getResponse()));
    }
    this.setNiceOutput(true);
  }


  class GoogleHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
      BaseApp.get().getAuthorizationUrl(GOOGLE);
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

  class FacebookHandler implements ClickHandler {
    public void onClick(ClickEvent event) {authFacebook();}
    private void authFacebook() {
      facebookAuthServer.facebookAuthServer(nameField.getText(), new AsyncCallback<String>() {
        public void onFailure(Throwable caught) {
          logger.severe("facebookAuthServer onFailure");
          dialogBox.setText("Remote Procedure Call - Failure "+SERVER_ERROR);
          dialogBox.center();
          closeDialogButton.setFocus(true);
        }
        public void onSuccess(String result) {
          logger.info("facebookAuthServer onSuccess");
          dialogBox.setText("Remote Procedure Call "+result);
          dialogBox.center();
          closeDialogButton.setFocus(true);
        }
      });
    }
  }

  public void getAuthorizationUrl(final String authProvider){
//    String authProviderName = Utils.getAuthProviderName(authProvider);
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
            Utils.saveAuthProvider(authProvider);
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
//    selectionModel.addSelectionChangeHandler(event -> {
//      GoogleDriveFile selectedGoogleDriveFile = selectionModel.getSelectedObject();
//      if (selectedGoogleDriveFile != null) {
//        Window.alert("Selected: First line: " + selectedGoogleDriveFile.getId() + ", Second line: " + selectedGoogleDriveFile.getName());
//      }
//    });

//    List<GoogleDriveFile> addresses = new ArrayList<GoogleDriveFile>() {
//      {
//        add(new GoogleDriveFile("Cell Table", "First line", "Oxford", "UK"));
//        add(new GoogleDriveFile("Cell Table", "Second line", "Cambrige", "UK"));
//      }
//    };
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
}


