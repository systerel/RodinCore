/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.obligationexplorer;

import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.rodinp.core.RodinDBException;

public class ProofStatus {

	IPSRoot psFile;
	
	boolean fullStatistics;
	
	public ProofStatus(IPSRoot psFile, boolean fullStatistics) {
		this.psFile = psFile;
		this.fullStatistics = fullStatistics;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("(");
		IPSStatus[] statuses; 
		try {
			statuses = psFile.getStatuses();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e, UserAwareness.IGNORE);
			stringBuilder.append(')');
			return stringBuilder.toString();
		}
		
		int auto = 0;
		int manual = 0;
		int reviewed = 0;
		int remaining = 0;
		for (IPSStatus status : statuses) {
			boolean isBroken;
			try {
				isBroken = status.isBroken();
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleGetAttributeException(e, UserAwareness.IGNORE);
				continue;				
			}
			if (isBroken) {
				++remaining;
				continue;
			}
			boolean isDischarged;
			try {
				isDischarged = ProverUIUtils.isDischarged(status);
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleGetAttributeException(e, UserAwareness.IGNORE);
				continue;
			}
			if (isDischarged) {
				boolean isAutomatic;
				try {
					isAutomatic = ProverUIUtils.isAutomatic(status);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleGetAttributeException(e, UserAwareness.IGNORE);
					continue;
				}
				if (isAutomatic) {
					auto += 1;
				}
				else {
					manual += 1;
				}
			}
			else {
				boolean isReviewed;
				try {
					isReviewed = ProverUIUtils.isReviewed(status);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleGetAttributeException(e, UserAwareness.IGNORE);
					continue;
				}
				if (isReviewed)
					reviewed += 1;
				else {
					remaining += 1;
				}
			}
		}
		stringBuilder.append(statuses.length);
		stringBuilder.append("|");
		stringBuilder.append(auto);
		stringBuilder.append(",");
		stringBuilder.append(manual);
		stringBuilder.append("|");
		stringBuilder.append(reviewed);
		stringBuilder.append(",");
		stringBuilder.append(remaining);
		stringBuilder.append(')');
		if (fullStatistics) {
			stringBuilder.append("\n");
			stringBuilder.append("-----\n");
			stringBuilder.append("Total POs: " + statuses.length + " \n");
			stringBuilder.append("Auto. discharged: " + auto + " \n");
			stringBuilder.append("Manual discharged: " + manual + " \n");
			stringBuilder.append("Reviewed: " + reviewed + " \n");
			stringBuilder.append("Undischarged: " + remaining + " ");
		}
		return stringBuilder.toString();
	}
	

}
