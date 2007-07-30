package org.eventb.internal.ui.obligationexplorer;

import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.rodinp.core.RodinDBException;

public class ProofStatus {

	IPSFile psFile;
	
	public ProofStatus(IPSFile psFile) {
		this.psFile = psFile;
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
		return stringBuilder.toString();
	}
	

}
