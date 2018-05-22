package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import jgc.asai.gwtoauth.shared.Credential;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The client-side stub for the RPC service.
 */

@RemoteServiceRelativePath("googleAuth")
public interface GoogleAuthService extends RemoteService {
  String googleAuthServer() throws IllegalArgumentException;
  String getGoogleAuthorizationUrl(Credential credential) throws Exception;
  String googleAccessToken(String code, String secretState) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
  String googleGetResource() throws IllegalArgumentException, InterruptedException, ExecutionException, IOException;
}
