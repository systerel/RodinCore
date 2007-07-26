package org.eventb.core.tests.pm;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.pom.POUtil;
import org.eventb.internal.core.pm.UserSupport;
import org.eventb.internal.core.pom.AutoProver;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportChangeDeltas extends TestPMDelta {

	IPOFile poFile;

	IPSFile psFile;

	IPRFile prFile;

	IUserSupport userSupport;

	private static String originalPO = "original PO";

	private static String dischargedPO = "discharged PO";

	private static String copyOriginalPO = "copy original PO";

	private static String reusablePO = "reusable PO";

	private static String rebuiltPO = "rebuilt PO";

	private static IPOPredicateSet hyp0;

	private static IPOPredicateSet hyp1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		poFile = createPOFile("x");
		psFile = poFile.getPSFile();
		prFile = poFile.getPRFile();

		hyp0 = POUtil.addPredicateSet(poFile, "hyp0", null, mTypeEnvironment(
				"x", "ℤ", "f", "ℙ(ℤ×ℤ)"), "1=1", "2=2", "x∈ℕ", "f∈ℕ ⇸ ℕ");
		hyp1 = POUtil.addPredicateSet(poFile, "hyp1", null, mTypeEnvironment(
				"x", "ℤ", "f", "ℙ(ℤ×ℤ)"), "1=1", "2=2", "x∈ℕ", "f∈ℕ ⇸ ℕ",
				"f(x)∈ℕ", "x∈dom(f)");

		// Turn on beginner mode
		EventBPlugin.getPostTacticPreference().setEnabled(false);

		userSupport = new UserSupport();
	}

	@Override
	IPOFile createPOFile(String fileName) throws RodinDBException {
		IPOFile poFile = (IPOFile) rodinProject.getRodinFile(fileName + ".bpo");
		poFile.create(true, null);
		return poFile;
	}

	@Override
	protected void tearDown() throws Exception {
		userSupport.dispose();
		poFile.delete(true, null);
		prFile.delete(true, null);
		psFile.delete(true, null);
		super.tearDown();
	}

	public void testRemoveCurrentPO() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, null);

		startDeltas();
		PSWrapperUtil.removePO(poFile, psFile, prFile, originalPO);
		// Check the delta here
		assertDeltas("Original PO has been removed first ",
				"[*] x.bps [STATE]\n"
						+ "  [-] original PO[org.eventb.core.psStatus] []");
		clearDeltas();
		PSWrapperUtil.removePO(poFile, psFile, prFile, dischargedPO);
		// Check the status of the User Support here
		assertDeltas("Discharged PO has been removed ", "[*] x.bps [STATE]\n"
				+ "  [-] discharged PO[org.eventb.core.psStatus] []");
		stopDeltas();
	}

	public void testRemoveOtherPO() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, null);

		startDeltas();
		PSWrapperUtil.removePO(poFile, psFile, prFile, dischargedPO);
		// Check the delta here
		assertDeltas("Dicharged PO has been removed first ",
				"[*] x.bps [STATE]\n"
						+ "  [-] discharged PO[org.eventb.core.psStatus] []");
		clearDeltas();
		PSWrapperUtil.removePO(poFile, psFile, prFile, originalPO);
		// Check the status of the User Support here
		assertDeltas("Original PO has been removed ", "[*] x.bps [STATE]\n"
				+ "  [-] original PO[org.eventb.core.psStatus] []");
		stopDeltas();
	}

	public void testAddPO() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, null);
		startDeltas();
		PSWrapperUtil
				.copyPO(poFile, psFile, prFile, originalPO, copyOriginalPO);
		assertDeltas("Copied original PO ", "[*] x.bps [STATE]\n"
				+ "  [+] copy original PO[org.eventb.core.psStatus] []");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is not loaded. The
	 * User Support does nothing (no deltas).
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePONotLoaded() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, null);
		startDeltas();
		PSWrapperUtil.copyPO(poFile, psFile, prFile, originalPO, dischargedPO);
		assertDeltas("Changed: PO is not loaded ", "");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is loaded but not
	 * modified. The proof tree is RELOADED from the database, hence the CACHE,
	 * SEARCH has been reset to empty, a new PROOFTREE is create with the new
	 * current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePONotModified() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, new NullProgressMonitor());
		startDeltas();
		PSWrapperUtil.copyPO(poFile, psFile, prFile, dischargedPO, originalPO);

		assertDeltas(
				"Change: PO is loaded and NOT modified ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "Proof Tree is reloaded (priority 2)\n" 
						+ "  [*] original PO[org.eventb.core.psStatus] [CACHE|SEARCH|NODE|PROOFTREE]");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is modified but the
	 * new PO is discharged automatically in DB. The proof tree is TRASHED and
	 * RELOADED from the database, hence the CACHE, SEARCH has been reset to
	 * empty, a new PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePOModifiedAndDischargedAutoInDB() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, new NullProgressMonitor());

		// Modified current PPO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startDeltas();
		PSWrapperUtil.copyPO(poFile, psFile, prFile, dischargedPO, originalPO);
		assertDeltas(
				"Change: PO is modified and discharged automatically in DB ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "Proof Tree is reloaded (priority 2)\n"
						+ "  [*] original PO[org.eventb.core.psStatus] [CACHE|SEARCH|NODE|PROOFTREE]");
		stopDeltas();
	}

	/**
	 * This corresponding to the case where the orinal PO is modified but it is
	 * still reusable with the new PO. The proof tree is REUSED hence a new
	 * PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePOModifiedAndReusable() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "1 = 2", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poFile, reusablePO, "1 = 2", hyp1,
						mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, null);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startDeltas();
		PSWrapperUtil.copyPO(poFile, psFile, prFile, reusablePO, originalPO);
		assertDeltas(
				"Change: PO is modified and reusable ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] original PO[org.eventb.core.psStatus] [NODE|PROOFTREE]");
		stopDeltas();
	}
	
	/**
	 * This corresponding to the case where the orinal PO is modified but it is
	 * NOT reusable with the new PO. The proof tree is TRASHED then REBUILT, hence a new
	 * PROOFTREE is create with the new current NODE.
	 * <p>
	 * TODO associated with document REQUIREMENTS
	 */
	public void testChangePOModifiedAndNotReusable() throws Exception {
		POUtil
				.addSequent(poFile, originalPO, "x∈dom(f)∧f∼;({x} ◁ f)⊆id(ℤ)", hyp0,
						mTypeEnvironment());
		POUtil.addSequent(poFile, dischargedPO, "1 = 1", hyp1,
				mTypeEnvironment());
		POUtil
		.addSequent(poFile, rebuiltPO, "1 = 2", hyp1,
				mTypeEnvironment());
		POUtil
				.addSequent(poFile, reusablePO, "1 = 2", hyp1,
						mTypeEnvironment());
		poFile.save(null, true);
		AutoProver.enable();
		runBuilder();
		userSupport.setInput(psFile, null);
		// Modified current PO
		try {
			userSupport.applyTactic(Tactics.review(1), false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PSWrapperUtil.copyPO(poFile, psFile, prFile, rebuiltPO, originalPO);
		startDeltas();
		PSWrapperUtil.copyPO(poFile, psFile, prFile, reusablePO, originalPO);
		assertDeltas(
				"Change: PO is modified and NOT reusable ",
				"[*] x.bps [STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "  [*] original PO[org.eventb.core.psStatus] [NODE|PROOFTREE]");
		stopDeltas();
	}
}
