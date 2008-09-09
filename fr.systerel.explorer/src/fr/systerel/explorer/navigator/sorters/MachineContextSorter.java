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


package fr.systerel.explorer.navigator.sorters;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;

/**
 * A sorter for the <code>MachineContextContentProvider</code>
 * @author Maria Husmann
 *
 */
public class MachineContextSorter extends ViewerSorter {


	@Override
	public int category(Object element) {
		if (element instanceof IContextFile) {
			return 1;
		}
		if (element instanceof IMachineFile) {
			return 2;
		}
		return 3;
	}
}
