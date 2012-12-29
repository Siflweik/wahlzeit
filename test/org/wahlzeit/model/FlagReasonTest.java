package org.wahlzeit.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class FlagReasonTest extends TestCase {

	private Map<FlagReason, TestEntry> entries;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		entries = new HashMap<FlagReason, TestEntry>();
		
		entries.put(FlagReason.MISMATCH, new TestEntry("mismatch", 0));
		entries.put(FlagReason.OFFENSIVE, new TestEntry("offensive", 1));
		entries.put(FlagReason.COPYRIGHT, new TestEntry("copyright", 2));
		entries.put(FlagReason.OTHER, new TestEntry("other", 3));
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FlagReasonTest.class);
	}
	
	protected void expectException(Class<? extends Exception> expected, Exception actual)	{
		assertTrue(actual != null && actual.getClass().equals(expected));
	}
	
	protected void failGetFromInt(int i)	{
		Exception ex = null;
		
		try	{
			FlagReason.getFromInt(i);
		} catch (Exception caught)	{
			ex = caught;
		}
		
		expectException(IllegalArgumentException.class, ex);
	}
	
	protected void failGetFromString(String str)	{
		Exception ex = null;
		
		try	{
			FlagReason.getFromString(str);
		} catch (Exception caught)	{
			ex = caught;
		}
		
		expectException(IllegalArgumentException.class, ex);
	}
	
	public void testInvalidGetFromInt()	{
		failGetFromInt(-1);
		failGetFromInt(FlagReason.MAX_VALUE + 1);
	}
	
	public void testInvalidGetFromString()	{
		failGetFromString(null);
		failGetFromString("");
		failGetFromString("some_random_string");
		failGetFromString("MiSmAtCh");
	}
	
	public void testGetFromInt()	{
		for (Map.Entry<FlagReason, TestEntry> entry : entries.entrySet()) {
			assertEquals(entry.getKey(), FlagReason.getFromInt(entry.getValue().getInt()));
		}
	}

	public void testGetFromString()	{
		for (Map.Entry<FlagReason, TestEntry> entry : entries.entrySet()) {
			assertEquals(entry.getKey(), FlagReason.getFromString(entry.getValue().getString()));
		}
	}
	
	public void testAsInt()	{
		for (Map.Entry<FlagReason, TestEntry> entry : entries.entrySet()) {
			assertEquals(entry.getValue().getInt(), entry.getKey().asInt());
		}
	}

	public void testAsString()	{
		for (Map.Entry<FlagReason, TestEntry> entry : entries.entrySet()) {
			assertEquals(entry.getValue().getString(), entry.getKey().asString());
		}
	}
	
	public void testGetValues()	{
		List<FlagReason> values = Arrays.asList(FlagReason.values());
		
		assertNotNull(values);
		assertEquals(FlagReason.MAX_VALUE + 1, values.size());
		
		for (FlagReason flagReason : entries.keySet()) {
			assertTrue(values.contains(flagReason));
		}
	}
	
	private class TestEntry	{
		private String str;
		private int i;
		
		public TestEntry(String str, int i)	{
			this.str = str;
			this.i = i;
		}
		
		public String getString()	{
			return str;
		}
		
		public int getInt()	{
			return i;
		}
	}
}
