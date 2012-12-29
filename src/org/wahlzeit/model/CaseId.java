package org.wahlzeit.model;

import java.util.HashMap;

public class CaseId {
	public static final CaseId NULL_ID = new CaseId(-1);
	
	private static int max = 0;
	private static HashMap<Integer, CaseId> cache = new HashMap<Integer, CaseId>();
	
	public static synchronized CaseId getNextId()	{
		++max;
		
		CaseId next = new CaseId(max);
		cache.put(max, next);
			
		return next;
	}
	
	public static synchronized CaseId getId(int id)	{
		if (id < 0 || id > max)	{
			return NULL_ID;
		}
		
		if (!cache.containsKey(id))	{
			cache.put(id, new CaseId(id));
		}
		
		return cache.get(id);
	}
	
	public static synchronized void setValue(int value)	{
		max = value;
	}
	
	public static synchronized int getValue()	{
		return max;
	}
	
	private int id;
	
	protected CaseId(int id)	{
		this.id = id;
	}
	
	public int asInt()	{
		return id;
	}
	
	public String asString()	{
		return "#cid" + id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof CaseId) && (((CaseId)obj).id == this.id);
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public String toString()	{
		return asString();
	}
}
