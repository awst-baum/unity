/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.webui.common;

public class FormValidationException extends Exception
{
	public FormValidationException()
	{
		super();
	}

	public FormValidationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FormValidationException(String message)
	{
		super(message);
	}

	public FormValidationException(Throwable cause)
	{
		super(cause);
	}
}
