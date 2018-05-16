package jgc.asai.gwtoauth.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import jgc.asai.gwtoauth.shared.LoginInfo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("googleAuth")
public interface GoogleAuthService extends RemoteService {
  String googleAuthServer(String name) throws IllegalArgumentException;
  String googleOauthCallback(String code, String secretState) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
  String googleGetResource() throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
//  String getUserEmail(String token);
//  LoginInfo login(String requestUri);
//  LoginInfo loginDetails(String token);
}
