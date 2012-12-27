/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.formula;

public abstract class AbstractContext {

	private boolean isQuantified;
	private boolean isForall;
	private boolean isPositive = true;
	private boolean isDisjunction;
	private int equivalenceCount;
	
	public AbstractContext() {
		super();
	}

	public boolean isQuantified() {
		return isQuantified;
	}

	protected void setQuantified(boolean isQuantified) {
		this.isQuantified = isQuantified;
	}

	public boolean isForall() {
		return isForall;
	}

	protected void setForall(boolean isForall) {
		this.isForall = isForall;
	}

	public boolean isPositive() {
		return isPositive;
	}

	protected void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}
	
	protected int getEquivalenceCount() {
		return equivalenceCount;
	}
	
	public boolean isEquivalence() {
		return this.equivalenceCount > 0;
	}

	protected void incrementEquivalenceCount() {
		this.equivalenceCount++;
	}

	protected void setEquivalenceCount(int equivalenceCount) {
		this.equivalenceCount = equivalenceCount;
	}
	
	public boolean isDisjunction() {
		return isDisjunction;
	}

	protected void setDisjunction(boolean isDisjunction) {
		this.isDisjunction = isDisjunction;
	}

}