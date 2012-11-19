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

import org.mortbay.http.*;
import org.mortbay.http.handler.*;
import org.mortbay.jetty.servlet.*;

import org.wahlzeit.agents.AgentManager;
import org.wahlzeit.handlers.*;
import org.wahlzeit.model.*;
import org.wahlzeit.services.*;
import org.wahlzeit.services.mailing.EmailServer;

/**
 * A Main class that runs a Wahlzeit web server.
 * 
 * @author dirkriehle
 *
 */
public abstract class ServerMain extends ModelMain {
	
	/**
	 * 
	 */
	protected boolean isToStop = false;
	
	protected AgentManager agentManager;
	protected EmailServer emailServer;
	protected WebPartHandlerManager handlerManager;
	
	protected ServerMain(UserManager userManager, AgentManager agentManager, EmailServer emailServer, WebPartHandlerManager handlerManager, PhotoManager photoManager)	{
		super(userManager, photoManager);
			
		this.agentManager = agentManager;
		this.handlerManager = handlerManager;
		this.emailServer = emailServer;
	}
	
	/**
	 * 
	 */
	public synchronized void requestStop() {
		SysLog.logInfo("setting stop signal for http server");
		
		isToStop = true;
		notify();
	}
	
	/**
	 * 
	 */
	public boolean isShuttingDown() {
		return isToStop;
	}
	
	/**
	 * 
	 */
	protected HttpServer httpServer = null;
	
	/**
	 * 
	 */
	protected void startUp() throws Exception {
		super.startUp();

		httpServer = createHttpServer();
		configureHttpServer(httpServer);
		
		configurePartHandlers();
		configureLanguageModels();
		
		PhotoFactory.initialize();
			
		agentManager.startAllThreads();

		startHttpServer(httpServer);
	}
	
	/**
	 * 
	 */
	protected void execute() throws Exception {
		wait(); // really, any condition is fine
	}

	/**
	 * 
	 */
	protected void shutDown() throws Exception {
		agentManager.stopAllThreads();
		
		if (httpServer != null) {
			stopHttpServer(httpServer);
		}
		
		super.shutDown();
	}
	
	/**
	 * 
	 */
	protected HttpServer createHttpServer() throws IOException {
		HttpServer server = new HttpServer();

		SocketListener listener = new SocketListener();
		listener.setPort(SysConfig.getHttpPortAsInt());
		server.addListener(listener);

		return server;
	}
	
	/**
	 * 
	 */
	protected void configureHttpServer(HttpServer server) {

		// Favicon hack
		
		HttpContext faviconContext = new HttpContext();
		faviconContext.setContextPath("/favicon.ico");
		server.addContext(faviconContext);

		ResourceHandler faviconHandler = new ResourceHandler();
		faviconContext.setResourceBase(SysConfig.getStaticDir().getRootPath());
		faviconContext.addHandler(faviconHandler);

		faviconContext.addHandler(new NotFoundHandler());

		// robots.txt hack
		
		HttpContext robotsContext = new HttpContext();
		robotsContext.setContextPath("/robots.txt");
		server.addContext(robotsContext);

		ResourceHandler robotsHandler = new ResourceHandler();
		robotsContext.setResourceBase(SysConfig.getStaticDir().getRootPath());
		robotsContext.addHandler(robotsHandler);

		robotsContext.addHandler(new NotFoundHandler());
		
		// Dynamic content
		
		HttpContext servletContext = new HttpContext();
		servletContext.setContextPath("/");
		server.addContext(servletContext);
		
		ServletHandler servlets = new ServletHandler();
		servletContext.addHandler(servlets);
		servlets.addServlet("/*","org.wahlzeit.main.MainServlet");

		servletContext.addHandler(new NotFoundHandler());

		// Photos content
		
		HttpContext photosContext = new HttpContext();
		photosContext.setContextPath(SysConfig.getPhotosUrlPathAsString());
		server.addContext(photosContext);

		ResourceHandler photosHandler = new ResourceHandler();
		photosContext.setResourceBase(SysConfig.getPhotosDirAsString());
		photosContext.addHandler(photosHandler);

		photosContext.addHandler(new NotFoundHandler());

		// Static content
		
		HttpContext staticContext = new HttpContext();
		staticContext.setContextPath(SysConfig.getStaticDir().getRootUrl());
		server.addContext(staticContext);

		ResourceHandler staticHandler = new ResourceHandler();
		staticContext.setResourceBase(SysConfig.getStaticDir().getRootPath());
		staticContext.addHandler(staticHandler);

		// Not Found
		staticContext.addHandler(new NotFoundHandler());
	}

	/**
	 * 
	 */
	public void startHttpServer(HttpServer httpServer) throws Exception {
		httpServer.start();
		SysLog.logInfo("http server was started");
	}
	
	/**
	 * 
	 */
	public void stopHttpServer(HttpServer httpServer) {
		try {
			httpServer.stop(true);
		} catch (InterruptedException ie) {
			SysLog.logThrowable(ie);
		}
		
		SysLog.logInfo("http server was stopped");
	}
	
	/**
	 * 
	 */
	public void configurePartHandlers() {
		WebPartHandler temp = null;
		
		// NullInfo and NullForm
		handlerManager.addWebPartHandler(PartUtil.NULL_FORM_NAME, new NullFormHandler(handlerManager, photoManager));
		
		// Note page
		handlerManager.addWebPartHandler(PartUtil.SHOW_NOTE_PAGE_NAME, new ShowNotePageHandler(handlerManager, photoManager));

		// ShowPhoto page
		handlerManager.addWebPartHandler(PartUtil.FILTER_PHOTOS_FORM_NAME, new FilterPhotosFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.PRAISE_PHOTO_FORM_NAME, new PraisePhotoFormHandler(agentManager, handlerManager, photoManager));

		temp = new ShowPhotoPageHandler(handlerManager, photoManager);
		handlerManager.addWebPartHandler(PartUtil.SHOW_PHOTO_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.ENGAGE_GUEST_FORM_NAME, temp);
		
		handlerManager.addWebPartHandler(PartUtil.FILTER_PHOTOS_PAGE_NAME, new FilterPhotosPageHandler(handlerManager, photoManager));

		handlerManager.addWebPartHandler(PartUtil.RESET_SESSION_PAGE_NAME, new ResetSessionPageHandler(handlerManager, photoManager));
		
		// About and Terms pages
		handlerManager.addWebPartHandler(PartUtil.ABOUT_PAGE_NAME, new ShowInfoPageHandler(AccessRights.GUEST, PartUtil.ABOUT_INFO_FILE, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.CONTACT_PAGE_NAME, new ShowInfoPageHandler(AccessRights.GUEST, PartUtil.CONTACT_INFO_FILE, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.IMPRINT_PAGE_NAME, new ShowInfoPageHandler(AccessRights.GUEST, PartUtil.IMPRINT_INFO_FILE, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.TERMS_PAGE_NAME, new ShowInfoPageHandler(AccessRights.GUEST, PartUtil.TERMS_INFO_FILE, handlerManager, photoManager));

		// Flag, Send, Tell, and Options pages
		temp = handlerManager.addWebPartHandler(PartUtil.FLAG_PHOTO_FORM_NAME, new FlagPhotoFormHandler(emailServer, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.FLAG_PHOTO_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.SEND_EMAIL_FORM_NAME, new SendEmailFormHandler(emailServer, userManager, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.SEND_EMAIL_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.TELL_FRIEND_FORM_NAME, new TellFriendFormHandler(emailServer, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.TELL_FRIEND_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.SET_OPTIONS_FORM_NAME, new SetOptionsFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.SET_OPTIONS_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));
		
		// Signup, Login, EmailUserName/Password, and Logout pages
		temp = handlerManager.addWebPartHandler(PartUtil.SIGNUP_FORM_NAME, new SignupFormHandler(userManager, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.SIGNUP_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));

		handlerManager.addWebPartHandler(PartUtil.CONFIRM_ACCOUNT_PAGE_NAME, new ConfirmAccountPageHandler(userManager, handlerManager, photoManager));

		temp = handlerManager.addWebPartHandler(PartUtil.LOGIN_FORM_NAME, new LoginFormHandler(userManager, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.LOGIN_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.EMAIL_USER_NAME_FORM_NAME, new EmailUserNameFormHandler(emailServer, userManager, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.EMAIL_USER_NAME_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.EMAIL_PASSWORD_FORM_NAME, new EmailPasswordFormHandler(emailServer, userManager, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.EMAIL_PASSWORD_PAGE_NAME, new ShowPartPageHandler(AccessRights.GUEST, temp, handlerManager, photoManager));

		handlerManager.addWebPartHandler(PartUtil.LOGOUT_PAGE_NAME, new LogoutPageHandler(handlerManager, photoManager));
		
		// SetLanguage pages
		temp = new SetLanguagePageHandler(handlerManager, photoManager);
		handlerManager.addWebPartHandler(PartUtil.SET_ENGLISH_LANGUAGE_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SET_GERMAN_LANGUAGE_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SET_SPANISH_LANGUAGE_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SET_JAPANESE_LANGUAGE_PAGE_NAME, temp);

		// SetPhotoSize pages
		temp = new SetPhotoSizePageHandler(handlerManager, photoManager);
		handlerManager.addWebPartHandler(PartUtil.SET_EXTRA_SMALL_PHOTO_SIZE_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SET_SMALL_PHOTO_SIZE_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SET_MEDIUM_PHOTO_SIZE_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SET_LARGE_PHOTO_SIZE_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SET_EXTRA_LARGE_PHOTO_SIZE_PAGE_NAME, temp);

		// ShowHome page
		handlerManager.addWebPartHandler(PartUtil.SHOW_USER_PROFILE_FORM_NAME, new ShowUserProfileFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.SHOW_USER_PHOTO_FORM_NAME, new ShowUserPhotoFormHandler(userManager, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.SHOW_USER_HOME_PAGE_NAME, new ShowUserHomePageHandler(handlerManager, photoManager));
		
		// EditProfile, ChangePassword, EditPhoto, and UploadPhoto pages
		temp = handlerManager.addWebPartHandler(PartUtil.EDIT_USER_PROFILE_FORM_NAME, new EditUserProfileFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.EDIT_USER_PROFILE_PAGE_NAME, new ShowPartPageHandler(AccessRights.USER, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.CHANGE_PASSWORD_FORM_NAME, new ChangePasswordFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.CHANGE_PASSWORD_PAGE_NAME, new ShowPartPageHandler(AccessRights.USER, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.EDIT_USER_PHOTO_FORM_NAME, new EditUserPhotoFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.EDIT_USER_PHOTO_PAGE_NAME, new ShowPartPageHandler(AccessRights.USER, temp, handlerManager, photoManager));
		temp = handlerManager.addWebPartHandler(PartUtil.UPLOAD_PHOTO_FORM_NAME, new UploadPhotoFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.UPLOAD_PHOTO_PAGE_NAME, new ShowPartPageHandler(AccessRights.USER, temp, handlerManager, photoManager));
		
		handlerManager.addWebPartHandler(PartUtil.EDIT_PHOTO_CASE_FORM_NAME, new EditPhotoCaseFormHandler(handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.SHOW_PHOTO_CASES_PAGE_NAME, new ShowPhotoCasesPageHandler(handlerManager, photoManager));

		// Admin page incl. AdminUserProfile and AdminUserPhoto
		temp = new ShowAdminPageHandler(userManager, this, handlerManager, photoManager);
		handlerManager.addWebPartHandler(PartUtil.SHOW_ADMIN_PAGE_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.SHOW_ADMIN_MENU_FORM_NAME, temp);
		handlerManager.addWebPartHandler(PartUtil.ADMIN_USER_PROFILE_FORM_NAME, new AdminUserProfileFormHandler(userManager, handlerManager, photoManager));
		handlerManager.addWebPartHandler(PartUtil.ADMIN_USER_PHOTO_FORM_NAME, new AdminUserPhotoFormHandler(handlerManager, photoManager));
	}
	
	/**
	 * 
	 */
	public static void configureLanguageModels() {
		LanguageConfigs.put(Language.ENGLISH, new EnglishModelConfig());
		LanguageConfigs.put(Language.GERMAN, new GermanModelConfig());
	}		
}
