package jgc.asai.gwtoauth.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import jgc.asai.gwtoauth.client.GoogleAuthService;
import jgc.asai.gwtoauth.client.GreetingService;
import jgc.asai.gwtoauth.shared.FieldVerifier;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GoogleAuthServiceImpl extends RemoteServiceServlet implements GoogleAuthService {

  @Override
  public String googleAuthServer(String name) throws IllegalArgumentException {
    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");

    // Escape data from the client to avoid cross-site script vulnerabilities.
    name = escapeHtml(name);
    userAgent = escapeHtml(userAgent);

    return "Hello, " + name + "!<br><br>I am running " + serverInfo
            + ".<br><br>It looks like you are using:<br>" + userAgent;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   *
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
            ">", "&gt;");
  }

}
