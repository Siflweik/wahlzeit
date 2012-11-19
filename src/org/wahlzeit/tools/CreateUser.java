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

package org.wahlzeit.tools;

import java.io.*;

import org.wahlzeit.services.mailing.EmailServer;
import org.wahlzeit.services.mailing.SmtpEmailServer;
import org.wahlzeit.utils.*;
import org.wahlzeit.main.*;
import org.wahlzeit.model.*;

/**
 * 
 * @author dirkriehle
 *
 */
public class CreateUser extends ModelMain {

	protected CreateUser(UserManager userManager, PhotoManager photoManager) {
		super(userManager, photoManager);
	}

	/**
	 * 
	 */
	public static void main(String[] argv) {
		EmailServer emailServer = new SmtpEmailServer();
		PhotoManager photoManager = new PhotoManager(null);
		UserManager userManager = new UserManager(emailServer, photoManager);
		
		CreateUser instance = new CreateUser(userManager, photoManager);
		instance.run(argv);
	}
	
	/**
	 * 
	 */
	protected String userName = "testuser";
	protected String password = "testuser";
	protected String photoDir = "config/photos";
	
	/**
	 * 
	 */
	protected void handleArgv(String[] argv) {
		for (int i = 0; i < argv.length; i++) {
			String arg = argv[i];
			if (arg.equals("--password")) {
				password = argv[++i];
			} else if (arg.equals("--username")) {
				userName = argv[++i];
			} else if (arg.equals("--photodir")) {
				photoDir = argv[++i];
			}
		}
		
		if (StringUtil.isNullOrEmptyString(password)) {
			password = userName;
		}
	}
	
	/**
	 * 
	 */
	protected void execute() throws Exception {
		long confirmationCode = userManager.createConfirmationCode();
		User user = new User(userName, password, "info@wahlzeit.org", confirmationCode, photoManager);
		userManager.addUser(user);
		
		File photoDirFile = new File(photoDir);
		FileFilter photoFileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".jpg");
			}
		};

		File[] photoFiles = photoDirFile.listFiles(photoFileFilter);
		for (int i = 0; i < photoFiles.length; i++) {
			Photo newPhoto = photoManager.createPhoto(photoFiles[i]);
			user.addPhoto(newPhoto);
		}
	}
	
}
