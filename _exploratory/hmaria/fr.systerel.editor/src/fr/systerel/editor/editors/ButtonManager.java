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

package fr.systerel.editor.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorItem;

/**
 *
 */
public class ButtonManager {

	private DocumentMapper documentMapper;
	private StyledText styledText;
	private ProjectionViewer viewer;
	private Menu menu;
	private RodinEditor editor;

	public ButtonManager(DocumentMapper documentMapper, StyledText styledText,
			ProjectionViewer viewer, RodinEditor editor) {
		this.documentMapper = documentMapper;
		this.styledText = styledText;
		this.viewer = viewer;
		this.editor = editor;
	}

	public void createButtons() {
//		createMenu();
//		EditorItem[] elements = documentMapper.getEditorElements();
//		ElementButton button;
//		for (EditorItem element : elements) {
//			button = new ElementButton(styledText, element,
//					viewer);
//			button.getButton().setMenu(menu);
//		}
	}

	public void createMenu() {
		String id = "buttonMenu";
		MenuManager manager = new MenuManager(id, id);
		menu = manager.createContextMenu(styledText);
		manager.add(editor.getAction(ITextEditorActionConstants.DELETE));
		manager.add(new Action("test action") {
			public void run() {
				System.out.println("action!");
			}
		});
	}

}
