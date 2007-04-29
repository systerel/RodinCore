package org.eventb.internal.ui.prover;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

public class ProverUIUtils {

	// Debug flag.
	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProverUI *** ";

	/**
	 * Print out the message if the <code>ProverUI.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}

	public static IUserSupportDelta getUserSupportDelta(
			IUserSupportManagerDelta delta, IUserSupport userSupport) {
		IUserSupportDelta[] affectedUserSupports = delta
				.getAffectedUserSupports();
		for (IUserSupportDelta affectedUserSupport : affectedUserSupports) {
			if (affectedUserSupport.getUserSupport() == userSupport) {
				return affectedUserSupport;
			}
		}
		return null;
	}

	public static IProofStateDelta getProofStateDelta(IUserSupportDelta delta,
			IProofState proofState) {
		IProofStateDelta[] affectedProofStates = delta.getAffectedProofStates();
		for (IProofStateDelta affectedProofState : affectedProofStates) {
			if (affectedProofState.getProofState() == proofState) {
				return affectedProofState;
			}
		}
		return null;
	}

	public static void applyTacticWithProgress(Shell shell,
			final IUserSupport userSupport, final ITactic tactic,
			final boolean applyPostTactic) {
		UIUtils.runWithProgressDialog(shell, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					userSupport.applyTactic(tactic, applyPostTactic, monitor);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public static ArrayList<String> parseString(String stringList) {
        StringTokenizer st = new StringTokenizer(stringList, ",");//$NON-NLS-1$
        ArrayList<String> result = new ArrayList<String>();
        while (st.hasMoreElements()) {
            result.add((String) st.nextElement());
        }
        return result;
	}

	public static String toCommaSeparatedList(ArrayList<Object> objects) {
		// Return the comma separated list of items
		StringBuffer buffer = new StringBuffer();
		boolean sep = false;
		for (Object item : objects) {
			if (sep) {
				sep = true;
			}
			else {
				buffer.append(",");
			}
			buffer.append(item);
		}
		return buffer.toString();
	}

}
