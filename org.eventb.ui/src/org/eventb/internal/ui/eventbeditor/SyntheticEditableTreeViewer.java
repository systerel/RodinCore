package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public class SyntheticEditableTreeViewer extends EventBEditableTreeViewer {

	public SyntheticEditableTreeViewer(Composite parent, int style, IRodinFile rodinFile) {
		super(parent, style, rodinFile);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createTreeColumns(Tree tree) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void commit(IRodinElement element, int col, String text) {
		// TODO Auto-generated method stub

	}

}
