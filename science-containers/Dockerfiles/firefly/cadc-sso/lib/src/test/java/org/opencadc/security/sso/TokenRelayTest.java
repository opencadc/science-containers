package org.opencadc.security.sso;

import edu.caltech.ipac.firefly.data.userdata.UserInfo;
import edu.caltech.ipac.firefly.server.RequestAgent;
import edu.caltech.ipac.firefly.server.network.HttpServiceInput;
import edu.caltech.ipac.firefly.server.security.SsoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.Cookie;

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
    void testGetAuthToken_ValidSSOCookie() {
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
    void testGetAuthToken_InvalidSSOCookieDomain() {
        Cookie invalidCookie = new Cookie("CADC_SSO", "valid_token");
        invalidCookie.setDomain(".invalid.com");

        when(mockAgent.getCookie("CADC_SSO")).thenReturn(invalidCookie);

        TokenRelay spyRelay = Mockito.spy(tokenRelay);
        doReturn(mockAgent).when(spyRelay).getRequestAgent();

        SsoAdapter.Token token = spyRelay.getAuthToken();

        assertNull(token);
    }

    @Test
    void testGetAuthToken_NoCookie() {
        when(mockAgent.getCookie("CADC_SSO")).thenReturn(null);

        TokenRelay spyRelay = Mockito.spy(tokenRelay);
        doReturn(mockAgent).when(spyRelay).getRequestAgent();

        SsoAdapter.Token token = spyRelay.getAuthToken();

        assertNull(token);
    }

    // @Test
    // void testSetAuthCredential_ValidToken() {
    //     HttpServiceInput input = new HttpServiceInput("https://secure.canfar.net/api");
        
    //     TokenRelay spyRelay = Mockito.spy(tokenRelay);
    //     SsoAdapter.Token mockToken = new SsoAdapter.Token("mock_token");
    //     doReturn(mockToken).when(spyRelay).getAuthToken();

    //     spyRelay.setAuthCredential(input);

    //     assertEquals("Bearer mock_token", input.getHeaders().get("Authorization"));
    // }

    // @Test
    // void testSetAuthCredential_NoToken() {
    //     HttpServiceInput input = new HttpServiceInput("https://secure.canfar.net/api");
        
    //     TokenRelay spyRelay = Mockito.spy(tokenRelay);
    //     doReturn(null).when(spyRelay).getAuthToken();

    //     spyRelay.setAuthCredential(input);

    //     assertNull(input.getHeaders().get("Authorization"));
    // }

    // @Test
    // void testGetUserInfo() {
    //     UserInfo userInfo = tokenRelay.getUserInfo();
    //     assertNotNull(userInfo);
    //     assertNull(userInfo.getLoginName()); // Default empty user
    // }
}
