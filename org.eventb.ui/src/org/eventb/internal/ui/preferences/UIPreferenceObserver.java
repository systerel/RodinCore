/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static org.eventb.internal.ui.preferences.PreferenceConstants.P_HIGHLIGHT_IN_PROVERUI;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.prover.ProverUI;

/**
 * This class is responsible for reflecting the UI preference store towards
 * preference instances (such as {@link IAutoTacticPreference}).
 * <p>
 * It listens to preference store changes and automatically updates
 * corresponding preferences. Thus, preference setters should not directly
 * access preference instances, but rather set preferences in the preference
 * store.
 * </p>
 * <p>
 * Observed preferences are:
 * <li>P_POSTTACTIC_ENABLE</li>
 * <li>P_AUTOTACTIC_ENABLE</li>
 * <li>P_CONSIDER_HIDDEN_HYPOTHESES</li>
 * </p>
 * <p>
 * This class is intended to be used in the following way during preference
 * initialisation:
 * 
 * <pre>
 * final IPreferenceStore store = getPreferenceStore();
 * final UIPreferenceObserver prefObs = new UIPreferenceObserver(store);
 * prefObs.initPreferences();
 * store.addPropertyChangeListener(prefObs);
 * </pre>
 * 
 * </p>
 * 
 * @author Nicolas Beauger
 */
public class UIPreferenceObserver implements IPropertyChangeListener {

	private static void updateProvingEditors(boolean activation) {
		final IWorkbenchWindow[] wws = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (IWorkbenchWindow w : wws) {
			for (IWorkbenchPage p : w.getPages()) {
				for (IEditorReference r : p.getEditorReferences()) {
					final IEditorPart editor = r.getEditor(false);
					if (editor instanceof ProverUI) {
						((ProverUI) editor).getHighlighter().activateHighlight(
								activation);
					}
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		final String property = event.getProperty();
		if (property.equals(P_HIGHLIGHT_IN_PROVERUI)) {
			updateProvingEditors((Boolean) event.getNewValue());
		}
	}
}