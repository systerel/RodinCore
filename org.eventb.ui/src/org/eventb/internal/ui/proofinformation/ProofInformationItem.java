/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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
			return new ProofInfoPredicateItem(element);
		}
		if (element instanceof IExpressionElement) {
			return new ProofInfoExpressionItem(element);
		}
		if (element instanceof IAssignmentElement) {
			return new ProofInfoAssignmentItem(element);
		}
		if (element instanceof IRefinesEvent) {
			return new ProofInfoRefinesEventItem(element);
		}
		if (element instanceof ILabeledElement) {
			return new ProofInfoLabeledItem(element);			
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

		public ProofInfoRefinesEventItem(IRodinElement element) {
			super(element);
		}

		@Override
		protected String getIdentStr(IRodinElement element)
				throws RodinDBException {
			return ((IRefinesEvent) element).getAbstractEventLabel();
		}

	}

	public static class ProofInfoLabeledItem extends ProofInformationItem {

		protected String label;
		protected String separatedText;
		protected final String SEPARATOR = ": ";

		public ProofInfoLabeledItem(IRodinElement element) {
			super(element);
			try {
				this.label = getLabelText(element);
				this.separatedText = getSeparatedText();
			} catch (RodinDBException e) {
				this.label = N_A;
			}
		}

		public String getLabelText(IRodinElement element)
				throws RodinDBException {
			final ILabeledElement labeledElt = (ILabeledElement) element;
			return makeHyperlink(id, labeledElt.getLabel());
		}

		public String getSeparatedText() {
			return "";
		}

		@Override
		public String getInfoText() {
			return label + SEPARATOR + getSeparatedText();
		}

	}

	public static class ProofInfoPredicateItem extends ProofInfoLabeledItem {

		public ProofInfoPredicateItem(IRodinElement element) {
			super(element);
			try {
				separatedText = UIUtils.XMLWrapUp(((IPredicateElement) element)
						.getPredicateString());
			} catch (RodinDBException e) {
				separatedText = N_A;
			}
		}

		@Override
		public String getSeparatedText() {
			return this.separatedText;
		}

	}

	public static class ProofInfoExpressionItem extends ProofInfoLabeledItem {

		public ProofInfoExpressionItem(IRodinElement element) {
			super(element);
			try {
				separatedText = UIUtils
						.XMLWrapUp(((IExpressionElement) element)
								.getExpressionString());
			} catch (RodinDBException e) {
				separatedText = N_A;
			}
		}

		@Override
		public String getSeparatedText() {
			return this.separatedText;
		}

	}

	public static class ProofInfoAssignmentItem extends ProofInfoLabeledItem {

		public ProofInfoAssignmentItem(IRodinElement element) {
			super(element);
			try {
				separatedText = UIUtils
						.XMLWrapUp(((IAssignmentElement) element)
								.getAssignmentString());
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
