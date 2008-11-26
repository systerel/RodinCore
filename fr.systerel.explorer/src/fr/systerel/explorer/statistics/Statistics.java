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


package fr.systerel.explorer.statistics;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.IModelElement;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelPOContainer;
import fr.systerel.explorer.model.ModelProject;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * This class represents a simple statistics that is not aggregated. 
 * 
 * @see fr.systerel.explorer.statistics.IStatistics
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
	
	/**
	 * Calculates the statistics from the given parent.
	 */
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
			if (node.getParent() instanceof IContextRoot) {
				cont = ModelController.getContext((IContextRoot) node.getParent());
			}
			if (node.getParent() instanceof IMachineRoot) {
				cont = ModelController.getMachine((IMachineRoot) node.getParent());
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
		if (parent instanceof IElementNode) {
			return ((IElementNode) parent).getLabel();
		}
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
		if (internal_parent instanceof IEventBRoot) {
			return ((IEventBRoot) internal_parent).getComponentName();
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + manual;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + reviewed;
		result = prime * result + total;
		result = prime * result + undischarged;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Statistics other = (Statistics) obj;
		if (manual != other.manual)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (reviewed != other.reviewed)
			return false;
		if (total != other.total)
			return false;
		if (undischarged != other.undischarged)
			return false;
		return true;
	}


	public void buildCopyString(StringBuilder builder, boolean copyLabel,
			Character separator) {
		if (copyLabel) {
			builder.append(getParentLabel()) ;
			builder.append(separator);
		}
		builder.append(getTotal());
		builder.append(separator);
		builder.append(getAuto());
		builder.append(separator);
		builder.append(getManual());
		builder.append(separator);
		builder.append(getReviewed());
		builder.append(separator);
		builder.append(getUndischargedRest());
		builder.append(System.getProperty("line.separator"));
	}
}
