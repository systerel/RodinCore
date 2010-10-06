/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed double click behavior
 *     Systerel - set tree focus when dispose
 *     ETH Zurich - adapted to org.rodinp.keyboard
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.EventBUtils.getFormulaFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.eventb.internal.ui.autocompletion.EventBContentProposalAdapter;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This class implements the decorator for Text input that is used in
 *         the Editable Tree Viewer.
 */
public abstract class ElementText extends TimerText implements ModifyListener {

	IRodinElement element;

	int column;

	TreeEditor editor;

	IEventBInputText text;

	TreeItem item;

	Tree tree;

	int inset;

	String original;

	final EventBContentProposalAdapter adapter ;
	
	/**
	 * @author htson
	 *         <p>
	 *         This class handles the different changes to the Text.
	 */
	class ElementTextListener implements Listener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		@Override
		public void handleEvent(Event event) {
			Text textWidget = text.getTextWidget();
			final String contents = textWidget.getText();
			switch (event.type) {
			case SWT.FocusOut:
				if (adapter.isProposalPopupOpen())
					break;
				commit(element, column, RodinKeyboardPlugin.getDefault()
						.translate(contents));
				textWidget.getParent().dispose();
				text.dispose();
				break;
			case SWT.Verify:
				// UIUtils.debug("Verify");
				String newText = textWidget.getText();
				String leftText = newText.substring(0, event.start);
				String rightText = newText.substring(event.end, newText
						.length());
				GC gc = new GC(textWidget);
				Point size = gc.textExtent(leftText + event.text + rightText);
				gc.dispose();
				size = textWidget.computeSize(size.x, SWT.DEFAULT);
				editor.horizontalAlignment = SWT.LEFT;
				Rectangle itemRect = item.getBounds(),
				rect = tree.getClientArea();
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils.debug("ItemRect: " + itemRect);
					EventBEditorUtils.debug("Rect: " + rect);
					EventBEditorUtils.debug("Size: " + size.x);
				}
				editor.minimumWidth = Math.max(size.x, itemRect.width) + inset
						* 2;
				int left = itemRect.x,
				right = rect.x + rect.width;
				editor.minimumWidth = Math.min(editor.minimumWidth, right
						- left);
				editor.minimumHeight = size.y + inset * 2;
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Editor layout --- Height: "
							+ editor.minimumHeight + " Width: "
							+ editor.minimumWidth);
				editor.layout();
				break;
			case SWT.Traverse:
				if (adapter.isProposalPopupOpen())
					break;
				switch (event.detail) {
				case SWT.TRAVERSE_RETURN:
					commit(element, column, RodinKeyboardPlugin.getDefault()
							.translate(contents));
					textWidget.getParent().dispose();
					text.dispose();
					event.doit = false;
					tree.setFocus();
					break;
				case SWT.TRAVERSE_ESCAPE:
					commit(element, column, original);
					textWidget.getParent().dispose();
					text.dispose();
					event.doit = false;
					tree.setFocus();
					break;
				case SWT.TRAVERSE_TAB_NEXT:
					commit(element, column, RodinKeyboardPlugin.getDefault()
							.translate(contents));
					textWidget.getParent().dispose();
					text.dispose();
					nextEditableCell();
					event.doit = false;
					break;
				case SWT.TRAVERSE_TAB_PREVIOUS:
					commit(element, column, RodinKeyboardPlugin.getDefault()
							.translate(contents));
					textWidget.getParent().dispose();
					text.dispose();
					prevEditableCell();
					event.doit = false;
					break;
				}
			}
		}
	}

	/**
	 * Committing the content of the text according to the current column and
	 * the given content.
	 * <p>
	 * 
	 * @param elm
	 *            The Rodin Element corresponding to this Text
	 * 
	 * @param col
	 *            The column in the Editable Tree Viewer.
	 * @param contents
	 *            The content that will be committed.
	 */
	public abstract void commit(IRodinElement elm, int col,
			String contents);

	/**
	 * Select the next editable cell in the Editable Tree Viewer. If there are
	 * no next editable cell then do nothing.
	 */
	public abstract void nextEditableCell();

	/**
	 * Select the previous editable cell in the Editable Tree Viewer. If there
	 * are no previous editable cell then do nothing.
	 */
	public abstract void prevEditableCell();

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param text
	 *            The actual Text Widget.
	 * @param editor
	 *            The Tree Editor.
	 * @param item
	 *            The Tree Item associated with this Text Widget.
	 * @param element
	 *            The Rodin Element corresponds to this Text Widget
	 * @param column
	 *            The column in the Editable Tree Viewer of the Text Widget.
	 */
	public ElementText(IEventBInputText text, TreeEditor editor, Tree tree,
			TreeItem item, IRodinElement element, int column, int delay) {
		super(text.getTextWidget(), delay);
		this.adapter = getProposalAdapter(element, column, text.getTextWidget());
		this.text = text;
		this.element = element;
		this.editor = editor;
		this.column = column;
		this.tree = tree;
		this.item = item;
		this.original = item.getText(column);
		boolean isCarbon = SWT.getPlatform().equals("carbon");
		inset = isCarbon ? 0 : 1;
		Listener textListener = new ElementTextListener();
		Text textWidget = text.getTextWidget();
		textWidget.addListener(SWT.FocusOut, textListener);
		textWidget.addListener(SWT.Traverse, textListener);
		textWidget.addListener(SWT.Verify, textListener);
		textWidget.addModifyListener(this);
	}

	private static EventBContentProposalAdapter getProposalAdapter(
			IRodinElement elem, int col, Text txt) {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		final IAttributeDesc attrDesc = registry.getElementDesc(elem).atColumn(
				col);
		final IAttributeType attrType = attrDesc.getAttributeType();
		final IInternalElement element = (IInternalElement) elem;
		final IAttributeLocation location = RodinCore.getInternalLocation(
				element, attrType);
		return ContentProposalFactory.makeContentProposal(location, txt,
				getFormulaFactory(element));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.TimerText#response()
	 */
	@Override
	protected void response() {
		Text textWidget = text.getTextWidget();
		String contents = textWidget.getText();
		commit(element, column, contents);
	}

}
