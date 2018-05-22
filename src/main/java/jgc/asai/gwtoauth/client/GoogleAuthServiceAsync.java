package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.http.client.Request;
import jgc.asai.gwtoauth.shared.Credential;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GoogleAuthServiceAsync {
  void googleAuthServer(AsyncCallback<String> callback) throws IllegalArgumentException;
  void getGoogleAuthorizationUrl(Credential credential, AsyncCallback<String> callback) throws IllegalArgumentException;
  void googleAccessToken(String code, String secretState, AsyncCallback<String> asyncCallback) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
  void googleGetResource(AsyncCallback<String> asyncCallback) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
}
