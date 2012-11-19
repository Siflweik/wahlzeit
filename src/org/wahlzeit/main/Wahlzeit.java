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

package org.wahlzeit.main;

import org.wahlzeit.agents.Agent;
import org.wahlzeit.agents.AgentManager;
import org.wahlzeit.agents.NotifyAboutPraiseAgent;
import org.wahlzeit.handlers.WebPartHandlerManager;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.services.*;
import org.wahlzeit.services.mailing.EmailServer;
import org.wahlzeit.services.mailing.SmtpEmailServer;

/**
 * 
 * @author dirkriehle
 *
 */
public class Wahlzeit extends ServerMain {

	public Wahlzeit(UserManager userManager, AgentManager agentManager, EmailServer emailServer, WebPartHandlerManager handlerManager, PhotoManager photoManager) {
		super(userManager, agentManager, emailServer, handlerManager, photoManager);
	}

	/**
	 * Create and wire the individual components together
	 */
	public static void main(String[] argv) {
		EmailServer emailServer = new SmtpEmailServer();
		PhotoManager photoManager = new PhotoManager(null);
		
		UserManager userManager = new UserManager(emailServer, photoManager);
		
		Agent[] defaultAgents = new Agent[] { new NotifyAboutPraiseAgent(emailServer) };
		
		AgentManager agentManager = new AgentManager(defaultAgents);
		WebPartHandlerManager handlerManager = new WebPartHandlerManager();

		Wahlzeit instance = new Wahlzeit(userManager, agentManager, emailServer, handlerManager, photoManager);
		instance.run(argv);
	}

	/**
	 * 
	 */
	protected SysConfig createProdSysConfig() {
		return new SysConfig("flowers.wahlzeit.com");
	}

}
