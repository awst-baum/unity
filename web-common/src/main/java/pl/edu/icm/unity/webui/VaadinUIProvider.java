/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.webui;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import pl.edu.icm.unity.server.endpoint.BindingAuthn;
import pl.edu.icm.unity.types.endpoint.EndpointDescription;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * Creates UI object by retrieving it from Spring context.
 *  
 * @author K. Benedyczak
 */
public class VaadinUIProvider extends UIProvider
{
	private static final long serialVersionUID = 1L;
	private transient ApplicationContext applicationContext;
	private transient String uiBeanName;
	private transient EndpointDescription description;
	private transient List<Map<String, BindingAuthn>> authenticators;

	public VaadinUIProvider(ApplicationContext applicationContext, String uiBeanName,
			EndpointDescription description,
			List<Map<String, BindingAuthn>> authenticators)
	{
		super();
		this.applicationContext = applicationContext;
		this.uiBeanName = uiBeanName;
		this.description = description;
		this.authenticators = authenticators;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
	{
		return (Class<? extends UI>) applicationContext.getType(uiBeanName);
	}

	@Override
	public UI createInstance(UICreateEvent event)
	{
		UI ui = (UI) applicationContext.getBean(uiBeanName);
		if (ui instanceof UnityWebUI)
		{
			((UnityWebUI)ui).configure(description, authenticators);
		}
		return ui;
	}
}