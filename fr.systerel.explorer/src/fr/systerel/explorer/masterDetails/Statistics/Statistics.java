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


package fr.systerel.explorer.masterDetails.Statistics;

import org.eventb.core.IContextFile;
import org.eventb.core.IEventBFile;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.IModelElement;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelPOContainer;
import fr.systerel.explorer.model.ModelProject;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * @author Administrator
 *
 */
public class Statistics implements IStatistics{

	private Object parent;
	private int total;
	private int undischarged;
	private int manual;
	private int reviewed;
	
	public Statistics(Object parent) {
		this.parent = parent;
		calculate();
	}
	
	public void calculate() {
		if (parent instanceof ModelPOContainer) {
			ModelPOContainer container = (ModelPOContainer) parent;
			total = container.getPOcount();
			undischarged = container.getUndischargedPOcount();
			manual = container.getManuallyDischargedPOcount();
			reviewed = container.getReviewedPOcount();
		}
		if (parent instanceof ModelProject) {
			ModelProject project = (ModelProject) parent;
			total = project.getPOcount();
			undischarged = project.getUndischargedPOcount();
			manual = project.getManuallyDischargedPOcount();
			reviewed = project.getReviewedPOcount();
		}
		if (parent instanceof IElementNode) {
			IElementNode node = (IElementNode) parent;
			ModelPOContainer cont =  null;
			if (node.getParent() instanceof IContextFile) {
				cont = ModelController.getContext((IContextFile) node.getParent());
			}
			if (node.getParent() instanceof IMachineFile) {
				cont = ModelController.getMachine((IMachineFile) node.getParent());
			}
			if (cont != null) {
				total =  cont.getPOcount(node.getChildrenType());
				undischarged = cont.getUndischargedPOcount(node.getChildrenType());
				manual = cont.getManuallyDischargedPOcount(node.getChildrenType());
				reviewed = cont.getReviewedPOcount(node.getChildrenType());
			}
		}
	}
	
	public int getTotal(){
		return total;
	}

	public int getUndischarged(){
		return undischarged;
	}
	
	public int getManual(){
		return manual;
	}

	public int getAuto(){
		return total - undischarged -manual;
	}
	
	public int getReviewed(){
		return reviewed;
	}
	
	/**
	 * 
	 * @return the number of Proof Obligations that are undischarged but not reviewed
	 */
	public int getUndischargedRest() {
		return undischarged- reviewed;
	}
	
	public String getParentLabel() {
		Object internal_parent = null;
		if (parent instanceof IModelElement) {
			internal_parent = ((IModelElement) parent).getInternalElement();
		}
		if (internal_parent instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) internal_parent).getLabel();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (internal_parent instanceof IEventBFile) {
			return ((IEventBFile) internal_parent).getBareName();
		}
		if (internal_parent instanceof IRodinElement) {
			return ((IRodinElement) internal_parent).getElementName();
		}
		return parent.toString();
	}

	public boolean isAggregate() {
		return false;
	}

	public Object getParent() {
		return parent;
	}
	
}
