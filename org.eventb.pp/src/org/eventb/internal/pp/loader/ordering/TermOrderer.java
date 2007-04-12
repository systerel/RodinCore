package org.eventb.internal.pp.loader.ordering;

import java.util.Comparator;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class TermOrderer implements Comparator<TermSignature> {
	// it is important that TRUE and FALSE are always inferior than other terms
	// because A = TRUE must not be interpreted as TRUE = A
	// if it is the case, the boolean simplification A = TRUE ~> A1 never occurs 
	
	public int compare(TermSignature o1, TermSignature o2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
