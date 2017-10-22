/*
 * Copyright (c) 2017 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.webui.common.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Strings;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import pl.edu.icm.unity.engine.api.msg.UnityMessageSource;
import pl.edu.icm.unity.types.I18nString;
import pl.edu.icm.unity.webui.common.Images;
import pl.edu.icm.unity.webui.common.Styles;
import pl.edu.icm.unity.webui.common.safehtml.HtmlEscapers;

/**
 * Shows {@link I18nString} in read only mode. Implemented as Custom field for
 * convenience.
 * 
 * The content of the message is html escaped.
 * 
 * When option with preview is used then a link is shown which opens an window
 * with full blown html preview.
 * 
 * @author Roman Krysinski
 */
public class I18nLabelWithPreview extends CustomField<I18nString>
{
	private UnityMessageSource msg;
	
	private Map<String, HPairLayout> translationTFs = new HashMap<>();
	private VerticalLayout main;
	private boolean withPreview;

	public I18nLabelWithPreview(UnityMessageSource msg, boolean withPreview)
	{
		this.msg = msg;
		this.withPreview = withPreview;
		initUI();
	}

	public I18nLabelWithPreview(UnityMessageSource msg, String caption, boolean withPreview)
	{
		this(msg, withPreview);
		setCaption(caption);
	}
	
	private void initUI()
	{
		VerticalLayout main = new VerticalLayout();
		msg.getEnabledLocales().values().stream()
			.map(Locale::toString)
			.forEach(localeKey -> 
			{
				HPairLayout pair = new HPairLayout(msg, withPreview);
				Resource image = Images.getFlagForLocale(localeKey);
				if (image != null)
					pair.addImage(image);
				main.addComponent(pair);
				translationTFs.put(localeKey, pair);
			});
		main.setSpacing(false);
		this.main = main;
	}

	@Override
	protected Component initContent()
	{
		return main;
	}

	@Override
	public void setValue(I18nString value)
	{
		super.setValue(value);
		translationTFs.values().forEach(tf -> tf.setVisible(false));
		
		value.getMap().forEach((locale, message) -> 
		{
			HPairLayout tf = translationTFs.get(locale);
			if (tf != null)
			{
				tf.setTextWithPreview(message);
				tf.setVisible(true);
			}
		});
		
		String defaultLocale = msg.getDefaultLocaleCode();
		boolean hasDefaultLocale = value.getMap().keySet().contains(defaultLocale);
		
		if (!hasDefaultLocale && value.getDefaultValue() != null)
		{
			translationTFs.get(defaultLocale).setTextWithPreview(value.getDefaultValue());
			translationTFs.get(defaultLocale).setVisible(true);
		}
	}

	@Override
	public Class<? extends I18nString> getType()
	{
		return I18nString.class;
	}

	/**
	 * Horizontal pair layout with image on the left and text on right.
	 * Has preview option which opens a window with fully blown html.
	 * 
	 * @author Roman Krysinski (roman@unity-idm.eu)
	 */
	private class HPairLayout extends HorizontalLayout
	{
		private Label label;
		private Button preview;
		private static final String HTML_SPACE = "&nbsp";

		public HPairLayout(UnityMessageSource msg, boolean withPreview)
		{
			setSpacing(true);
			setWidth(100, Unit.PERCENTAGE);
			
			VerticalLayout vLayout = new VerticalLayout();
			
			if (withPreview)
			{
				preview = new Button(msg.getMessage("MessageTemplateViewer.preview"));
				preview.setStyleName(Styles.vButtonLink.toString());
				vLayout.addComponent(this.preview);
			}
			
			label = new Label();
			label.setContentMode(ContentMode.HTML);
			vLayout.addComponent(this.label);
			
			addComponent(vLayout);
			setExpandRatio(vLayout, 1.0f);
			setComponentAlignment(vLayout, Alignment.TOP_LEFT);
		}

		public void addImage(Resource res)
		{
			Image img = new Image();
			img.setSource(res);
			img.addStyleName(Styles.smallMargin.toString());
			addComponentAsFirst(img);
			setComponentAlignment(img, Alignment.TOP_LEFT);
		}

		public void setTextWithPreview(String value)
		{
			setPreview(value);
			this.label.setValue(escapeHtmlAndPrepareForDisplaying(value));
		}
		
		private void setPreview(String value)
		{
			if (preview == null)
				return;
			preview.getListeners(ClickListener.class).stream()
				.map(listener -> (ClickListener)listener)
				.forEach(preview::removeClickListener);
			preview.addClickListener(event -> getUI().addWindow(new HtmlPreviewWindow(value)));
		}

		private String escapeHtmlAndPrepareForDisplaying(String value)
		{
			return HtmlEscapers
					.escape(value)
					.replace("\n", "<br>")
					.replace("\t", Strings.repeat(HTML_SPACE, 4))
					.replace(" ", Strings.repeat(HTML_SPACE, 2));
		}
	}
	
	public static class HtmlPreviewWindow extends Window
	{
		public HtmlPreviewWindow(String htmlToPreview)
		{
            Label htmlPreview = new Label();
            htmlPreview.setContentMode(ContentMode.HTML);
            htmlPreview.setValue(htmlToPreview);
            setContent(htmlPreview);
            setModal(true);
		}
	}
	
	public static Builder builder(UnityMessageSource msg, String caption)
	{
		return new Builder(msg, caption);
	}
	public static class Builder
	{
		private UnityMessageSource msg;
		private String caption;
		private boolean withPreview;
		
		public Builder(UnityMessageSource msg, String caption)
		{
			this.msg = msg;
			this.caption = caption;
		}
		public Builder withPreview(boolean withPreview)
		{
			this.withPreview = withPreview;
			return this;
		}
		public I18nLabelWithPreview buildWithValue(I18nString content)
		{
			I18nLabelWithPreview label = new I18nLabelWithPreview(msg, caption, withPreview);
			label.setValue(content);
			return label;
		}
	}
}
