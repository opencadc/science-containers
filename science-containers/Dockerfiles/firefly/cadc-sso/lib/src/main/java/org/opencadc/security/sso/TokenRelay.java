/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2025.                            (c) 2025.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 ************************************************************************
 */
package org.opencadc.security.sso;

import edu.caltech.ipac.firefly.data.userdata.UserInfo;
import edu.caltech.ipac.firefly.server.RequestAgent;
import edu.caltech.ipac.firefly.server.ServerContext;
import edu.caltech.ipac.firefly.server.network.HttpServiceInput;
import edu.caltech.ipac.firefly.server.security.SsoAdapter;
import edu.caltech.ipac.firefly.server.util.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

import javax.servlet.http.Cookie;

/**
 * The {@code TokenRelay} class implements the {@link SsoAdapter} interface to handle
 * Single Sign-On (SSO) authentication within the CADC environment.
 * 
 * <p>This class is responsible for retrieving the authentication token from the SSO cookie
 * and setting the authorization credential for HTTP service inputs.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * TokenRelay tokenRelay = new TokenRelay();
 * Token token = tokenRelay.getAuthToken();
 * if (token != null) {
 *     System.out.println("Token ID: " + token.getId());
 * }
 * }</pre>
 * 
 * <p>Environment variables used:</p>
 * <ul>
 *   <li>CADC_SSO_COOKIE_NAME: The name of the SSO cookie (default: "CADC_SSO").</li>
 *   <li>CADC_SSO_COOKIE_DOMAIN: The domain of the SSO cookie (default: ".canfar.net").</li>
 *   <li>CADC_ALLOWED_DOMAIN: The domain of the downstream service (default: ".canfar.net").</li>
 * </ul>
 * 
 * <p>Methods:</p>
 * <ul>
 *   <li>{@link #getAuthToken()}: Retrieves the authentication token from the SSO cookie.</li>
 *   <li>{@link #setAuthCredential(HttpServiceInput)}: Sets the authorization credential for the given HTTP service input.</li>
 *   <li>{@link #getUserInfo()}: Retrieves the user information associated with the current session.</li>
 *   <li>{@link #getRequestAgent()}: Retrieves the request agent from the server context.</li>
 * </ul>
 */
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
    private static boolean ENFORCE_DOMAIN = Boolean.parseBoolean(System.getenv().getOrDefault("CADC_SSO_ENFORCE_COOKIE_DOMAIN", "false"));
    private static boolean DEBUG = Boolean.parseBoolean(System.getenv().getOrDefault("DEBUG", "false"));
    
    /**
     * Downstream Service Properties
     * ALLOWED_DOMAIN: The domain of the downstream service.
     */
    private static final String ALLOWED_DOMAIN = System.getenv().getOrDefault("CADC_ALLOWED_DOMAIN", ".canfar.net");
    /**
     * Retrieves the authentication token from the SSO cookie.
     * 
     * This method retrieves the SSO cookie from the request agent and, if the cookie is not null,
     * extracts the token value and domain. If the domain matches the expected SSO cookie domain,
     * a new {@link Token} object is created with the token value.
     * 
     * @return Token The authentication token retrieved from the SSO cookie.
     */
    @Override
    public Token getAuthToken() {
        token = null;
        try {
            RequestAgent agent = getRequestAgent();
            Cookie ssoCookie = agent.getCookie(SSO_COOKIE_NAME);
            if (ssoCookie != null) {
                if (DEBUG){
                    cookieDetails(agent, ssoCookie);
                }
                
                String ssoToken = ssoCookie.getValue(); // Get the value of the cookie
                String cookieDomain = ssoCookie.getDomain(); // Get the domain of the cookie

                if (ssoToken == null || ssoToken.isEmpty()) {
                    LOGGER.error("Null or empty token value");
                    return null;
                }

                if (ENFORCE_DOMAIN) {
                    if (cookieDomain == null || cookieDomain.isEmpty() || !cookieDomain.endsWith(SSO_COOKIE_DOMAIN)) {
                        LOGGER.error("Invalid cookie domain. Expected: " + SSO_COOKIE_DOMAIN + ", Actual: " + cookieDomain);
                        return null;
                    }
                }
                // Create a new Token object with the token value from the cookie
                token = new Token(ssoToken);
                LOGGER.info("Retrieved SSO Token");
            }
        }
        catch (Exception error){
            error.printStackTrace();
        }
        return token;
    }


    /**
     * Logs detailed information about the provided SSO cookie and request agent.
     *
     * @param agent The request agent associated with the cookie.
     * @param ssoCookie The SSO cookie whose details are to be logged.
     */
    private void cookieDetails(RequestAgent agent, Cookie ssoCookie) {
        LOGGER.info("==================================="); // Log the cookie properties
        LOGGER.info("COOKIE_NAME    : " + SSO_COOKIE_NAME);
        LOGGER.info("COOKIE_DOMAIN  : " + SSO_COOKIE_DOMAIN);
        LOGGER.info("ENFORCE_DOMAIN : " + ENFORCE_DOMAIN);
        LOGGER.info("==================================="); // Log the cookie properties
        LOGGER.info("Cookie Name    : " + ssoCookie.getName());
        LOGGER.info("Cookie Domain  : " + ssoCookie.getDomain());
        LOGGER.info("Cookie Value   : " + ssoCookie.getValue().substring(0, 10) + "...");
        LOGGER.info("Cookie MaxAge  : " + ssoCookie.getMaxAge());
        LOGGER.info("Cookie Secure  : " + ssoCookie.getSecure());
        LOGGER.info("Cookie HttpOnly: " + ssoCookie.isHttpOnly());
        LOGGER.info("Cookie Agent   : " + agent.getClass().getName());
        LOGGER.info("==================================="); // Log the cookie properties
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
        String requestURL = inputs.getRequestUrl();
        Boolean allowed = isRequestToAllowedDomain(requestURL, ALLOWED_DOMAIN);
        if (token != null && token.getId() != null && allowed) {
            inputs.setHeader("Authorization", "Bearer " + token.getId());
            LOGGER.info("Authorization Header Set");
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

    /**
     * Retrieves the request agent from the server context. This method is package-private
     * to allow for testing with a mock request agent.
     * 
     * @return RequestAgent The request agent associated with the current request.
     */
    RequestAgent getRequestAgent() {
        return ServerContext.getRequestOwner().getRequestAgent();
    }

    /**
     * Checks if the given request URL belongs to the allowed domain.
     *
     * @param requestURL The URL of the request to be checked.
     * @param allowedDomain The domain that is allowed.
     * @return true if the request URL's host ends with the allowed domain, false otherwise.
     */
    public static boolean isRequestToAllowedDomain(String requestURL, String allowedDomain){
        if (DEBUG){
            LOGGER.info("Request URL    : " + requestURL);
            LOGGER.info("Allowed Domain : " + allowedDomain);
        }
        URL url;
        boolean isAllowed = false;
        if (requestURL == null || allowedDomain == null || requestURL.isEmpty() || allowedDomain.isEmpty()) {
            LOGGER.error("Invalid request or allowed URL");
            return false;
        }
        try {
            url = new URI(requestURL).toURL();
            String host = url.getHost();
            isAllowed = host.endsWith(allowedDomain);
        } catch (URISyntaxException err) {
            err.printStackTrace();
        } catch (MalformedURLException err) {
            err.printStackTrace();
        }
        LOGGER.info("Valid Token Domain : " + isAllowed);
        return isAllowed;
    }
}
