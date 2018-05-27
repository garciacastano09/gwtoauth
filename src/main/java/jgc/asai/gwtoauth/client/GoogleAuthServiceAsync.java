package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import jgc.asai.gwtoauth.shared.JGCException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GoogleAuthServiceAsync {
  void googleAuthServer(AsyncCallback<String> callback);
  void getGoogleAuthorizationUrl(AsyncCallback<String> callback) throws JGCException;
  void googleAccessToken(String code, String secretState, AsyncCallback<String> asyncCallback) throws InterruptedException, ExecutionException, IOException;
  void googleGetResource(String urlResource, AsyncCallback<String> asyncCallback) throws InterruptedException, ExecutionException, IOException;
}
