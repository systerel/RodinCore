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
		SCTool.SHOW_REMOVE = true;
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
				"CSC remove /P/x.csc"
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
				"CSC run /P/x.csc\n" + 
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
				"CSC run /P/x.csc\n" + 
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/z.ctx\n" + 
				"CSC run /P/y.csc\n" + 
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
				"CSC extract /P/y.ctx\n" + 
				"CSC extract /P/z.ctx\n" + 
				"CSC run /P/z.csc"
		);
	}
	/**
	 * Test that the test case for database problems can work correctly
	 */
	public void testRodinDBProblem() throws Exception {
		
		try {
			CSCTool.FAULTY = true;

			IRodinFile ctx = createRodinFile("P/x.ctx");
			createData(ctx, "one");
			ctx.save(null, true);

			IRodinFile cty = createRodinFile("P/y.ctx");
			createDependency(cty, "x");
			createData(cty, "two");
			cty.save(null, true);		
			runBuilder(
					"CSC extract /P/y.ctx\n" + 
					"CSC extract /P/x.ctx\n" + 
					"CSC run /P/x.csc"
			);
		} finally {
			CSCTool.FAULTY = false;
		}
	}

	
	
	/**
	 * Proper treatment of database errors while tools are run
	 */
	public void testRodinDBProblemInTool() throws Exception {
		final IRodinFile ctx;
		try {
			CSCTool.FAULTY = true;
		
			ctx = createRodinFile("P/x.ctx");
			createData(ctx, "one");
			ctx.save(null, true);

			IRodinFile cty = createRodinFile("P/y.ctx");
			createDependency(cty, "x");
			createData(cty, "two");
			cty.save(null, true);		
			runBuilder(null);
			ToolTrace.flush();
		} finally {
			CSCTool.FAULTY = false;
		}

		createData(ctx, "three");
		ctx.save(null, true);
		runBuilder(null);
		
		runBuilder(
				"CSC extract /P/x.ctx\n" + 
				"CSC run /P/x.csc\n" + 
				"CSC run /P/y.csc"
		);
	}
		
}
