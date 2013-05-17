/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.engine.mock;

import pl.edu.icm.unity.exceptions.IllegalCredentialException;
import pl.edu.icm.unity.exceptions.IllegalIdentityValueException;
import pl.edu.icm.unity.server.authn.AbstractLocalVerificator;
import pl.edu.icm.unity.server.authn.EntityWithCredential;
import pl.edu.icm.unity.stdext.identity.X500Identity;
import pl.edu.icm.unity.types.authn.LocalCredentialState;

public class MockPasswordVerificator extends AbstractLocalVerificator implements MockExchange
{
	private static final String[] ID_TYPES = {X500Identity.ID};
	private int minLen = 6;
	
	public MockPasswordVerificator(String name, String description, String exchangeId)
	{
		super(name, description, exchangeId);
	}

	public int getMinLen()
	{
		return minLen;
	}

	public void setMinLen(int minLen)
	{
		this.minLen = minLen;
	}


	@Override
	public String getSerializedConfiguration()
	{
		return minLen+"";
	}

	@Override
	public void setSerializedConfiguration(String json)
	{
		try
		{
			minLen = Integer.parseInt(json);
		} catch (Exception e)
		{
			throw new IllegalArgumentException("The configuration of the mock password handler is invlid", e);
		}
	}

	@Override
	public String prepareCredential(String credential, String currentCredential)
			throws IllegalCredentialException
	{
		return "PPP" + credential;
	}

	@Override
	public LocalCredentialState checkCredentialState(String credential)
	{
		if (credential == null)
			return LocalCredentialState.notSet;
		if (credential.startsWith("PPP"))
			return LocalCredentialState.correct;
		return LocalCredentialState.outdated;
	}

	@Override
	public long checkPassword(String username, String password)
			throws IllegalIdentityValueException, IllegalCredentialException
	{
		EntityWithCredential entityWithCred = identityResolver.resolveIdentity(username, 
				ID_TYPES, getCredentialName());

		if (!password.equals(entityWithCred.getCredentialValue()))
			throw new IllegalCredentialException("Wrong password");
		return entityWithCred.getEntityId();
	}

}