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

import java.util.*;

import org.wahlzeit.model.*;
import org.wahlzeit.model.clients.roles.AdministratorRole;
import org.wahlzeit.model.clients.roles.ModeratorRole;
import org.wahlzeit.model.clients.roles.RegisteredUserRole;
import org.wahlzeit.services.*;
import org.wahlzeit.utils.*;
import org.wahlzeit.webparts.*;

/**
 * 
 * @author dirkriehle
 *
 */
public class AdminUserProfileFormHandler extends AbstractWebFormHandler {
	
	private static final String MODERATOR_KEY = "isModerator";
	private static final String ADMINISTRATOR_KEY = "isAdministrator";
	
	/**
	 *
	 */
	public AdminUserProfileFormHandler() {
		initialize(PartUtil.ADMIN_USER_PROFILE_FORM_FILE, AccessRights.ADMINISTRATOR);
	}
	
	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Map<String, Object> args = ctx.getSavedArgs();

		String userId = ctx.getAndSaveAsString(args, "userId");
		RegisteredUserRole user = UserManager.getInstance().getUserByName(userId);
	
		Photo photo = user.getUserPhoto();
		part.addString(Photo.THUMB, getPhotoThumb(ctx, photo));

		part.maskAndAddString("userId", user.getName());
		part.maskAndAddString(RegisteredUserRole.NAME, user.getName());
		part.addSelect(RegisteredUserRole.STATUS, UserStatus.class, (String) args.get(RegisteredUserRole.STATUS));

		if (user.hasRole(ModeratorRole.class))	{		
			part.addString(MODERATOR_KEY, HtmlUtil.CHECKBOX_CHECK);
		}
		
		if (user.hasRole(AdministratorRole.class))	{		
			part.addString(ADMINISTRATOR_KEY, HtmlUtil.CHECKBOX_CHECK);
		}
		
		part.addSelect(RegisteredUserRole.GENDER, Gender.class, (String) args.get(RegisteredUserRole.GENDER));
		part.addSelect(RegisteredUserRole.LANGUAGE, Language.class, (String) args.get(RegisteredUserRole.LANGUAGE));
		part.maskAndAddStringFromArgsWithDefault(args, RegisteredUserRole.EMAIL_ADDRESS, user.getEmailAddress().asString());
		part.maskAndAddStringFromArgsWithDefault(args, RegisteredUserRole.HOME_PAGE, user.getHomePage().toString());
		
		if (user.getNotifyAboutPraise()) {
			part.addString(RegisteredUserRole.NOTIFY_ABOUT_PRAISE, HtmlUtil.CHECKBOX_CHECK);
		}
	}

	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map args) {
		UserManager um = UserManager.getInstance();
		String userId = ctx.getAndSaveAsString(args, "userId");
		RegisteredUserRole user = um.getUserByName(userId);
		
		String status = ctx.getAndSaveAsString(args, RegisteredUserRole.STATUS);
		boolean isModerator = ctx.getAndSaveAsString(args, MODERATOR_KEY).equals("on");
		boolean isAdministrator = ctx.getAndSaveAsString(args, ADMINISTRATOR_KEY).equals("on");
		String gender = ctx.getAndSaveAsString(args, RegisteredUserRole.GENDER);
		String language = ctx.getAndSaveAsString(args, RegisteredUserRole.LANGUAGE);
		String emailAddress = ctx.getAndSaveAsString(args, RegisteredUserRole.EMAIL_ADDRESS);
		String homePage = ctx.getAndSaveAsString(args, RegisteredUserRole.HOME_PAGE);
		String notifyAboutPraise = ctx.getAndSaveAsString(args, RegisteredUserRole.NOTIFY_ABOUT_PRAISE);
		
		if (!StringUtil.isValidStrictEmailAddress(emailAddress)) {
			ctx.setMessage(ctx.cfg().getEmailAddressIsInvalid());
			return PartUtil.SHOW_ADMIN_PAGE_NAME;
		} else if (!StringUtil.isValidURL(homePage)) {
			ctx.setMessage(ctx.cfg().getUrlIsInvalid());
			return PartUtil.SHOW_ADMIN_PAGE_NAME;
		}
		
		user.setStatus(UserStatus.getFromString(status));
		
		if (isModerator)	{
			user.addRole(new ModeratorRole(user.getClientCore()));
		}
		
		if (isAdministrator)	{
			user.addRole(new AdministratorRole(user.getClientCore()));
		}
		
		user.setGender(Gender.getFromString(gender));
		user.setLanguage(Language.getFromString(language));
		user.setEmailAddress(EmailAddress.getFromString(emailAddress));
		user.setHomePage(StringUtil.asUrl(homePage));
		user.setNotifyAboutPraise((notifyAboutPraise != null) && notifyAboutPraise.equals("on"));

		um.removeUser(user);
		user = um.getUserByName(userId);
		ctx.setSavedArg("userId", userId);

		StringBuffer sb = UserLog.createActionEntry("AdminUserProfile");
		UserLog.addUpdatedObject(sb, "User", user.getName());
		UserLog.log(sb);
		
		ctx.setMessage(ctx.cfg().getProfileUpdateSucceeded());

		return PartUtil.SHOW_ADMIN_PAGE_NAME;
	}
	
}
