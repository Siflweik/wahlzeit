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

import org.wahlzeit.model.ReadWriteException;

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
	public DatabaseConnection getDatabaseConnection() throws ReadWriteException {
		return ContextManager.getDatabaseConnection();
	}
	    
	/**
	 * 
	 */
	protected PreparedStatement getReadingStatement(String stmt) throws ReadWriteException {
    	DatabaseConnection dbc = getDatabaseConnection();
    	try {
			return dbc.getReadingStatement(stmt);
		} catch (SQLException e) {
			throw new ReadWriteException(e);
		}
	}
	
	/**
	 * 
	 */
	protected PreparedStatement getUpdatingStatement(String stmt) throws ReadWriteException {
    	DatabaseConnection dbc = getDatabaseConnection();
    	try {
			return dbc.getUpdatingStatement(stmt);
		} catch (SQLException e) {
			throw new ReadWriteException(e);
		}
	}
	
	/**
	 * 
	 */
	protected Persistent readObject(PreparedStatement stmt, int value) throws ReadWriteException {
		Persistent result = null;
		
		try	{
    		stmt.setInt(1, value);
    		SysLog.logQuery(stmt);
    		ResultSet rset = stmt.executeQuery();
    		
    		if (rset.next()) {
    			result = createObject(rset);
    		}
		} catch (SQLException e) {
			throw new ReadWriteException(e);
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	protected Persistent readObject(PreparedStatement stmt, String value) throws ReadWriteException {
   		Persistent result = null;
   		
		try	{
    		stmt.setString(1, value);
    		SysLog.logQuery(stmt);
    		ResultSet rset = stmt.executeQuery();
    		if (rset.next()) {
    			result = createObject(rset);
    		}
		} catch (SQLException ex)	{
			throw new ReadWriteException(ex);
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	protected void readObjects(Collection result, PreparedStatement stmt) throws ReadWriteException {
    	try	{
    		SysLog.logQuery(stmt);
    		ResultSet rset = stmt.executeQuery();
    		while (rset.next()) {
    			Persistent obj = createObject(rset);
    			result.add(obj);
    		}
    	} catch (SQLException ex)	{
    		throw new ReadWriteException(ex);
    	}
	}
		
	/**
	 * 
	 */
	protected void readObjects(Collection result, PreparedStatement stmt, String value) throws ReadWriteException {
		try	{
			stmt.setString(1, value);
    		SysLog.logQuery(stmt);
    		ResultSet rset = stmt.executeQuery();
    		while (rset.next()) {
    			Persistent obj = createObject(rset);
    			result.add(obj);
    		}
    	} catch (SQLException ex)	{
    		throw new ReadWriteException(ex);
    	}
	}
		
	/**
	 * 
	 */
	protected abstract Persistent createObject(ResultSet rset) throws ReadWriteException;

	/**
	 * 
	 */
	protected void createObject(Persistent obj, PreparedStatement stmt, int value) throws ReadWriteException {
		try	{
			stmt.setInt(1, value);
			SysLog.logQuery(stmt);
			stmt.executeUpdate();
		} catch (SQLException ex)	{
    		throw new ReadWriteException(ex);
    	}
	}
	
	/**
	 * 
	 */
	protected void createObject(Persistent obj, PreparedStatement stmt, String value) throws ReadWriteException {
		try	{
			stmt.setString(1, value);
    		SysLog.logQuery(stmt);
    		stmt.executeUpdate();
    	} catch (SQLException e) {
    		throw new ReadWriteException(e);
    	}
	}
	
	/**
	 * 
	 */
	protected void updateObject(Persistent obj, PreparedStatement stmt) throws ReadWriteException {
		try	{
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
    	} catch (SQLException e) {
    		throw new ReadWriteException(e);
    	}
	}
	
	/**
	 * 
	 */
	protected void updateObjects(Collection coll, PreparedStatement stmt) throws ReadWriteException {
		for (Iterator i = coll.iterator(); i.hasNext(); ) {
			Persistent obj = (Persistent) i.next();
			updateObject(obj, stmt);
		}
	}
	
	/**
	 * 
	 */
	protected void updateDependents(Persistent obj) throws ReadWriteException {
		// do nothing
	}
	
	/**
	 * 
	 */
	protected void deleteObject(Persistent obj, PreparedStatement stmt) throws ReadWriteException {
		try {
			obj.writeId(stmt, 1);
			SysLog.logQuery(stmt);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new ReadWriteException(e);
		}
	}

	/**
	 * 
	 */
	protected void assertIsNonNullArgument(Object arg) {
		assertIsNonNullArgument(arg, "anonymous");
	}
	
	/**
	 * 
	 */
	protected void assertIsNonNullArgument(Object arg, String label) {
		if (arg == null) {
			throw new IllegalArgumentException(label + " should not be null");
		}
	}
	
	protected void handleReadWriteException(ReadWriteException ex) throws ReadWriteException	{
		SysLog.logThrowable(ex);
		throw ex;
	}
	
	protected void handleSQLException(SQLException ex) throws ReadWriteException	{
		handleReadWriteException(new ReadWriteException(ex));
	}
}
