/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticRefMaker;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticXMLSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.ui.UIUtils;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProfileImportExport {

	// maximum size of imported file (in bytes)
	private static final int MAX_IMPORT_SIZE = 1000000;
	private static final String MAX_IMPORT_SIZE_REPR = "1 Mo";

	private static class ProfileContentProvider implements
			IStructuredContentProvider {

		public ProfileContentProvider() {
			// avoid synthetic accessors
		}

		@Override
		public void dispose() {
			// do nothing

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof CachedPreferenceMap)) {
				return null;
			}
			@SuppressWarnings("unchecked")
			final CachedPreferenceMap<ITacticDescriptor> cache = (CachedPreferenceMap<ITacticDescriptor>) inputElement;
			final List<IPrefMapEntry<ITacticDescriptor>> entries = cache
					.getEntries();

			return entries.toArray(new Object[entries.size()]);
		}

	}

	private static class ProfileLabelProvider extends LabelProvider {

		public ProfileLabelProvider() {
			// avoid synthetic accessors
		}

		@Override
		public String getText(Object element) {
			if (!(element instanceof IPrefMapEntry)) {
				return super.getText(element);
			}
			final IPrefMapEntry<?> entry = (IPrefMapEntry<?>) element;
			return entry.getKey();
		}
	}

	public static ListSelectionDialog makeProfileSelectionDialog(
			Shell parentShell, CachedPreferenceMap<ITacticDescriptor> input,
			String message, List<IPrefMapEntry<ITacticDescriptor>> initSelected) {
		final ListSelectionDialog dialog = new ListSelectionDialog(parentShell,
				input, new ProfileContentProvider(),
				new ProfileLabelProvider(), message);
		dialog.setInitialElementSelections(initSelected);
		return dialog;
	}

	public static void saveExported(Shell parentShell,
			CachedPreferenceMap<ITacticDescriptor> exported) {
		final String path = new FileDialog(parentShell, SWT.SAVE).open();
		if (path == null) {
			return;
		}

		final File file = new File(path);

		final String prefStr = exported.extract();
		writeFile(file, prefStr);
	}

	public static CachedPreferenceMap<ITacticDescriptor> loadImported(
			Shell parentShell) {
		final FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
		final String path = fileDialog.open();
		if (path == null) {
			return null;
		}

		final File file = new File(path);
		if (!file.isFile()) {
			return null;
		}

		final CachedPreferenceMap<ITacticDescriptor> newCache = new CachedPreferenceMap<ITacticDescriptor>(
				makeTacticXMLSerializer(), makeTacticRefMaker());
		final String prefStr = readFile(file);
		if (prefStr == null) {
			showImportError(parentShell, path);
			return null;
		}
		try {
			newCache.inject(prefStr);
			final int unitErrorCount = newCache.getUnitErrorCount();
			if (unitErrorCount > 0) {
				final String message = "Problems occurred for "
						+ unitErrorCount
						+ " tactic profiles.\n" +
						"These profiles cannot be imported and will not be displayed.\n" +
						"See error log for details.";
				MessageDialog.openWarning(parentShell, "Import warning",
						message);
			}
			return newCache;
		} catch (IllegalArgumentException e) {
			// error already logged
			showImportError(parentShell, path);
			return null;
		}
	}

	private static void showImportError(Shell shell, String path) {
		final String message = "Error while importing tactic profiles from "
				+ path + "\nSee error log for details.";
		MessageDialog.openError(shell, "Import error", message);
	}

	private static void writeFile(File file, String contents) {
		try {
			final FileWriter writer = new FileWriter(file);
			writer.write(contents);
			writer.close();
		} catch (IOException e) {
			UIUtils.log(e, "while exporting profiles to: " + file.getPath());
		}
	}

	private static String readFile(File file) {
		final long length = file.length();
		if (length > MAX_IMPORT_SIZE) {
			UIUtils.log(null, "Tactic profile import error: file is too big "
					+ file.getPath() + "\nmax size is " + MAX_IMPORT_SIZE_REPR);
			return null;
		}
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					file));
			final StringBuilder sb = new StringBuilder();
			String strLine;
			while ((strLine = reader.readLine()) != null) {
				sb.append(strLine);
			}
			reader.close();
			return sb.toString();
		} catch (IOException e) {
			UIUtils.log(e, "while reading " + file.getPath());
			return null;
		}
	}

}
