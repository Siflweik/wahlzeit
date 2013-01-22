package org.wahlzeit.services.persistence;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.wahlzeit.services.Persistent;

// This class enables persistent storage of its members via reflection
public abstract class PersistentObject implements Persistent {
	private long writeCount;
	private HashMap<String, Serializer<?, ?>> serializers;
	
	protected PersistentObject()	{
		serializers = new HashMap<String, Serializer<?, ?>>();
		
		resetWriteCount();
	}
		
	protected Persistent getPersistent()	{
		return this;
	}
	
	public static final String ID = "id";
	
	@Override
	public synchronized boolean isDirty() {
		return (writeCount > 0);
	}

	@Override
	public synchronized void incWriteCount() {
		++writeCount;
	}

	@Override
	public synchronized void resetWriteCount() {
		writeCount = 0;	
	}

	//TODO: implement visitor pattern to reduce code duplication? Java definitely needs delegates :|
	
	@Override
	public synchronized void readFrom(ResultSet rset) throws SQLException {
		Persistent object = getPersistent();
		Field[] fields = object.getClass().getFields();
		
		for (Field field : fields) {
			PersistentField attr = field.getAnnotation(PersistentField.class);

			if (attr != null)	{
				try {
					Serializer<Object, ?> serializer = getSerializerForField(field, attr.serializerClass());
					field.set(object, serializer.readFrom(rset, attr.columnName()));
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public synchronized void writeOn(ResultSet rset) throws SQLException {
		Persistent object = getPersistent();
		Field[] fields = object.getClass().getFields();
		
		for (Field field : fields) {
			PersistentField attr = field.getAnnotation(PersistentField.class);

			if (attr != null)	{
				try {
					Serializer<Object, ?> serializer = getSerializerForField(field, attr.serializerClass());
					serializer.writeOn(rset, attr.columnName(), field.get(object));
				} catch (Exception e) {
				}
			}
		}
	}
	
	// I hate generics in java: type erasure is a b*tch - or maybe i'm just stupid?
	@SuppressWarnings("unchecked")
	protected synchronized Serializer<Object, Object> getSerializerForField(Field field, Class<? extends Serializer<?, ?>> serializerClass)	{
		String key = serializerClass.getName();
		
		Serializer<Object, Object> serializer = null;
		
		if (serializers.containsKey(key))	{
			serializer = (Serializer<Object, Object>)serializers.get(key);
		} else	{
			try {
				serializer = (Serializer<Object, Object>)serializerClass.newInstance();
				serializer.setOwner(this);
				
				serializers.put(key, serializer);
			} catch (Exception e) {
				//TODO error handling, otherwise there will be a NPE upon access
			}
		}
		
		return serializer;
	}
}
