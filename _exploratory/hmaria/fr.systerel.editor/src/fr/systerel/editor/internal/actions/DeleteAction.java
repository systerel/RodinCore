/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.editor.documentModel.EditorElement;
import fr.systerel.editor.editors.RodinEditor;

public class DeleteAction extends RodinEditorAction {

	public DeleteAction(RodinEditor editor) {
		super(editor);
	}

	public void run() {
		ISelection selection = editor.getSelectionProvider().getSelection();
		if (selection instanceof TextSelection) {
			TextSelection text = (TextSelection) selection;
			EditorElement element = editor.getDocumentMapper().findEditorElement(
					text.getOffset(), text.getLength());
			if (element != null) {
				IRodinElement toDelete = element.getRodinElement();
				if (toDelete != null) {
					try {
						toDelete.getRodinDB().delete(
								new IRodinElement[] { toDelete }, true,
								null);
					} catch (RodinDBException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
