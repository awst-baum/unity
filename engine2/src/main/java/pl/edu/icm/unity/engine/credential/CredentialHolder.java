/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.engine.credential;

import pl.edu.icm.unity.JsonUtil;
import pl.edu.icm.unity.engine.api.authn.local.LocalCredentialVerificator;
import pl.edu.icm.unity.engine.api.authn.local.LocalCredentialVerificatorFactory;
import pl.edu.icm.unity.engine.api.authn.local.LocalCredentialsRegistry;
import pl.edu.icm.unity.types.authn.CredentialDefinition;

/**
 * Internal management of {@link CredentialDefinition} with Local credential verifier. Checks
 * if the local verificator is installed and allows to easily get it.
 * @author K. Benedyczak
 */
public class CredentialHolder
{
	private CredentialDefinition credential;
	private LocalCredentialVerificator handler;

	public CredentialHolder(CredentialDefinition credDef, LocalCredentialsRegistry reg) 
	{
		checkCredentialDefinition(credDef, reg);
		credential = credDef;
	}

	private void checkCredentialDefinition(CredentialDefinition def, LocalCredentialsRegistry reg) 
	{
		LocalCredentialVerificatorFactory fact = reg.getLocalCredentialFactory(def.getTypeId());
		if (fact == null)
			throw new IllegalArgumentException("The credential type " + def.getTypeId() + " is unknown");
		LocalCredentialVerificator handler = fact.newInstance();
		handler.setSerializedConfiguration(JsonUtil.serialize(def.getJsonConfiguration()));
		this.handler = handler;
	}
	
	public CredentialDefinition getCredentialDefinition()
	{
		return credential;
	}
	
	public LocalCredentialVerificator getHandler()
	{
		return handler;
	}
}
