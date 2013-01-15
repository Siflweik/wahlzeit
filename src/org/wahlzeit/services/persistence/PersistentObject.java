package org.wahlzeit.services.persistence;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.wahlzeit.services.Persistent;

public abstract class PersistentObject implements Persistent {
	private long writeCount;
	private HashMap<String, Serializer> serializers;
	
	protected PersistentObject()	{
		serializers = new HashMap<String, Serializer>();
		
		resetWriteCount();
	}
		
	protected Persistent getPersistent()	{
		return this;
	}
	
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
	//TODO: is there a solution with templates? Type erasure is a b*tch
	
	@Override
	public synchronized void readFrom(ResultSet rset) throws SQLException {
		Persistent object = getPersistent();
		Field[] fields = object.getClass().getFields();
		
		for (Field field : fields) {
			PersistentField attr = field.getAnnotation(PersistentField.class);

			if (attr != null)	{
				try {
					Serializer serializer = getSerializerForField(field, attr.serializerClass());
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
					Serializer serializer = getSerializerForField(field, attr.serializerClass());
					serializer.writeOn(rset, attr.columnName(), field.get(object));
				} catch (Exception e) {
				}
			}
		}
	}
	
	protected synchronized Serializer getSerializerForField(Field field, Class<? extends Serializer> serializerClass)	{
		Serializer serializer = createSerializer(serializerClass);		

		// Add decorator, if necessary
		PersistentConvertableField attr = field.getAnnotation(PersistentConvertableField.class);		
		if (attr != null)	{
			Converter converter = (Converter)createSerializer(attr.converterClass());
			converter.setSerializer(serializer);
			serializer = converter;
		}
		
		return serializer;
	}
	
	protected synchronized Serializer createSerializer(Class<? extends Serializer> serializerClass)	{
		String key = serializerClass.getName();
		
		if (!serializers.containsKey(key))	{
			try {
				serializers.put(key, serializerClass.newInstance());
			} catch (Exception e) {
				//TODO error handling, otherwise there will be a NPE upon access
			}
		}
		
		return serializers.get(key);
	}
}
