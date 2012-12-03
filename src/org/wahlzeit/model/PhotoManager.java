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

import java.io.*;
import java.sql.*;
import java.util.*;

import org.wahlzeit.main.*;
import org.wahlzeit.services.*;

/**
 * A photo manager provides access to and manages photos.
 * 
 * @author dirkriehle
 * 
 */
public class PhotoManager extends ObjectManager {

	/**
	 * In-memory cache for photos
	 */
	protected Map<PhotoId, Photo> photoCache = new HashMap<PhotoId, Photo>();
	
	/**
	 * 
	 */
	protected PhotoTagCollector photoTagCollector = null;
	
	protected ModelMain modelMain;
	
	/**
	 * 
	 */
	public PhotoManager(ModelMain modelMain) {
		assertIsNotNull(modelMain);
		
		photoTagCollector = PhotoFactory.getInstance().createPhotoTagCollector();
		
		assertIsNotNull(photoTagCollector);
		
		this.modelMain = modelMain;
	}

	/**
	 * 
	 */
	public final boolean hasPhoto(String id) {
		assertIsValidStringID(id);
		
		return hasPhoto(PhotoId.getId(id));
	}
	
	protected void assertIsValidStringID(String id)	{
		assertIsValidString(id);
		assertIsValidID(Integer.parseInt(id));
	}
	
	protected void assertIsValidPhotoID(PhotoId id)	{
		assertIsNotNull(id);
		assertIsValidID(id.intValue);
	}
		
	protected void assertIsValidPhoto(Photo photo)	{
		assertIsNotNull(photo);
		assertIsValidPhotoID(photo.getId());
	}
		
	protected void assertIsValidPhotoFilter(PhotoFilter filter)	{
		assertIsNotNull(filter);
	}
	
	/**
	 * 
	 */
	public final boolean hasPhoto(PhotoId id) {
		assertIsValidPhotoID(id);
		
		return getPhotoFromId(id) != null;
	}
	
	/**
	 * 
	 */
	public final Photo getPhoto(String id) {
		assertIsValidStringID(id);
		
		return getPhotoFromId(PhotoId.getId(id));
	}
	
	/**
	 * @methodtype boolean-query
	 * @methodproperties primitive
	 */
	protected boolean doHasPhoto(PhotoId id) {
		assertIsValidPhotoID(id);
		
		return photoCache.containsKey(id);
	}
	
	/**
	 * 
	 */
	public Photo getPhotoFromId(PhotoId id) {
		assertIsValidPhotoID(id);
		
		if (id.isNullId()) {
			return null;
		}

		Photo result = doGetPhotoFromId(id);
		
		if (result == null) {
			try {
				result = (Photo) readObject(getReadingStatement("SELECT * FROM photos WHERE id = ?"), id.asInt());
			} catch (SQLException sex) {
				SysLog.logThrowable(sex);
			}
			if (result != null) {
				doAddPhoto(result);
			}
		}
		
		// Photo may be null?
		//assertIsValidPhoto(result);
		
		return result;
	}
		
	/**
	 * @methodtype get
	 * @methodproperties primitive
	 */
	protected Photo doGetPhotoFromId(PhotoId id) {
		assertIsValidPhotoID(id);
		
		Photo photo = photoCache.get(id);
		
		assertIsValidPhoto(photo);
		
		return photo;
	}
	
	/**
	 * 
	 */
	protected Photo createObject(ResultSet rset) throws SQLException {
		assertIsNotNull(rset);
		
		Photo photo = PhotoFactory.getInstance().createPhoto(rset);
		
		assertIsValidPhoto(photo);
		
		return photo;
	}
	
	/**
	 * 
	 */
	public void addPhoto(Photo photo) {
		assertIsValidPhoto(photo);
		
		PhotoId id = photo.getId();
		assertIsNewPhoto(id);
		doAddPhoto(photo);

		try {
			createObject(photo, getReadingStatement("INSERT INTO photos(id) VALUES(?)"), id.asInt());
			saveGlobals();
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * @methodtype command
	 * @methodproperties primitive
	 */
	protected void doAddPhoto(Photo myPhoto) {
		assertIsValidPhoto(myPhoto);
		
		photoCache.put(myPhoto.getId(), myPhoto);
	}

	/**
	 * 
	 */
	public void loadPhotos(Collection<Photo> result) {
		assertIsNotNull(result);
		assert (result.size() > 0);
		
		try {
			readObjects(result, getReadingStatement("SELECT * FROM photos"));
			for (Iterator<Photo> i = result.iterator(); i.hasNext(); ) {
				Photo photo = i.next();
				
				assertIsValidPhoto(photo);
				
				if (!doHasPhoto(photo.getId())) {
					doAddPhoto(photo);
				} else {
					SysLog.logValueWithInfo("photo", photo.getId().asString(), "photo had already been loaded");
				}
			}
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		SysLog.logInfo("loaded all photos");
	}

	/**
	 * 
	 */
	public void savePhoto(Photo photo) {
		assertIsValidPhoto(photo);
		
		try {
			updateObject(photo, getUpdatingStatement("SELECT * FROM photos WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public void savePhotos() {
		try {
			updateObjects(photoCache.values(), getUpdatingStatement("SELECT * FROM photos WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public Set<Photo> findPhotosByOwner(String ownerName) {
		assertIsValidString(ownerName);
		
		Set<Photo> result = new HashSet<Photo>();
		try {
			readObjects(result, getReadingStatement("SELECT * FROM photos WHERE owner_name = ?"), ownerName);
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		for (Iterator<Photo> i = result.iterator(); i.hasNext(); ) {
			doAddPhoto(i.next());
		}

		return result;
	}
	
	/**
	 * 
	 */
	public Photo getVisiblePhoto(PhotoFilter filter) {
		assertIsValidPhotoFilter(filter);
		
		Photo result = getPhotoFromFilter(filter);
		
		if(result == null) {
			java.util.List<PhotoId> list = getFilteredPhotoIds(filter);
			filter.setDisplayablePhotoIds(list);
			result = getPhotoFromFilter(filter);
		}

		assertIsValidPhoto(result);
		
		return result;
	}
	
	/**
	 * 
	 */
	protected Photo getPhotoFromFilter(PhotoFilter filter) {
		assertIsValidPhotoFilter(filter);
		
		PhotoId id = filter.getRandomDisplayablePhotoId();
		Photo result = getPhotoFromId(id);
		while((result != null) && !result.isVisible()) {
			id = filter.getRandomDisplayablePhotoId();
			result = getPhotoFromId(id);
			if ((result != null) && !result.isVisible()) {
				filter.addProcessedPhoto(result);
			}
		}
		
		assertIsValidPhoto(result);
		
		return result;
	}
	
	/**
	 * 
	 */
	protected java.util.List<PhotoId> getFilteredPhotoIds(PhotoFilter filter) {
		assertIsValidPhotoFilter(filter);
		
		java.util.List<PhotoId> result = new LinkedList<PhotoId>();

		try {
			java.util.List<String> filterConditions = filter.getFilterConditions();

			int noFilterConditions = filterConditions.size();
			PreparedStatement stmt = getUpdatingStatementFromConditions(noFilterConditions);
			for (int i = 0; i < noFilterConditions; i++) {
				stmt.setString(i + 1, filterConditions.get(i));
			}
			
			SysLog.logQuery(stmt);
			ResultSet rset = stmt.executeQuery();

			if (noFilterConditions == 0) {
				noFilterConditions++;
			}

			int[] ids = new int[PhotoId.getValue() + 1];
			while(rset.next()) {
				int id = rset.getInt("photo_id");
				if (++ids[id] == noFilterConditions) {
					PhotoId photoId = PhotoId.getId(id);
					if (!filter.isProcessedPhotoId(photoId)) {
						result.add(photoId);
						
						assertIsValidPhotoID(photoId);
					}
				}
			}
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		assertIsNotNull(result);
		
		return result;
	}
		
	/**
	 * 
	 */
	protected PreparedStatement getUpdatingStatementFromConditions(int no) throws SQLException {
		assert (no > -1);
		
		String query = "SELECT * FROM tags";
		if (no > 0) {
			query += " WHERE";
		}

		for (int i = 0; i < no; i++) {
			if (i > 0) {
				query += " OR";
			}
			query += " (tag = ?)";
		}
		
		return getUpdatingStatement(query);
	}
	
	/**
	 * 
	 */
	protected void updateDependents(Persistent obj) throws SQLException {
		assertIsValidPersistent(obj);
		
		Photo photo = (Photo) obj;
		
		assertIsValidPhoto(photo);
		
		deleteObject(obj, getReadingStatement("DELETE FROM tags WHERE photo_id = ?"));
		
		PreparedStatement stmt = getReadingStatement("INSERT INTO tags VALUES(?, ?)");
		
		Set<String> tags = new HashSet<String>();
		photoTagCollector.collect(tags, photo);
		for (Iterator<String> i = tags.iterator(); i.hasNext(); ) {
			String tag = i.next();
			stmt.setString(1, tag);
			stmt.setInt(2, photo.getId().asInt());
			SysLog.logQuery(stmt);
			stmt.executeUpdate();					
		}
	}
		
	/**
	 * 
	 */
	public Photo createPhoto(File file) throws Exception {
		assertIsNotNull(file);
		assert (file.exists());
		
		PhotoId id = PhotoId.getNextId();
		Photo result = PhotoUtil.createPhoto(file, id);
		addPhoto(result);
		
		assertIsValidPhoto(result);
		
		return result;
	}
	
	/**
	 * @methodtype assertion
	 */
	protected void assertIsNewPhoto(PhotoId id) {
		assertIsValidPhotoID(id);
		
		if (hasPhoto(id)) {
			throw new IllegalStateException("Photo already exists!");
		}
	}

	protected void saveGlobals() throws SQLException	{
		if (modelMain != null)	{
			modelMain.saveGlobals();
		}
	}
	
	public void setModelMain(ModelMain modelMain)	{
		assertIsNotNull(modelMain);
		
		this.modelMain = modelMain;
	}
}
