package jgc.asai.gwtoauth.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import jgc.asai.gwtoauth.shared.Credential;
import jgc.asai.gwtoauth.shared.JGCException;
import java.util.logging.Logger;

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

  private JSONString response;
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

    signInLink.getElement().setClassName("login-area");
    signInLink.setTitle("sign out");
    loginImage.getElement().setClassName("login-area");
    loginPanel.add(signInLink);

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

    RootPanel.get("toggleResponseRawNice").add(toggleResponseRawNice);
    RootPanel.get("rawResponsePanel").add(rawResponsePanel);
    RootPanel.get("niceResponsePanel").add(niceResponsePanel);
    RootPanel.get("googleApiList").add(googleApiList);
    RootPanel.get("googleButtonContainer").add(googleButton);
    RootPanel.get("getGoogleResourceButton").add(getGoogleResourceButton);
    RootPanel.get("facebookButtonContainer").add(facebookButton);
    RootPanel.get("errorLabelContainer").add(errorLabel);
    RootPanel.get("loginPanelContainer").add(loginPanel);

    handleRedirect();
  }

//  public void updateLoginStatus(){
////     if there is a client side session show, Logout link
//    if (Utils.alreadyLoggedIn()){
////      showLogoutAnchor();
//      googleButton.setVisible(false);
//      googleAuthService.googleOauthCallback();
//      // TODO show information google
//      // TODO hide google button
//      // TODO show query options for google
//    }
//    else
//    {
//      showLoginScreen();
//      // TODO show login options
//    }
//    updateLoginLabel();
//  }

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
              logger.info("Access Token="+s);
              rawResponsePanel.clear();
              rawResponsePanel.add(new HTML(s));
              rawResponsePanel.setVisible(true);
              googleButton.setVisible(false);
              getGoogleResourceButton.setVisible(true);
              googleApiList.setVisible(true);
            }
          });
        } catch (Exception e) {e.printStackTrace();}
      } else {logger.info("Redirected, no logged in..");}
    } else {logger.info("No redirection..");}
//    updateLoginStatus();
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

  public JSONString getResponse() {
    return response;
  }

  public void setResponse(JSONString response) {
    this.response = response;
  }

  public void updateResponseUI(String r){
    this.setResponse(new JSONString(r));
    rawResponsePanel.clear();
    rawResponsePanel.add(new HTML(this.getResponse().stringValue()));
    niceResponsePanel.clear();
    niceResponsePanel.add(new HTML("A falta de dejar nice: "+this.getResponse().stringValue()));
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
        googleAuthService.googleGetResource(Utils.getApiName(googleApiList.getSelectedItemText()), new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable throwable) {
            throwable.printStackTrace();
          }
          @Override
          public void onSuccess(String s) {
            logger.info("resource="+s);
            updateResponseUI(s);
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
}


