package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import jgc.asai.gwtoauth.shared.JGCException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface LinkedInAuthServiceAsync {
  void linkedInAuthServer(AsyncCallback<String> callback);
  void getLinkedInAuthorizationUrl(AsyncCallback<String> callback) throws JGCException;
  void linkedInAccessToken(String code, String secretState, AsyncCallback<String> asyncCallback) throws InterruptedException, ExecutionException, IOException;
  void linkedInGetResource(String urlResource, AsyncCallback<String> asyncCallback) throws InterruptedException, ExecutionException, IOException;
}
