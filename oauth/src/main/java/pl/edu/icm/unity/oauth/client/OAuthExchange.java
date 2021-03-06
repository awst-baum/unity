/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.oauth.client;

import java.io.IOException;
import java.net.URISyntaxException;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;

import pl.edu.icm.unity.engine.api.authn.AuthenticationException;
import pl.edu.icm.unity.engine.api.authn.AuthenticationResult;
import pl.edu.icm.unity.engine.api.authn.CredentialExchange;
import pl.edu.icm.unity.oauth.client.config.OAuthClientProperties;

/**
 * Defines comunication of a OAuth verificator and retrieval.
 * @author K. Benedyczak
 */
public interface OAuthExchange extends CredentialExchange
{
	public static final String ID = "OAuth2 exchange";
	
	OAuthClientProperties getSettings();
	
	OAuthContext createRequest(String providerKey) 
			throws URISyntaxException, SerializeException, ParseException, IOException;
	
	AuthenticationResult verifyOAuthAuthzResponse(OAuthContext context) throws AuthenticationException;
}
