/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.ws.authn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;

import pl.edu.icm.unity.exceptions.AuthenticationException;
import pl.edu.icm.unity.server.authn.AuthenticatedEntity;
import pl.edu.icm.unity.server.authn.AuthenticationProcessorUtil;
import pl.edu.icm.unity.server.authn.AuthenticationResult;
import pl.edu.icm.unity.server.authn.InvocationContext;
import pl.edu.icm.unity.server.endpoint.BindingAuthn;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.server.utils.UnityMessageSource;

/**
 * Performs a final authentication, basing on the endpoint's configuration.
 * 
 * @author K. Benedyczak
 */
public class AuthenticationInterceptor extends AbstractPhaseInterceptor<Message>
{
	private static final Logger log = Log.getLogger(Log.U_SERVER_WS, AuthenticationInterceptor.class);
	private UnityMessageSource msg;
	protected List<Map<String, BindingAuthn>> authenticators;
	
	
	public AuthenticationInterceptor(UnityMessageSource msg, List<Map<String, BindingAuthn>> authenticators)
	{
		super(Phase.PRE_INVOKE);
		this.msg = msg;
		this.authenticators = authenticators;
	}

	@Override
	public void handleMessage(Message message) throws Fault
	{
		Map<String, AuthenticationResult> authnCache = new HashMap<String, AuthenticationResult>();
		InvocationContext ctx = new InvocationContext(); 
		InvocationContext.setCurrent(ctx);
		AuthenticationException firstError = null;
		for (Map<String, BindingAuthn> authenticatorSet: authenticators)
		{
			AuthenticatedEntity client;
			try
			{
				client = processAuthnSet(authnCache, authenticatorSet);
			} catch (AuthenticationException e)
			{
				if (log.isDebugEnabled())
					log.debug("Authentication set failed to authenticate the client, " +
							"will try another: " + e);
				if (firstError == null)
					firstError = new AuthenticationException(msg.getMessage(e.getMessage()));
				continue;
			}
			ctx.setAuthenticatedEntity(client);
			break;
		}
		AuthenticatedEntity client = ctx.getAuthenticatedEntity();
		if (client == null)
		{
			log.info("Authentication failed for client");
			
			throw new Fault(firstError == null ? new Exception("Authentication failed") : firstError);
		} else
		{
			if (log.isDebugEnabled())
				log.debug("Client was successfully authenticated: [" + 
						client.getEntityId() + "] " + client.getAuthenticatedWith().toString());
		}
	}
	
	private AuthenticatedEntity processAuthnSet(Map<String, AuthenticationResult> authnCache,
			Map<String, BindingAuthn> authenticatorSet) throws AuthenticationException
	{
		List<AuthenticationResult> setResult = new ArrayList<AuthenticationResult>();
		for (Map.Entry<String, BindingAuthn> authenticator: authenticatorSet.entrySet())
		{
			AuthenticationResult result = authnCache.get(authenticator.getKey());
			if (result == null)
			{
				CXFAuthentication myAuth = (CXFAuthentication) authenticator.getValue();
				result = myAuth.getAuthenticationResult();
				authnCache.put(authenticator.getKey(), result);
			}
			setResult.add(result);
		}
		return AuthenticationProcessorUtil.processResults(setResult);
	}
}