/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.webui.common;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import pl.edu.icm.unity.engine.api.msg.UnityMessageSource;
import pl.edu.icm.unity.webui.common.safehtml.HtmlTag;

/**
 * Component maintaining a list of values. The values are displayed as labels. Additionally it is possible 
 * to have a remove and edit buttons with custom action handlers for each entry.
 * 
 * @author K. Benedyczak
 */
public class ListOfElements<T> extends VerticalLayout
{
	private UnityMessageSource msg;
	private List<Entry> components;
	private LabelConverter<T> labelConverter;
	private EditHandler<T> editHandler;
	private RemoveHandler<T> removeHandler;
	private boolean addSeparatorLine;
	
	public ListOfElements(UnityMessageSource msg, LabelConverter<T> labelConverter)
	{
		this.msg = msg;
		this.labelConverter = labelConverter;
		this.components = new ArrayList<>();
		this.setId("ListOfElements");
		this.setMargin(false);
		this.setSpacing(false);
	}
	
	public ListOfElements(UnityMessageSource msg)
	{
		this(msg, new DefaultLabelConverter<T>());
	}
	
	public void setEditHandler(EditHandler<T> editHandler)
	{
		this.editHandler = editHandler;
	}

	public boolean isAddSeparatorLine()
	{
		return addSeparatorLine;
	}

	public void setAddSeparatorLine(boolean addSeparatorLine)
	{
		this.addSeparatorLine = addSeparatorLine;
	}

	public void setRemoveHandler(RemoveHandler<T> removeHandler)
	{
		this.removeHandler = removeHandler;
	}

	public void addEntry(T entry)
	{
		Entry component = new Entry(entry);
		components.add(component);
		addComponent(component);
	}
	
	private void removeEntry(Entry entry)
	{
		components.remove(entry);
		removeComponent(entry);
	}
	
	public void clearContents()
	{
		components.clear();
		removeAllComponents();
	}
	
	public List<T> getElements()
	{
		List<T> ret = new ArrayList<>(components.size());
		for (Entry e: components)
			ret.add(e.getElement());
		return ret;
	}
	
	public int size()
	{
		return components.size();
	}
	
	private class Entry extends CustomComponent
	{
		private T element;
		private Label label;
		private Button edit;
		private Button remove;
		
		public Entry(T elementV)
		{
			HorizontalLayout cont = new HorizontalLayout();
			cont.setMargin(false);
			cont.setSpacing(false);
			cont.setWidth(100, Unit.PERCENTAGE);
			
			this.element = elementV;
			setSpacing(true);
			if (editHandler != null)
			{
				edit = new Button();
				edit.setIcon(Images.edit.getResource());
				edit.setDescription(msg.getMessage("edit"));
				edit.setStyleName(Styles.vButtonSmall.toString());
				edit.addClickListener(new Button.ClickListener()
				{
					@Override
					public void buttonClick(ClickEvent event)
					{
						element = editHandler.edit(element);
						removeComponent(label);
						addComponent(labelConverter.toLabel(element));
					}
				});
				cont.addComponent(edit);
			}
			if (removeHandler != null)
			{
				remove = new Button();
				remove.setIcon(Images.delete.getResource());
				remove.setDescription(msg.getMessage("remove"));
				remove.setStyleName(Styles.vButtonSmall.toString());
				remove.addClickListener(new Button.ClickListener()
				{
					@Override
					public void buttonClick(ClickEvent event)
					{
						boolean done = removeHandler.remove(element);
						if (done)
							removeEntry(Entry.this);
					}
				});
				cont.addComponent(remove);
			}
			cont.addComponent(labelConverter.toLabel(element));
			VerticalLayout main = new VerticalLayout(cont);
			if (addSeparatorLine)
			{
				main.addComponent(HtmlTag.horizontalLine());
			}
			main.setSpacing(true);
			main.setMargin(false);
			setCompositionRoot(main);
		}
		
		public T getElement()
		{
			return element;
		}
	}

	/**
	 * Converts generic object to string with its toString method.
	 * @author K. Benedyczak
	 * @param <T>
	 */
	public static class DefaultLabelConverter<T> implements LabelConverter<T>
	{
		@Override
		public Label toLabel(T value)
		{
			return new Label(value.toString());
		}
	}
	
	public interface LabelConverter<T>
	{
		public Component toLabel(T value);
	}
	
	public interface EditHandler<T>
	{
		public T edit(T value);
	}

	public interface RemoveHandler<T>
	{
		public boolean remove(T value);
	}
}
