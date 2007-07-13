package org.eventb.core.tests.pom;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Class containing unit tests to test that pr stamps are properly generated.
 * 
 * @author Farhad Mehta
 *
 */public class PRStampTests extends TestCase {
	
	protected IRodinProject rodinProject;
	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	private IPRFile prFile;
	

	public final void testPRstamps() throws RodinDBException{
		
		long prFileStamp = prFile.getPRStamp();
		
		IPRProof proof1 = prFile.getProof("proof1");
		
		// prFileStamp is still the same
		assertTrue("PR stamp changed although no change performed", prFileStamp == prFile.getPRStamp());
		
		// Creating a new proof should change the prFileStamp
		proof1.createPRProof(null, null);
		assertFalse("PR stamp for file with added proof unchanged", prFileStamp == prFile.getPRStamp());
		
		IPRProof proof2 = prFile.getProof("proof2");
		proof2.create(null, null);
		
		// Recollect pr stamps
		prFileStamp = prFile.getPRStamp();
		long proof1Stamp = proof1.getPRStamp();
		long proof2Stamp = proof2.getPRStamp();

		// generating a sample proof tree.
		IProverSequent sequent = TestLib.genSeq("|- ⊤");
		IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		
		// Change proof1
		proof1.setProofTree(proofTree, null);
		assertFalse("PR stamp for changed proof unchanged", proof1Stamp == proof1.getPRStamp());
		assertFalse("PR stamp for file containing changed proof unchanged", prFileStamp == prFile.getPRStamp());
		assertTrue("PR stamp for unchanged proof changed", proof2Stamp == proof2.getPRStamp());
		
		// Recollect pr stamps
		prFileStamp = prFile.getPRStamp();
		proof1Stamp = proof1.getPRStamp();
		proof2Stamp = proof2.getPRStamp();
		
		// Change proof2
		proof2.setProofTree(proofTree, null);
		assertFalse("PR stamp for changed proof unchanged", proof2Stamp == proof2.getPRStamp());
		assertFalse("PR stamp for file containing changed proof unchanged", prFileStamp == prFile.getPRStamp());
		assertTrue("PR stamp for unchanged proof changed", proof1Stamp == proof1.getPRStamp());
		
		// Recollect pr stamps
		prFileStamp = prFile.getPRStamp();
		proof1Stamp = proof1.getPRStamp();
		proof2Stamp = proof2.getPRStamp();
		
		// Change both proofs
		proof1.setProofTree(proofTree, null);
		proof2.setProofTree(proofTree, null);
		assertFalse("PR stamp for changed proof unchanged", proof1Stamp == proof1.getPRStamp());
		assertFalse("PR stamp for changed proof unchanged", proof2Stamp == proof2.getPRStamp());
		assertFalse("PR stamp for file containing changed proof unchanged", prFileStamp == prFile.getPRStamp());
		
		// Recollect pr stamps
		prFileStamp = prFile.getPRStamp();
		proof1Stamp = proof1.getPRStamp();
		proof2Stamp = proof2.getPRStamp();
		
		// Remove proof1; this should change the prFileStamp, but not the proof2Stamp
		proof1.deletePRProof(true, null);
		assertFalse("PR stamp for file containing deleted proof unchanged", prFileStamp == prFile.getPRStamp());
		assertTrue("PR stamp for unchanged proof changed", proof2Stamp == proof2.getPRStamp());
	}
		
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		// Create a new project
		IProject project = workspace.getRoot().getProject("P");
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		rodinProject = RodinCore.valueOf(project);
		
		// Create a new proof file
		prFile = (IPRFile) rodinProject.getRodinFile("x.bpr");
		prFile.create(true, null);
		
		assertTrue(prFile.exists());
		assertEquals(0, prFile.getProofs().length);
	}
	
	
	protected void tearDown() throws Exception {
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}

}
