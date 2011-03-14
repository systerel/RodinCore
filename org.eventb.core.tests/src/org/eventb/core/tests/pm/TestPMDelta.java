package org.eventb.core.tests.pm;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;

public abstract class TestPMDelta extends TestPM implements
		IUserSupportManagerChangedListener {

	List<IUserSupportManagerDelta> deltas;

	/**
	 * Starts recording delta for the given proof tree.
	 * 
	 * @param tree
	 *            the proof tree for which deltas should get recorded
	 */
	void startDeltas() {
		deltas = new ArrayList<IUserSupportManagerDelta>();
		manager.addChangeListener(this);
	}

	/**
	 * Stops recording delta for the given proof tree.
	 * 
	 * @param tree
	 *            the proof tree for which deltas should not be recorded
	 */
	void stopDeltas() {
		manager.removeChangeListener(this);
		deltas = new ArrayList<IUserSupportManagerDelta>();
	}

	void clearDeltas() {
		deltas = new ArrayList<IUserSupportManagerDelta>();
	}

	void assertDeltas(String message, String expected) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IUserSupportManagerDelta delta : deltas) {
			if (sep)
				builder.append('\n');
			builder.append(delta);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		assertTrue(deltas != null);
		deltas.add(delta);
	}

}
