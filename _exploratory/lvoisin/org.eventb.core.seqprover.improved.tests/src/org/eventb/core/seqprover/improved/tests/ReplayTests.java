package org.eventb.core.seqprover.improved.tests;

import static org.eventb.core.seqprover.improved.utils.ProjectImporter.importProjectFiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IRodinFile;

public class ReplayTests extends BuilderTest {

	private void importProject(String prjName) throws Exception {
		importProjectFiles(rodinProject.getProject(), prjName);
	}

	public void testSmall() throws Exception {
		importProject("Small");
		runBuilder();
		assertUndischarged("c", "axm4/THM");

		// Replay proofs here if needed

		assertUndischarged("c", "axm4/THM");
	}

	private void assertUndischarged(String compName, String... pos)
			throws CoreException {
		final Set<String> actual = getUndischarged(compName);
		final Set<String> expected = new HashSet<String>(Arrays.asList(pos));
		assertEquals("Undischarged POs do not match", expected, actual);
	}

	private Set<String> getUndischarged(String compName) throws CoreException {
		final Set<String> result = new HashSet<String>();
		final IRodinFile psFile = eventBProject.getPSFile(compName);
		final IPSRoot psRoot = (IPSRoot) psFile.getRoot();
		for (final IPSStatus s : psRoot.getStatuses()) {
			if (s.getConfidence() <= IConfidence.REVIEWED_MAX || s.isBroken()) {
				result.add(s.getElementName());
			}
		}
		return result;
	}

}
