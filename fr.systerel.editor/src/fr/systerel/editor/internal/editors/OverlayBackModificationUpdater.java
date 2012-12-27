/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

/**
 * Class able to refresh the contents of the overlay editor when the underneath
 * document is modified. This is the case for example, when the user "undoes"
 * the modification he made on the overlay contents.
 * 
 * @author "Thomas Muller"
 */
public class OverlayBackModificationUpdater implements IDocumentListener {

	final OverlayEditor parent;

	public OverlayBackModificationUpdater(OverlayEditor overlayEditor) {
		this.parent = overlayEditor;
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// ignore
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		parent.refreshOverlayContents(event);
	}

}
