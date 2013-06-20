/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.samlidp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pl.edu.icm.unity.Constants;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.exceptions.InternalException;
import pl.edu.icm.unity.server.api.PreferencesManagement;
import pl.edu.icm.unity.server.authn.AuthenticatedEntity;
import pl.edu.icm.unity.server.authn.InvocationContext;
import pl.edu.icm.unity.types.JsonSerializable;
import pl.edu.icm.unity.types.basic.EntityParam;


/**
 * User's preferences for the SAML endpoints.
 * @author K. Benedyczak
 */
public class SamlPreferences implements JsonSerializable
{
	public static final String ID = SamlPreferences.class.getName();
	public static final String DEFAULT = "DEFAULT SOAP/WEB SERVICE PREFERENCES";
	protected final ObjectMapper mapper = Constants.MAPPER;

	private Map<String, SPSettings> spSettings = new HashMap<String, SamlPreferences.SPSettings>();
	
	@Override
	public String getSerializedConfiguration() throws InternalException
	{
		ObjectNode main = mapper.createObjectNode();
		serializeAll(main);
		try
		{
			return mapper.writeValueAsString(main);
		} catch (JsonProcessingException e)
		{
			throw new InternalException("Can't perform JSON serialization", e);
		}
	}

	protected void serializeAll(ObjectNode main)
	{
		ObjectNode settingsN = main.with("spSettings");
		for (Map.Entry<String, SPSettings> entry: spSettings.entrySet())
			settingsN.put(entry.getKey(), serializeSingle(entry.getValue()));
	}
	
	protected ObjectNode serializeSingle(SPSettings what)
	{
		ObjectNode main = mapper.createObjectNode();
		main.put("doNotAsk", what.doNotAsk);
		main.put("defaultAccept", what.defaultAccept);
		ArrayNode hN = main.withArray("hidden");
		for (String h: what.hiddenAttribtues)
			hN.add(h);
		main.put("selectedIdentity", what.selectedIdentity);
		return main;
	}
	
	@Override
	public void setSerializedConfiguration(String json) throws InternalException
	{
		if (json == null || json.equals(""))
		{
			spSettings = new HashMap<String, SamlPreferences.SPSettings>();
			return;
		}
		try
		{
			ObjectNode main = mapper.readValue(json, ObjectNode.class);
			deserializeAll(main);
		} catch (Exception e)
		{
			throw new InternalException("Can't perform JSON deserialization", e);
		}
	}

	protected void deserializeAll(ObjectNode main)
	{
		ObjectNode spSettingsNode = main.with("spSettings");
		Iterator<String> keys = spSettingsNode.fieldNames();
		for (String key; keys.hasNext();)
		{
			key=keys.next();
			spSettings.put(key, deserializeSingle(spSettingsNode.with(key)));
		}
	}
	
	protected SPSettings deserializeSingle(ObjectNode from)
	{
		SPSettings ret = new SPSettings();
		ret.setDefaultAccept(from.get("defaultAccept").asBoolean());
		ret.setDoNotAsk(from.get("doNotAsk").asBoolean());
		Set<String> hidden = new HashSet<String>();
		ArrayNode hiddenA = from.withArray("hidden");
		for (int i=0; i<hiddenA.size(); i++)
			hidden.add(hiddenA.get(i).asText());
		ret.setHiddenAttribtues(hidden);
		ret.setSelectedIdentity(from.get("selectedIdentity").asText());
		return ret;
	}

	public static SamlPreferences getPreferences(PreferencesManagement preferencesMan) throws EngineException
	{
		AuthenticatedEntity ae = InvocationContext.getCurrent().getAuthenticatedEntity();
		EntityParam entity = new EntityParam(String.valueOf(ae.getEntityId()));
		String raw = preferencesMan.getPreference(entity, SamlPreferences.ID);
		SamlPreferences ret = new SamlPreferences();
		ret.setSerializedConfiguration(raw);
		return ret;
	}
	
	public static void savePreferences(PreferencesManagement preferencesMan, SamlPreferences preferences) 
			throws EngineException
	{
		AuthenticatedEntity ae = InvocationContext.getCurrent().getAuthenticatedEntity();
		EntityParam entity = new EntityParam(String.valueOf(ae.getEntityId()));
		preferencesMan.setPreference(entity, SamlPreferences.ID, preferences.getSerializedConfiguration());
	}
	
	/**
	 * @param sp
	 * @return settings for the given Service provider. Never null - if there are no preferences, then 
	 * the default settings are returned.
	 */
	public SPSettings getSPSettings(String sp)
	{
		SPSettings ret = spSettings.get(sp);
		if (ret == null)
			ret = new SPSettings();
		return ret;
	}
	
	public void setSPSettings(String sp, SPSettings settings)
	{
		spSettings.put(sp, settings);
	}
	
	public static class SPSettings
	{
		private boolean doNotAsk=false;
		private boolean defaultAccept=true;
		private Set<String> hiddenAttribtues = new HashSet<String>();
		private String selectedIdentity;

		public boolean isDoNotAsk()
		{
			return doNotAsk;
		}
		public void setDoNotAsk(boolean doNotAsk)
		{
			this.doNotAsk = doNotAsk;
		}
		public boolean isDefaultAccept()
		{
			return defaultAccept;
		}
		public void setDefaultAccept(boolean defaultAccept)
		{
			this.defaultAccept = defaultAccept;
		}
		public Set<String> getHiddenAttribtues()
		{
			Set<String> ret = new HashSet<String>();
			ret.addAll(hiddenAttribtues);
			return ret;
		}
		public void setHiddenAttribtues(Set<String> hiddenAttribtues)
		{
			this.hiddenAttribtues.clear();
			this.hiddenAttribtues.addAll(hiddenAttribtues);
		}
		public String getSelectedIdentity()
		{
			return selectedIdentity;
		}
		public void setSelectedIdentity(String selectedIdentity)
		{
			this.selectedIdentity = selectedIdentity;
		}
	}
}
