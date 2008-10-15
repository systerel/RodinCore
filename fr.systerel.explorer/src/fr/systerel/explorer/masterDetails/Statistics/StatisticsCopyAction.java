/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.masterDetails.Statistics;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import fr.systerel.explorer.Activator;

/**
 * This is a copy action for IStatistics.
 *
 */
public class StatisticsCopyAction extends Action implements ISelectionChangedListener{
	private Clipboard clipboard;
	private ISelectionProvider selectionProvider;
	private boolean copyLabel;
	
	/**
	 * This is a copy Action for IStatistics.
	 * @param selectionProvider
	 * @param copyLabel Indicates whether the label of the statistics is copied too.
	 */
	public StatisticsCopyAction (ISelectionProvider selectionProvider, boolean copyLabel) {
		super("Copy");
	    clipboard= new Clipboard(Activator.getDefault().getWorkbench().getDisplay());
	    this.selectionProvider = selectionProvider;
	    selectionProvider.addSelectionChangedListener(this);
	    this.copyLabel = copyLabel;
	    setEnabled(false);
	}

	@Override
	public void run() {
		if (selectionProvider.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();
			//create a suitable string from the selected statistics.
			String text = "";
			for (Object element : selection.toArray()) {
				if (element instanceof IStatistics){
					IStatistics stats = (IStatistics) element;
					if (copyLabel) {
						text += stats.getParentLabel() +'\u0009';
					}
					text += stats.getTotal();
					text += '\u0009';
					text += stats.getAuto();
					text += '\u0009';
					text += stats.getManual();
					text += '\u0009';
					text += stats.getReviewed();
					text += '\u0009';
					text += stats.getUndischargedRest();
					text += System.getProperty("line.separator");
				}
			}
			// copy the string to the clipboard.
			if (text.length() > 0) {
				clipboard.setContents(new Object[] { text },
						new Transfer[] { TextTransfer.getInstance() });
			}
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		//disable the action for empty selections
		if (event.getSelection().isEmpty()) {
			setEnabled(false);
		} else setEnabled(true);
		
	}

}
