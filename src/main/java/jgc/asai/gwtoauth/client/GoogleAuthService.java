package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import jgc.asai.gwtoauth.shared.LoginInfo;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("googleAuth")
public interface GoogleAuthService extends RemoteService {
  String googleAuthServer(String name) throws IllegalArgumentException;
//  String getUserEmail(String token);
//  LoginInfo login(String requestUri);
//  LoginInfo loginDetails(String token);
}
