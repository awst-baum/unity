/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.test.headlessui;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

/**
 * 
 * @author K. Benedyczak
 */
public class TestBasic extends SeleniumTestBase
{
	@Test
	public void loginTest() throws Exception
	{
		driver.get(baseUrl + "/admin/admin");
		waitForPageLoad(By.id("AuthenticationUI.authnenticateButton"));
		
		Cookie sessionBefore = driver.manage().getCookieNamed("JSESSIONID");
		driver.findElement(By.id("AuthenticationUI.username")).clear();
		driver.findElement(By.id("AuthenticationUI.username")).sendKeys("a");
		driver.findElement(By.id("WebPasswordRetrieval.password")).clear();
		driver.findElement(By.id("WebPasswordRetrieval.password")).sendKeys("a");
		driver.findElement(By.id("AuthenticationUI.authnenticateButton")).click();
		
		waitForPageLoad(By.id("MainHeader.logout"));
		assertTrue(driver.findElement(By.id("MainHeader.loggedAs")).getText().contains("Default Administrator"));
		driver.findElement(By.id("MainHeader.logout"));
		Cookie sessionAfter = driver.manage().getCookieNamed("JSESSIONID");
		assertNotEquals(sessionBefore.getValue(), sessionAfter.getValue());
		waitForElement(By.id("MainHeader.logout")).click();
	}
}
