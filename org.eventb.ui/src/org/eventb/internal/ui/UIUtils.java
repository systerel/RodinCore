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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.ISees;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.eventb.internal.ui.prover.ProverUI;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * This is a class which store utility static method that can be used in the
 * development
 */
public class UIUtils {
	
	public static final boolean debug = false;
	
	public static final String CONJI_SYMBOL = "\u2227";
	public static final String IMPI_SYMBOL = "\u21d2";
	public static final String ALLI_SYMBOL = "\u2200";
	public static final String EXI_SYMBOL = "\u2203";
	public static final String NEG_SYMBOL = "\u00ac";
	
	public static final String ALLF_SYMBOL = "\u2200";
	public static final String CONJD_SYMBOL = "\u2227";
	public static final String IMPD1_SYMBOL = "\u21d2";
	public static final String IMPD2_SYMBOL = "ip1";
	public static final String DISJE_SYMBOL = "\u22c1";
	public static final String EXF_SYMBOL = "\u2203";
	public static final String EQE1_SYMBOL = "eh";
	public static final String EQE2_SYMBOL = "he";
	public static final String FALSIFY_SYMBOL = "\u22a5";
	
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
			return UIUtils.getImage(obj);
		}
	}


	public static List<String> getApplicableToGoal(Predicate goal) {
		List<String> names = new ArrayList<String>();
		
		if (Tactics.impI_applicable(goal)) names.add(IMPI_SYMBOL);
		if (Tactics.conjI_applicable(goal)) names.add(CONJI_SYMBOL);
		if (Tactics.allI_applicable(goal)) names.add(ALLI_SYMBOL);
		if (Tactics.exI_applicable(goal)) names.add(EXI_SYMBOL);
		if (Tactics.removeNegGoal_applicable(goal)) names.add(NEG_SYMBOL);
		// Extra tactics applicable to goal should be added here.
		return names;
	}

	
	public static List<String> getApplicableToHypothesis(Hypothesis hyp) {
		List<String> names = new ArrayList<String>();
		
		names.add(FALSIFY_SYMBOL);
		if (Tactics.allF_applicable(hyp)) names.add(ALLF_SYMBOL);
		if (Tactics.conjD_applicable(hyp)) names.add(CONJD_SYMBOL);
		if (Tactics.impD_applicable(hyp)) {
			names.add(IMPD1_SYMBOL);
			names.add(IMPD2_SYMBOL);
		}
		if (Tactics.exF_applicable(hyp)) names.add(EXF_SYMBOL);
		if (Tactics.disjE_applicable(hyp)) names.add(DISJE_SYMBOL);
		if (Tactics.eqE_applicable(hyp)) {
			names.add(EQE1_SYMBOL);
			names.add(EQE2_SYMBOL);
		}
		
		// Extra tactics applicable to hypothesis should be added here.
		return names;
	}

	public static String XMLWrapUp(String input) {
		String output = input;
		output = output.replaceAll("&", "&amp;");
		output = output.replaceAll("<", "&lt;");
		output = output.replaceAll(">", "&gt;");
		return output;
	}

	/*
	 * Link the current object to an Prover UI editor.
	 */
	public static void linkToProverUI(Object obj) {
		String editorId = ProverUI.EDITOR_ID;
		
		IPRFile construct = null;
		if (obj instanceof IRodinProject) return; 
		if (obj instanceof IPRFile) construct = (IPRFile) obj; 
		else if (obj instanceof IRodinElement) 
			construct = (IPRFile) ((IRodinElement) obj).getParent();
		Assert.isTrue(construct != null, "construct must be initialised by now");
//		if (UIUtils.debug) System.out.println("Link to " + construct.getElementName());
		try {
			IEditorInput fileInput = new FileEditorInput(construct.getResource());
			ProverUI editor = (ProverUI) EventBUIPlugin.getActivePage().openEditor(fileInput, editorId);
			if (!(obj instanceof IPRFile)) editor.setCurrentPO((IPRSequent) obj);
		} catch (PartInitException e) {
			MessageDialog.openError(null, null, "Error open the editor");
			e.printStackTrace();
			// TODO EventBImage.logException(e);
		}
		return;
	}

	/*
	 * Link the current object to an Event-B editor.
	 */
	public static void linkToEventBEditor(Object obj) {
		String editorId = EventBEditor.EDITOR_ID;
		IRodinFile construct;
		
		if (!(obj instanceof IRodinProject)) {
			construct = (IRodinFile) UIUtils.getOpenable(obj); 
			try {
				IEditorInput fileInput = new FileEditorInput(construct.getResource());
				EventBEditor editor = (EventBEditor) EventBUIPlugin.getActivePage().openEditor(fileInput, editorId);
				editor.setSelection(obj);
			} catch (PartInitException e) {
				MessageDialog.openError(null, null, "Error open the Event-B Editor");
				e.printStackTrace();
				// TODO EventBUIPlugin.logException(e);
			}
		}
		return;
	}


	/**
	 * Utitlity method to create a text and link with the same label
	 * <p>
	 * @param link a String
	 * @return XML formatted string represents the link
	 */
	public static String makeHyperlink(String link) {
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">" + UIUtils.XMLWrapUp(link) + "</a>";
	}

	/**
	 * Utitlity method to create a text and link with the same label
	 * <p>
	 * @param link a String
	 * @return XML formatted string represents the link
	 */
	public static String makeHyperlink(String link, String text) {
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">" + UIUtils.XMLWrapUp(text) + "</a>";
	}


	public static void activateView(String View_ID) {
		IViewPart aView = EventBUIPlugin.getActivePage().findView(View_ID);
		if (aView != null){
			EventBUIPlugin.getActivePage().activate(aView);
		}
		return;
	}
}
