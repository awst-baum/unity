/*
 * Copyright (c) 2015 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.ldap.endpoint;

import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.server.api.AttributesManagement;
import pl.edu.icm.unity.server.api.IdentitiesManagement;
import pl.edu.icm.unity.server.api.internal.NetworkServer;
import pl.edu.icm.unity.server.api.internal.SessionManagement;
import pl.edu.icm.unity.server.authn.AuthenticationOption;
import pl.edu.icm.unity.server.endpoint.AbstractEndpoint;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.server.utils.UnityServerConfiguration;
import eu.unicore.util.configuration.ConfigurationException;

/**
 * LDAP endpoint exposes a stripped LDAP protocol interface to Unity's database.
 */
public class LdapEndpoint extends AbstractEndpoint
{
	private static final Logger LOG = Log.getLogger(Log.U_SERVER_LDAP, LdapServerProperties.class);
	
	public static final String SERVER_WORK_DIRECTORY = "/ldapServer"; 
	
	private LdapServerProperties configuration;

	private SessionManagement sessionMan;

	private AttributesManagement attributesMan;

	private IdentitiesManagement identitiesMan;

	private UnityServerConfiguration mainConfig;

	private NetworkServer httpServer;

	public LdapEndpoint(NetworkServer server, SessionManagement sessionMan,
			AttributesManagement attributesMan, IdentitiesManagement identitiesMan, 
			UnityServerConfiguration mainConfig)
	{
		this.httpServer = server;
		this.sessionMan = sessionMan;
		this.attributesMan = attributesMan;
		this.identitiesMan = identitiesMan;
		this.mainConfig = mainConfig;
	}

	@Override
	protected void setSerializedConfiguration(String serializedState)
	{
		properties = new Properties();
		try
		{
			properties.load(new StringReader(serializedState));
			configuration = new LdapServerProperties(properties);
		} catch (Exception e)
		{
			throw new ConfigurationException("Can't initialize the the LDAP"
					+ " endpoint's configuration", e);
		}
	}

	@Override
	public void start() throws EngineException
	{
		RawPasswordRetrieval rpr = (RawPasswordRetrieval) (authenticators.get(0)
				.getPrimaryAuthenticator());
		startLdapEmbeddedServer(rpr);
	}
	
	@Override
	public void updateAuthenticationOptions(List<AuthenticationOption> authenticationOptions)
			throws UnsupportedOperationException
	{
	}

	private void startLdapEmbeddedServer(RawPasswordRetrieval rpr)
	{
		String host = configuration.getValue(LdapServerProperties.HOST);
		if (null == host || host.isEmpty())
		{
			host = httpServer.getAdvertisedAddress().getHost();
		}
		int port = configuration.getIntValue(LdapServerProperties.LDAP_PORT);

		String workDirectory = mainConfig.getValue(UnityServerConfiguration.WORKSPACE_DIRECTORY) 
				+ SERVER_WORK_DIRECTORY;
		LdapApacheDSInterceptor ladi = new LdapApacheDSInterceptor(rpr, sessionMan,
				this.description.getRealm(), attributesMan, identitiesMan,
				configuration);
		LdapServerFacade ldf = new LdapServerFacade(host, port, 
				"ldap server interface", workDirectory);
		ladi.setLdapServerFacade(ldf);
		try
		{
			ldf.init(false, ladi);
			ldf.changeAdminPasswordBeforeStart(configuration.getValue(LdapServerProperties.BIND_PASSWORD));
			ldf.start();

		} catch (Exception e)
		{
			LOG.error("LDAP embedded server failed to start", e);
		}
	}
}
