/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

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
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.eventbeditor.ElementAttributeInputDialog;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.eventbeditor.NewEventInputDialog;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.eventb.internal.ui.prover.ProverUI;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is a class which store utility static method that can be used in
 *         the development
 */
public class UIUtils {

	public static boolean DEBUG = false;

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

	// public static final String FALSIFY_SYMBOL = "\u22a5";
	public static final String FALSIFY_SYMBOL = "ct";
	
	/**
	 * Print out the message if the <code>DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debug(String message) {
		if (DEBUG)
			System.out.println(message);
	}

	/**
	 * Print out the message if the <code>EventBEditor.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugEventBEditor(String message) {
		if (EventBEditor.DEBUG)
			System.out.println(message);
	}

	/**
	 * Print out the message if the <code>ProjectExplorer.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugProjectExplorer(String message) {
		if (ProjectExplorer.DEBUG)
			System.out.println(message);
	}

	/**
	 * Print out the message if the <code>ObligationExplorer.DEBUG</code> flag
	 * is <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugObligationExplorer(String message) {
		if (ObligationExplorer.DEBUG)
			System.out.println(message);
	}

	/**
	 * Print out the message if the <code>ProverUI.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugProverUI(String message) {
		if (ProverUI.DEBUG)
			System.out.println(message);
	}

	/**
	 * Getting the image corresponding to an object.
	 * <p>
	 * 
	 * @param obj
	 *            Any object
	 * @return The image for displaying corresponding to the input object
	 */
	public static Image getImage(Object obj) {
		if (obj instanceof IRodinElement)
			return (getRodinElementImage((IRodinElement) obj));
		if (obj instanceof TreeNode)
			return (getTreeNodeImage((TreeNode) obj));
		return null;
	}

	/**
	 * Getting the impage corresponding to a Rodin element.
	 * <p>
	 * 
	 * @param element
	 *            A Rodin element
	 * @return The image for displaying corresponding to the input element
	 */
	private static Image getRodinElementImage(IRodinElement element) {
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		if (element instanceof IRodinProject)
			return registry.get(EventBImage.IMG_PROJECT);

		if (element instanceof IMachineFile)
			return registry.get(EventBImage.IMG_MACHINE);

		if (element instanceof IContextFile)
			return registry.get(EventBImage.IMG_CONTEXT);

		if (element instanceof ISeesContext)
			return registry.get(EventBImage.IMG_CONTEXT);

		if (element instanceof IRefinesMachine)
			return EventBImage.getOverlayIcon("IMG_REFINES_MACHINE");

		if (element instanceof IExtendsContext)
			return registry.get(EventBImage.IMG_CONTEXT);

		if (element instanceof IVariable)
			return registry.get(EventBImage.IMG_VARIABLE);

		if (element instanceof IInvariant)
			return registry.get(EventBImage.IMG_INVARIANT);

		if (element instanceof ITheorem)
			return registry.get(EventBImage.IMG_THEOREM);

		if (element instanceof IEvent)
			return registry.get(EventBImage.IMG_EVENT);

		if (element instanceof IRefinesEvent)
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

		return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
	}

	/**
	 * Getting the impage corresponding to a tree node.
	 * <p>
	 * 
	 * @param element
	 *            A Tree node
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
		if (node.isType(IEvent.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_EVENTS);
		if (node.isType(ICarrierSet.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_CARRIER_SETS);
		if (node.isType(IConstant.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_CONSTANTS);
		if (node.isType(IAxiom.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_AXIOMS);

		return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
	}

	/**
	 * Method to return the openable for an object (IRodinElement or TreeNode).
	 * <p>
	 * 
	 * @param node
	 *            A Rodin Element or a tree node
	 * @return The IRodinFile corresponding to the input object
	 */
	public static IOpenable getOpenable(Object node) {
		if (node instanceof TreeNode)
			return ((IRodinElement) ((TreeNode) node).getParent())
					.getOpenable();
		else if (node instanceof IRodinElement)
			return ((IRodinElement) node).getOpenable();

		return null;
	}

	/**
	 * @author htson
	 *         <p>
	 *         Class which provide the label for Rodin elements.
	 */
	public static class ElementLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof ISeesContext || obj instanceof IAction
					|| obj instanceof IRefinesMachine
					|| obj instanceof IExtendsContext) {
				try {
					return ((IInternalElement) obj).getContents();
				} catch (RodinDBException e) {
					e.printStackTrace();
					return "";
				}
			}
			if (obj instanceof IInternalElement)
				return ((IInternalElement) obj).getElementName();
			return obj.toString();
		}

		public Image getImage(Object obj) {
			return UIUtils.getImage(obj);
		}
	}

	/**
	 * Getting the list of tactics that are applicable to the current goal.
	 * <p>
	 * 
	 * @param goal
	 *            the current goal
	 * @return a list of tactic symbols (strings)
	 */
	public static List<String> getApplicableToGoal(Predicate goal) {
		List<String> names = new ArrayList<String>();

		if (Tactics.impI_applicable(goal))
			names.add(IMPI_SYMBOL);
		if (Tactics.conjI_applicable(goal))
			names.add(CONJI_SYMBOL);
		if (Tactics.allI_applicable(goal))
			names.add(ALLI_SYMBOL);
		if (Tactics.exI_applicable(goal))
			names.add(EXI_SYMBOL);
		if (Tactics.removeNegGoal_applicable(goal))
			names.add(NEG_SYMBOL);
		if (Tactics.disjToImpGoal_applicable(goal))
			names.add(DISJE_SYMBOL);
		// Extra tactics applicable to goal should be added here.
		return names;
	}

	/**
	 * Getting the list of tactics that are applicable to a hypothesis.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis
	 * @return a list of tactic symbols (strings)
	 */
	public static List<String> getApplicableToHypothesis(Hypothesis hyp) {
		List<String> names = new ArrayList<String>();

		names.add(FALSIFY_SYMBOL);
		if (Tactics.allF_applicable(hyp))
			names.add(ALLF_SYMBOL);
		if (Tactics.conjD_applicable(hyp))
			names.add(CONJD_SYMBOL);
		if (Tactics.impD_applicable(hyp)) {
			names.add(IMPD1_SYMBOL);
			names.add(IMPD2_SYMBOL);
		}
		if (Tactics.exF_applicable(hyp))
			names.add(EXF_SYMBOL);
		if (Tactics.disjE_applicable(hyp))
			names.add(DISJE_SYMBOL);
		if (Tactics.eqE_applicable(hyp)) {
			names.add(EQE1_SYMBOL);
			names.add(EQE2_SYMBOL);
		}
		if (Tactics.removeNegHyp_applicable(hyp))
			names.add(NEG_SYMBOL);

		// Extra tactics applicable to hypothesis should be added here.
		return names;
	}

	/**
	 * Link the current object to an Prover UI editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. a proof obligation or a Rodin file)
	 */
	public static void linkToProverUI(Object obj) {
		String editorId = ProverUI.EDITOR_ID;

		IPRFile component = null;
		if (obj instanceof IRodinProject)
			return;
		if (obj instanceof IPRFile)
			component = (IPRFile) obj;
		else if (obj instanceof IRodinElement)
			component = (IPRFile) ((IRodinElement) obj).getParent();
		Assert
				.isTrue(component != null,
						"component must be initialised by now");
		try {
			UIUtils.debugObligationExplorer("Link to : " + obj);

			IEditorInput fileInput = new FileEditorInput(component
					.getResource());
			// IEditorReference [] editors =
			// EventBUIPlugin.getActivePage().getEditorReferences();
			// for (IEditorReference editor : editors) {
			// if (editor.getEditorInput().equals(fileInput)) {
			// IEditorPart part = editor.getEditor(true);
			// if (part instanceof ProverUI) {
			// if (obj instanceof IPRSequent) {
			// ((ProverUI) part).setCurrentPO((IPRSequent) obj);
			// return;
			// }
			// }
			// }
			//				
			// }

			ProverUI editor = (ProverUI) EventBUIPlugin.getActivePage()
					.openEditor(fileInput, editorId);
			if (!(obj instanceof IPRFile))
				editor.setCurrentPO((IPRSequent) obj);
		} catch (PartInitException e) {
			MessageDialog.openError(null, null, "Error open the editor");
			e.printStackTrace();
			// TODO EventBImage.logException(e);
		}
		return;
	}

	/**
	 * Link the current object to an Event-B editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 */
	public static void linkToEventBEditor(Object obj) {

		IRodinFile component;

		if (!(obj instanceof IRodinProject)) {
			component = (IRodinFile) UIUtils.getOpenable(obj);
			try {
				IEditorInput fileInput = new FileEditorInput(component
						.getResource());
				String editorId = "";
				if (component instanceof IMachineFile) {
					editorId = EventBMachineEditor.EDITOR_ID;
				} else if (component instanceof IContextFile) {
					editorId = EventBContextEditor.EDITOR_ID;
				}
				EventBEditor editor = (EventBEditor) EventBUIPlugin
						.getActivePage().openEditor(fileInput, editorId);
				editor.edit(obj);
			} catch (PartInitException e) {
				MessageDialog.openError(null, null,
						"Error open the Event-B Editor");
				e.printStackTrace();
				// TODO EventBUIPlugin.logException(e);
			}
		}
		return;
	}

	/**
	 * Convert a string input to XML format by replacing special characters (&, <,
	 * >).
	 * <p>
	 * 
	 * @param input
	 *            the input string
	 * @return a string corresponding to the input in XML format
	 */
	public static String XMLWrapUp(String input) {
		String output = input;
		output = output.replaceAll("&", "&amp;");
		output = output.replaceAll("<", "&lt;");
		output = output.replaceAll(">", "&gt;");
		return output;
	}

	/**
	 * Utitlity method to create a text and link with the same label
	 * <p>
	 * 
	 * @param link
	 *            a String
	 * @return XML formatted string represents the link
	 */
	public static String makeHyperlink(String link) {
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">"
				+ UIUtils.XMLWrapUp(link) + "</a>";
	}

	/**
	 * Utitlity method to create a text and link with the same label
	 * <p>
	 * 
	 * @param link
	 *            a String
	 * @return XML formatted string represents the link
	 */
	public static String makeHyperlink(String link, String text) {
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">"
				+ UIUtils.XMLWrapUp(text) + "</a>";
	}

	/**
	 * Activate a particular view given the ID.
	 * <p>
	 * 
	 * @param View_ID
	 *            the ID of the view which will be activate
	 */
	public static void activateView(String View_ID) {
		IViewPart aView = EventBUIPlugin.getActivePage().findView(View_ID);
		if (aView != null) {
			EventBUIPlugin.getActivePage().activate(aView);
		}
		return;
	}

	/**
	 * Running a runable asynchronously.
	 * <p>
	 * 
	 * @param r
	 *            the runnable
	 * @param ctrl
	 *            the control that the runnable attached to
	 */
	public static void asyncPostRunnable(final Runnable r, Control ctrl) {
		final Runnable trackedRunnable = new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					// removePendingChange();
					// if (UIUtils.DEBUG) System.out.println("Runned");
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

	/**
	 * Running a runable synchronously.
	 * <p>
	 * 
	 * @param r
	 *            the runnable
	 * @param ctrl
	 *            the control that the runnable attached to
	 */
	public static void syncPostRunnable(final Runnable r, Control ctrl) {
		final Runnable trackedRunnable = new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					// removePendingChange();
					// if (UIUtils.DEBUG) System.out.println("Runned");
				}
			}
		};
		if (ctrl != null && !ctrl.isDisposed()) {
			try {
				ctrl.getDisplay().syncExec(trackedRunnable);
			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e;
			}
		}
	}

	/**
	 * Utility method to create new variables using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new variables will be created in
	 */
	public static void newVariables(EventBEditor editor, IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE).length;
			ElementAttributeInputDialog dialog = new ElementAttributeInputDialog(
					Display.getCurrent().getActiveShell(), "New Variables",
					"Name", "var" + (counter + 1));

			dialog.open();
			Collection<String> names = dialog.getAttributes();
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				IInternalElement var = rodinFile.createInternalElement(
						IVariable.ELEMENT_TYPE, name, null, null);
				editor.addNewElement(var);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Utility method to create a event with its local variables, guards and
	 * actions using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the event will be created in
	 */
	public static void newEvent(EventBEditor editor, IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE).length;
			NewEventInputDialog dialog = new NewEventInputDialog(Display
					.getCurrent().getActiveShell(), "New Events", "evt"
					+ (counter + 1));

			dialog.open();
			String name = dialog.getName();
			if (name != null) {
				String[] varNames = dialog.getVariables();
				String[] grdNames = dialog.getGrdNames();
				String[] grdPredicates = dialog.getGrdPredicates();
				String[] actNames = dialog.getActNames();
				String[] actSubstitutions = dialog.getActSubstitutions();

				IInternalElement evt = rodinFile.createInternalElement(
						IEvent.ELEMENT_TYPE, name, null, null);
				editor.addNewElement(evt);
				for (String varName : varNames) {
					IInternalElement var = evt.createInternalElement(
							IVariable.ELEMENT_TYPE, varName, null, null);
					editor.addNewElement(var);
				}

				for (int i = 0; i < grdNames.length; i++) {
					IInternalElement grd = evt.createInternalElement(
							IGuard.ELEMENT_TYPE, grdNames[i], null, null);
					grd.setContents(grdPredicates[i]);
					editor.addNewElement(grd);
				}

				for (int i = 0; i < actNames.length; i++) {
					IInternalElement act = evt.createInternalElement(
							IAction.ELEMENT_TYPE, actNames[i], null, null);
					act.setContents(actSubstitutions[i]);
					editor.addNewElement(act);
				}

			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create new carrier sets using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new carrier sets will be created in
	 */
	public static void newCarrierSets(EventBEditor editor, IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(ICarrierSet.ELEMENT_TYPE).length;
			ElementAttributeInputDialog dialog = new ElementAttributeInputDialog(
					Display.getCurrent().getActiveShell(), "New Carrier Sets",
					"Name", "set" + (counter + 1));

			dialog.open();
			Collection<String> names = dialog.getAttributes();
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				IInternalElement set = rodinFile.createInternalElement(
						ICarrierSet.ELEMENT_TYPE, name, null, null);
				editor.addNewElement(set);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create new constants using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new constants will be created in
	 */
	public static void newConstants(EventBEditor editor, IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IConstant.ELEMENT_TYPE).length;
			ElementAttributeInputDialog dialog = new ElementAttributeInputDialog(
					Display.getCurrent().getActiveShell(), "New Constants",
					"Name", "cst" + (counter + 1));
			dialog.open();
			Collection<String> names = dialog.getAttributes();
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				IInternalElement cst = rodinFile.createInternalElement(
						IConstant.ELEMENT_TYPE, name, null, null);
				editor.addNewElement(cst);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

}
