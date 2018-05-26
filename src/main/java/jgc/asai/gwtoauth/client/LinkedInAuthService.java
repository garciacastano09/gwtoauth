package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import jgc.asai.gwtoauth.shared.Credential;
import jgc.asai.gwtoauth.shared.JGCException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("linkedInAuth")
public interface LinkedInAuthService extends RemoteService {
  String linkedInAuthServer();
  String getLinkedInAuthorizationUrl(Credential credential) throws JGCException;
  String linkedInAccessToken(String code, String secretState) throws InterruptedException, ExecutionException, IOException;
  String linkedInGetResource(String urlResource) throws InterruptedException, ExecutionException, IOException;
}
