package fr.systerel.explorer.poModel;

import java.util.LinkedList;
import java.util.List;

public class POContainer {

	private List<ProofObligation> proofObligations = new LinkedList<ProofObligation>();

	public ProofObligation[] getProofObligations() {
		ProofObligation[] proofs = new ProofObligation[proofObligations.size()];
		return proofObligations.toArray(proofs);
	}
	
	public void addProofObligation(ProofObligation po) {
		proofObligations.add(po);
	}
	
	public void removeProofObligation(ProofObligation po) {
		proofObligations.remove(po);
	}
	
	public static String DISPLAY_NAME = "Proof Obligations";

}
