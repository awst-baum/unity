/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.oauth.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import pl.edu.icm.unity.server.utils.Log;
import eu.unicore.util.configuration.ConfigurationException;
import eu.unicore.util.configuration.DocumentationReferenceMeta;
import eu.unicore.util.configuration.DocumentationReferencePrefix;
import eu.unicore.util.configuration.PropertiesHelper;
import eu.unicore.util.configuration.PropertyMD;

/**
 * Configuration of OAuth client.
 * @author K. Benedyczak
 */
public class OAuthClientProperties extends PropertiesHelper
{
	private static final Logger log = Log.getLogger(Log.U_SERVER_OAUTH, OAuthClientProperties.class);
	
	@DocumentationReferencePrefix
	public static final String P = "unity.oauth2.client.";

	public static final String DISPLAY_NAME = "displayName";
	
	public static final String PROVIDERS = "providers.";	
	
	public static final String PROVIDER_LOCATION = "authEndpoint";
	public static final String ACCESS_TOKEN_ENDPOINT = "accessTokenEndpoint";
	public static final String PROFILE_ENDPOINT = "profileEndpoint";
	public static final String PROVIDER_NAME = "name";
	public static final String CLIENT_ID = "clientId";
	public static final String CLIENT_SECRET = "clientSecret";
	public static final String SCOPES = "scopes";
	public static final String OPENID_CONNECT = "openIdConnect";
	public static final String OPENID_DISCOVERY = "openIdConnectDiscoveryEndpoint";
	public static final String REGISTRATION_FORM = "registrationFormForUnknown";
	public static final String TRANSLATION_PROFILE = "translationProfile";
	
	@DocumentationReferenceMeta
	public final static Map<String, PropertyMD> META = new HashMap<String, PropertyMD>();
	
	static 
	{
		META.put(DISPLAY_NAME, new PropertyMD("OAuth2 authentication").setDescription("Name of this authentication "
				+ "option to be displayed in the web interface"));
		
		META.put(PROVIDERS, new PropertyMD().setStructuredList(false).setMandatory().
				setDescription("Prefix, under which the available oauth providers are defined."));
		META.put(PROVIDER_LOCATION, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Location (URL) of OAuth2 provider's authorization endpoint"));
		META.put(ACCESS_TOKEN_ENDPOINT, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Location (URL) of OAuth2 provider's access token endpoint"));
		META.put(PROFILE_ENDPOINT, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Location (URL) of OAuth2 provider's user's profile endpoint. "
						+ "It is used to obtain additional user's attributes. "
						+ "If undefined then the call to this endpoint won't be done."));
		META.put(PROVIDER_NAME, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Name of the OAuth provider to be displayed"));
		META.put(CLIENT_ID, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Client identifier, obtained during Unity's "
				+ "registration at the provider"));
		META.put(CLIENT_SECRET, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Client secret, obtained during Unity's "
				+ "registration at the provider"));
		META.put(SCOPES, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Space separated list of authorization scopes to "
				+ "be requested"));
		META.put(OPENID_CONNECT, new PropertyMD("false").setStructuredListEntry(PROVIDERS).
				setDescription("If set to true, then the provider is treated as OpenID "
						+ "Connect 1.0 provider. For such providers specifying " + 
						PROFILE_ENDPOINT + " is not mandatory as the basic user information "
						+ "is retrieved together with access token. However the " 
						+ "discovery endpoint must be set"));
		META.put(OPENID_DISCOVERY, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("OpenID Connect Discovery endpoint address, relevant (and required) "
						+ "only when OpenID Connect mode is turned on."));
		META.put(REGISTRATION_FORM, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Registration form to be shown for the locally unknown users which "
						+ "were successfuly authenticated remotely."));
		META.put(TRANSLATION_PROFILE, new PropertyMD().setStructuredListEntry(PROVIDERS).
				setDescription("Translation profile which will be used to map received user "
						+ "information to a local representation."));
	}
	
	public OAuthClientProperties(Properties properties) throws ConfigurationException
	{
		super(P, properties, META, log);
		Set<String> keys = getStructuredListKeys(PROVIDERS);
		for (String key: keys)
			validateProvider(key);
	}

	private void validateProvider(String key)
	{
		
	}
	
	public Properties getProperties()
	{
		return properties;
	}
}
