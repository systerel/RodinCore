//package org.rodinp.internal.core.index.tests;
//
//import org.rodinp.core.IRodinFile;
//import org.rodinp.internal.core.index.Location;
//
//import junit.framework.TestCase;
//
//public class LocationTests extends TestCase {
//
//	private IRodinFile file = null; // TODO assign a non null value
//	private Location loc;
//	private final int defaultLineNumber = 25;
//	private final int defaultStart = 1;
//	private final int defaultEnd = 3;
//	
//	protected void setUp() throws Exception {
//		super.setUp();
//		loc = new Location(file, defaultLineNumber, defaultStart, defaultEnd);
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	
//	public void testConstructor() throws Exception {	
//		assertEquals("Location construction was not correct", file, loc.getFile());
//		assertEquals("Location construction was not correct", 25, loc.getLineNumber());
//		assertEquals("Location construction was not correct", 1, loc.getStart());
//		assertEquals("Location construction was not correct", 3, loc.getEnd());
//	}
//	
////	forget this (client will be responsible for managing overall coherence):
////	/**
////	 * Asserts exception thrown when attempting to create a Location pointing to
////	 * an incorrect place in the IRodinFile.
////	 * 
////	 * @throws Exception
////	 */
////	public void testConstructorIncorrectLocation() throws Exception {
////		
////	}
//	
//	public void testSetFile() throws Exception {
//		IRodinFile f = null; // TODO: assign non null value
//		loc.setFile(f);
//		assertEquals("failure when setting file", f, loc.getFile());
//	}
//
//	public void testSetLineNumber() throws Exception {
//		loc.setLineNumber(314);
//		assertEquals("failure when setting line number", 314, loc.getLineNumber());
//	}
//
//	public void testSetStart() throws Exception {
//		loc.setStart(2);
//		assertEquals("failure when setting start", 2, loc.getStart());
//	}
//
//	public void testSetEnd() throws Exception {
//		loc.setEnd(21);
//		assertEquals("failure when setting end", 21, loc.getEnd());
//	}
//
//
//
//}
