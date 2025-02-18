/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2020.                            (c) 2020.
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
import edu.caltech.ipac.firefly.server.security.SsoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.Cookie;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenRelayTest {
    
    private TokenRelay tokenRelay;
    private RequestAgent mockAgent;

    @BeforeEach
    void setUp() {
        tokenRelay = new TokenRelay();
        mockAgent = mock(RequestAgent.class);
    }

    @Test
    void testGetAuthTokenValidSSOCookie() {
        Cookie validCookie = new Cookie("CADC_SSO", "valid_token");
        validCookie.setDomain(".canfar.net");

        when(mockAgent.getCookie("CADC_SSO")).thenReturn(validCookie);

        // Use reflection to inject mock agent
        TokenRelay spyRelay = Mockito.spy(tokenRelay);
        doReturn(mockAgent).when(spyRelay).getRequestAgent();

        SsoAdapter.Token token = spyRelay.getAuthToken();  // Fix: Correctly reference Token

        assertNotNull(token);
        assertEquals("valid_token", token.getId());
    }

    @Test
    void testGetUserInfo() {
        UserInfo userInfo = tokenRelay.getUserInfo();
        assertNotNull(userInfo);
        assertNotNull(userInfo.getLoginName()); // Default User
        assert(userInfo.getLoginName().equals("Guest")); // Default User is Guest
    }

    @Test
    void testGetAuthTokenNullCookie() {
        when(mockAgent.getCookie("CADC_SSO")).thenReturn(null);

        TokenRelay spyRelay = Mockito.spy(tokenRelay);
        doReturn(mockAgent).when(spyRelay).getRequestAgent();

        SsoAdapter.Token token = spyRelay.getAuthToken();

        assertNull(token);
    }

    @Test
    void testGetAuthTokenEmptyTokenValue() {
        Cookie emptyTokenCookie = new Cookie("CADC_SSO", "");
        emptyTokenCookie.setDomain(".canfar.net");

        when(mockAgent.getCookie("CADC_SSO")).thenReturn(emptyTokenCookie);

        TokenRelay spyRelay = Mockito.spy(tokenRelay);
        doReturn(mockAgent).when(spyRelay).getRequestAgent();

        SsoAdapter.Token token = spyRelay.getAuthToken();

        assertNull(token);
    }

    @Test
    void testGetAuthTokenInvalidDomain() {
        Cookie invalidDomainCookie = new Cookie("CADC_SSO", "valid_token");
        invalidDomainCookie.setDomain(".invalid.net");

        when(mockAgent.getCookie("CADC_SSO")).thenReturn(invalidDomainCookie);

        TokenRelay spyRelay = Mockito.spy(tokenRelay);
        doReturn(mockAgent).when(spyRelay).getRequestAgent();

        // Set ENFORCE_DOMAIN to true using reflection
        try {
            Field enforceDomainField = TokenRelay.class.getDeclaredField("ENFORCE_DOMAIN");
            Field debugField = TokenRelay.class.getDeclaredField("DEBUG");
            enforceDomainField.setAccessible(true);
            enforceDomainField.setBoolean(spyRelay, true);
            debugField.setAccessible(true);
            debugField.setBoolean(spyRelay, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SsoAdapter.Token token = spyRelay.getAuthToken();

        assertNull(token);
    }

    @Test
    void testGetAuthTokenValidDomain() {
        Cookie validDomainCookie = new Cookie("CADC_SSO", "valid_token");
        validDomainCookie.setDomain(".canfar.net");

        when(mockAgent.getCookie("CADC_SSO")).thenReturn(validDomainCookie);

        TokenRelay spyRelay = Mockito.spy(tokenRelay);
        doReturn(mockAgent).when(spyRelay).getRequestAgent();

        // Set ENFORCE_DOMAIN to true using reflection
        try {
            Field enforceDomainField = TokenRelay.class.getDeclaredField("ENFORCE_DOMAIN");
            enforceDomainField.setAccessible(true);
            enforceDomainField.setBoolean(spyRelay, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SsoAdapter.Token token = spyRelay.getAuthToken();

        assertNotNull(token);
        assertEquals("valid_token", token.getId());
    }

    @Test
    void testIsRequestToAllowedDomainValid() {
        String requestURL = "https://example.canfar.net/resource";
        String allowedDomain = ".canfar.net";

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertTrue(result);
    }

    @Test
    void testIsRequestToAllowedDomainInvalid() {
        String requestURL = "https://example.invalid.net/resource";
        String allowedDomain = ".canfar.net";

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertFalse(result);
    }

    @Test
    void testIsRequestToAllowedDomainMalformedURL() {
        String requestURL = "htp://malformed-url";
        String allowedDomain = ".canfar.net";

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertFalse(result);
    }

    @Test
    void testIsRequestToAllowedDomainEmptyURL() {
        String requestURL = "";
        String allowedDomain = ".canfar.net";

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertFalse(result);
    }

    @Test
    void testIsRequestToAllowedDomainNullURL() {
        String requestURL = null;
        String allowedDomain = ".canfar.net";

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertFalse(result);
    }

    @Test
    void testIsRequestToAllowedDomainEmptyAllowedDomain() {
        String requestURL = "https://example.canfar.net/resource";
        String allowedDomain = "";

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertFalse(result);
    }

    @Test
    void testIsRequestToAllowedDomainNullAllowedDomain() {
        String requestURL = "https://example.canfar.net/resource";
        String allowedDomain = null;

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertFalse(result);
    }

    @Test
    void testURISyntaxException() {
        String requestURL = "\"https://example.com/search?q=hello world\"";
        String allowedDomain = ".canfar.net";

        boolean result = TokenRelay.isRequestToAllowedDomain(requestURL, allowedDomain);

        assertFalse(result);
    }
}