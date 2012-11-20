/*
 * Copyright (c) 2006-2009 by Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Wahlzeit photo rating application.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.wahlzeit.handlers;

import java.lang.reflect.Method;
import java.util.Enumeration;

import org.wahlzeit.main.Wahlzeit;
import org.wahlzeit.model.UserSession;
import junit.framework.*;

public class HandlerTestSuite extends TestSuite implements HandlerTest {
	
	/**
	 * 
	 */
	public HandlerTestSuite() {
		super();
	}

	/**
	 * 
	 */
	public HandlerTestSuite(Class testClass) {
		super(testClass);
	}

	/**
	 * Adds the tests from the given class to the suite
	 */
	public void addTestSuite(Class testClass) {
		addTest(new HandlerTestSuite(testClass));
	}

	@Override
	public void setUserSession(UserSession mySession) {
		invokeForEachTest("setUserSession", mySession);
	}
	
	@Override
	public void setWahlzeit(Wahlzeit wahlzeit) {
		invokeForEachTest("setWahlzeit", wahlzeit);		
	}
	
	protected void invokeForEachTest(String method, Object param)	{
		Enumeration myTests = tests();
		
		while(myTests.hasMoreElements()) {
			Test next = (Test) myTests.nextElement();
			if (next instanceof HandlerTest) {
				HandlerTest test = (HandlerTest) next;
				
				invokeMethod(method, test, param);
			}
		}
	}
		
	/*
	 * Invoke target.methodName(param) via reflection
	 */
	protected void invokeMethod(String methodName, Object target, Object param)	{
		try {
			Class<?> paramClass = param.getClass();
			Method[] methods = target.getClass().getMethods();
			
			// Find a method which (a) has the requested name and (b) accepts the given parameter.
			// We cannot use Class.getMethod(String, Class<?>...) since the actual parameter might be the instance of a subclass of the required parameter's class
			for (Method meth : methods) {
				if (meth.getName().equals(methodName))	{
					Class<?>[] params = meth.getParameterTypes();

					if (params.length == 1 && params[0].isAssignableFrom(paramClass))	{
						meth.invoke(target, param);
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("[EX] " + e.getMessage());
		}
	}
}
