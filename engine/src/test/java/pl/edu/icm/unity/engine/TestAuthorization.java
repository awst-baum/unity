/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import pl.edu.icm.unity.engine.authz.AuthorizationManagerImpl;
import pl.edu.icm.unity.engine.authz.RoleAttributeTypeProvider;
import pl.edu.icm.unity.engine.server.EngineInitialization;
import pl.edu.icm.unity.exceptions.AuthorizationException;
import pl.edu.icm.unity.stdext.attr.EnumAttribute;
import pl.edu.icm.unity.stdext.credential.pass.PasswordToken;
import pl.edu.icm.unity.stdext.identity.UsernameIdentity;
import pl.edu.icm.unity.types.basic.Attribute;
import pl.edu.icm.unity.types.basic.EntityParam;
import pl.edu.icm.unity.types.basic.EntityState;
import pl.edu.icm.unity.types.basic.Group;
import pl.edu.icm.unity.types.basic.Identity;
import pl.edu.icm.unity.types.basic.IdentityParam;
import pl.edu.icm.unity.types.basic.IdentityTaV;


public class TestAuthorization extends DBIntegrationTestBase
{
	private void setAdminsRole(String role) throws Exception
	{
		Attribute roleAt = EnumAttribute.of(RoleAttributeTypeProvider.AUTHORIZATION_ROLE,
				"/", role);
		EntityParam adminEntity = new EntityParam(new IdentityTaV(UsernameIdentity.ID, "admin"));
		insecureAttrsMan.setAttribute(adminEntity, roleAt);
	}
	
	@Test
	public void test() throws Exception
	{
		setAdminsRole(AuthorizationManagerImpl.CONTENTS_MANAGER_ROLE);
		try
		{
			//tests standard deny
			serverMan.resetDatabase();
			fail("reset db possible for contents man");
		} catch(AuthorizationException e) {}
		
		IdentityParam toAdd = new IdentityParam(UsernameIdentity.ID, "user1");
		Identity added = idsMan.addEntity(toAdd, EngineInitialization.DEFAULT_CREDENTIAL_REQUIREMENT, 
				EntityState.valid, false);
		EntityParam entity = new EntityParam(added.getEntityId());
		attrsMan.createAttribute(entity, EnumAttribute.of(RoleAttributeTypeProvider.AUTHORIZATION_ROLE,
				"/", AuthorizationManagerImpl.USER_ROLE));
		setupUserContext("user1", null);
		try
		{
			//tests standard deny
			serverMan.resetDatabase();
			fail("reset db possible for user");
		} catch(AuthorizationException e) {}
		try
		{
			//tests standard deny
			groupsMan.addGroup(new Group("/A"));
			fail("addGrp possible for user");
		} catch(AuthorizationException e) {}
		
		//tests self access
		attrsMan.getAttributes(entity, "/", null);
		
		setupUserContext("admin", null);
		groupsMan.addGroup(new Group("/A"));
		groupsMan.addMemberFromParent("/A", entity);
		attrsMan.removeAttribute(entity, "/", RoleAttributeTypeProvider.AUTHORIZATION_ROLE);
		
		attrsMan.createAttribute(entity, EnumAttribute.of(RoleAttributeTypeProvider.AUTHORIZATION_ROLE,
				"/A", AuthorizationManagerImpl.SYSTEM_MANAGER_ROLE));
		setupUserContext("user1", null);
		try
		{
			//tests standard deny
			serverMan.resetDatabase();
			fail("reset db possible for user");
		} catch(AuthorizationException e) {}
		//tests group authz
		groupsMan.addGroup(new Group("/A/B"));
		//tests searching of the role attribute in the parent group
		groupsMan.removeGroup("/A/B", true);
		
		//test if limited role in subgroup won't be effective (should get roles from the parent)
		groupsMan.addGroup(new Group("/A/G"));
		groupsMan.addMemberFromParent("/A/G", entity);
		attrsMan.createAttribute(entity, EnumAttribute.of(RoleAttributeTypeProvider.AUTHORIZATION_ROLE,
				"/A/G", AuthorizationManagerImpl.ANONYMOUS_ROLE));
		groupsMan.addGroup(new Group("/A/G/Z"));
		
		
		try
		{
			//tests standard deny
			groupsMan.addGroup(new Group("/B"));
			fail("addGrp possible for no-role");
		} catch(AuthorizationException e) 
		{
			assertThat(e.toString().contains("addGroup"), is(true));
		}
		
		//tests outdated credential
		setupUserContext("admin", null);
		attrsMan.createAttribute(entity, EnumAttribute.of(RoleAttributeTypeProvider.AUTHORIZATION_ROLE,
				"/", AuthorizationManagerImpl.USER_ROLE));
		setupUserContext("admin", EngineInitialization.DEFAULT_CREDENTIAL);
		try
		{
			attrsMan.setAttribute(entity, EnumAttribute.of(RoleAttributeTypeProvider.AUTHORIZATION_ROLE,
					"/", AuthorizationManagerImpl.INSPECTOR_ROLE));
			fail("set attributes with outdated credential");
		} catch(AuthorizationException e) {}
		
		eCredMan.setEntityCredential(entity, EngineInitialization.DEFAULT_CREDENTIAL, 
				new PasswordToken("foo12!~").toJson());
		idTypeMan.getIdentityTypes();
	}
}
