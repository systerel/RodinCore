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
		
		int discharged = 0;
		int autoDischarged = 0;
		for (IPSStatus status : statuses) {
			boolean isDischarged;
			try {
				isDischarged = ProverUIUtils.isDischarged(status);
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleGetAttributeException(e, UserAwareness.IGNORE);
				continue;
			}
			if (isDischarged) {
				discharged += 1;
				boolean isAutomatic;
				try {
					isAutomatic = ProverUIUtils.isAutomatic(status);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleGetAttributeException(e, UserAwareness.IGNORE);
					continue;
				}
				if (isAutomatic) {
					autoDischarged += 1;
				}
			}
		}
		stringBuilder.append(statuses.length);
		stringBuilder.append("/");
		stringBuilder.append(discharged);
		stringBuilder.append("/");
		stringBuilder.append(autoDischarged);
		
		stringBuilder.append(')');
		return stringBuilder.toString();
	}
	

}
