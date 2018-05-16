package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import jgc.asai.gwtoauth.shared.LoginInfo;
import com.google.gwt.http.client.Request;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GoogleAuthServiceAsync {
  Request googleAuthServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
  Request googleOauthCallback(String code, String secretState, AsyncCallback<LoginInfo> asyncCallback) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
  Request googleGetResource(AsyncCallback<LoginInfo> asyncCallback) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
}
