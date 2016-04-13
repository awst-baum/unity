/*
 * Copyright (c) 2016 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.store.attribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import pl.edu.icm.unity.base.internal.TransactionalRunner;
import pl.edu.icm.unity.store.GenericCompositeDAOImpl;
import pl.edu.icm.unity.store.api.AttributeTypeDAO;
import pl.edu.icm.unity.store.generic.DependencyNotificationManager;
import pl.edu.icm.unity.types.basic.AttributeType;

/**
 * Router of {@link AttributeTypeDAO}.
 * @author K. Benedyczak
 */
@Component
@Primary
public class AttributeTypeDAOImpl extends GenericCompositeDAOImpl<AttributeType> implements AttributeTypeDAO
{
	public static final String NOTIFICATION_ID = "identityTypes";

	@Autowired
	public AttributeTypeDAOImpl(AttributeTypeHzStore hzDAO, AttributeTypeRDBMSStore rdbmsDAO,
			TransactionalRunner tx, DependencyNotificationManager notificationsManager)
	{
		super(hzDAO, rdbmsDAO, tx, notificationsManager, NOTIFICATION_ID);
	}

	@Override
	protected String getKey(AttributeType at)
	{
		return at.getName();
	}
}