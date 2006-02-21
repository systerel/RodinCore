/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Rodin Group
 *******************************************************************************/

package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.ISees;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.prover.rules.AllI;
import org.eventb.core.prover.rules.ConjI;
import org.eventb.core.prover.rules.Hyp;
import org.eventb.core.prover.rules.IProofRule;
import org.eventb.core.prover.rules.ImpI;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * This is a class which store utility static method that can be used in the
 * development
 */
public class Utils {
	
	public static final String CONJI_SYMBOL = "∧";
	
	public static final String IMPI_SYMBOL = "⇒";
	
	public static final String HYP_SYMBOL = "hp";
	
	public static final String ALLI_SYMBOL = "∀";
	
	public static final String TRIVIAL_SYMBOL = "⊤";
	
	/**
	 * Getting the file name without the extension.
	 * <p>
	 * @param fileName the full file name (with the extension)
	 * <p>
	 * @return the name without the extension
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) return fileName.substring(0, dotLoc);
		else return fileName;
	}
	

	/**
	 * Getting the image corresponding to an object.
	 * <p>
	 * @param obj Any object
	 * @return The image for displaying corresponding to the input object
	 */
	public static Image getImage(Object obj) {
		if (obj instanceof IRodinElement)
			return (getRodinElementImage((IRodinElement) obj));
		if (obj instanceof TreeNode)
			return (getTreeNodeImage((TreeNode) obj));
		return null;
	}
	
	
	/*
	 * Getting the impage corresponding to a Rodin element.
	 * <p>
	 * @param element A Rodin element
	 * @return The image for displaying corresponding to the input element
	 */
	private static Image getRodinElementImage(IRodinElement element) {
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		if (element instanceof IRodinProject)
			return registry.get(EventBImage.IMG_PROJECT);
		
		if (element instanceof IMachine)
			return registry.get(EventBImage.IMG_MACHINE);
		
		if (element instanceof IContext)
			return registry.get(EventBImage.IMG_CONTEXT);

		if (element instanceof ISees)
			return registry.get(EventBImage.IMG_CONTEXT);
		
		if (element instanceof IVariable)
			return registry.get(EventBImage.IMG_VARIABLE);

		if (element instanceof IInvariant)
			return registry.get(EventBImage.IMG_INVARIANT);

		if (element instanceof ITheorem)
			return registry.get(EventBImage.IMG_THEOREM);

		if (element instanceof IEvent)
			return registry.get(EventBImage.IMG_EVENT);

		if (element instanceof IGuard)
			return registry.get(EventBImage.IMG_GUARD);
		
		if (element instanceof IAction)
			return registry.get(EventBImage.IMG_ACTION);
		
		if (element instanceof ICarrierSet)
			return registry.get(EventBImage.IMG_CARRIER_SET);

		if (element instanceof IConstant)
			return registry.get(EventBImage.IMG_CONSTANT);

		if (element instanceof IAxiom)
			return registry.get(EventBImage.IMG_AXIOM);

		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}


	/*
	 * Getting the impage corresponding to a tree node.
	 * <p>
	 * @param element A Tree node
	 * @return The image for displaying corresponding to the tree node
	 */
	private static Image getTreeNodeImage(TreeNode node) {
		
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		
		if (node.isType(IVariable.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_VARIABLES);
		if (node.isType(IInvariant.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_INVARIANTS);
		if (node.isType(ITheorem.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_THEOREMS);
		if (node.isType(IAction.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_INITIALISATION);
		if (node.isType(IEvent.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_EVENTS);
		if (node.isType(IGuard.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_GUARDS);
		if (node.isType(ICarrierSet.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_CARRIER_SETS);
		if (node.isType(IConstant.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_CONSTANTS);
		if (node.isType(IAxiom.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_AXIOMS);
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}


	/**
	 * Method to return the openable for an object (IRodinElement or TreeNode).
	 * <p>
	 * @param node A Rodin Element or a tree node
	 * @return The IRodinFile corresponding to the input object
	 */
	public static IOpenable getOpenable(Object node) {
		if (node instanceof TreeNode) return ((IRodinElement) ((TreeNode) node).getParent()).getOpenable();
		else if (node instanceof IRodinElement) return ((IRodinElement) node).getOpenable();
		
		return null;
	}


	/**
	 * @author htson
	 * <p>
	 * Class which provide the label for Rodin elements.
	 */
	public static class ElementLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof ISees || obj instanceof IAction) {
				try {
					return  ((IUnnamedInternalElement) obj).getContents();
				}
				catch (RodinDBException e) {
					e.printStackTrace();
					return "";
				}
			}
			if (obj instanceof IInternalElement) return ((IInternalElement) obj).getElementName();
			return obj.toString();
		}

		public Image getImage(Object obj) {
			return Utils.getImage(obj);
		}
	}


	public static List<String> getApplicableToGoal(IProverSequent ps) {
		List<String> names = new ArrayList<String>();
		
		IProofRule rule;
		rule = new ConjI();
		if (rule.isApplicable(ps)) names.add(CONJI_SYMBOL);
		
		rule = new ImpI();
		if (rule.isApplicable(ps)) names.add(IMPI_SYMBOL);
		
		rule = new Hyp();
		if (rule.isApplicable(ps)) names.add(HYP_SYMBOL);
		
		rule = new AllI();
		if (rule.isApplicable(ps)) names.add("ALLI_SYMBOL");
		
		return names;
	}

	
	public static List<String> getApplicableToHypothesis(Hypothesis hyp) {
		List<String> result = new ArrayList<String>();
		result.add("∧");
		result.add("⇒");
		result.add("hp");
		return result;
	}

}
