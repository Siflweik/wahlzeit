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

import javax.servlet.*;
import javax.servlet.http.*;

import org.mortbay.servlet.*;
import org.wahlzeit.handlers.WebFormHandler;
import org.wahlzeit.handlers.WebPageHandler;
import org.wahlzeit.handlers.WebPartHandlerManager;
import org.wahlzeit.handlers.PartUtil;
import org.wahlzeit.model.UserLog;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.services.SysConfig;
import org.wahlzeit.services.SysLog;
import org.wahlzeit.webparts.WebPart;



/**
 * 
 * @author dirkriehle
 *
 */
public class MainServlet extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 42L; // any one does; class never serialized

	protected static WebPartHandlerManager handlerManager;

	/**
	 * 
	 */
	public void myGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		UserSession ctx = ensureWebContext(request);
		
		String link = request.getRequestURI();
		int linkStart = link.lastIndexOf("/") + 1;
		int linkEnd = link.indexOf(".html");
		if (linkEnd == -1) {
			linkEnd = link.length();
		}
		
		link = link.substring(linkStart, linkEnd);
		UserLog.logValue("requested", link);

		WebPageHandler handler = handlerManager.getWebPageHandlerFor(link);
		String newLink = PartUtil.DEFAULT_PAGE_NAME;
		if (handler != null) {
			Map args = getRequestArgs(request);
			SysLog.logInfo("GET arguments: " + getRequestArgsAsString(ctx, args));
			newLink = handler.handleGet(ctx, link, args);
		}

		if (newLink.equals(link)) { // no redirect necessary
			WebPart result = handler.makeWebPart(ctx);
			ctx.addProcessingTime(System.currentTimeMillis() - startTime);
			configureResponse(ctx, response, result);
			ctx.clearSavedArgs(); // saved args go from post to next get
			ctx.resetProcessingTime();
		} else {
			SysLog.logValue("redirect", newLink);
			redirectRequest(response, newLink);
			ctx.addProcessingTime(System.currentTimeMillis() - startTime);
		}
	}
	
	/**
	 * 
	 */
	public void myPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		UserSession ctx = ensureWebContext(request);
		
		String link = request.getRequestURI();
		int linkStart = link.lastIndexOf("/") + 1;
		int linkEnd = link.indexOf(".form");
		if (linkEnd != -1) {
			link = link.substring(linkStart, linkEnd);
		} else {
			link = PartUtil.NULL_FORM_NAME;
		}
		UserLog.logValue("postedto", link);
			
		Map args = getRequestArgs(request);
		SysLog.logInfo("POST arguments: " + getRequestArgsAsString(ctx, args));
		
		WebFormHandler formHandler = handlerManager.getWebFormHandlerFor(link);
		link = PartUtil.DEFAULT_PAGE_NAME;
		if (formHandler != null) {
			link = formHandler.handlePost(ctx, args);
		}

		redirectRequest(response, link);
		ctx.addProcessingTime(System.currentTimeMillis() - startTime);
	}

	/**
	 * 
	 */
	protected Map getRequestArgs(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        if ((contentType != null) && contentType.startsWith("multipart/form-data")) {
        	MultiPartRequest multiPartRequest = new MultiPartRequest(request);
			return getRequestArgs(multiPartRequest);
		} else {
			return request.getParameterMap();
		}
	}

	/**
	 * 
	 */
	protected Map getRequestArgs(MultiPartRequest request) throws IOException {
		Map<String, String> result = new HashMap<String, String>();

		String[] keys = request.getPartNames();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = null;
			if (key.equals("fileName")) {
				InputStream in = request.getInputStream(key);
				String tempName = SysConfig.getTempDirAsString() + Thread.currentThread().getId();
				FileOutputStream out = new FileOutputStream(new File(tempName));
				int uploaded = 0;
				for (int avail = in.available(); (avail > 0) && (uploaded < 1000000); avail = in.available()) {
					byte[] buffer = new byte[avail];
					in.read(buffer, 0, avail);
					out.write(buffer);
					uploaded += avail;
				}
				out.close();
				value = tempName;
			} else {
				value = request.getString(key);
			}
			result.put(key, value);
		}
		
		return result;
	}

	public static void setWebPartHandlerManager(WebPartHandlerManager manager)	{
		handlerManager = manager;
	}
}
