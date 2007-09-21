package org.eventb.internal.ui.obligationexplorer.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSFile;
import org.eventb.internal.core.pom.AutoPOM;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorerUtils;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.rodinp.core.RodinDBException;

public class ObligationsAutoProver implements IViewActionDelegate {

	ISelection sel;
	
	public ObligationsAutoProver() {
		// TODO Auto-generated constructor stub
	}

	public void init(IViewPart view) {
		// Do nothing
	}

	public void run(IAction action) {
		// Rerun the auto prover on selected elements.
		// The enablement condition guarantees that only machineFiles and
		// contextFiles are selected.
		
		assert (sel instanceof IStructuredSelection);
		IStructuredSelection ssel = (IStructuredSelection) sel;
		
		// Collecting all the PSFiles
		Object[] objects = ssel.toArray();
		final List<IPSFile> psFiles = new ArrayList<IPSFile>(objects.length);
		for (Object obj : objects) {
			assert (obj instanceof IMachineFile || obj instanceof IContextFile);
			if (obj instanceof IMachineFile) {
				psFiles.add(((IMachineFile) obj).getPSFile());
			}
			else
				psFiles.add(((IContextFile) obj).getPSFile());
		}
		
		// Run the auto prover on all remaining POs
		IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				for (IPSFile psFile : psFiles) {
					if (psFile.exists()) {
						if (ObligationExplorerUtils.DEBUG)
							ObligationExplorerUtils.debug("Run Auto Prover on "
									+ psFile.getBareName());
						try {
							(new AutoPOM()).run(null, psFile.getResource(),
									monitor);
						} catch (RodinDBException e) {
							if (ObligationExplorerUtils.DEBUG)
								e.printStackTrace();
							continue;
						}
					}
				}
			}
			
		};
		
		runWithProgress(op);
	}

	private void runWithProgress(IRunnableWithProgress op) {
		final Shell shell = Display.getDefault().getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.sel = selection;
	}

}
