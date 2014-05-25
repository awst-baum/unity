/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.saml.ecp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.unicore.samly2.validators.ReplayAttackChecker;
import pl.edu.icm.unity.saml.sp.SAMLSPProperties;
import pl.edu.icm.unity.server.api.AttributesManagement;
import pl.edu.icm.unity.server.api.TranslationProfileManagement;
import pl.edu.icm.unity.server.api.internal.IdentityResolver;

/**
 * ECP servlet which performs the actual ECP profile processing over PAOS binding.
 * <p>
 * The GET request is used to ask for SAML request. The POST request is used to provide SAML response 
 * and obtain a JWT token which can be subsequently used with other Unity endpoints.
 * 
 * @author K. Benedyczak
 */
public class ECPServlet extends HttpServlet
{
	private ECPStep1Handler step1Handler;
	private ECPStep2Handler step2Handler;

	public ECPServlet(SAMLSPProperties samlProperties, ECPContextManagement samlContextManagement, 
			String myAddress, ReplayAttackChecker replayAttackChecker, IdentityResolver identityResolver,
			TranslationProfileManagement profileManagement, AttributesManagement attrMan)
	{
		step1Handler = new ECPStep1Handler(samlProperties, samlContextManagement, myAddress);
		step2Handler = new ECPStep2Handler(samlProperties, samlContextManagement, myAddress,
				replayAttackChecker, identityResolver, profileManagement, attrMan);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		step1Handler.processECPGetRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		step2Handler.processECPPostRequest(req, resp);
	}
}
