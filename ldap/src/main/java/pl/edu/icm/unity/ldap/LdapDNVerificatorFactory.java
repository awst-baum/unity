/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pl.edu.icm.unity.server.api.PKIManagement;
import pl.edu.icm.unity.server.api.TranslationProfileManagement;
import pl.edu.icm.unity.server.authn.CredentialVerificator;
import pl.edu.icm.unity.server.authn.CredentialVerificatorFactory;
import pl.edu.icm.unity.server.authn.remote.InputTranslationEngine;
import pl.edu.icm.unity.stdext.credential.CertificateExchange;

/**
 * Produces pseudo verificators which search for and resolve attributes of an externally verified certificate 
 * (typically via authenticated TLS).
 * 
 * @author K. Benedyczak
 */
@Component
public class LdapDNVerificatorFactory implements CredentialVerificatorFactory
{
	public static final String NAME = "ldap-cert";
	
	private TranslationProfileManagement profileManagement;
	private InputTranslationEngine trEngine;
	private PKIManagement pkiManagement;

	@Autowired
	public LdapDNVerificatorFactory(@Qualifier("insecure") TranslationProfileManagement profileManagement, 
			InputTranslationEngine trEngine, PKIManagement pkiManagement)
	{
		this.profileManagement = profileManagement;
		this.trEngine = trEngine;
		this.pkiManagement = pkiManagement;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getDescription()
	{
		return "Resolves certificate subject's information using LDAPv3 protocol";
	}

	@Override
	public CredentialVerificator newInstance()
	{
		return new LdapVerificator(getName(), getDescription(), profileManagement, trEngine, pkiManagement,
				CertificateExchange.ID);
	}
}
