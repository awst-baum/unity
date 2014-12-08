/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.oauth.as;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Locale;

import javax.ws.rs.core.Response;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.junit.Test;

import pl.edu.icm.unity.oauth.as.token.AccessTokenResource;
import pl.edu.icm.unity.server.api.internal.LoginSession;
import pl.edu.icm.unity.server.api.internal.TokensManagement;
import pl.edu.icm.unity.server.authn.InvocationContext;
import pl.edu.icm.unity.types.authn.AuthenticationRealm;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCAccessTokenResponse;

public class AccessTokenResourceTest 
{
	@Test
	public void userInfoFailsWithWrongClient() throws Exception
	{
		TokensManagement tokensManagement = new MockTokensMan();
		OAuthASProperties config = OAuthTestUtils.getConfig();
		AccessTokenResource tested = new AccessTokenResource(tokensManagement, config);
		setupInvocationContext(111);
		
		AuthorizationSuccessResponse step1Resp = OAuthTestUtils.initOAuthFlowAccessCode(tokensManagement,
				100);
		
		Response r = tested.getToken(GrantType.AUTHORIZATION_CODE.getValue(), 
				step1Resp.getAuthorizationCode().getValue(), "https://return.host.com/foo");
		assertEquals(HTTPResponse.SC_BAD_REQUEST, r.getStatus());
	}
	
	@Test
	public void userInfoFailsWithWrongRedirect() throws Exception
	{
		TokensManagement tokensManagement = new MockTokensMan();
		OAuthASProperties config = OAuthTestUtils.getConfig();
		AccessTokenResource tested = new AccessTokenResource(tokensManagement, config);
		setupInvocationContext(100);
		
		AuthorizationSuccessResponse step1Resp = OAuthTestUtils.initOAuthFlowAccessCode(tokensManagement,
				100);
		
		Response r = tested.getToken(GrantType.AUTHORIZATION_CODE.getValue(), 
				step1Resp.getAuthorizationCode().getValue(), "https://wrong.com");
		assertEquals(HTTPResponse.SC_BAD_REQUEST, r.getStatus());
	}
	
	@Test
	public void userInfoFailsOnInvalidToken() throws Exception
	{
		TokensManagement tokensManagement = new MockTokensMan();
		OAuthASProperties config = OAuthTestUtils.getConfig();
		AccessTokenResource tested = new AccessTokenResource(tokensManagement, config);
		setupInvocationContext(100);

		Response resp = tested.getToken(GrantType.AUTHORIZATION_CODE.getValue(), 
				"1234", "https://return.host.com/foo");
		assertEquals(400, resp.getStatus());
		JSONObject ret = (JSONObject) JSONValue.parse(resp.getEntity().toString());
		assertEquals("invalid_grant", ret.get("error"));
	}
	
	@Test
	public void userInfoWorksWithValidToken() throws Exception
	{
		TokensManagement tokensManagement = new MockTokensMan();
		OAuthASProperties config = OAuthTestUtils.getConfig();
		AccessTokenResource tested = new AccessTokenResource(tokensManagement, config);
		setupInvocationContext(100);

		AuthorizationSuccessResponse step1Resp = OAuthTestUtils.initOAuthFlowAccessCode(tokensManagement,
				100);
		
		Response resp = tested.getToken(GrantType.AUTHORIZATION_CODE.getValue(), 
				step1Resp.getAuthorizationCode().getValue(), "https://return.host.com/foo");

		HTTPResponse httpResp = new HTTPResponse(resp.getStatus());
		httpResp.setContent(resp.getEntity().toString());
		httpResp.setContentType("application/json");
		OIDCAccessTokenResponse parsed = OIDCAccessTokenResponse.parse(httpResp);
		assertNotNull(parsed.getAccessToken());
		assertNotNull(parsed.getIDToken());
		ReadOnlyJWTClaimsSet idToken = parsed.getIDToken().getJWTClaimsSet();
		assertEquals("userA", idToken.getSubject());
		assertTrue(idToken.getAudience().contains("clientC"));
		assertEquals("https://localhost:2443/oauth-as", idToken.getIssuer());
		
	}

	private void setupInvocationContext(long entityId)
	{
		AuthenticationRealm realm = new AuthenticationRealm("foo", "", 5, 10, -1, 1000);
		InvocationContext virtualAdmin = new InvocationContext(null, realm);
		LoginSession loginSession = new LoginSession("sid", new Date(), 1000, entityId, "foo");
		virtualAdmin.setLoginSession(loginSession);
		virtualAdmin.setLocale(Locale.ENGLISH);
		InvocationContext.setCurrent(virtualAdmin);
	}
	
}