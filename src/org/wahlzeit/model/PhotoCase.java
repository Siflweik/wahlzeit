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

import java.sql.*;

import org.wahlzeit.services.persistence.PersistentField;
import org.wahlzeit.services.persistence.serializers.CaseIdSerializer;
import org.wahlzeit.services.persistence.serializers.FlagReasonSerializer;
import org.wahlzeit.services.persistence.serializers.PhotoSerializer;
import org.wahlzeit.services.persistence.serializers.basic.BooleanSerializer;
import org.wahlzeit.services.persistence.serializers.basic.LongSerializer;
import org.wahlzeit.services.persistence.serializers.basic.StringSerializer;


/**
 * A photo case is a case where someone flagged a photo as inappropriate.
 * 
 * @author dirkriehle
 *
 */
public class PhotoCase extends Case {
	
	/**
	 * 
	 */
	public static final String FLAGGER = "flagger";
	public static final String REASON = "reason";
	public static final String EXPLANATION = "explanation";
	public static final String CREATED_ON = "createdOn";
	public static final String WAS_DECIDED = "wasDecided";
	public static final String DECIDED_ON = "decidedOn";

	/**
	 * 
	 */
	protected int applicationId = 0; // application id (unused on Java level)
	
	@PersistentField(columnName = "id", serializerClass = CaseIdSerializer.class)
	protected CaseId id = CaseId.NULL_ID; // case id
	
	@PersistentField(columnName = "photo", serializerClass = PhotoSerializer.class)
	protected Photo photo = null; // photo id -> photo
	
	@PersistentField(columnName = "flagger", serializerClass = StringSerializer.class)
	protected String flagger = "unknown";
	
	@PersistentField(columnName = "reason", serializerClass = FlagReasonSerializer.class)
	protected FlagReason reason = FlagReason.OTHER;
	
	@PersistentField(columnName = "explanation", serializerClass = StringSerializer.class)
	protected String explanation = "none";	
	
	@PersistentField(columnName = "creation_time", serializerClass = LongSerializer.class)
	protected long createdOn = System.currentTimeMillis();
	
	@PersistentField(columnName = "was_decided", serializerClass = BooleanSerializer.class)
	protected boolean wasDecided = false;
	
	@PersistentField(columnName = "decision_time", serializerClass = LongSerializer.class)
	protected long decidedOn = 0;
	
	/**
	 * 
	 */
	public PhotoCase(Photo myPhoto) {
		id = getNextCaseId();
		photo = myPhoto;
		
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public PhotoCase(ResultSet rset) throws SQLException {
		readFrom(rset);
	}
	
	/**
	 * 
	 */
	public String getIdAsString() {
		return String.valueOf(id);
	}
	
	/**
	 * 
	 */
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		stmt.setInt(pos, id.asInt());
	}
	
	/**
	 * 
	 */
	public CaseId getId() {
		return id;
	}
	
	/**
	 * 
	 */
	public Photo getPhoto() {
		return photo;
	}
	
	/**
	 * 
	 */
	public long getCreationTime() {
		return createdOn;
	}

	/**
	 * 
	 */
	public String getFlagger() {
		return flagger;
	}
	
	/**
	 * 
=	 */
	public void setFlagger(String newFlagger) {
		flagger = newFlagger;
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public FlagReason getReason() {
		return reason;
	}
	
	/**
	 * 
=	 */
	public void setReason(FlagReason newReason) {
		reason = newReason;
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public String getExplanation() {
		return explanation;
	}
	
	/**
	 * 
=	 */
	public void setExplanation(String newExplanation) {
		explanation = newExplanation;
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public boolean wasDecided() {
		return wasDecided;
	}
	
	/**
	 * 
	 */
	public void setDecided() {
		wasDecided = true;
		decidedOn = System.currentTimeMillis();
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public long getDecisionTime() {
		return decidedOn;
	}

	/**
	 * 
	 */
	public String getPhotoOwnerName() {
		return photo.getOwnerName();
	}
	
	/**
	 * 
	 */
	public PhotoStatus getPhotoStatus() {
		return photo.getStatus();
	}
	
}
