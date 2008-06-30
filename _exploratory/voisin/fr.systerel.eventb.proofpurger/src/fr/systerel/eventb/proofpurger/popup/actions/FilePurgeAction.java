package fr.systerel.eventb.proofpurger.popup.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eventb.core.IEventBFile;
import org.eventb.core.IPRFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class FilePurgeAction implements IObjectActionDelegate {
	
	private IWorkbenchPartSite site;

	private IStructuredSelection selection;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		site = targetPart.getSite();
	}
	
	private void collectInformation() {
		// TODO implement
	}

	public void run(IAction action) {
		
		final IPRFile[] files;
		try {
			files = getSelectedPRFiles();
		} catch (RodinDBException e) {
			// TODO Display the DB error to the user
			return;
		}
		
		if (files == null) {
			// TODO Indicate invalid selection to the user
			return;
		}
		
		final StringBuilder b = new StringBuilder();
		for (IPRFile file: files) {
			b.append('\t');
			b.append(file.getPath());
			b.append('\n');
		}

		// TODO Implement a progress monitor here.
		BusyIndicator.showWhile(site.getShell().getDisplay(), new Runnable() {
			public void run() {
				collectInformation();
			}
		});

        
		// TODO properly implement this action
		final Shell shell = site.getShell();
		MessageDialog.openInformation(
			shell,
			"Proof Purger Plug-in",
			"Purge unused proofs was executed on\n" + b.toString());
	}

	private IPRFile[] getSelectedPRFiles() throws RodinDBException {
		if (selection == null) {
			return null;
		}
		final List<IPRFile> result = new ArrayList<IPRFile>(selection.size());
		for (Object o : selection.toList()) {
			final IRodinElement elem = asRodinElement(o);
			if (elem instanceof IRodinProject) {
				addProjectPRFiles(result, (IRodinProject) elem);
			} else if (elem instanceof IEventBFile) {
				result.add(((IEventBFile) elem).getPRFile());
			} else {
				// Unexpected openable or null
				return null;
			}
		}
		return result.toArray(new IPRFile[result.size()]);
	}

	private IRodinElement asRodinElement(Object o) {
		if (o instanceof IRodinElement) {
			return (IRodinElement) o;
		}
		if (o instanceof IAdaptable) {
			final IAdaptable adaptable = (IAdaptable) o;
			return (IRodinElement) adaptable.getAdapter(IRodinElement.class); 
		}
		return null;
	}

	private void addProjectPRFiles(List<IPRFile> list, IRodinProject prj)
			throws RodinDBException {
		final IPRFile[] prjFiles = prj.getChildrenOfType(IPRFile.ELEMENT_TYPE);
		list.addAll(Arrays.asList(prjFiles));
	}

	public void selectionChanged(IAction action, ISelection s) {
		if (s instanceof IStructuredSelection) {
			selection = (IStructuredSelection) s;
		} else {
			selection = null;
		}
	}

}
