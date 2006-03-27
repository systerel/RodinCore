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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
import org.eventb.internal.ui.eventbeditor.ElementAtributeInputDialog;
import org.eventb.internal.ui.eventbeditor.ElementNameContentInputDialog;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.eventbeditor.IntelligentNewVariableInputDialog;
import org.eventb.internal.ui.eventbeditor.NewEventInputDialog;
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
	
	public static final boolean DEBUG = false;
	
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
	
	public static void debug(String message) {
		if (DEBUG) System.out.println(message);
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
		if (Tactics.disjToImpGoal_applicable(goal)) names.add(DISJE_SYMBOL);
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
		if (Tactics.removeNegHyp_applicable(hyp)) names.add(NEG_SYMBOL);
		
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
		
		IPRFile component = null;
		if (obj instanceof IRodinProject) return; 
		if (obj instanceof IPRFile) component = (IPRFile) obj; 
		else if (obj instanceof IRodinElement) 
			component = (IPRFile) ((IRodinElement) obj).getParent();
		Assert.isTrue(component != null, "component must be initialised by now");
		try {
			IEditorInput fileInput = new FileEditorInput(component.getResource());
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
		
		IRodinFile component;
		
		if (!(obj instanceof IRodinProject)) {
			component = (IRodinFile) UIUtils.getOpenable(obj); 
			try {
				IEditorInput fileInput = new FileEditorInput(component.getResource());
				String editorId = "";
				if (component instanceof IMachine) {
					 editorId = EventBMachineEditor.EDITOR_ID;
				}
				else if (component instanceof IContext) {
					editorId = EventBContextEditor.EDITOR_ID;
				}
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


	public static void postRunnable(final Runnable r, Control ctrl) {
		final Runnable trackedRunnable= new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					//removePendingChange();
					//if (UIUtils.DEBUG) System.out.println("Runned");
				}
			}
		};
		if (ctrl != null && !ctrl.isDisposed()) {
			try {
				ctrl.getDisplay().asyncExec(trackedRunnable); 
			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e; 
			}
		}
	}

	public static void newVariables(IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE).length;
			ElementAtributeInputDialog dialog = new ElementAtributeInputDialog(Display.getCurrent().getActiveShell(), "New Variables", "Name", "var" + (counter + 1));

			dialog.open();
			Collection<String> names = dialog.getAttributes();
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				rodinFile.createInternalElement(IVariable.ELEMENT_TYPE, name, null, null);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	public static void intelligentNewVariables(IRodinFile rodinFile) {
		try {
			final int counter = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE).length;
			final int invCounter = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE).length;
			IntelligentNewVariableInputDialog dialog = 
				new IntelligentNewVariableInputDialog(
						Display.getCurrent().getActiveShell(), 
						"New Variable",
						"var" + (counter + 1),
						"inv" + (invCounter + 1)
				);

			dialog.open();
			String name = dialog.getName();
			String invariantName = dialog.getInvariantName();
			String init = dialog.getInit();
			boolean newInit = true;
			
			if (name != null) {
				rodinFile.createInternalElement(IVariable.ELEMENT_TYPE, name, null, null);
				
				IInternalElement invariant = rodinFile.createInternalElement(IInvariant.ELEMENT_TYPE, invariantName, null, null);
				invariant.setContents(dialog.getInvariantPredicate());
				
				IRodinElement [] events = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE);
				for (IRodinElement event : events) {
					if (event.getElementName().equals("INITIALISATION")) {
						newInit = false;
						IUnnamedInternalElement action = (IUnnamedInternalElement) ((IInternalElement) event).createInternalElement(IAction.ELEMENT_TYPE, "", null, null);
						action.setContents(init);
					}
				}
				if (newInit) {
					IInternalElement event = rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, "INITIALISATION", null, null);
					IUnnamedInternalElement action = (IUnnamedInternalElement) event.createInternalElement(IAction.ELEMENT_TYPE, null, null, null);
					action.setContents(init);
				}
			}
			
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	
	public static void newInvariants(IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE).length;
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(Display.getCurrent().getActiveShell(), "New Invariants", "Name and predicate", "inv", counter + 1);
			dialog.open();
			String [] names = dialog.getNewNames();
			String [] contents = dialog.getNewContents();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String content = contents[i];
				IInternalElement invariant = rodinFile.createInternalElement(IInvariant.ELEMENT_TYPE, name, null, null);
				invariant.setContents(content);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}	
	}


	public static void newTheorems(IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(ITheorem.ELEMENT_TYPE).length;
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(Display.getCurrent().getActiveShell(), "New Theorems", "Name and predicate", "thm", counter + 1);
			dialog.open();
			String [] names = dialog.getNewNames();
			String [] contents = dialog.getNewContents();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String content = contents[i];
				IInternalElement theorem = rodinFile.createInternalElement(ITheorem.ELEMENT_TYPE, name, null, null);
				theorem.setContents(content);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}


	public static void newEvent(IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE).length;
			NewEventInputDialog dialog = new NewEventInputDialog(Display.getCurrent().getActiveShell(), "New Events", "evt" + (counter + 1));
			
			dialog.open();
			String name = dialog.getName();
			if (name != null) {
				String [] varNames = dialog.getVariables();
				String [] grdNames = dialog.getGrdNames();
				String [] grdPredicates = dialog.getGrdPredicates();
				String [] actions = dialog.getActions();
				
				IInternalElement event = rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, name, null, null);
				for (String varName : varNames) {
					event.createInternalElement(IVariable.ELEMENT_TYPE, varName, null, null);
				}
				
				for (int i = 0; i < grdNames.length; i++) {
					IInternalElement grd = event.createInternalElement(IGuard.ELEMENT_TYPE, grdNames[i], null, null);
					grd.setContents(grdPredicates[i]);
				}
				
				for (String action : actions) {
					IInternalElement act = event.createInternalElement(IAction.ELEMENT_TYPE, null, null, null);
					act.setContents(action);
				}
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}		
	}


	public static void newCarrierSets(IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(ICarrierSet.ELEMENT_TYPE).length;
			ElementAtributeInputDialog dialog = new ElementAtributeInputDialog(Display.getCurrent().getActiveShell(), "New Carrier Sets", "Name", "set" + (counter + 1));

			dialog.open();
			Collection<String> names = dialog.getAttributes();
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				rodinFile.createInternalElement(ICarrierSet.ELEMENT_TYPE, name, null, null);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}


	public static void newConstants(IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IConstant.ELEMENT_TYPE).length;
			ElementAtributeInputDialog dialog = new ElementAtributeInputDialog(Display.getCurrent().getActiveShell(), "New Constants", "Name", "cst" + (counter + 1));
			dialog.open();
			Collection<String> names = dialog.getAttributes();
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				rodinFile.createInternalElement(IConstant.ELEMENT_TYPE, name, null, null);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}


	public static void newAxioms(IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IAxiom.ELEMENT_TYPE).length;
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(Display.getCurrent().getActiveShell(), "New Axioms", "Name and predicate", "axm", counter + 1);
			dialog.open();
			String [] names = dialog.getNewNames();
			String [] contents = dialog.getNewContents();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String content = contents[i];
				IInternalElement theorem = rodinFile.createInternalElement(IAxiom.ELEMENT_TYPE, name, null, null);
				theorem.setContents(content);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

}
