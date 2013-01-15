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

package org.wahlzeit.model;

import java.util.*;
import java.sql.*;

import org.wahlzeit.model.clients.ClientCore;
import org.wahlzeit.model.clients.roles.AdministratorRole;
import org.wahlzeit.model.clients.roles.ModeratorRole;
import org.wahlzeit.model.clients.roles.RegisteredUserRole;
import org.wahlzeit.services.*;
import org.wahlzeit.services.mailing.*;

/**
 * The UserManager provides access to and manages Users (including Moderators and Administrators).
 * 
 * @author dirkriehle
 *
 */
public class UserManager extends ObjectManager {

	/**
	 *
	 */
	protected static UserManager instance = new UserManager();

	/**
	 * 
	 */
	public static UserManager getInstance() {
		return instance;
	}
	
	/**
	 * Maps nameAsTag to user of that name (as tag)
	 */
	protected Map<String, RegisteredUserRole> users = new HashMap<String, RegisteredUserRole>();
	
	/**
	 * 
	 */
	protected Random codeGenerator = new Random(System.currentTimeMillis());

	/**
	 * 
	 */
	public boolean hasUserByName(String name) {
		assertIsNonNullArgument(name, "user-by-name");
		return hasUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public boolean hasUserByTag(String tag) {
		assertIsNonNullArgument(tag, "user-by-tag");
		return getUserByTag(tag) != null;
	}
	
	/**
	 * 
	 */
	protected boolean doHasUserByTag(String tag) {
		return doGetUserByTag(tag) != null;
	}
	
	/**
	 * 
	 */
	public RegisteredUserRole getUserByName(String name) {
		return getUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public RegisteredUserRole getUserByTag(String tag) {
		assertIsNonNullArgument(tag, "user-by-tag");

		RegisteredUserRole result = doGetUserByTag(tag);

		if (result == null) {
			try {
				result = (RegisteredUserRole) readObject(getReadingStatement("SELECT * FROM users WHERE name_as_tag = ?"), tag);
			} catch (SQLException sex) {
				SysLog.logThrowable(sex);
			}
			
			if (result != null) {
				doAddUser(result);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	protected RegisteredUserRole doGetUserByTag(String tag) {
		return users.get(tag);
	}
	
	/**
	 * 
	 * @methodtype factory
	 */
	protected RegisteredUserRole createObject(ResultSet rset) throws SQLException {
		AccessRights rights = AccessRights.getFromInt(rset.getInt("rights"));
		
		if (rights == AccessRights.GUEST || rights == AccessRights.NONE)	{
			SysLog.logInfo("received NONE rights value");
		}
		
		ClientCore core = new ClientCore();
		RegisteredUserRole result = new RegisteredUserRole(core, "", "", 0);
		result.readFrom(rset);
		
		//For backward compatibility: admins are a subset of moderators
		if (rights == AccessRights.ADMINISTRATOR || rights == AccessRights.MODERATOR)	{
			result.addRole(new ModeratorRole(core));
		}
		
		if (rights == AccessRights.ADMINISTRATOR)	{
			result.addRole(new AdministratorRole(core));
		}

		return result;
	}
	
	/**
	 * 
	 */
	public void addUser(RegisteredUserRole user) {
		assertIsNonNullArgument(user);
		assertIsUnknownUserAsIllegalArgument(user);

		try {
			int id = user.getId();
			createObject(user, getReadingStatement("INSERT INTO users(id) VALUES(?)"), id);
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		doAddUser(user);		
	}
	
	/**
	 * 
	 */
	protected void doAddUser(RegisteredUserRole user) {
		users.put(user.getNameAsTag(), user);
	}
	
	/**
	 * 
	 */
	public void deleteUser(RegisteredUserRole user) {
		assertIsNonNullArgument(user);
		doDeleteUser(user);

		try {
			deleteObject(user, getReadingStatement("DELETE FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		assertIsUnknownUserAsIllegalState(user);
	}
	
	/**
	 * 
	 */
	protected void doDeleteUser(RegisteredUserRole user) {
		users.remove(user.getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void loadUsers(Collection<RegisteredUserRole> result) {
		try {
			readObjects(result, getReadingStatement("SELECT * FROM users"));
			for (Iterator<RegisteredUserRole> i = result.iterator(); i.hasNext(); ) {
				RegisteredUserRole user = i.next();
				if (!doHasUserByTag(user.getNameAsTag())) {
					doAddUser(user);
				} else {
					SysLog.logValueWithInfo("user", user.getName(), "user had already been loaded");
				}
			}
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		SysLog.logInfo("loaded all users");
	}
	
	/**
	 * 
	 */
	public long createConfirmationCode() {
		return Math.abs(codeGenerator.nextLong() / 2);
	}
	
	/**
	 * 
	 */
	public void emailWelcomeMessage(UserSession ctx, RegisteredUserRole user) {
		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = user.getEmailAddress();

		String emailSubject = ctx.cfg().getWelcomeEmailSubject();
		String emailBody = ctx.cfg().getWelcomeEmailBody() + "\n\n";
		emailBody += ctx.cfg().getWelcomeEmailUserName() + user.getName() + "\n\n"; 
		emailBody += ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += SysConfig.getSiteUrlAsString() + "confirm?code=" + user.getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		EmailService emailService = EmailServiceManager.getDefaultService();
		emailService.sendEmailIgnoreException(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void emailConfirmationRequest(UserSession ctx, RegisteredUserRole user) {
		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = user.getEmailAddress();

		String emailSubject = ctx.cfg().getConfirmAccountEmailSubject();
		String emailBody = ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += SysConfig.getSiteUrlAsString() + "confirm?code=" + user.getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		EmailService emailService = EmailServiceManager.getDefaultService();
		emailService.sendEmailIgnoreException(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void saveUser(RegisteredUserRole user) {
		try {
			updateObject(user, getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public void removeUser(RegisteredUserRole user) {
		saveUser(user);
		users.remove(user.getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void saveUsers() {
		try {
			updateObjects(users.values(), getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public RegisteredUserRole getUserByEmailAddress(String emailAddress) {
		return getUserByEmailAddress(EmailAddress.getFromString(emailAddress));
	}

	/**
	 * 
	 */
	public RegisteredUserRole getUserByEmailAddress(EmailAddress emailAddress) {
		RegisteredUserRole result = null;
		try {
			result = (RegisteredUserRole) readObject(getReadingStatement("SELECT * FROM users WHERE email_address = ?"), emailAddress.asString());
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		if (result != null) {
			RegisteredUserRole current = doGetUserByTag(result.getNameAsTag());
			if (current == null) {
				doAddUser(result);
			} else {
				result = current;
			}
		}

		return result;
	}
	
	/**
	 * 
	 * @methodtype assertion
	 */
	protected void assertIsUnknownUserAsIllegalArgument(RegisteredUserRole user) {
		if (hasUserByTag(user.getNameAsTag())) {
			throw new IllegalArgumentException(user.getName() + "is already known");
		}
	}
	
	/**
	 * 
	 * @methodtype assertion
	 */
	protected void assertIsUnknownUserAsIllegalState(RegisteredUserRole user) {
		if (hasUserByTag(user.getNameAsTag())) {
			throw new IllegalStateException(user.getName() + "should not be known");
		}
	}
	
}
