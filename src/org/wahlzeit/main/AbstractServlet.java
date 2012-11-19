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

import java.io.*;
import java.util.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.wahlzeit.model.*;
import org.wahlzeit.services.*;
import org.wahlzeit.utils.*;
import org.wahlzeit.webparts.*;

/**
 * 
 * @author dirkriehle
 *
 */
public abstract class AbstractServlet extends HttpServlet {
	
	protected ServerMain serverMain;
	
	/**
	 * 
	 */
	protected static int lastSessionId = 0; // system and agent are named differently
	private static final long serialVersionUID = 42L; // any one does; class never serialized
	
	/**
	 * 
	 */
	public static synchronized int getLastSessionId() {
		return lastSessionId;
	}
	
	/**
	 * 
	 */
	public static void setLastSessionId(int newSessionId) {
		lastSessionId = newSessionId;
	}
	
	/**
	 * 
	 */
	public static synchronized int getNextSessionId() {
		return ++lastSessionId;
	}
		
	public AbstractServlet(ServerMain serverMain)	{
		this.serverMain = serverMain;
	}
	
	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserSession ctx = ensureWebContext(request);	
		ContextManager.setThreadLocalContext(ctx);
		
		if (serverMain.isShuttingDown() || (ctx == null)) {
			displayNullPage(request, response);
		} else {
			myGet(request, response);
			ctx.dropDatabaseConnection();
		}

		ContextManager.dropThreadLocalContext();
	}
	
	/**
	 * 
	 */
	protected void myGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// do nothing
	}
		
	/**
	 * 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserSession ctx = ensureWebContext(request);	
		ContextManager.setThreadLocalContext(ctx);
		
		if (serverMain.isShuttingDown() || (ctx == null)) {
			displayNullPage(request, response);
		} else {
			myPost(request, response);
			ctx.dropDatabaseConnection();
		}

		ContextManager.dropThreadLocalContext();
	}
	
	/**
	 * 
	 */
	protected void myPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// do nothing
	}

	/**
	 * 
	 */
	protected UserSession ensureWebContext(HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		UserSession result = (UserSession) httpSession.getAttribute("context");
		if (result == null) {
			try {
				String ctxName = "ctx" + getNextSessionId();
				result = new UserSession(ctxName);
				SysLog.logCreatedObject("WebContext", ctxName);

				// yes, "Referer"; typo in original standard documentation
				String referrer = request.getHeader("Referer");
				SysLog.logInfo("request referrer: " + referrer);

				if (request.getLocale().getLanguage().equals("de")) {
					result.setConfiguration(LanguageConfigs.get(Language.GERMAN));
				}
			} catch (Exception ex) {
				SysLog.logThrowable(ex);
			}
			
			httpSession.setAttribute("context", result);
			httpSession.setMaxInactiveInterval(24 * 60 * 60); // time out after 24h
		}
		
		return result;
	}

	/**
	 * 
	 */
	protected void displayNullPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.print("The system is undergoing maintenance and will be back in a minute. Thank you for your patience!");
		out.close();

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * 
	 */
	protected void redirectRequest(HttpServletResponse response, String link) throws IOException {
		response.setContentType("text/html");
		response.sendRedirect(link + ".html");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * 
	 */
	protected void configureResponse(Session ctx, HttpServletResponse response, WebPart result) throws IOException {
		long processingTime = ctx.getProcessingTime();
		result.addString("processingTime", StringUtil.asStringInSeconds((processingTime == 0) ? 1 : processingTime));
		SysLog.logValue("proctime", String.valueOf(processingTime));
		
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		result.writeOn(out);
		out.close();

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * 
	 */
	protected boolean isLocalHost(HttpServletRequest request) {
		String remoteHost = request.getRemoteHost();
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception ex) {
			// ignore
		}
		return remoteHost.equals(localHost) || remoteHost.equals("localhost");
	}
	
	/**
	 * 
	 */
	protected String getRequestArgsAsString(UserSession ctx, Map args) {
		StringBuffer result = new StringBuffer(96);
		for (Iterator i = args.keySet().iterator(); i.hasNext(); ) {
			String key = i.next().toString();
			String value = ctx.getAsString(args, key);
			result.append(key + "=" + value);
			if (i.hasNext()) {
				 result.append("; ");
			}
		}
		return "[" + result.toString() + "]";
	}

}
