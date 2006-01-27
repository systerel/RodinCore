package org.rodinp.core.tests.builder;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author lvoisin
 *
 */
public class BuilderTest extends AbstractBuilderTest {
	
	private IRodinProject project;
	
	public BuilderTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}
	
	protected void tearDown() throws Exception {
		project.getProject().delete(true, true, null);
	}

	private void runBuilder(String expectedTrace) throws CoreException {
		super.runBuilder(project, expectedTrace);
	}
	
	/**
	 * Ensures that extractors and tools are run when a file is created.
	 */
	public void testOneBuild() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder(
				"SC extract /P/x.ctx\n" +
				"SC run /P/x.csc"
		);
		
		IRodinFile scCtx = getRodinFile("P/x.csc");
		assertContents("Invalid contents of checked context",
				"x.csc\n" +
				"  data: one",
				scCtx);
	}
		
	/**
	 * Ensures that generated files are cleaned up when there source is deleted.
	 */
	public void testOneDelete() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder(null);

		ctx.delete(true, null);
		runBuilder(
				"SC clean /P/x.csc"
		);
	}
		
//			TODO encode tests below with the database. 
//			
//			IRodinElement[] tops = obj.getChildren();
//			assertEquals("Checked context should contain one element.")
//			
//			createFile("one.ctx", oneContents);
//			String contents = getContents("one.csc");
//			
//			assertEquals("CONTEXT one LOCAL one GLOBAL one", contents);
//		}
//		if (DEBUG)
//			System.out.println("Create file two.ctx");
//		{
//			String twoContents =
//				"CONTEXT two " +
//				"REFINES one " +
//				"LOCAL two " +
//				"GLOBAL two";
//			
//			
//			USE ELEMENT TYPES + MODIFY TOOL MANAGER + GRAPH MANAGER.
//			
//			
//			
//			createFile("two.ctx", twoContents);
//			String contents = getContents("two.csc");
//			
//			assertEquals("CONTEXT two REFINES one LOCAL two GLOBAL two one", contents);
//		}
//		if (DEBUG)
//			System.out.println("Create file three.ctx");
//		{
//			String threeContents =
//				"CONTEXT three " +
//				"REFINES one " +
//				"LOCAL three " +
//				"GLOBAL three";
//			
//			createFile("three.ctx", threeContents);
//			String contents = getContents("three.csc");
//			
//			assertEquals("CONTEXT three REFINES one LOCAL three GLOBAL three one", contents);
//		}
//		if (DEBUG)
//			System.out.println("Change file two.ctx");
//		{
//			String twoContents =
//				"CONTEXT two " +
//				"REFINES three " +
//				"LOCAL two " +
//				"GLOBAL two";
//			
//			changeFile("two.ctx", twoContents);
//			String contents = getContents("two.csc");
//			
//			assertEquals("CONTEXT two REFINES three one LOCAL two GLOBAL two three one", contents);
//		}
//		if (DEBUG)
//			System.out.println("Change file one.ctx");
//		{
//			
//			String oneContents =
//				"CONTEXT one " +
//				"LOCAL one " +
//				"GLOBAL one new";
//			
//			changeFile("one.ctx", oneContents);
//			
//			String contentsOne = getContents("one.csc");
//			String contentsTwo = getContents("two.csc");
//			String contentsThree = getContents("three.csc");
//			
//			assertEquals("CONTEXT one LOCAL one GLOBAL one new", contentsOne);
//			assertEquals("CONTEXT two REFINES three one LOCAL two GLOBAL two three one new", contentsTwo);
//			assertEquals("CONTEXT three REFINES one LOCAL three GLOBAL three one new", contentsThree);
//		}
	
}
