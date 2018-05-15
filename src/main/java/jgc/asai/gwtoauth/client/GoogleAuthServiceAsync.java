package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import jgc.asai.gwtoauth.shared.LoginInfo;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GoogleAuthServiceAsync {
  void googleAuthServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
  void getUserEmail(String token, AsyncCallback<String> callback);
  void login(String requestUri, AsyncCallback<LoginInfo> asyncCallback);
  void loginDetails(String token, AsyncCallback<LoginInfo> asyncCallback);

}
