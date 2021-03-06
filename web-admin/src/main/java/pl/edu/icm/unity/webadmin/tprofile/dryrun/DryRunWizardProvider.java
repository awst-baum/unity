/*
 * Copyright (c) 2015 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.webadmin.tprofile.dryrun;

import org.vaadin.teemu.wizards.Wizard;

import com.vaadin.ui.UI;

import pl.edu.icm.unity.engine.api.TranslationProfileManagement;
import pl.edu.icm.unity.engine.api.msg.UnityMessageSource;
import pl.edu.icm.unity.engine.translation.in.InputTranslationActionsRegistry;
import pl.edu.icm.unity.webui.association.IntroStep;
import pl.edu.icm.unity.webui.sandbox.SandboxAuthnEvent;
import pl.edu.icm.unity.webui.sandbox.SandboxAuthnNotifier;
import pl.edu.icm.unity.webui.sandbox.wizard.AbstractSandboxWizardProvider;

/**
 * Configures profile dry run wizard 
 * @author K. Benedyczak
 */
public class DryRunWizardProvider extends AbstractSandboxWizardProvider
{
	private UnityMessageSource msg;
	private TranslationProfileManagement tpMan;
	private InputTranslationActionsRegistry taRegistry;

	public DryRunWizardProvider(UnityMessageSource msg, String sandboxURL, SandboxAuthnNotifier sandboxNotifier, 
			TranslationProfileManagement tpMan, InputTranslationActionsRegistry taRegistry)
	{
		super(sandboxURL, sandboxNotifier);
		this.msg = msg;
		this.tpMan = tpMan;
		this.taRegistry = taRegistry;
	}

	@Override
	public Wizard getWizardInstance()
	{
		final Wizard wizard = new Wizard();
		wizard.setSizeFull();
		final DryRunStep dryrunStep = new DryRunStep(msg, sandboxURL, tpMan, taRegistry);
		wizard.addStep(new IntroStep(msg, "DryRun.IntroStepComponent.introLabel"));
		wizard.addStep(dryrunStep);
		
		//for the initial page
		openSandboxPopupOnNextButton(wizard);
		
		//and when the page is loaded with back button
		showSandboxPopupAfterGivenStep(wizard, IntroStep.class);
		
		addSandboxListener(new HandlerCallback()
		{
			@Override
			public void handle(SandboxAuthnEvent event)
			{
				dryrunStep.handle(event);
			}
		}, wizard, UI.getCurrent());
		return wizard;
	}

	@Override
	public String getCaption()
	{
		return msg.getMessage("DryRun.wizardCaption");
	}
}
