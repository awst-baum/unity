/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.server.endpoint;

import java.util.List;

import pl.edu.icm.unity.exceptions.IllegalConfigurationDataException;
import pl.edu.icm.unity.types.JsonSerializable;
import pl.edu.icm.unity.types.authn.AuthenticatorSet;
import pl.edu.icm.unity.types.endpoint.EndpointDescription;

/**
 * Generic endpoint instance.
 * 
 * Lifecycle:
 * <ol>
 *  <li>configure or setSerializedConfiguration (once)
 *  <li>setId (once)
 *  
 *  <li>operation.... can include setAuthenticators and setDescription 
 *  
 *  <li>destroy (once)
 * </ol>
 * Destroy might be called also before setId if there was server deployment error.
 * <p>
 * set/get SerializedConfiguration must save/load the whole state of the endpoint, except of the 
 * id, which is always set after initialization.
 * @author K. Benedyczak
 */
public interface EndpointInstance extends JsonSerializable
{
	public void configure(String contextAddress, String jsonConfiguration) throws IllegalConfigurationDataException;
	
	public void setId(String id);

	public EndpointDescription getEndpointDescription();
	
	public void setAuthenticators(List<AuthenticatorSet> authenticatorsInfo, List<BindingAuthn> authenticators);
	
	public void setDescription(String description);
	
	public void destroy();
}
