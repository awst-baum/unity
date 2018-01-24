/*
 * Copyright (c) 2013 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.webui.providers;

import pl.edu.icm.unity.webui.common.Images;

/**
 * Provides additional tab in HomeUI
 * @author P.Piernik
 *
 */
public interface HomeUITabProvider extends UITabProviderBase
{
	Images getIcon();
}
