package org.rodinp.core.tests.builder;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author lvoisin
 *
 */
public class CBuilderTest extends AbstractBuilderTest {
	
	private IRodinProject project;
	
	public CBuilderTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = true;
		SCTool.SHOW_EXTRACT = true;
		SCTool.SHOW_RUN = true;
		project = createRodinProject("P");
		ToolTrace.flush();
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
				"CSC extract /P/x.ctx\n" + 
				"CSC extract /P/x.ctx\n" +
				"CSC run /P/x.csc"
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
		ToolTrace.flush();

		ctx.delete(true, null);
		runBuilder(
				"CSC clean /P/x.csc"
		);
	}
	
	/**
	 * Ensures dependency is followed if source of dependency is created before target 
	 */
	public void testOneTwoCreate() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
//		ToolTrace.flush();
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder(null);
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		runBuilder(
				"CSC extract /P/x.ctx\n" + 
				"CSC extract /P/x.ctx\n" + 
				"CSC run /P/x.csc\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC run /P/y.csc"
		);
	}

	/**
	 * Ensures dependency is followed if target of dependency is created before source 
	 */
	public void testTwoOneCreate() throws Exception {
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		runBuilder(null);

		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		
		runBuilder(
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/x.ctx\n" + 
				"CSC extract /P/x.ctx\n" + 
				"CSC run /P/x.csc\n" + 
				"CSC run /P/y.csc"
		);
	}
	
	/**
	 * Ensures dependency is followed transitively
	 */
	public void testOneTwoThreeCreateChange() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder(null);
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		
		IRodinFile ctz = createRodinFile("P/z.ctx");
		createDependency(ctz, "y");
		createData(ctz, "three");
		ctz.save(null, true);
	
		runBuilder(
				"CSC extract /P/x.ctx\n" + 
				"CSC extract /P/x.ctx\n" + 
				"CSC run /P/x.csc\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/z.ctx\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC run /P/y.csc\n" + 
				"CSC extract /P/z.ctx\n" + 
				"CSC run /P/z.csc"
		);
	}
	
	/**
	 * Ensures cycles are ignored
	 */
	public void testOneTwoThreeCreateCycle() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createDependency(ctx, "y");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder(null);
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		
		IRodinFile ctz = createRodinFile("P/z.ctx");
		createData(ctz, "three");
		ctz.save(null, true);
	
		runBuilder(
				"CSC extract /P/x.ctx\n" + 
				"CSC extract /P/x.ctx\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/z.ctx\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/z.ctx\n" + 
				"CSC run /P/z.csc"
		);
	}
	
//			TODO enco/**
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
//			System.out.println("Change file two./**
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
