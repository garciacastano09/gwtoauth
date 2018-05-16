package jgc.asai.gwtoauth.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import java.util.logging.Logger;
import jgc.asai.gwtoauth.shared.LoginInfo;

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
  private final Button googleButton = new Button("Google");
  private final Button facebookButton = new Button("Facebook");
  private final Label errorLabel = new Label();
  private final DialogBox dialogBox = new DialogBox();
  private final VerticalPanel googleResponsePanel = new VerticalPanel();
  private final VerticalPanel dialogVPanel = new VerticalPanel();
  private final Button closeDialogButton = new Button("Close");

  public void onModuleLoad() {
//    final Button sendButton = new Button("Send");

    // We can add style names to widgets
//    sendButton.addStyleName("sendButton");

    nameField.setText("GWT User");
    nameField.setFocus(true);
    nameField.selectAll();

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
    facebookButton.addClickHandler(new FacebookHandler());

    RootPanel.get("googleResponsePanel").add(googleResponsePanel);
    RootPanel.get("nameFieldContainer").add(nameField);
//    RootPanel.get("sendButtonContainer").add(sendButton);
    RootPanel.get("googleButtonContainer").add(googleButton);
    RootPanel.get("facebookButtonContainer").add(facebookButton);
    RootPanel.get("errorLabelContainer").add(errorLabel);
    RootPanel.get("loginPanelContainer").add(loginPanel);

  }

  private void loadLogin(final LoginInfo loginInfo) {
    signInLink.setHref(loginInfo.getLoginUrl());
    signInLink.setText("Please, sign in with your Google Account");
    signInLink.setTitle("Sign in");
  }

  private void loadLogout(final LoginInfo loginInfo) {
    signInLink.setHref(loginInfo.getLogoutUrl());
    signInLink.setText(loginInfo.getName());
    signInLink.setTitle("Sign out");
  }

  class GoogleHandler implements ClickHandler {
    public void onClick(ClickEvent event) {authGoogle();}
    private void authGoogle() {
      googleAuthService.googleAuthServer(nameField.getText(), new AsyncCallback<String>() {
        public void onFailure(Throwable caught) {
          logger.severe("googleAuthServer onFailure");
          googleResponsePanel.setVisible(true);
          googleResponsePanel.clear();
          googleResponsePanel.add(new HTML("<b>"+SERVER_ERROR+"</b>"));
        }
        public void onSuccess(String result) {
          logger.info("googleAuthServer onSuccess");
          googleResponsePanel.setVisible(true);
          googleResponsePanel.clear();
          googleResponsePanel.add(new HTML("<b>Google says:</b><b>"+result+"</b>"));
        }
      });
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


}


