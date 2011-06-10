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
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.tests.BuilderTest;
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
 * <li>Then add the proposed unimplemented methods (<code>getMRTName()</code>
 * and <code>execute()</code>) as well as <code>setUp()</code> and
 * <code>tearDown()</code>:
 * <ul>
 * <li><b><code>getMRTName()</code></b> : return the name of the Method or the
 * Reasoner or the Tactics which is tested (just needed to get more details
 * while printing in the console).</li>
 * <li><b><code>execute()</code></b> : apply your Method/Reasoner/Tactics to the
 * sequent. This is the core of the test. <i>NNFRewrites_GetPosition</i> and
 * <i>GeneralizedMP</i> can be used as examples.</li>
 * <li><b><code>setUp()</code></b> : should only contain
 * <code>super.setUp();</code>.</li>
 * <li><b><code>tearDown()</code></b> : should only contain
 * <code>super.tearDown();</code>.</li>
 * </ul>
 * </li>
 * <li>For testing a project, create a new method preceded by the tag
 * <code>@Test</code>. Then, there is two possibility :</li>
 * <ul>
 * <li>Either you consider the project as a big one. In that case, you should
 * use the method <b><code>test_HeavyProject(String projectName)</code></b>. For
 * each .bpo files of the given project, it records all the sequents in a
 * sequentExtractor and then measures the time execution of the
 * Method/Reasoner/Tactic over those sequents.</li>
 * <li>Or you consider the project as a small one. In that case, you should use
 * the method <b><code>test_SoftProjects(String... projectsName).</code></b> It
 * records every sequents of all given projects in a sequentExtractor and then
 * measures the time execution of the Method/Reasoner/Tactic over those
 * sequents.<br>
 * If your computer has enough heap memory, you can try to test a heavy project
 * with this method. But don't be surprised if it fails.</li>
 * </ul>
 * <li>Finally, to save your result, redirect the output of the console to a
 * file (<i>Run Configurations > Common > </i>Tick<i> File </i>and then choose
 * <i>File System...</i>)</li>
 * </ul> If you want to increase your heap memory : <i>Run Configurations >
 * Arguments > VM arguments: [...] -Xmx<b>**your value**</b>m</i>.
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
 * 
 * @author Emmanuel Billaud
 */
public abstract class PerfsTest extends BuilderTest {
	private List<IRodinProject> rProjects;
	private SequentExtractor x;
	private String listProject;
	private final int nbPerfTests = 30;

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
		x = new SequentExtractor();
		listProject = "";
		rProjects = getIRodinProject();
		System.gc();
	}

	/**
	 * Method called after each test.
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

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

	/**
	 * Return the IRodinProject named <code>projectName</code>.
	 * 
	 * @param projectName
	 *            the name of the IRodinProject
	 * @return the IRodinProject named <code>projectName</code>.
	 */
	public IRodinProject getProject(String projectName) {
		for (IRodinProject p : rProjects) {
			if (projectName.equals(p.getElementName())) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Compute the execution time for each .bpo file contained in the project
	 * named <code>projectName</code>.
	 * 
	 * @param projectName
	 *            the name of the project whose .bpo files will be tested
	 * @throws Exception
	 *             if a problem occurs while accessing to the project's files or
	 *             while extracting those files.
	 */
	protected void test_HeavyProject(String projectName) throws Exception {
		IRodinProject rProject = getProject(projectName);
		System.out.println(getMRTName() + " is being tested on  : "
				+ projectName);
		if (rProject != null) {
			for (IRodinFile file : rProject.getRodinFiles()) {
				System.gc();
				x = extractFile(file);
				System.out.println("Testing file : " + file.getElementName());
				for (int j = 0; j < nbPerfTests; j++)
					perfMeasure();
			}
		}
	}

	/**
	 * Compute the execution time for every .bpo file contained in the list of
	 * projects named <code>projectsName</code>
	 * 
	 * @param projectsName
	 *            the name of the projects whose .bpo files will be tested
	 * @throws Exception
	 *             if a problem occurs while extracting a project.
	 */
	protected void test_SoftProjects(String... projectsName) throws Exception {
		x = extractProjects(projectsName);
		System.out.println(getMRTName()
				+ " is being tested on the following projects :");
		System.out.println(listProject + "\r");
		for (int j = 0; j < nbPerfTests; j++) {
			perfMeasure();
		}
	}

	/**
	 * Call the extract method of the sequentExtractor on severals
	 * IRodinProject.
	 * 
	 * @param stringProjects
	 *            the names of the IRodinProject to extract
	 * @return a sequent extractor containing all the IPOSequent of the projects
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 *             obligation
	 */
	public SequentExtractor extractProjects(String... stringProjects)
			throws RodinDBException {
		SequentExtractor se = new SequentExtractor();
		listProject = "";
		IRodinProject rProject;
		for (String s : stringProjects) {
			rProject = getProject(s);
			if (rProject == null) {
				continue;
			}
			se.extract(rProject);
			listProject += " ~ " + rProject.getElementName() + "\r";
		}
		return se;
	}

	/**
	 * Call the extract method of the sequentExtractor on a IRodinFile.
	 * 
	 * @param rFile
	 *            the file to extract
	 * @return a sequent extractor containing all the IPOSequent of the file
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 *             obligation
	 */
	public SequentExtractor extractFile(IRodinFile rFile)
			throws RodinDBException {
		SequentExtractor se = new SequentExtractor();
		se.extract(rFile);
		return se;
	}

	/**
	 * Return the name of the method/reasoner/tactic tested.
	 */
	public abstract String getMRTName();

	/**
	 * Measure the execution time of the reasoner/tactic/method tested over all
	 * the IProverSequent recorded in the SequentExtractor <code>x</code>.
	 */
	public void perfMeasure() {
		System.gc();
		if (x.isEmpty()) {
			return;
		}
		Chrono c = new Chrono();
		c.startTime();
		for (List<IProverSequent> l : x) {
			for (IProverSequent seq : l) {
				execute(seq);
			}
		}
		c.getTime();
	}

	/**
	 * Execute the reasoner/tactic/method on the given IProverSequent
	 * <code>sequent</code>.
	 * 
	 * @param sequent
	 *            the considered IProverSequent.
	 */
	public abstract void execute(IProverSequent sequent);

}