/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.AssociativeExpressionComplement;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.basis.engine.MatchingUtilities;

/**
 * A very tedious implementations that only accounts for three possible cases.
 * <p> TODO fix this
 * @author maamria
 * 
 */
public class AExpressionProblem extends AssociativityProblem<Expression> {

	public AExpressionProblem(int tag, Expression[] formulae, Expression[] patterns, IBinding existingBinding) {
		super(tag, formulae, patterns, existingBinding);
	}

	@Override
	public IBinding solve(boolean acceptPartialMatch) {
		if (!isSolvable) {
			return null;
		}
		if (indexedPatterns.size() > 2) {
			return null;
		}
		List<IndexedFormula<Expression>> availableFormulae = new ArrayList<IndexedFormula<Expression>>(indexedFormulae);
		Collections.sort(availableFormulae, getIndexedFormulaComparator());
		if (variables.size() == 1 && searchSpace.size() == 1) {
			return oneVariableOneFormula(availableFormulae, acceptPartialMatch);
		}
		else if (variables.size() == 2) {
			return twoVariables(availableFormulae, acceptPartialMatch);
		} 
		else if (searchSpace.size() == 2) {
			return twoFormulae(availableFormulae, acceptPartialMatch);
		}
		return null;
	}

	protected Match<Expression> getMatchWithRank(List<Match<Expression>> list, boolean highest) {
		Collections.sort(list, getMatchComparator());
		if (highest)
			return list.get(list.size() - 1);
		else
			return list.get(0);
	}

	protected Match<Expression> getSubsequentMatch(List<Match<Expression>> available, int index) {
		for (Match<Expression> form : available) {
			if (form.getIndexedFormula().getIndex() == index + 1) {
				return form;
			}
		}
		return null;
	}

	protected List<IndexedFormula<Expression>> getSublist(List<IndexedFormula<Expression>> list, int index, boolean before) {
		List<IndexedFormula<Expression>> newList = new ArrayList<IndexedFormula<Expression>>();
		for (IndexedFormula<Expression> indexedFormula : list) {
			if (before) {
				if (indexedFormula.getIndex() < index) {
					newList.add(indexedFormula);
				}
			} else {
				if (indexedFormula.getIndex() > index) {
					newList.add(indexedFormula);
				}
			}
		}
		return newList;
	}

	protected Comparator<IndexedFormula<Expression>> getIndexedFormulaComparator() {
		return new Comparator<IndexedFormula<Expression>>() {
			@Override
			public int compare(IndexedFormula<Expression> o1, IndexedFormula<Expression> o2) {
				if (o1.getIndex() > o2.getIndex()) {
					return 1;
				}
				if (o1.getIndex() < o2.getIndex()) {
					return -1;
				}
				if (o1.equals(o2)) {
					return 0;
				}
				return 1;
			}
		};
	}
	
	protected Comparator<Match<Expression>> getMatchComparator() {
		return new Comparator<Match<Expression>>() {
			@Override
			public int compare(Match<Expression> o1, Match<Expression> o2) {
				if (o1.getIndexedFormula().getIndex() > o2.getIndexedFormula().getIndex()) {
					return 1;
				}
				if (o1.getIndexedFormula().getIndex() < o2.getIndexedFormula().getIndex()) {
					return -1;
				}
				if (o1.equals(o2)) {
					return 0;
				}
				return 1;
			}
		};
	}

	protected Expression getAssociativeExpression(List<Expression> list) {
		if (list.size() == 0) {
			return null;
		}
		return MatchingUtilities.makeAppropriateAssociativeExpression(tag, existingBinding.getFormulaFactory(), list.toArray(new Expression[list.size()]));
	}

	private IBinding oneVariableOneFormula(List<IndexedFormula<Expression>> availableFormulae, boolean acceptPartialMatch) {
		if (variables.size() == 1 && searchSpace.size() == 1) {
			IndexedFormula<Expression> var = variables.get(0);
			FreeIdentifier identifier = (FreeIdentifier) var.getFormula();
			IBinding initialBinding = existingBinding.clone();
			MatchEntry<Expression> entry = searchSpace.get(0);
			Expression varMapping = initialBinding.getCurrentMapping(identifier);
			if (varMapping != null) {
				IndexedFormula<Expression> match = getMatch(availableFormulae, varMapping);
				if (match == null) {
					return null;
				}
				int firstIndex = match.getIndex();
				Match<Expression> groundMatch = getSubsequentMatch(entry.getMatches(), firstIndex);
				if (groundMatch == null) {
					return null;
				} else {
					initialBinding.insertBinding(groundMatch.getBinding());
					List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, firstIndex, true));
					List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, firstIndex + 1, false));
					if (beforeSublist.size() != 0 || afterSublist.size() != 0) {
						if (!acceptPartialMatch) {
							return null;
						} else {
							initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, getAssociativeExpression(beforeSublist), getAssociativeExpression(afterSublist)));
						}
					}
				}
			} else {
				boolean varIsBefore = var.getIndex() < entry.getIndexedPattern().getIndex();
				if (varIsBefore) {
					Match<Expression> match = getMatchWithRank(entry.getMatches(), true);
					int axisIndex = match.getIndexedFormula().getIndex();
					List<IndexedFormula<Expression>> beforeIndexedSublist = getSublist(availableFormulae, axisIndex, true);
					List<Expression> beforeSublist = getFormulae(beforeIndexedSublist);
					if (beforeSublist.size() == 0) {
						return null;
					} else {
						Expression varMatch = getAssociativeExpression(beforeSublist);
						if(!initialBinding.putExpressionMapping(identifier, varMatch)){
							return null;
						}
						List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, axisIndex, false));
						if (afterSublist.size() > 0) {
							if (!acceptPartialMatch) {
								return null;
							} else {
								initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, null, getAssociativeExpression(afterSublist)));
							}
						}
					}
				} else {
					Match<Expression> match = getMatchWithRank(entry.getMatches(), false);
					int axisIndex = match.getIndexedFormula().getIndex();
					List<IndexedFormula<Expression>> afterIndexedSublist = getSublist(availableFormulae, axisIndex, false);
					List<Expression> afterSublist = getFormulae(afterIndexedSublist);
					if (afterSublist.size() == 0) {
						return null;
					} else {
						Expression varMatch = getAssociativeExpression(afterSublist);
						if(!initialBinding.putExpressionMapping(identifier, varMatch)){
							return null;
						}
						List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, axisIndex, true));
						if (beforeSublist.size() > 0) {
							if (!acceptPartialMatch) {
								return null;
							} else {
								initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, getAssociativeExpression(beforeSublist), null));
							}
						}
					}
				}
			}
			return initialBinding;
		}
		return null;
	}

	private IBinding twoVariables(List<IndexedFormula<Expression>> availableFormulae, boolean acceptPartialMatch) {
		if (variables.size() == 2) {
			IBinding initialBinding = existingBinding.clone();
			IndexedFormula<Expression> var1 = variables.get(0);
			FreeIdentifier identifier1 = (FreeIdentifier) var1.getFormula();
			IndexedFormula<Expression> var2 = variables.get(1);
			FreeIdentifier identifier2 = (FreeIdentifier) var2.getFormula();
			Expression varMapping1 = initialBinding.getCurrentMapping(identifier1);
			Expression varMapping2 = initialBinding.getCurrentMapping(identifier2);
			if (varMapping1 != null && varMapping2 != null) {
				if (availableFormulae.size() != 2) {
					return null;
				} else {
					IndexedFormula<Expression> if1 = getMatch(availableFormulae, varMapping1);
					IndexedFormula<Expression> if2 = getMatch(availableFormulae, varMapping2);
					if (if1 == null || if2 == null || if1.equals(if2))
						return null;

				}
			} else if (varMapping1 != null) {
				IndexedFormula<Expression> if1 = getMatch(availableFormulae, varMapping1);
				if (if1 == null) {
					return null;
				}
				List<IndexedFormula<Expression>> afterIndexedSublist = getSublist(availableFormulae, if1.getIndex(), false);
				List<Expression> afterSublist = getFormulae(afterIndexedSublist);
				if (afterSublist.size() == 0) {
					return null;
				} else {
					Expression varMatch = getAssociativeExpression(afterSublist);
					if(!initialBinding.putExpressionMapping(identifier2, varMatch)){
						return null;
					}
					List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, if1.getIndex(), true));
					if (beforeSublist.size() > 0) {
						if (!acceptPartialMatch) {
							return null;
						} else {
							initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, getAssociativeExpression(beforeSublist), null));
						}
					}
				}
			} else if (varMapping2 != null) {
				IndexedFormula<Expression> if2 = getMatch(availableFormulae, varMapping2);
				if (if2 == null) {
					return null;
				}
				List<IndexedFormula<Expression>> beforeIndexedSublist = getSublist(availableFormulae, if2.getIndex(), true);
				List<Expression> beforeSublist = getFormulae(beforeIndexedSublist);
				if (beforeSublist.size() == 0) {
					return null;
				} else {
					Expression varMatch = getAssociativeExpression(beforeSublist);
					if(!initialBinding.putExpressionMapping(identifier1, varMatch)){
						return null;
					}
					List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, if2.getIndex(), false));
					if (afterSublist.size() > 0) {
						if (!acceptPartialMatch) {
							return null;
						} else {
							initialBinding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, null, getAssociativeExpression(afterSublist)));
						}
					}
				}
			} else {
				IndexedFormula<Expression> var2Match = availableFormulae.get(availableFormulae.size() - 1);
				if(!initialBinding.putExpressionMapping(identifier2, var2Match.getFormula())){
					return null;
				}
				List<Expression> beforeFormulae = getFormulae(getSublist(availableFormulae, availableFormulae.size() - 1, true));
				if(!initialBinding.putExpressionMapping(identifier1, getAssociativeExpression(beforeFormulae))){
					return null;
				}
			}
			return initialBinding;
		}
		return null;
	}
	
	private IBinding twoFormulae(List<IndexedFormula<Expression>> availableFormulae, boolean acceptPartialMatch){
		if (searchSpace.size() == 2) {
			IBinding initialBinding = existingBinding.clone();
			MatchEntry<Expression> matchEntry1 = searchSpace.get(0);
			MatchEntry<Expression> matchEntry2 = searchSpace.get(1);
			IndexedFormula<Expression> indexedFormula1 = matchEntry1.getIndexedPattern();
			IndexedFormula<Expression> indexedFormula2 = matchEntry2.getIndexedPattern();
			if (comesBefore(indexedFormula1, indexedFormula2)){
				for (Match<Expression> match : matchEntry1.getMatches()){
					for (Match<Expression> otherMatch : matchEntry2.getMatches()){
						if (otherMatch.equals(match)){
							continue;
						}
						int matchIndex = match.getIndexedFormula().getIndex();
						if (matchIndex == otherMatch.getIndexedFormula().getIndex() - 1){
							initialBinding.insertBinding(match.getBinding());
							initialBinding.insertBinding(otherMatch.getBinding());
							List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, matchIndex, true));
							List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, matchIndex+1, false));
							if (beforeSublist.size() != 0 || afterSublist.size() != 0) {
								if(!acceptPartialMatch){
									continue;
								}
								else {
									initialBinding.setAssociativeExpressionComplement(
											new AssociativeExpressionComplement(tag, 
													getAssociativeExpression(beforeSublist), getAssociativeExpression(afterSublist)));
								}
							}
							return initialBinding;
						}
					}
				}
			}
			else {
				for (Match<Expression> match : matchEntry2.getMatches()){
					for (Match<Expression> otherMatch : matchEntry1.getMatches()){
						if (otherMatch.equals(match)){
							continue;
						}
						int matchIndex = match.getIndexedFormula().getIndex();
						if (match.getIndexedFormula().getIndex() == otherMatch.getIndexedFormula().getIndex() - 1){
							initialBinding.insertBinding(match.getBinding());
							initialBinding.insertBinding(otherMatch.getBinding());
							List<Expression> beforeSublist = getFormulae(getSublist(availableFormulae, matchIndex, true));
							List<Expression> afterSublist = getFormulae(getSublist(availableFormulae, matchIndex+1, false));
							if (beforeSublist.size() != 0 || afterSublist.size() != 0) {
								if(!acceptPartialMatch){
									continue;
								}
								else {
									initialBinding.setAssociativeExpressionComplement(
											new AssociativeExpressionComplement(tag, 
													getAssociativeExpression(beforeSublist), getAssociativeExpression(afterSublist)));
								}
							}
							return initialBinding;
						}
					}
				}
			}
			
		}
		return null;
	}
	
	private boolean comesBefore(IndexedFormula<Expression> indexedFormula1, IndexedFormula<Expression> indexedFormula2){
		if (indexedFormula1.getIndex() > indexedFormula2.getIndex()){
			return false;
		}
		return true;
	}
}
