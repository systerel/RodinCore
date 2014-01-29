/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofinformation;

import static org.eventb.internal.ui.UIUtils.makeHyperlink;

import org.eventb.core.IAssignmentElement;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Classes for inner elements to be displayed in the ProofInformation view.
 * @author "Thomas Muller"
 */
public abstract class ProofInformationItem {

	private static final String N_A = "n/a";
	protected final String id;

	public ProofInformationItem(IRodinElement element) {
		this.id = element.getHandleIdentifier();
	}

	public String getInfoText() {
		return N_A;
	}
	
	public static ProofInformationItem getItem(IRodinElement element) {
		if (element instanceof IIdentifierElement) {
			return new ProofInfoIdentItem(element);
		}
		if (element instanceof IPredicateElement) {
			return new ProofInfoPredicateItem((IPredicateElement) element);
		}
		if (element instanceof IExpressionElement) {
			return new ProofInfoExpressionItem((IExpressionElement) element);
		}
		if (element instanceof IAssignmentElement) {
			return new ProofInfoAssignmentItem((IAssignmentElement) element);
		}
		if (element instanceof IRefinesEvent) {
			return new ProofInfoRefinesEventItem((IRefinesEvent) element);
		}
		if (element instanceof ILabeledElement) {
			return new ProofInfoMaybeLabeledElement(element);			
		}
		return null;
	}

	public static class ProofInfoIdentItem extends ProofInformationItem {

		private String ident;

		public ProofInfoIdentItem(IRodinElement element) {
			super(element);
			try {
				final String identStr = getIdentStr(element);
				this.ident = makeHyperlink(id, identStr);
			} catch (RodinDBException e) {
				this.ident = N_A;
			}
		}

		protected String getIdentStr(IRodinElement element)
				throws RodinDBException {
			return ((IIdentifierElement) element).getIdentifierString();
		}

		@Override
		public String getInfoText() {
			return ident;
		}

	}

	public static class ProofInfoRefinesEventItem extends ProofInfoIdentItem {

		public ProofInfoRefinesEventItem(IRefinesEvent element) {
			super(element);
		}

		@Override
		protected String getIdentStr(IRodinElement element)
				throws RodinDBException {
			return ((IRefinesEvent) element).getAbstractEventLabel();
		}

	}

	public static class ProofInfoMaybeLabeledElement extends ProofInformationItem {

		protected String label;
		protected String separatedText;
		protected final String SEPARATOR = ": ";

		public ProofInfoMaybeLabeledElement(IRodinElement element) {
			super(element);
			try {
				this.label = getLabelText(element);
			} catch (RodinDBException e) {
				this.label = N_A;
			}
			this.separatedText = getSeparatedText();
		}

		public final String getLabelText(IRodinElement element)
				throws RodinDBException {
			final String elementLabel;
			if (element instanceof ILabeledElement) {
				elementLabel = ((ILabeledElement) element).getLabel();
			} else {
				// happens for variants
				return null;
			}
			return makeHyperlink(id, elementLabel);
		}

		public String getSeparatedText() {
			return "";
		}

		@Override
		public String getInfoText() {
			if (label == null) {
				return makeHyperlink(id, getSeparatedText());
			}
			return label + SEPARATOR + getSeparatedText();
		}

	}

	public static class ProofInfoPredicateItem extends ProofInfoMaybeLabeledElement {

		public ProofInfoPredicateItem(IPredicateElement element) {
			super(element);
			try {
				separatedText = UIUtils.XMLWrapUp(element.getPredicateString());
			} catch (RodinDBException e) {
				separatedText = N_A;
			}
		}

		@Override
		public String getSeparatedText() {
			return this.separatedText;
		}

	}

	public static class ProofInfoExpressionItem extends ProofInfoMaybeLabeledElement {

		public ProofInfoExpressionItem(IExpressionElement element) {
			super(element);
			try {
				separatedText = UIUtils
						.XMLWrapUp(element.getExpressionString());
			} catch (RodinDBException e) {
				separatedText = N_A;
			}
		}

		@Override
		public String getSeparatedText() {
			return this.separatedText;
		}

	}

	public static class ProofInfoAssignmentItem extends ProofInfoMaybeLabeledElement {

		public ProofInfoAssignmentItem(IAssignmentElement element) {
			super(element);
			try {
				separatedText = UIUtils
						.XMLWrapUp(element.getAssignmentString());
			} catch (RodinDBException e) {
				separatedText = N_A;
			}
		}

		@Override
		public String getSeparatedText() {
			return this.separatedText;
		}

	}

}
