/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE file for licensing information.
 */
package pl.edu.icm.unity.stdext.credential.pass;

import java.util.Collections;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import pl.edu.icm.unity.base.msgtemplates.MessageTemplateDefinition;
import pl.edu.icm.unity.base.notifications.FacilityName;

/**
 * Defines template used for sending password reset confirmation code by email.
 * @author P. Piernik
 * 
 */
@Component
public class EmailPasswordResetTemplateDef extends PasswordResetTemplateDefBase implements MessageTemplateDefinition
{
	public static final String NAME = "EmailPasswordResetCode";
	
	@Override
	public String getDescriptionKey()
	{
		return "MessageTemplateConsumer.EmailPasswordReset.desc";
	}

	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public Set<String> getCompatibleFacilities()
	{
		 return Collections.unmodifiableSet(Sets.newHashSet(FacilityName.EMAIL.toString()));
	}
}
