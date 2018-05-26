package jgc.asai.gwtoauth.server;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import jgc.asai.gwtoauth.client.GoogleAuthService;
import jgc.asai.gwtoauth.client.Utils;
import jgc.asai.gwtoauth.shared.Credential;
import jgc.asai.gwtoauth.shared.JGCException;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static jgc.asai.gwtoauth.shared.ApiScopes.GOOGLE_DRIVE_FILES;
import static jgc.asai.gwtoauth.shared.ApiScopes.GOOGLE_PLUS_USER_INFO_PROFILE;
import static jgc.asai.gwtoauth.shared.UrlResources.CALLBACK_URL;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GoogleAuthServiceImpl extends RemoteServiceServlet implements GoogleAuthService {
  private final Logger logger = java.util.logging.Logger.getLogger("GoogleAuthServiceImpl");

  private final String clientId = "276349452111-joj2vs62nk72aglcocpl190lookiu8vs.apps.googleusercontent.com";
  private final String clientSecret = "wFpgzM6H6Begphf-7ARA9daJ";
  private final String secretState = "secret" + new Random().nextInt(999_999);
  private final OAuth20Service service = new ServiceBuilder(clientId)
          .apiSecret(clientSecret)
          .scope(GOOGLE_PLUS_USER_INFO_PROFILE + " " + GOOGLE_DRIVE_FILES)
          .state(secretState)
          .callback(CALLBACK_URL)
          .build(GoogleApi20.instance());
  private OAuth2AccessToken accessToken;

  private final String SESSION_ID             = "GWTOAuthLoginDemo_sessionid";
  private final String SESSION_REQUEST_TOKEN  = "GWTOAuthLoginDemo_request_token";
  private final String SESSION_NONCE          = "GWTOAuthLoginDemo_nonce";
  private final String SESSION_PROTECTED_URL  = "GWTOAuthLoginDemo_protected_url";
  private final String SESSION_ACCESS_TOKEN   = "GWTOAuthLoginDemo_access_token";
  private final String SESSION_AUTH_PROVIDER  = "GWTOAuthLoginDemo_auth_provider";

  private final String DEFAULT_USERNAME = "test";
  private final String DEFAULT_PASSWORD = "secret";
  private final String DEFAULT_JSON = "{" +
          "\n" +
          "  \":username:\" " + "\"" + DEFAULT_USERNAME + "\""+
          "\n" +
          "}";


  @Override
  public String googleAuthServer(){
    logger.info("googleAuthServer");
    final Map<String, String> additionalParams = new HashMap<>();
    additionalParams.put("access_type", "offline");
    additionalParams.put("prompt", "consent");
    return service.getAuthorizationUrl(additionalParams);
  }

  @Override
  public String googleAccessToken(String code, String secretState) throws InterruptedException, ExecutionException, IOException {
    logger.info("googleAccessToken code:"+code+" secretState:"+secretState);
    accessToken = service.getAccessToken(code);
    accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
    return accessToken.getRawResponse();
  }

  @Override
  public String googleGetResource(String apiName) throws InterruptedException, ExecutionException, IOException {
    logger.info("googleGetResource: "+apiName);
    final OAuthRequest request = new OAuthRequest(Verb.GET, Utils.getUrlResource(apiName));
    service.signRequest(accessToken, request);
    final Response response = service.execute(request);
    logger.info("googleGetResource response.getBody: "+response.getBody());
    return response.getBody();
  }

  public String getGoogleAuthorizationUrl(Credential credential) throws JGCException {
    logger.info("callback url: " + credential.getRedirectUrl());
    String authorizationUrl = null;
    Token requestToken = null;

    if (service == null){
      throw new JGCException("Could not build OAuthService");
    }

    logger.info("Getting Authorization url...");
    try
    {
      if (requestToken != null){
        logger.info("Using request token: " + requestToken);
      }
      authorizationUrl = this.googleAuthServer();
      logger.info("Got Authorization URL" + authorizationUrl);
      // if the provider supports "state", save it to session
    }
    catch(Exception e)
    {
      logger.severe("Exception caught: " + e);
      throw new JGCException("Could not get Authorization url: " + e.getMessage());
    }

    logger.info("Returning: " + authorizationUrl);
    return authorizationUrl;
  }
}
