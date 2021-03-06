/*
 * Copyright (c) 2015 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.engine.forms.reg;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.edu.icm.unity.base.utils.Log;
import pl.edu.icm.unity.engine.api.translation.form.TranslatedRegistrationRequest;
import pl.edu.icm.unity.engine.forms.BaseRequestValidator;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.exceptions.IllegalFormContentsException;
import pl.edu.icm.unity.store.api.generic.InvitationDB;
import pl.edu.icm.unity.types.registration.RegistrationForm;
import pl.edu.icm.unity.types.registration.RegistrationParam;
import pl.edu.icm.unity.types.registration.RegistrationRequest;
import pl.edu.icm.unity.types.registration.invite.InvitationWithCode;
import pl.edu.icm.unity.types.registration.invite.PrefilledEntry;
import pl.edu.icm.unity.types.registration.invite.PrefilledEntryMode;

/**
 * Helper component with methods to validate registration requests. There are methods to validate both the request 
 * being submitted (i.e. whether it is valid wrt its form) and validating the translated request before it is 
 * accepted.
 * <p>
 * What is more this component implements invitations handling, i.e. the overall validation and 
 * updating the request with mandatory invitation information (what must be done prior to base validation).
 * 
 * @author K. Benedyczak
 */
@Component
public class RegistrationRequestValidator extends BaseRequestValidator
{
	private static final Logger log = Log.getLogger(Log.U_SERVER,
			RegistrationRequestValidator.class);
	@Autowired
	private InvitationDB invitationDB;
	
	public void validateSubmittedRequest(RegistrationForm form, RegistrationRequest request,
			boolean doCredentialCheckAndUpdate) throws EngineException
	{
		boolean byInvitation = processInvitationAndValidateCode(form, request);
		
		super.validateSubmittedRequest(form, request, doCredentialCheckAndUpdate);

		if (byInvitation)
		{
			String code = request.getRegistrationCode();
			log.debug("Received registration request for invitation " + code + ", removing it");
			invitationDB.delete(code);
		}
	}

	public void validateTranslatedRequest(RegistrationForm form, RegistrationRequest originalRequest, 
			TranslatedRegistrationRequest request) throws EngineException
	{
		validateFinalAttributes(request.getAttributes());
		validateFinalCredentials(originalRequest.getCredentials());
		validateFinalIdentities(request.getIdentities());
	}

	/**
	 * Code is validated, wrt to invitation or form fixed code. What is more the request attributes
	 * groups and identities are set to those from invitation when necessary and errors are reported
	 * if request tries to overwrite mandatory elements from invitation.
	 * 
	 * @param form
	 * @param request
	 * @param sql
	 * @return true if the request is by invitation
	 * @throws EngineException
	 */
	private boolean processInvitationAndValidateCode(RegistrationForm form, RegistrationRequest request) 
			throws IllegalFormContentsException
	{
		String codeFromRequest = request.getRegistrationCode();

		if (codeFromRequest == null && form.isByInvitationOnly())
			throw new IllegalFormContentsException("This registration form is available "
					+ "only by invitation with correct code");
		
		if (codeFromRequest == null || form.getRegistrationCode() != null)
		{
			validateRequestCode(form, request);
			return false;
		}
				
		InvitationWithCode invitation = getInvitation(codeFromRequest);
		
		if (!invitation.getFormId().equals(form.getName()))
			throw new IllegalFormContentsException("The invitation is for different registration form");
		
		if (invitation.isExpired())
			throw new IllegalFormContentsException("The invitation has already expired");
		
		processInvitationElements(form.getIdentityParams(), request.getIdentities(), 
				invitation.getIdentities(), "identity");
		processInvitationElements(form.getAttributeParams(), request.getAttributes(), 
				invitation.getAttributes(), "attribute");
		processInvitationElements(form.getGroupParams(), request.getGroupSelections(), 
				invitation.getGroupSelections(), "group");
		return true;
	}

	private <T> void processInvitationElements(List<? extends RegistrationParam> paramDef,
			List<T> requested, Map<Integer, PrefilledEntry<T>> fromInvitation, String elementName) 
					throws IllegalFormContentsException
	{
		validateParamsCount(paramDef, requested, elementName);
		for (Map.Entry<Integer, PrefilledEntry<T>> invitationEntry : fromInvitation.entrySet())
		{
			if (invitationEntry.getKey() >= requested.size())
			{
				log.warn("Invitation has " + elementName + 
						" parameter beyond form limit, skipping it: " + invitationEntry.getKey());
				continue;
			}
			
			if (invitationEntry.getValue().getMode() == PrefilledEntryMode.DEFAULT)
			{
				if (requested.get(invitationEntry.getKey()) == null)
					requested.set(invitationEntry.getKey(), invitationEntry.getValue().getEntry());
			} else
			{
				if (requested.get(invitationEntry.getKey()) != null)
					throw new IllegalFormContentsException("Registration request can not override " 
							+ elementName +	" " + invitationEntry.getKey() + 
							" specified in invitation");
				requested.set(invitationEntry.getKey(), invitationEntry.getValue().getEntry());
			}
		}
	}
	
	
	private InvitationWithCode getInvitation(String codeFromRequest) throws IllegalFormContentsException
	{
		try
		{
			return invitationDB.get(codeFromRequest);
		} catch (Exception e)
		{
			throw new IllegalFormContentsException("The provided registration code is invalid", e);
		}
	}
	
	private void validateRequestCode(RegistrationForm form, RegistrationRequest request)
			throws IllegalFormContentsException
	{
		String formCode = form.getRegistrationCode();
		String code = request.getRegistrationCode();
		if (formCode != null && code == null)
			throw new IllegalFormContentsException("This registration "
					+ "form require a registration code.");
		if (formCode != null && code != null && !formCode.equals(code))
			throw new IllegalFormContentsException("The registration code is invalid.");
	}

	private void validateParamsCount(List<? extends RegistrationParam> paramDefinitions,
			List<?> params, String info) throws IllegalFormContentsException
	{
		if (paramDefinitions.size() != params.size())
			throw new IllegalFormContentsException("There should be "
					+ paramDefinitions.size() + " " + info + " parameters");
	}	
}
