package jgc.asai.gwtoauth.shared;

import java.io.Serializable;

public class Credential implements Serializable
{
    private String authProviderName;
    private String authProvider;
    private String redirectUrl;
    private String state;        // facebook
    private String verifier;     // code for facebook
    private String loginName;
    private String password;
    private String email;

    public String getAuthProviderName()
    {
        return authProviderName;
    }
    public void setAuthProviderName(String authProviderName)
    {
        this.authProviderName=authProviderName;
    }
    public String getAuthProvider()
    {
        return authProvider;
    }
    public void setAuthProvider(String authProvider)
    {
        this.authProvider=authProvider;
    }
    public String getRedirectUrl()
    {
        return redirectUrl;
    }
    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl=redirectUrl;
    }
    public String getState()
    {
        return state;
    }
    public void setState(String state)
    {
        this.state=state;
    }
    public String getVerifier()
    {
        return verifier;
    }
    public void setVerifier(String verifier)
    {
        this.verifier=verifier;
    }
    public String getLoginName()
    {
        return loginName;
    }
    public void setLoginName(String loginName)
    {
        this.loginName=loginName;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password=password;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email=email;
    }

}

