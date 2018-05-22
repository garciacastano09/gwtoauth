package jgc.asai.gwtoauth.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import jgc.asai.gwtoauth.shared.Credential;
import org.codehaus.jackson.map.Serializers;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static jgc.asai.gwtoauth.client.Utils.GOOGLE;
import static jgc.asai.gwtoauth.server.GoogleAuthServiceImpl.CALLBACK_URL;

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

  private final HorizontalPanel loginPanel = new HorizontalPanel();
  private final Anchor signInLink = new Anchor("");
  private final Image loginImage = new Image();
  private final TextBox nameField = new TextBox();
  private final Button googleButton = new Button("Login Google");
  private final Button getGoogleResourceButton = new Button("Get Google Resource");
  private final Button facebookButton = new Button("Facebook");
  private final Label errorLabel = new Label();
  private final DialogBox dialogBox = new DialogBox();
  private final VerticalPanel googleResponsePanel = new VerticalPanel();
  private final VerticalPanel dialogVPanel = new VerticalPanel();
  private final Button closeDialogButton = new Button("Close");

  private static BaseApp singleton;

  public static BaseApp get(){
    return singleton;
  }

  public void onModuleLoad() {
    singleton = this;

    nameField.setText("GWT User");
    nameField.setFocus(true);
    nameField.selectAll();
    nameField.setVisible(false);

    dialogBox.setText("Remote Procedure Call");
    dialogBox.setAnimationEnabled(true);

    closeDialogButton.getElement().setId("closeButton");
    closeDialogButton.addClickHandler(event -> dialogBox.hide());

    final Label textToServerLabel = new Label();
    final HTML serverResponseLabel = new HTML();
    dialogVPanel.addStyleName("dialogVPanel");
    dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
    dialogVPanel.add(textToServerLabel);
    dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
    dialogVPanel.add(serverResponseLabel);
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    dialogVPanel.add(closeDialogButton);
    dialogBox.setWidget(dialogVPanel);

    googleResponsePanel.addStyleName("googleResponsePanel");
    googleResponsePanel.add(new HTML("<b>Google says:</b>"));
    googleResponsePanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    googleResponsePanel.setVisible(false);

    signInLink.getElement().setClassName("login-area");
    signInLink.setTitle("sign out");
    loginImage.getElement().setClassName("login-area");
    loginPanel.add(signInLink);

    googleButton.addClickHandler(new GoogleHandler());
    getGoogleResourceButton.addClickHandler(new GoogleGetResourceHandler());
    facebookButton.addClickHandler(new FacebookHandler());
    facebookButton.setVisible(false);

    RootPanel.get("googleResponsePanel").add(googleResponsePanel);
    RootPanel.get("nameFieldContainer").add(nameField);
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
              googleResponsePanel.clear();
              googleResponsePanel.add(new HTML(s));
              googleResponsePanel.setVisible(true);
              googleButton.setVisible(false);
              getGoogleResourceButton.setVisible(true);
            }
          });
        } catch (Exception e) {e.printStackTrace();}
      } else {logger.info("Redirected, no logged in..");}
    } else {logger.info("No redirection..");}
//    updateLoginStatus();
  }


  class GoogleHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
//      authGoogle();
      BaseApp.get().getAuthorizationUrl(GOOGLE);
    }
  }

  class GoogleGetResourceHandler implements ClickHandler {
    public void onClick(ClickEvent event) {
      try {
        googleAuthService.googleGetResource(new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable throwable) {
            throwable.printStackTrace();
          }
          @Override
          public void onSuccess(String s) {
            logger.info("resource="+s);
            googleResponsePanel.clear();
            googleResponsePanel.add(new HTML(s));
            googleResponsePanel.setVisible(true);
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
    }
  }
}


