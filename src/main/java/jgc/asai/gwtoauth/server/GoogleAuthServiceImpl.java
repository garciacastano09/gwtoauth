package jgc.asai.gwtoauth.server;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import jgc.asai.gwtoauth.client.GoogleAuthService;
import jgc.asai.gwtoauth.client.Utils;
import jgc.asai.gwtoauth.shared.Credential;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GoogleAuthServiceImpl extends RemoteServiceServlet implements GoogleAuthService {
  private final Logger logger = java.util.logging.Logger.getLogger("GoogleAuthServiceImpl");

  public static final String CALLBACK_URL = "http://127.0.0.1:8888/BaseApp.html";

  private final String clientId = "276349452111-joj2vs62nk72aglcocpl190lookiu8vs.apps.googleusercontent.com";
  private final String clientSecret = "wFpgzM6H6Begphf-7ARA9daJ";
  private final String secretState = "secret" + new Random().nextInt(999_999);
  private final OAuth20Service service = new ServiceBuilder(clientId)
          .apiSecret(clientSecret)
          .scope("profile") // replace with desired scope
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
  public String googleAuthServer() throws IllegalArgumentException {
    logger.info("googleAuthServer");
    final Map<String, String> additionalParams = new HashMap<>();
    additionalParams.put("access_type", "offline");
    //force to reget refresh token (if usera are asked not the first time)
    additionalParams.put("prompt", "consent");
    final String authorizationUrl = service.getAuthorizationUrl(additionalParams);
    return authorizationUrl;
  }

  @Override
  public String googleAccessToken(String code, String secretState) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException {
    logger.info("googleOauthCallback code:"+code+" secretState:"+secretState);
    accessToken = service.getAccessToken(code);
    accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
    return accessToken.getRawResponse();
  }

  @Override
  public String googleGetResource() throws IllegalArgumentException, InterruptedException, ExecutionException, IOException {
    logger.info("googleGetResource");
    final OAuthRequest request = new OAuthRequest(Verb.GET,"https://www.googleapis.com/plus/v1/people/me");
    service.signRequest(accessToken, request);
    final Response response = service.execute(request);
    logger.info("googleGetResource response.getBody: "+response.getBody());
    return response.getBody();
  }

  public String getGoogleAuthorizationUrl(Credential credential) throws Exception{
    logger.info("callback url: " + credential.getRedirectUrl());
    String authorizationUrl = null;
    Token requestToken = null;

    String authProvider = credential.getAuthProvider();

    if (service == null){
      throw new Exception("Could not build OAuthService");
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
      if (authProvider == Utils.FACEBOOK){
        logger.info("Auth URL should have state in QUERY_STRING");
        logger.info("Extract state from URL");
        String state = ServerUtils.getQueryStringValueFromUrl(authorizationUrl,"state");
        if (state != null)
        {
          logger.info("state: " + state);
          logger.info("Save state to session");
          saveStateToSession(state);
        }
      }
    }
    catch(Exception e)
    {
      logger.severe("Exception caught: " + e);
      throw new Exception("Could not get Authorization url: " + e.getMessage());
    }

    logger.info("Returning: " + authorizationUrl);
    return authorizationUrl;
  }

  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
            ">", "&gt;");
  }

  private void saveStateToSession(String state) throws Exception{
    HttpSession session = getHttpSession();
    if (session == null){
      throw new Exception("Sesion Expirada bitch");
    }
    session.setAttribute(SESSION_NONCE,state);
  }

  private HttpSession getHttpSession(){
    return getThreadLocalRequest().getSession();
  }
}
