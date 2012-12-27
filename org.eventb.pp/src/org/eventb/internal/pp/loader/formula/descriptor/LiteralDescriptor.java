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
package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableSignature;
import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public abstract class LiteralDescriptor {
	// hashcode and equals are inherited from Object hashcode and equals
	
	private List<IIntermediateResult> termList = new ArrayList<IIntermediateResult>();
	private IContext context;
	
	public LiteralDescriptor(IContext context, List<IIntermediateResult> termList) {
		this.termList = termList;
		this.context = context;
	}

	public LiteralDescriptor(IContext context){
		this.context = context;
	}
	
	public void addResult(IIntermediateResult result) {
		termList.add(result);
	}
	
	public List<IIntermediateResult> getResults() {
		return termList;
	}
	
	public IContext getContext() {
		return context;
	}
	
	
	public List<TermSignature> unifiedIndexCache;
	public List<TermSignature> getUnifiedResults() {
		if (unifiedIndexCache == null) calculateIndexCache();
		return unifiedIndexCache;
	}

	public List<TermSignature> getSimplifiedList(List<TermSignature> list) {
		if (unifiedIndexCache==null) calculateIndexCache();
		
		HashSet<TermSignature> set = new HashSet<TermSignature>();
		List<TermSignature> result = new ArrayList<TermSignature>();
		for (int i = 0; i < unifiedIndexCache.size(); i++) {
			TermSignature key = unifiedIndexCache.get(i);
			if (!key.isConstant() && !set.contains(key)) {
				set.add(key);
				result.add(list.get(i));
			}
		}
		return result;
	}
	
	private void calculateIndexCache() {
		if (termList.size() == 1)
			unifiedIndexCache = termList.get(0).getTerms();
		else {
			unifiedIndexCache = unify(termList.get(0).getTerms(), termList.get(1).getTerms());
			for (int i = 2; i < termList.size(); i++) {
				unifiedIndexCache = unify(unifiedIndexCache, termList.get(i).getTerms());
			}
		}
	}
	
	private List<TermSignature> unify(List<TermSignature> list1,
			List<TermSignature> list2) {
		assert list1.size() == list2.size();

		int varIndex = 0;
		Hashtable<TermPair, TermSignature> map = new Hashtable<TermPair, TermSignature>();
		List<TermSignature> result = new ArrayList<TermSignature>();
		for (int i = 0; i < list1.size(); i++) {
			TermSignature ind1 = list1.get(i);
			TermSignature ind2 = list2.get(i);
			assert ind1.getSort().equals(ind2.getSort());
			TermPair pair = new TermPair(ind1, ind2);
			TermSignature index = map.get(pair);
			if (index == null) {
				if (ind1.isConstant() && ind2.isConstant() && ind1.equals(ind2)) {
					index = ind1;
				}
				else {
					// we put a negative index here to be sure it is not
					// interpreted as a quantified variable
					int v = --varIndex;
					index = new VariableSignature(context.getFreshVariableIndex(),v,ind1.getSort());
				}
				map.put(pair, index);
			}
			result.add(index);
		}
		return result;
	}
	
	private static class TermPair {
		TermSignature term1;
		TermSignature term2;
		
		TermPair(TermSignature term1, TermSignature term2) {
			this.term1 = term1;
			this.term2 = term2;
		}
		
        @Override
        public boolean equals(Object obj) {
        	if (obj instanceof TermPair) {
        		TermPair temp = (TermPair) obj;
        		return term1.equals(temp.term1) && term2.equals(temp.term2);
        	}
        	return false;
        }

        @Override
        public int hashCode() {
        	return term1.hashCode() * 31 + term2.hashCode();
        }
	}
    
    public String toStringWithInfo() {
        StringBuffer str = new StringBuffer();
        str.append("["+ toString() + "], Used: " + getResults().size() + "\n"/*+ ", To labelize: " + isSimplifiable()*/);
        for (IIntermediateResult result : termList) {
			str.append(result.toString()+"\n");
		}
        str.deleteCharAt(str.length()-1);
        return str.toString();
    }
	
	
}
