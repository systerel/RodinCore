package org.eventb.core.testpom;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRStatus.Status;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.core.pom.AutoPOM;
import org.eventb.internal.core.pom.PRUtil;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import junit.framework.TestCase;

public class AutoPOMTest extends TestCase {

	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	IRodinProject rodinProject;
	IPOFile poFile;
	IPRFile prFile;
	
	protected void setUp() throws Exception {
		super.setUp();
		RodinCore.create(workspace.getRoot()).open(null);  // TODO temporary kludge
		IProject project = workspace.getRoot().getProject("PROJ");
		project.create(null);
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(description, null);
		rodinProject = RodinCore.create(project);
		rodinProject.open(null);
		
		poFile = (IPOFile) rodinProject.createRodinFile("x.bpo", true, null);
		poFile.open(null);
		POUtil.addTypes(poFile, POUtil.mp("x"), POUtil.mp("ℤ"));
		POUtil.addPredicateSet(poFile, "hyp0", POUtil.mp("1=1","2=2","x∈ℕ"), null);
		POUtil.addSequent(poFile, "PO1", 
				"hyp0", 
				POUtil.mp(), POUtil.mp(), 
				POUtil.mh(
						POUtil.mp(), 
						POUtil.mp()), 
				POUtil.mp("1=1 ∧2=2 ∧x ∈ℕ"));
		POUtil.addSequent(poFile, "PO2", 
				"hyp0", 
				POUtil.mp("y"), POUtil.mp("ℤ"), 
				POUtil.mh(
						POUtil.mp("y∈ℕ"), 
						POUtil.mp()), 
				POUtil.mp("1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ"));
		POUtil.addSequent(poFile, "PO3", 
				"hyp0", 
				POUtil.mp(), POUtil.mp(), 
				POUtil.mh(
						POUtil.mp("3=3"), 
						POUtil.mp()), 
				POUtil.mp("∃x·x=3"));
		POUtil.addSequent(poFile, "PO4", 
				"hyp0", 
				POUtil.mp(), POUtil.mp(), 
				POUtil.mh(
						POUtil.mp("3=3"), 
						POUtil.mp()), 
				POUtil.mp("1=1 ∧2=2 ∧x ∈ℕ∧(∃x·(x=3))"));
		POUtil.addSequent(poFile, "PO5", 
				"hyp0", 
				POUtil.mp("y"), POUtil.mp("ℤ"), 
				POUtil.mh(
						POUtil.mp("y∈ℕ"), 
						POUtil.mp()), 
				POUtil.mp("x ≔y","1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ"));
		POUtil.addSequent(poFile, "PO6", 
				"hyp0", 
				POUtil.mp("y","x'"), POUtil.mp("ℤ","ℤ"), 
				POUtil.mh(
						POUtil.mp("y∈ℕ"), 
						POUtil.mp()), 
				POUtil.mp("x' ≔y","1=1 ∧2=2 ∧x ∈ℕ∧x' ∈ℕ"));
		poFile.save(null, true);
		
		prFile = (IPRFile) rodinProject.createRodinFile("x.bpr", true, null);
		prFile.open(null);
		prFile.save(null, true);
	}

	/*
	 * Test method for 'org.eventb.internal.core.pom.AutoPOM.AutoPOM(IPOFile, IInterrupt, IProgressMonitor)'
	 */
	public final void testAutoPOM() throws CoreException {
		AutoPOM autoPOM = new AutoPOM();
		autoPOM.init(poFile,prFile,null,null);
		autoPOM.writePRFile();
		Map<String, IProverSequent> poPOs = POUtil.readPOs(poFile);
		Map<String, IProverSequent> prPOs = PRUtil.readPOs(prFile);
		
		assertEquals(poPOs.keySet(), prPOs.keySet());
		for (String name : poPOs.keySet()){
			assertTrue(Lib.identical(poPOs.get(name),poPOs.get(name)));
		}
		
		Map<String, Status> prStatus = PRUtil.readStatus(prFile);
		assertEquals(prPOs.keySet(), prStatus.keySet());
		assertFalse(prStatus.values().contains(Status.DISCHARGED));
		autoPOM.runAutoProver();
		prStatus = PRUtil.readStatus(prFile);
		assertTrue(prStatus.values().contains(Status.DISCHARGED));
	}


	
	protected void tearDown() throws Exception {
		super.tearDown();
		rodinProject.getProject().delete(true, true, null);
	}

}
