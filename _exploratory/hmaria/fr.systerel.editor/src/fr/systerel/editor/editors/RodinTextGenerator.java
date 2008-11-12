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

package fr.systerel.editor.editors;

import java.util.ArrayList;

import org.eclipse.jface.text.Position;
import org.eventb.core.IAction;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IGuard;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 *
 */
public class RodinTextGenerator {

	
	private StringBuilder builder;
	private DocumentMapper documentMapper;
	private ArrayList<Position> foldingRegions = new ArrayList<Position>();
	private Object lineSeparator = System.getProperty("line.separator");

	
	public RodinTextGenerator(DocumentMapper documentMapper) {
		this.documentMapper = documentMapper;
	}
	
	/**
	 * Creates the text for the document and creates the intervals.
	 * 
	 * @param root
	 *            The machine or context that is displayed in the document.
	 */
	public String createText(IEventBRoot root) {
		builder = new StringBuilder();
		documentMapper.resetIntervals();
		
		if (root instanceof IMachineRoot) {
			IMachineRoot machine = (IMachineRoot) root;
			createMachineText(machine);
		}
		if (root instanceof IContextRoot) {
			IContextRoot context = (IContextRoot) root;
//			createContextText(context);
		}
		
		return builder.toString();
		
	}

	private void createMachineText(IMachineRoot machine) {
		try {
			addTitleRegion(machine.getComponentName());
			processElement(machine); //adds the comment
			
			addRefinesRegion(machine);
			addSeesRegion(machine);
			
			addVariablesRegion(machine);
			addInvariantsRegion(machine);
			addTheoremsRegion(machine);
			addEventsRegion(machine);
			
			
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addInvariantsRegion(IMachineRoot machine)
			throws RodinDBException {
		IInvariant[] invariants = machine.getInvariants();
		if (invariants.length > 0) {
			int start = builder.length();
			int length;
			addTitleRegion("Invariants:");
			for (IInvariant inv : invariants) {
				processElement(inv);
			}
			length = builder.length() - start;
			foldingRegions.add(new Position(start,length));
			builder.append(lineSeparator);
		}
	}

	private void addVariablesRegion(IMachineRoot machine)
			throws RodinDBException {
		
		IVariable[] variables = machine.getVariables();
		if (variables.length > 0) {
			int start = builder.length();
			int length;
			addTitleRegion("Variables:");
			for (IVariable var : variables) {
				processElement(var);
			}
			length = builder.length() -start;
			foldingRegions.add(new Position(start,length));
			builder.append(lineSeparator);
		}
	}

	private void addTheoremsRegion(IMachineRoot machine)
			throws RodinDBException {

		ITheorem[] theorems = machine.getTheorems();
		if (theorems.length > 0) {
			int start = builder.length();
			int length;
			addTitleRegion("Theorems:");
			for (ITheorem thm : theorems) {
				processElement(thm);
			}
			length = builder.length();
			foldingRegions.add(new Position(start, length));
			builder.append(lineSeparator);
		}
	}

	
	private void addEventsRegion(IMachineRoot machine) throws RodinDBException {

		IEvent[] events = machine.getEvents();
		if (events.length > 0) {
			int start = builder.length();
			int length;
			addTitleRegion("Events:");
			for (IEvent evt : events) {
				processEvent(evt);
			}
			length = builder.length() - start;
			foldingRegions.add(new Position(start, length));
			builder.append(lineSeparator);
		}
	}

	private void addRefinesRegion(IMachineRoot machine) throws RodinDBException {
		//TODO: Add intervals;

		int start = builder.length();
		int length;
		addLabelRegion("Refines: ", machine);
		
		for (IRefinesMachine refines : machine.getRefinesClauses()) {
			int offset = builder.length();
			builder.append(refines.getAbstractMachineName());
			length = builder.length() - offset;
			documentMapper.processInterval(offset, length, refines, RodinConfiguration.CONTENT_TYPE);
			builder.append(lineSeparator);
		}
		length = builder.length() - start;
		foldingRegions.add(new Position(start, length));
		builder.append(lineSeparator);
	}

	private void addSeesRegion(IMachineRoot machine) throws RodinDBException {
		//TODO: Add intervals;
		int start = builder.length();
		int length;
		addLabelRegion("Sees: ", machine);
		for (ISeesContext sees : machine.getSeesClauses()) {
			int offset = builder.length();
			builder.append(sees.getSeenContextName());
			length = builder.length() - offset;
			documentMapper.processInterval(offset, length, sees, RodinConfiguration.CONTENT_TYPE);
			builder.append(lineSeparator);
		}
		length = builder.length()-start;
		foldingRegions.add(new Position(start, length));
		builder.append(lineSeparator);
		builder.append(lineSeparator);
	}
	
	protected void addElementRegion(String text, IRodinElement element, String contentType) {
		int start = builder.length();
		builder.append(text);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, contentType);
		builder.append(lineSeparator);
		
	}

	protected void addLabelRegion(String text, IRodinElement element) {
		int start = builder.length();
		builder.append(text);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, RodinConfiguration.LABEL_TYPE);
		
	}
	
	protected void addCommentHeaderRegion(IRodinElement element) {
		int start = builder.length();
		builder.append("// ");
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, RodinConfiguration.COMMENT_HEADER_TYPE);
		
	}

	
	protected void addTitleRegion(String title) {
		int start = builder.length();
		builder.append(title);
		builder.append(lineSeparator);
		builder.append(lineSeparator);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, null, RodinConfiguration.TITLE_TYPE);
	}
	
	

	private void processCommentedElement(ICommentedElement element) {
		try {
			addCommentHeaderRegion(element);
			
			if (element.hasComment()) {
				addElementRegion(element.getComment(), element, RodinConfiguration.COMMENT_TYPE);
			} else {
				addElementRegion("", element, RodinConfiguration.COMMENT_TYPE);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processPredicateElement(IPredicateElement element) {
		try {
			addLabelRegion("Pred: ", element);
			
			if (element.hasPredicateString()) {
				addElementRegion(element.getPredicateString(), element, RodinConfiguration.CONTENT_TYPE);
			} else {
				addElementRegion("", element, RodinConfiguration.CONTENT_TYPE);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processAssignmentElement(IAssignmentElement element) {
		try {
			addLabelRegion(":= ", element);
			if (element.hasAssignmentString()) {
				addElementRegion(element.getAssignmentString(), element, RodinConfiguration.CONTENT_TYPE);
			} else {
				addElementRegion("", element, RodinConfiguration.CONTENT_TYPE);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void processLabeledElement(ILabeledElement element) {
		try {
			addLabelRegion("L: ", element);

			if (element.hasLabel()) {
				addElementRegion(element.getLabel(), element, RodinConfiguration.IDENTIFIER_TYPE);
			} else {
				addElementRegion("", element, RodinConfiguration.IDENTIFIER_TYPE);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processIdentifierElement(IIdentifierElement element) {
		try {
			addLabelRegion("ID: ", element);

			if (element.hasIdentifierString()) {
				addElementRegion(element.getIdentifierString(), element, RodinConfiguration.IDENTIFIER_TYPE);
			} else {
				addElementRegion("", element, RodinConfiguration.IDENTIFIER_TYPE);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void processElement(IRodinElement element) {
		if (element instanceof ILabeledElement) {
			processLabeledElement((ILabeledElement) element);
		}
		if (element instanceof IIdentifierElement) {
			processIdentifierElement((IIdentifierElement) element);
		}
		if (element instanceof ICommentedElement) {
			processCommentedElement((ICommentedElement) element);
		}
		if (element instanceof IPredicateElement) {
			processPredicateElement((IPredicateElement) element);
		}
		if (element instanceof IAssignmentElement) {
			processAssignmentElement((IAssignmentElement) element);
		}
		builder.append(lineSeparator);
	}
	

	private void processEvent(IEvent event) throws RodinDBException {
		int start = builder.length();
		processElement(event);
		IGuard[] guards = event.getGuards();
		for (IGuard guard : guards) {
			processElement(guard);
		}
		IWitness[] witnesses = event.getWitnesses();
		for (IWitness witness : witnesses) {
			processElement(witness);
		}
		
		IAction[] actions = event.getActions();
		for (IAction action : actions) {
			processElement(action);
		}
		int length = builder.length() - start;
		foldingRegions.add(new Position(start, length));
		
		builder.append(lineSeparator);


//		int start = builder.length();
//		processElement(event);
//		
//		
//		
//		IGuard[] guards = event.getGuards();
//		for (IGuard guard : guards) {
//			
//			StringBuilder text = new StringBuilder();
//			text.append('\u0009');
//			addLabelRegion(text.toString(), guard);
//			addElementRegion(guard.getLabel(), guard, RodinConfiguration.IDENTIFIER_TYPE);
//			addLabelRegion(": ", guard);
//			
//			if (guard.hasPredicateString()) {
//				addElementRegion(guard.getPredicateString(), guard, RodinConfiguration.CONTENT_TYPE);
//			} else {
//				addElementRegion("", guard, RodinConfiguration.CONTENT_TYPE);
//			}
//			builder.append(lineSeparator);
//			
//		}
		
		
//		IWitness[] witnesses = event.getWitnesses();
//		for (IWitness witness : witnesses) {
//			processElement(witness);
//		}
//		
//		IAction[] actions = event.getActions();
//		for (IAction action : actions) {
//			processElement(action);
//		}
//		int length = builder.length() - start;
//		foldingRegions.add(new Position(start, length));
//		
//		builder.append(lineSeparator);
		
	}
	
	public Position[] getFoldingRegions() {
		return foldingRegions.toArray(new Position[foldingRegions.size()]);
	}
	
}
