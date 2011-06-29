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

package fr.systerel.editor.internal.presentation;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.swt.custom.StyleRange;

/**
 *
 */
public class RodinDamagerRepairer implements IPresentationDamager,
		IPresentationRepairer {
	

	/** The document this object works on */
	protected IDocument fDocument;
	/** The default text attribute if non is returned as data by the current token */
	protected TextAttribute fDefaultTextAttribute;
	
	public RodinDamagerRepairer(TextAttribute defaultTextAttribute) {
		Assert.isNotNull(defaultTextAttribute);

		fDefaultTextAttribute = defaultTextAttribute;
	}

	@Override
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
			boolean documentPartitioningChanged) {
		if (!documentPartitioningChanged) {
			return new Region(event.getOffset(), event.getLength());
		}
		return partition;
	}

	@Override
	public void setDocument(IDocument document) {
		fDocument = document;

	}

	@Override
	public void createPresentation(TextPresentation presentation,
			ITypedRegion damage) {
		
		StyleRange range = new StyleRange(damage.getOffset(), damage.getLength(),
				fDefaultTextAttribute.getForeground(), fDefaultTextAttribute.getBackground(), fDefaultTextAttribute.getStyle() );
		presentation.addStyleRange(range);

	}

}
