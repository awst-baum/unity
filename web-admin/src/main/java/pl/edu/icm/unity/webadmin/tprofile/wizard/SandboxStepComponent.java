/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.webadmin.tprofile.wizard;

import pl.edu.icm.unity.server.utils.UnityMessageSource;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

/**
 * UI Component used by {@link SandboxStep}.
 * 
 * @author Roman Krysinski
 */
public class SandboxStepComponent extends CustomComponent 
{

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private Button sboxButton;
	@AutoGenerated
	private Label infoLabel;
	private static final long serialVersionUID = -7462883039112622390L;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param msg 
	 * @param sandboxURL 
	 */
	public SandboxStepComponent(UnityMessageSource msg, String sandboxURL) 
	{
		buildMainLayout();
		setCompositionRoot(mainLayout);

		infoLabel.setValue(msg.getMessage("Wizard.SandboxStepComponent.infoLabel"));
		sboxButton.setCaption(msg.getMessage("Wizard.SandboxStepComponent.sboxButton"));
		SandboxPopup popup = new SandboxPopup(new ExternalResource(sandboxURL));
		popup.attachButton(sboxButton);
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("-1px");
		
		// infoLabel
		infoLabel = new Label();
		infoLabel.setImmediate(false);
		infoLabel.setWidth("100.0%");
		infoLabel.setHeight("-1px");
		infoLabel.setValue("Label");
		mainLayout.addComponent(infoLabel);
		
		// sboxButton
		sboxButton = new Button();
		sboxButton.setCaption("");
		sboxButton.setImmediate(false);
		sboxButton.setWidth("-1px");
		sboxButton.setHeight("-1px");
		mainLayout.addComponent(sboxButton);
		
		return mainLayout;
	}

}
