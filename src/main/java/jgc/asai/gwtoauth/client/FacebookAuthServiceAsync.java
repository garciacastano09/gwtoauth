package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface FacebookAuthServiceAsync {
  void facebookAuthServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
}
