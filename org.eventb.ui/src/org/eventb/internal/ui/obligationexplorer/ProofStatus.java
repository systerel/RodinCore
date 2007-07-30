package org.eventb.internal.ui.obligationexplorer;

import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.EventBUIExceptionHandler;
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
		try {
			IPSStatus[] statuses = psFile.getStatuses();
			int discharged = 0;
			int autoDischarged = 0;
			for (IPSStatus status : statuses) {
				if (ProverUIUtils.isDischarged(status)) {
					discharged += 1;
					if (ProverUIUtils.isAutomatic(status)) {
						autoDischarged += 1;
					}
				}
			}
			stringBuilder.append(statuses.length);
			stringBuilder.append("/");
			stringBuilder.append(discharged);
			stringBuilder.append("/");
			stringBuilder.append(autoDischarged);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e);
			stringBuilder.append(')');
			return stringBuilder.toString();
		}
		
		stringBuilder.append(')');
		return stringBuilder.toString();
	}
	

}
