/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.engine.api;

import java.util.Collection;

import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.exceptions.WrongArgumentException;
import pl.edu.icm.unity.types.authn.AuthenticationRealm;

/**
 * Authentication realm is a group of endpoints which share the same authentication context:
 * in the first place login session. Also other artifacts can have realm scope, for instance
 * the transient identities.
 *   
 * @author K. Benedyczak
 */
public interface RealmsManagement extends NamedEngineDAO<AuthenticationRealm, AuthenticationRealm>
{
	/**
	 * Creates a new realm
	 * @param realm
	 * @throws EngineException
	 * 
	 * @deprecated <b>Use {@link RealmsManagement#create(AuthenticationRealm)} instead.</b>
	 */
	@Deprecated
	void addRealm(AuthenticationRealm realm) throws EngineException;
	
	/**
	 * Returns a realm by name
	 * @param name
	 * @return
	 * @throws WrongArgumentException
	 * @throws EngineException
	 * 
	 * @deprecated <b>Use {@link RealmsManagement#get(String)} instead.</b>
	 */
	@Deprecated
	AuthenticationRealm getRealm(String name) throws WrongArgumentException, EngineException;
	
	/**
	 * Returns all defined realms
	 * @return
	 * @throws EngineException
	 * 
	 * @deprecated <b>Use {@link RealmsManagement#getAll()} instead.</b>
	 */
	@Deprecated
	Collection<AuthenticationRealm> getRealms() throws EngineException;
	
	/**
	 * Update realm
	 * @param realm
	 * @throws EngineException
	 * 
	 * @deprecated <b>Use {@link RealmsManagement#update(AuthenticationRealm)} instead.</b>
	 */
	@Deprecated
	void updateRealm(AuthenticationRealm realm) throws EngineException;
	
	/**
	 * Remove realm
	 * @param name
	 * @throws EngineException
	 * 
	 * @deprecated <b>Use {@link RealmsManagement#deleteByName(String)} instead.</b>
	 */
	@Deprecated
	void removeRealm(String name) throws EngineException;
}
