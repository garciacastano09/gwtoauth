package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("facebookAuth")
public interface FacebookAuthService extends RemoteService {
  String facebookAuthServer(String name) throws IllegalArgumentException;
}
