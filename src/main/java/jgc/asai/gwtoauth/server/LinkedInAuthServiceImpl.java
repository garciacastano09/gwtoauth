package jgc.asai.gwtoauth.server;

import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import jgc.asai.gwtoauth.client.LinkedInAuthService;
import jgc.asai.gwtoauth.client.Utils;
import jgc.asai.gwtoauth.shared.JGCException;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static jgc.asai.gwtoauth.shared.ApiScopes.*;
import static jgc.asai.gwtoauth.shared.UrlResources.CALLBACK_URL;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LinkedInAuthServiceImpl extends RemoteServiceServlet implements LinkedInAuthService {
  private final Logger logger = java.util.logging.Logger.getLogger("LinkedInAuthServiceImpl");

  private final String clientId = "78uufk0ll6qojc";
  private final String clientSecret = "hbrWxkyyuIldxZWu";
  private final String secretState = "secret" + new Random().nextInt(999_999);
  private final OAuth20Service service = new ServiceBuilder(clientId)
          .apiSecret(clientSecret)
          .scope(LINKEDIN_BASIC_PROFILE +" "+LINKEDIN_EMAIL_ADDRESS) // replace with desired scope
          .callback(CALLBACK_URL)
          .state(secretState)
          .build(LinkedInApi20.instance());
  private OAuth2AccessToken accessToken;
  private final String SESSION_NONCE = "gwtoauth_nonce";

  @Override
  public String linkedInAuthServer() {
    logger.info("LinkedInAuthServer");
    final Map<String, String> additionalParams = new HashMap<>();
    additionalParams.put("access_type", "offline");
    additionalParams.put("prompt", "consent");
    return service.getAuthorizationUrl(additionalParams);
  }

  @Override
  public String linkedInAccessToken(String code, String secretState) throws InterruptedException, ExecutionException, IOException {
    logger.info("LinkedInAccessToken code:" + code + " secretState:" + secretState);
    accessToken = service.getAccessToken(code);
    return accessToken.getRawResponse();
  }

  @Override
  public String linkedInGetResource(String apiName) throws InterruptedException, ExecutionException, IOException {
    logger.info("LinkedInGetResource: " + apiName);
    final OAuthRequest request = new OAuthRequest(Verb.GET, Utils.getUrlResource(apiName));
    service.signRequest(accessToken, request);
    final Response response = service.execute(request);
    logger.info("LinkedInGetResource response.getBody: " + response.getBody());
    return response.getBody();
  }

  public String getLinkedInAuthorizationUrl() throws JGCException {
    logger.info("getLinkedInAuthorizationUrl");
    Token requestToken = null;
    String authorizationUrl;

    if (service == null) {
      throw new JGCException("Could not build LinkedIn OAuthService");
    }

    logger.info("Getting LinkedIn Authorization url...");
    try {
      if (requestToken != null) {
        logger.info("Using request token: " + requestToken);
      }
      authorizationUrl = this.linkedInAuthServer();
      logger.info("Got Authorization URL" + authorizationUrl);
      // if the provider supports "state", save it to session
      logger.info("Auth URL should have state in QUERY_STRING");
      logger.info("Extract state from URL");
      String state = ServerUtils.getQueryStringValueFromUrl(authorizationUrl, "state");
      if (state != null) {
        logger.info("state: " + state);
        logger.info("Save state to session");
        saveStateToSession(state);
      }
    } catch (Exception e) {
      logger.severe("Exception caught: " + e);
      throw new JGCException("Could not get Authorization url: " + e.getMessage());
    }

    logger.info("Returning: " + authorizationUrl);
    return authorizationUrl;
  }

  private void saveStateToSession(String state) throws Exception {
    HttpSession session = getHttpSession();
    if (session == null) {
      throw new Exception("Sesion Expirada LinkedIn");
    }
    session.setAttribute(SESSION_NONCE, state);
  }

  private HttpSession getHttpSession() {
    return getThreadLocalRequest().getSession();
  }

}
