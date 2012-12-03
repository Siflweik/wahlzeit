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

package org.wahlzeit.services;

import java.sql.*;
import java.util.*;

/**
 * An ObjectManager creates/reads/updates/deletes Persistent (objects) from a (relational) Database.
 * It is an abstract superclass that relies an inheritance interface and the Persistent interface.
 * Subclasses for specific types of object need to implement createObject and provide Statements.
 * 
 * @author dirkriehle
 *
 */
public abstract class ObjectManager {
	
	/**
	 * 
	 */
	public DatabaseConnection getDatabaseConnection() throws SQLException {
		DatabaseConnection conn = ContextManager.getDatabaseConnection();
		
		assertIsValidConnection(conn);
		
		return conn;
	}
	    
	protected void assertIsValidConnection(DatabaseConnection conn)	{
		assertIsNotNull(conn);
	}
	
	protected void assertIsValidString(String str)	{
		assert (str != null && !str.trim().equals(""));
	}
	
	protected void assertIsValidPreparedStatement(PreparedStatement stmt)	{
		assertIsNotNull(stmt);
	}
	
	protected void assertIsValidPersistent(Persistent result)	{
		assertIsNotNull(result);
	}
	
	protected void assertIsValidID(int value)	{
		assert (value > -1);
	}
	
	protected static void assertIsNotNull(Object instance)	{
		assert (instance != null);
	}
	
	/**
	 * 
	 */
	protected PreparedStatement getReadingStatement(String stmt) throws SQLException {
		assertIsValidString(stmt);
		
    	DatabaseConnection dbc = getDatabaseConnection();
    	PreparedStatement prep = dbc.getReadingStatement(stmt);
    	
    	assertIsValidPreparedStatement(prep);
    	
    	return prep;
	}
	
	/**
	 * 
	 */
	protected PreparedStatement getUpdatingStatement(String stmt) throws SQLException {
		assertIsValidString(stmt);
		
    	DatabaseConnection dbc = getDatabaseConnection();
    	PreparedStatement prep = dbc.getUpdatingStatement(stmt);
    	
    	assertIsValidPreparedStatement(prep);
    	
    	return prep;
	}
	
	/**
	 * 
	 */
	protected Persistent readObject(PreparedStatement stmt, int value) throws SQLException {
		assertIsValidPreparedStatement(stmt);
		assertIsValidID(value);
		
		Persistent result = null;
		stmt.setInt(1, value);
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		if (rset.next()) {
			result = createObject(rset);
		}
		
		assertIsValidPersistent(result);

		return result;
	}
	
	/**
	 * 
	 */
	protected Persistent readObject(PreparedStatement stmt, String value) throws SQLException {
		assertIsValidPreparedStatement(stmt);
		assertIsValidString(value);
		
		Persistent result = null;
		stmt.setString(1, value);
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		if (rset.next()) {
			result = createObject(rset);
		}

		assertIsValidPersistent(result);
		
		return result;
	}
	
	/**
	 * 
	 */
	protected void readObjects(Collection result, PreparedStatement stmt) throws SQLException {
		assertIsNotNull(result);
		assertIsValidPreparedStatement(stmt);
		
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		while (rset.next()) {
			Persistent obj = createObject(rset);
			result.add(obj);
		}
	}
		
	/**
	 * 
	 */
	protected void readObjects(Collection result, PreparedStatement stmt, String value) throws SQLException {
		assertIsNotNull(result);
		assertIsValidPreparedStatement(stmt);
		assertIsValidString(value);
		
		stmt.setString(1, value);
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		while (rset.next()) {
			Persistent obj = createObject(rset);
			result.add(obj);
		}
	}
		
	/**
	 * 
	 */
	protected abstract Persistent createObject(ResultSet rset) throws SQLException;

	/**
	 * 
	 */
	protected void createObject(Persistent obj, PreparedStatement stmt, int value) throws SQLException {
		assertIsValidPersistent(obj);
		assertIsValidPreparedStatement(stmt);
		assertIsValidID(value);
		
		stmt.setInt(1, value);
		SysLog.logQuery(stmt);
		stmt.executeUpdate();
	}
	
	/**
	 * 
	 */
	protected void createObject(Persistent obj, PreparedStatement stmt, String value) throws SQLException {
		assertIsValidPersistent(obj);
		assertIsValidPreparedStatement(stmt);
		assertIsValidString(value);
		
		stmt.setString(1, value);
		SysLog.logQuery(stmt);
		stmt.executeUpdate();
	}
	
	/**
	 * 
	 */
	protected void updateObject(Persistent obj, PreparedStatement stmt) throws SQLException {
		assertIsValidPersistent(obj);
		assertIsValidPreparedStatement(stmt);
		
		if (obj.isDirty()) {
			obj.writeId(stmt, 1);
			SysLog.logQuery(stmt);
			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				obj.writeOn(rset);
				rset.updateRow();
				updateDependents(obj);
				obj.resetWriteCount();
			} else {
				SysLog.logError("trying to update non-existent object: " + obj.getIdAsString() + "(" + obj.toString() + ")");
			}
		}
	}
	
	/**
	 * 
	 */
	protected void updateObjects(Collection coll, PreparedStatement stmt) throws SQLException {
		assertIsNotNull(coll);
		assertIsValidPreparedStatement(stmt);
		
		for (Iterator i = coll.iterator(); i.hasNext(); ) {
			Persistent obj = (Persistent) i.next();
			updateObject(obj, stmt);
		}
	}
	
	/**
	 * 
	 */
	protected void updateDependents(Persistent obj) throws SQLException {
		assertIsValidPersistent(obj);
		// do nothing
	}
	
	/**
	 * 
	 */
	protected void deleteObject(Persistent obj, PreparedStatement stmt) throws SQLException {
		assertIsValidPersistent(obj);
		assertIsValidPreparedStatement(stmt);
		
		obj.writeId(stmt, 1);
		SysLog.logQuery(stmt);
		stmt.executeUpdate();
	}
}
