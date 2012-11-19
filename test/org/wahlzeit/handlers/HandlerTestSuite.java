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
		
	protected void invokeMethod(String method, Object target, Object param)	{
		try {
			Method m = target.getClass().getMethod(method, param.getClass());
			m.invoke(target, param);
		} catch (Exception e) {
		}
	}
}
