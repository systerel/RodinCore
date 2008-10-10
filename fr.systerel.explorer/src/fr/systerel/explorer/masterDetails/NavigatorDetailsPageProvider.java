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


package fr.systerel.explorer.masterDetails;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * The is the DetailsPageProvider for the MasterDetails pattern.
 * It always provides the same page for all objects.
 * Users wishing to display their own content should add a tab to that details page
 * via the extension point.
 *
 */
public class NavigatorDetailsPageProvider implements IDetailsPageProvider {

	private final NavigatorDetailsPage detailsPage = new NavigatorDetailsPage();
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPageProvider#getPage(java.lang.Object)
	 */
	public IDetailsPage getPage(Object key) {
		//currently there is just one page
		if (key.equals(1)) {
			return detailsPage;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPageProvider#getPageKey(java.lang.Object)
	 */
	public Object getPageKey(Object object) {
		//always use the same page
		return 1;
	}

}
