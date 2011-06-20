/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.tactics.perfs;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.tests.BuilderTest;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewrites;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.tactics.perfs.utils.Chrono;
import fr.systerel.tactics.perfs.utils.SequentExtractor;

/**
 * Abstract class use for testing the performance of methods or reasoner or
 * tactics.
 * <p>
 * Here is the procedure to create a new test of performance :
 * <ul>
 * <li>First create a new class, in the same package, extending this class.</li>
 * <li>Then add the proposed unimplemented methods as well as
 * <code>setUp()</code> and <code>tearDown()</code>:
 * <ul>
 * <li><b><code>setUp()</code></b> : should only contain
 * <code>super.setUp();</code>.</li>
 * <li><b><code>tearDown()</code></b> : should only contain
 * <code>super.tearDown();</code>.</li>
 * <li>For the others, <i><b>NNFRewrites_GetPosition</b></i> and
 * <i><b>GeneralizedMP</b></i> can be used as examples.</li>
 * </ul>
 * </li>
 * <li>For testing a project, create a new method preceded by the tag
 * <code>@Test</code>. Then, there is three possibilities :</li>
 * <ul>
 * <li>Either you consider the project as a big one. In that case, you should
 * use the method <b><code>test_HeavyProject(String projectName)</code></b>. For
 * each .bpo files of the given project, it records all the sequents in a
 * sequentExtractor and then measures the time execution of the
 * Method/Reasoner/Tactic over those sequents.</li>
 * <li>Or you consider the project as a small one. In that case, you should use
 * the method <b>
 * <code>test_SoftProjects(int nbLoop, String... projectsName)</code></b>. It
 * records every sequents of all given projects in a sequentExtractor and then
 * measures the time execution of the Method/Reasoner/Tactic over those
 * sequents.<br>
 * If your computer has enough heap memory, you can try to test a heavy project
 * with this method. But don't be surprised if it fails.</li>
 * <li>Or, you want to work on the root of the IProofTree. In that case, you
 * should use the method <b>
 * <code>test_rootProject(String projectName, int nbLoop)</code></b>. For each
 * root of a IPOSequent, it applies the reasoner <b>TypeRewrites</b>. Then, it
 * executes the Method/Reasoner/Tactic as many times as defined with
 * <code>nbLoop</code> and measures the performance. Then it applies the tactic
 * returned by <b> <code>getTactic()</code></b>. If it succeeds, it measures the
 * performance the same way as previously on the opened descendants. And so on.<br>
 * You can choose to limit your results with the methods <b>
 * <code>getFailureNumber()</code></b> and <b><code>getSuccessNumber()</code>
 * </b>.</li>
 * </ul>
 * <li>Finally, to save your result, redirect the output of the console to a
 * file (<i>Run Configurations > Common > </i>Tick<i> File </i>and then choose
 * <i>File System...</i>)</li>
 * </ul> If you want to increase your heap memory : <i>Run Configurations >
 * Arguments > VM arguments: [...] -Xmx<b>[your value]</b>m</i>.
 * <p>
 * Here are the available projects for testing (you can find them in the folder
 * <i>projects</i> of that plugin) :
 * <ul>
 * <li><b>BirthadayBook</b> > small project</li>
 * <li><b>Celebrity</b> > small project</li>
 * <li><b>ch2_car</b> > small project</li>
 * <li><b>Closure - Sans PostTactics</b> > small project</li>
 * <li><b>Doors</b> > small project</li>
 * <li><b>Galois</b> > small project</li>
 * <li><b>XCore</b> > heavy project</li>
 * <li><b>XCoreEncoding</b> > heavy project</li>
 * </ul>
 * <p>
 * <b>Enhancement : </b>instead of applying only the TypeRewriters, it would be
 * smart applying a list of defined tactics at the beginning of the test, and
 * maybe even between each application of the tactic given by
 * {@link PerfsTest#getTactic()}.
 * </p>
 * 
 * 
 * @author Emmanuel Billaud
 */
public abstract class PerfsTest extends BuilderTest {
	private List<IRodinProject> rProjects;
	private SequentExtractor x;
	private int failureNumber;
	private int successNumber;
	// Create only once String which are often used.
	final private String isBeingTested = " is being tested ";
	final private String testingFile = "Testing file : ";
	final private String WorkOnRoot = "Working on the root of ";
	final private String isRan = " is ran ";
	final private String overSeq = " times over the sequent. #Position : ";
	final private String of = " of ";

	/**
	 * Method called before each test.
	 */
	@Override
	public void setUp() throws RodinDBException, Exception {
		super.setUp();
		final URL entry = getProjectsURL();
		final URL projectsURL = FileLocator.toFileURL(entry);
		final File projectsDir = new File(projectsURL.toURI());
		for (final File project : projectsDir.listFiles()) {
			if (project.isDirectory() && !project.getName().equals(".svn")) {
				final IRodinProject p = createRodinProject(project.getName());
				importProjectFiles(p.getProject(), p.getElementName());
			}
		}
		x = new SequentExtractor(false);
		rProjects = getIRodinProject();
		failureNumber = 0;
		successNumber = 0;
		System.gc();
	}

	/**
	 * Method called after each test.
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Methods used in the SetUp.
	 */
	/**
	 * Give the URL of the folder containing the projects we want to use.
	 */
	@Override
	protected URL getProjectsURL() {
		return Platform.getBundle("fr.systerel.tactics.perfs").getEntry(
				"projects");
	}

	/**
	 * Return a list of all the IRodinProject contained in the workspace.
	 * 
	 * @return a list of all the IRodinProject contained in the workspace.
	 */
	private List<IRodinProject> getIRodinProject() {
		List<IRodinProject> list = new ArrayList<IRodinProject>();
		final IProject[] projects = workspace.getRoot().getProjects();
		for (IProject p : projects) {
			final IRodinProject rProject = RodinCore.valueOf(p);
			if (rProject == null) {
				continue;
			}
			list.add(rProject);
		}
		return list;
	}

	/*
	 * Common methods to the tests.
	 */
	/**
	 * Return the IRodinProject named <code>projectName</code> or
	 * <code>null</code> if there is no project named like this.
	 * 
	 * @param projectName
	 *            the name of the IRodinProject
	 * @return the IRodinProject named <code>projectName</code>, or
	 *         <code>null</code> if there is no project named like this.
	 */
	private IRodinProject getProject(String projectName) {
		for (IRodinProject p : rProjects) {
			if (projectName.equals(p.getElementName())) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Measure the execution time of the method/reasoner/tactic tested over all
	 * the IProverSequent recorded in the SequentExtractor <code>x</code>.
	 * 
	 * @param nbLoop
	 *            represents the number of execution of the
	 *            method/reasoner/tactic over each IProverSequent
	 */
	private void perfMeasure(int nbLoop) {
		if (x.isEmpty()) {
			return;
		}
		Chrono c = new Chrono();
		c.startTime();
		for (List<IProverSequent> l : x) {
			for (IProverSequent seq : l) {
				for (int k = 0; k < nbLoop; k++) {
					execute(seq);
				}
			}
		}
		c.getTime();
	}

	/**
	 * Measure the execution time of the method/reasoner/tactic over all the
	 * IProverSequent recorded in the SequentExtractor <code>x</code> as many
	 * times as <code>getNbPerfTests()</code> returns.
	 * 
	 * @param nbLoop
	 *            represents the number of execution of the
	 *            method/reasoner/tactic over each IProverSequent
	 */
	private void suitePerfMeasure(int nbLoop) {
		for (int j = 0; j < getNbPerfTests(); j++) {
			perfMeasure(nbLoop);
		}
	}

	/*
	 * test_HeavyProject(String projectName)
	 */
	/**
	 * Compute the execution time for each .bpo file contained in the project
	 * named <code>projectName</code>. The method/reasoner/tactic is ran only
	 * once over each IProverSequent (parameter of
	 * {@link PerfsTest#suitePerfMeasure(int)} set to <code>1</code>).
	 * 
	 * @param projectName
	 *            the name of the project whose .bpo files will be tested
	 * @throws Exception
	 *             if a problem occurs while accessing to the project's files or
	 *             while extracting those files.
	 */
	protected void test_HeavyProject(String projectName) throws Exception {
		IRodinProject rProject = getProject(projectName);
		System.out.println(getMRTName() + isBeingTested + "on " + projectName);
		if (rProject == null) {
			return;
		}
		for (IRodinFile file : rProject.getRodinFiles()) {
			System.gc();
			x = extractFile(file);
			System.out.println(testingFile + file.getElementName());
			suitePerfMeasure(1);
		}
	}

	/**
	 * Call the extract method of the sequentExtractor on a IRodinFile.
	 * 
	 * @param rFile
	 *            the file to extract
	 * @return a sequent extractor containing all the IProverSequent of the file
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 *             obligation
	 */
	private SequentExtractor extractFile(IRodinFile rFile)
			throws RodinDBException {
		SequentExtractor se = new SequentExtractor(false);
		se.extract(rFile);
		se.computeProverSequent();
		return se;
	}

	/*
	 * test_SoftProjects(int nbLoop, String... projectsName)
	 */
	/**
	 * Compute the execution time for every .bpo file contained in the list of
	 * projects named <code>projectsName</code>. The method/reasoner/tactic is
	 * ran <code>nbLoop</code> over each IProverSequent (parameter of
	 * {@link PerfsTest#suitePerfMeasure(int)} set to <code>nbLoop</code>).
	 * 
	 * @param nbLoop
	 *            represents the number of execution of the
	 *            method/reasoner/tactic over each IProverSequent.
	 * @param projectsName
	 *            the name of the projects whose .bpo files will be tested
	 * @throws Exception
	 *             if a problem occurs while extracting a project.
	 */
	protected void test_SoftProjects(int nbLoop, String... projectsName)
			throws Exception {
		x = extractProjects(projectsName);
		System.out.println(getMRTName() + isRan + nbLoop
				+ " times over each sequent.");
		suitePerfMeasure(nbLoop);
	}

	/**
	 * Call the extract method of the sequentExtractor on severals
	 * IRodinProject.
	 * 
	 * @param stringProjects
	 *            the names of the IRodinProject to extract
	 * @return a sequent extractor containing all the IProverSequent of the
	 *         projects
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 *             obligation
	 */
	private SequentExtractor extractProjects(String... stringProjects)
			throws RodinDBException {
		SequentExtractor se = new SequentExtractor(false);
		String listProject = "";
		IRodinProject rProject;
		for (String s : stringProjects) {
			rProject = getProject(s);
			if (rProject == null) {
				continue;
			}
			se.extract(rProject);
			listProject += " ~ " + rProject.getElementName() + "\r";
		}
		se.computeProverSequent();
		System.out.println(getMRTName()
				+ " is being tested on the following projects :");
		System.out.println(listProject + "\r");
		return se;
	}

	/*
	 * test_rootProject(String projectName, int nbLoop)
	 */
	/**
	 * For the project named <code>projectName</code>, the
	 * <code>TypeRewrites</code> is applied on the IProofTreeNode root. Then,
	 * performance tests are computed. Then, the tactic given by
	 * {@link PerfsTest#getTactic()} is applied to the open IProofTreeNode. If
	 * it succeeds, same performances tests are applied on every opened
	 * descendants.
	 * <p>
	 * If enough tests have been proceeded, then it stops.
	 * 
	 * @param projectName
	 *            the name of the project which will be tested
	 * @param nbLoop
	 *            represents the number of execution of the
	 *            method/reasoner/tactic over each IProverSequent
	 * @throws Exception
	 *             if a problem occurs :
	 *             <ul>
	 *             <li>if the element named <code>projectName</code> does not
	 *             exist or if an exception occurs while accessing its
	 *             corresponding resource.</li> <li>if there is some problem
	 *             loading proof obligation.</li> <li>if there was a problem
	 *             accessing the database.</li>
	 *             </ul>
	 */
	protected void test_rootProject(String projectName, int nbLoop)
			throws Exception {
		IRodinProject rProject = getProject(projectName);
		if (rProject == null) {
			return;
		}
		System.out.println(getMRTName()
				+ " is being tested on the roots of the project : "
				+ projectName);
		for (IRodinFile file : rProject.getRodinFiles()) {
			final String fileName = file.getElementName();
			IInternalElement ielt = file.getRoot();
			if (!(ielt instanceof IPORoot)) {
				continue;
			}
			for (IPOSequent sequent : ((IPORoot) ielt).getSequents()) {
				System.gc();
				x = extractRoot(sequent);
				if (x.getPtNodeRoot().size() != 1) {
					continue;
				}
				final IProofTreeNode ptNode = x.getPtNodeRoot().get(0);
				System.out.println(WorkOnRoot + sequent.getElementName() + of
						+ fileName);
				ptNode.pruneChildren();
				final IProofTreeNode resultPtNode = applyTypeRewrites(nbLoop,
						ptNode);
				if (resultPtNode == null) {
					continue;
				}
				testingRootAndChildren(resultPtNode, nbLoop, "0", true);
				if (isEnough()) {
					printResult();
					return;
				}
			}
		}
		printResult();
	}

	private void printResult() {
		System.out.print("Succeeding sequent(s) : " + successNumber);
		if (getSuccessNumber() == -1) {
			System.out.println("");
		} else {
			System.out.println("/" + getSuccessNumber());
		}

		System.out.print("Failing sequent(s) : " + failureNumber);
		if (getFailureNumber() == -1) {
			System.out.println("");
		} else {
			System.out.println("/" + getFailureNumber());
		}
	}

	/**
	 * Call the extract method on a IPOSequent.
	 * 
	 * @param sequent
	 *            the considered IPOSequent
	 * @return a sequentExtractor containing the IProofTreeNode root of the
	 *         given IPOSequent
	 * @throws Exception
	 *             if there is some problem loading proof obligation
	 */
	private SequentExtractor extractRoot(IPOSequent sequent) throws Exception {
		SequentExtractor se = new SequentExtractor(true);
		se.extract(sequent);
		return se;
	}

	private IProofTreeNode applyTypeRewrites(int nbLoop, IProofTreeNode ptNode) {
		BasicTactics.reasonerTac(new TypeRewrites(), new EmptyInput()).apply(
				ptNode, null);
		if (ptNode.hasChildren()) {
			final IProofTreeNode[] childNodes = ptNode.getChildNodes();
			if (childNodes.length != 1) {
				return null;
			}
			return childNodes[0];
		} else {
			return ptNode;
		}
	}

	/**
	 * Apply <code>suitePerfMeasure</code> with the parameter
	 * <code>nbLoop</code> on the sequent contained in <code>ptNode</code> if it
	 * is not a root for the tests (<code>isRoot</code> equals
	 * <code>false</code>) or if not enough tests have been performed.
	 * 
	 * @param ptNode
	 *            the considered IProofTreeNode
	 * @param nbLoop
	 *            the parameter for <code>suitePerfMeasure</code>
	 * @param pos
	 *            the position of <code>ptNode</code> in the tree of tests
	 * @param isRoot
	 *            tells whether the given IProofTreeNode is a root for the tests
	 */
	private void testingRootAndChildren(IProofTreeNode ptNode, int nbLoop,
			String pos, boolean isRoot) {
		x = new SequentExtractor(true);
		x.setSingletonPtNode(ptNode);
		x.computeProverSequent();
		Object resultTac = getTactic().apply(ptNode, null);

		if (resultTac != null) {
			if (!isRoot || notEnoughFailure()) {
				System.out.println(getMRTName() + isRan + nbLoop + overSeq
						+ pos);
				suitePerfMeasure(nbLoop);
				if (isRoot) {
					failureNumber++;
				}
			}
		} else {
			if (!isRoot || notEnoughSuccess()) {
				System.out.println(getMRTName() + isRan + nbLoop + overSeq
						+ pos);
				suitePerfMeasure(nbLoop);
				int intPos = 0;
				for (IProofTreeNode child : ptNode.getOpenDescendants()) {
					intPos++;
					testingRootAndChildren(child, nbLoop, pos + "." + intPos,
							false);
				}
				if (isRoot) {
					successNumber++;
				}
			}
		}
	}

	/**
	 * Tells whether enough tests have been proceeded.
	 * 
	 * @return true if (<code>successNumber</code> is greater or equal to
	 *         <code>getSuccessNumber()</code>) and if (
	 *         <code>failureNumber</code> is greater or equal to
	 *         <code>getFailureNumber()</code>), false else.
	 */
	private boolean isEnough() {
		return !notEnoughFailure() && !notEnoughSuccess();
	}

	private boolean notEnoughSuccess() {
		return (getSuccessNumber() == -1 || successNumber < getSuccessNumber());
	}

	private boolean notEnoughFailure() {
		return (getFailureNumber() == -1 || failureNumber < getFailureNumber());
	}

	/*
	 * Protected abstract methods.
	 */
	/**
	 * Return the name of the method/reasoner/tactic tested.
	 */
	protected abstract String getMRTName();

	/**
	 * Execute the reasoner/tactic/method on the given IProverSequent
	 * <code>sequent</code>.
	 * 
	 * @param sequent
	 *            the considered IProverSequent.
	 */
	protected abstract void execute(IProverSequent sequent);

	/**
	 * Return the tactic to apply to a IRppofTreeNode.
	 * 
	 * @return the tactic to apply to a IRppofTreeNode.
	 */
	protected abstract ITactic getTactic();

	/**
	 * @return the number of IProverSequent tested on which the ITactic given by
	 *         {@link PerfsTest#getTactic()} fails. If <code>-1</code> then
	 *         every IProverSequent on which the given tactic fails are tested.
	 */
	protected abstract int getFailureNumber();

	/**
	 * @return the number of IProverSequent tested on which the ITactic given by
	 *         {@link PerfsTest#getTactic()} succeeds. If <code>-1</code> then
	 *         every IProverSequent on which the given tactic succeeds are
	 *         tested.
	 */
	protected abstract int getSuccessNumber();

	/**
	 * @return the number of execution of {@link PerfsTest#perfMeasure(int)}
	 *         (the number of performance tests proceeded).
	 */
	protected abstract int getNbPerfTests();

}