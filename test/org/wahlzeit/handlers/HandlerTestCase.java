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

import org.wahlzeit.main.Wahlzeit;
import org.wahlzeit.model.*;
import org.wahlzeit.services.mailing.EmailServer;

import junit.framework.*;

public class HandlerTestCase extends TestCase implements HandlerTest {
	
	/**
	 * 
	 */
	private UserSession session;
	private Wahlzeit wahlzeit;
		
	/**
	 * 
	 */
	public HandlerTestCase(String name) {
		super(name);
	}
	
	@Override
	public void setUserSession(UserSession mySession) {
		session = mySession;
	}

	protected UserSession getUserSession()	{
		return session;
	}
	
	@Override
	public void setWahlzeit(Wahlzeit wahlzeit) {
		this.wahlzeit = wahlzeit;
	}
		
	protected Wahlzeit getWahlzeit()	{
		assertNotNull(wahlzeit);
		
		return wahlzeit;
	}
	
	protected EmailServer getEmailServer()	{
		return getWahlzeit().getEmailServer();
	}
	
	protected WebPartHandlerManager getWebPartHandlerManager()	{
		return getWahlzeit().getWebPartHandlerManager();
	}
}
