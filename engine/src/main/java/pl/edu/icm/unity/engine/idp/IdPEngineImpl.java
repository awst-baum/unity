/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.engine.idp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import pl.edu.icm.unity.engine.api.AttributesManagement;
import pl.edu.icm.unity.engine.api.EntityManagement;
import pl.edu.icm.unity.engine.api.TranslationProfileManagement;
import pl.edu.icm.unity.engine.api.msg.UnityMessageSource;
import pl.edu.icm.unity.engine.api.userimport.UserImportSerivce;
import pl.edu.icm.unity.engine.translation.out.OutputTranslationActionsRegistry;
import pl.edu.icm.unity.engine.translation.out.OutputTranslationEngine;

/**
 * IdP engine is responsible for performing common IdP-related functionality. It resolves the information
 * about the user being queried, applies translation profile on the data and exposes it to the endpoint 
 * requiring the data.
 * 
 * @author K. Benedyczak
 */
@Component
@Primary
public class IdPEngineImpl extends IdPEngineImplBase
{
	@Autowired
	public IdPEngineImpl(AttributesManagement attributesMan, 
			EntityManagement identitiesMan,
			@Qualifier("insecure") TranslationProfileManagement profileManagement,
			OutputTranslationEngine translationEngine,
			UserImportSerivce userImportService,
			OutputTranslationActionsRegistry actionsRegistry,
			UnityMessageSource msg)
	{
		super(attributesMan, identitiesMan, profileManagement, translationEngine, 
				userImportService, actionsRegistry, msg);
	}
}
