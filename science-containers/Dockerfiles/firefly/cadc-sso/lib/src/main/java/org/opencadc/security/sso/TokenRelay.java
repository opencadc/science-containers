
/**
 * Provides classes and interfaces for handling Single Sign-On (SSO) authentication
 * within the CADC (Canadian Astronomy Data Centre) environment.
 *
 * <p>This package includes the following key components:</p>
 * <ul>
 *   <li>{@link org.opencadc.security.sso.TokenRelay} - A class that retrieves the authentication token from the SSO cookie.</li>
 * </ul>
 *
 * <p>The primary purpose of this package is to facilitate secure authentication and
 * authorization mechanisms for accessing CADC services.</p>
 */
package org.opencadc.security.sso;

import edu.caltech.ipac.firefly.data.userdata.UserInfo;
import edu.caltech.ipac.firefly.server.RequestAgent;
import edu.caltech.ipac.firefly.server.ServerContext;
import edu.caltech.ipac.firefly.server.network.HttpServiceInput;
import edu.caltech.ipac.firefly.server.security.SsoAdapter;
import edu.caltech.ipac.firefly.server.util.Logger;

import javax.servlet.http.Cookie;

public class TokenRelay implements SsoAdapter {

    private static final Logger.LoggerImpl LOGGER = Logger.getLogger();
    private Token token = null;

    /**
     * SSO Cookie Properties
     * SSO_COOKIE_NAME: The name of the SSO cookie.
     * SSO_COOKIE_DOMAIN: The domain of the SSO cookie.
     */
    private static final String SSO_COOKIE_NAME = System.getenv().getOrDefault("CADC_SSO_COOKIE_NAME", "CADC_SSO");
    private static final String SSO_COOKIE_DOMAIN = System.getenv().getOrDefault("CADC_SSO_COOKIE_DOMAIN", ".canfar.net");
    
    /**
     * Downstream Service Properties
     * ALLOWED_DOMAIN: The domain of the downstream service.
     */
    private static final String ALLOWED_DOMAIN = System.getenv().getOrDefault("CADC_ALLOWED_DOMAIN", ".canfar.net");

    /**
     * Retrieves the authentication token from the SSO cookie.
     *
     * This method attempts to retrieve the SSO (Single Sign-On) token from the request's cookies.
     * It first gets the request agent from the server context and then looks for the SSO cookie
     * using the predefined cookie name. If the cookie is found, it extracts the token value and
     * the domain of the cookie. The method then checks if the domain of the cookie is allowed.
     * If the domain is allowed, it creates a new Token object with the token value and logs the
     * successful retrieval. If the domain is not allowed or the cookie is not found, it logs the
     * appropriate message and returns null.
     *
     * @return Token The authentication token if the SSO cookie is found and the domain is allowed, otherwise null.
     */

    /**
     * Retrieves the request agent from the server context. This method is package-private
     * to allow for testing with a mock request agent.
     * 
     * @return RequestAgent The request agent associated with the current request.
     */
    RequestAgent getRequestAgent() {
        return ServerContext.getRequestOwner().getRequestAgent();
    }

    @Override
    public Token getAuthToken() {
        token = null;
        try {
            RequestAgent agent = getRequestAgent();
            Cookie ssoCookie = agent.getCookie(SSO_COOKIE_NAME);

            if (ssoCookie != null) {
                String ssoToken = ssoCookie.getValue(); // Get the value of the cookie
                String cookieDomain = ssoCookie.getDomain(); // Get the domain of the cookie
                if (!SSO_COOKIE_DOMAIN.endsWith(cookieDomain) || cookieDomain == null) {
                    LOGGER.info("SSO Token found, but domain is not allowed " + cookieDomain);
                    return null;
                }
                token = new Token(ssoToken);
                LOGGER.info("Retrieved SSO Token for domain " + cookieDomain);
            }
            else{
                LOGGER.info("SSO Token not found");
                LOGGER.info("SSO Token not found. Available cookies: " + agent.getCookies().keySet());
            }

        }
        catch (Exception error){
            LOGGER.error(error);
        }
        return token;
    }

    
    
    /**
     * Sets the authorization credential for the given HTTP service input.
     * 
     * This method retrieves an authentication token and, if the token is not null
     * and the request URL requires an authorization credential, sets the "Authorization"
     * header of the HTTP service input to "Bearer " followed by the token ID.
     * 
     * @param inputs The HTTP service input for which the authorization credential is to be set.
     */
    @Override
    public void setAuthCredential(HttpServiceInput inputs) {
        Token token = getAuthToken();
        if (token != null && token.getId() != null && SsoAdapter.requireAuthCredential(inputs.getRequestUrl(), ALLOWED_DOMAIN)) {
            inputs.setHeader("Authorization", "Bearer " + token.getId());
        }
    }

    /**
     * Retrieves the user information associated with the current session.
     *
     * @return Default implementation returns an empty {@link UserInfo} object.
     */
    @Override
    public UserInfo getUserInfo() {
        // Return default UserInfo with no user-specific data
        return new UserInfo();
    }
}
