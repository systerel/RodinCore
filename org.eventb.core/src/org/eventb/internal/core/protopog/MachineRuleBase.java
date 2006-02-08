/**
 * 
 */
package org.eventb.internal.core.protopog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eventb.core.IAction;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.protopog.ProofObligation.SForm;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class MachineRuleBase {

	private final IPOGMachineRule[] rules = new IPOGMachineRule[] {
			new IPOGMachineRule() {
				// MDL_INV_WD
				public List<ProofObligation> get(SCMachineCache cache) throws RodinDBException {
					ArrayList<ProofObligation> poList = new ArrayList<ProofObligation>(cache.getNewInvariants().length);
					for(IInvariant invariant : cache.getNewInvariants()) {
						Predicate wdPredicate = cache.getPredicate(invariant.getContents()).getWDPredicate(cache.getFactory());
						if(!wdPredicate.equals(cache.BTRUE)) {
							ProofObligation wdObligation = new ProofObligation(
									invariant.getElementName() +  "/WD",
									cache.getHypSetName(invariant.getElementName()),
									new ProofObligation.PForm(wdPredicate)
							);
							poList.add(wdObligation);
						}
					}
					return poList;
				}
			},
			new IPOGMachineRule() {
				// MDL_THM_WD and MDL_THM
				public List<ProofObligation> get(SCMachineCache cache) throws RodinDBException {
					ArrayList<ProofObligation> poList = new ArrayList<ProofObligation>(cache.getNewTheorems().length * 2);
					for(ITheorem theorem : cache.getNewTheorems()) {
						Predicate predicate = cache.getPredicate(theorem.getContents());
						Predicate wdPredicate = predicate.getWDPredicate(cache.getFactory());
						if(!wdPredicate.equals(cache.BTRUE)) {
							ProofObligation wdObligation = new ProofObligation(
									theorem.getElementName() + "/WD",
									cache.getHypSetName(theorem.getElementName()),
									new ProofObligation.PForm(wdPredicate)
							);
							poList.add(wdObligation);
						}
						if(!predicate.equals(cache.BTRUE)) {
							ProofObligation obligation = new ProofObligation(
									theorem.getElementName(),
									cache.getHypSetName(theorem.getElementName()),
									new ProofObligation.PForm(predicate)
							);
							poList.add(obligation);
						}
					}
					return poList;
				}
				
			},
			new IPOGMachineRule() {
				//	 MDL_INI_WD and MDL_INI_FIS and MDL_INI_INV
				public List<ProofObligation> get(SCMachineCache cache) throws RodinDBException {
					ArrayList<ProofObligation> poList = new ArrayList<ProofObligation>(cache.getNewTheorems().length * 2);
					
					return poList;
				}
			},
			new IPOGMachineRule() {
				//	MDL_GRD_WD and MDL_EVT_WD and MDL_EVT_FIS and MDL_EVT_INV
				//	MDL_INI_WD and MDL_INI_FIS and MDL_INI_INV
				public List<ProofObligation> get(SCMachineCache cache) throws RodinDBException {
					Predicate btrue = cache.getFactory().makeLiteralPredicate(Formula.BTRUE, null);
					ArrayList<ProofObligation> poList = new ArrayList<ProofObligation>(cache.getNewTheorems().length * 2 + 1);
					
					// disjuncts of deadlock-freeness predicate
					ArrayList<Predicate> dlkPredicate = new ArrayList<Predicate>(cache.getEvents().length);
					
					for(ISCEvent event : cache.getEvents()) {
						String evtName = event.getElementName();
						IGuard[] guards = event.getGuards();
						ITypeEnvironment typeEnvironment = cache.getTypeEnvironment(event.getSCVariables(), null);
						ITypeEnvironment fullTypeEnvironment = cache.getGlobalTypeEnvironment();
						fullTypeEnvironment.addAll(typeEnvironment);
						
						String globalHypsetName = (evtName.equals("INITIALISATION")) ? cache.getOldHypSetName() : cache.getNewHypsetName();
						
						// MDL_GRD_WD
						ArrayList<ProofObligation.Form> precGuards = new ArrayList<ProofObligation.Form>(guards.length);
						for(IGuard guard : guards) { // guards is the empty list for the initialisation
							Predicate predicate = cache.getPredicate(guard.getContents(), fullTypeEnvironment);
							Predicate wdPredicate = predicate.getWDPredicate(cache.getFactory());
							if(!wdPredicate.equals(btrue))
								poList.add(new ProofObligation(
										evtName + "/" + guard.getElementName() + "/WD",
										typeEnvironment,
										globalHypsetName,
										new ArrayList<ProofObligation.Form>(precGuards),
										new ProofObligation.PForm(wdPredicate),
										new HashMap<String, String>(0)
										)
								);
							precGuards.add(new ProofObligation.PForm(predicate));
						}
						
						// create existentially quantified guards (for use in MDL_DLK)
						if (precGuards.size() > 0) { // this is false for the initialisation
							Predicate[] gdPredicates = new Predicate[precGuards.size()];
							for (int i = 0; i < gdPredicates.length; i++) {
								gdPredicates[i] = ((ProofObligation.PForm) precGuards.get(i)).predicate;
							}
							Predicate conjGuard;
							if(gdPredicates.length>1)
								conjGuard = cache.getFactory().makeAssociativePredicate(Formula.LAND, gdPredicates, null);
							else
								conjGuard = gdPredicates[0];
							IVariable[] variables = event.getVariables();
							if (variables.length > 0) {
								ArrayList<BoundIdentDecl> identifiers = new ArrayList<BoundIdentDecl>(variables.length);
								ArrayList<FreeIdentifier> fIdentifiers = new ArrayList<FreeIdentifier>(variables.length);
								for (IVariable variable : variables) {
									identifiers.add(cache.getFactory().makeBoundIdentDecl(variable.getElementName(), null));
									FreeIdentifier fId = cache.getFactory().makeFreeIdentifier(variable.getElementName(), null);
									ITypeCheckResult res = fId.typeCheck(fullTypeEnvironment);
									assert(res.isSuccess());
									fIdentifiers.add(fId);
								}
								Predicate boundGuard = conjGuard.bindTheseIdents(fIdentifiers, cache.getFactory());
								dlkPredicate.add(cache.getFactory().makeQuantifiedPredicate(Formula.EXISTS, identifiers, boundGuard, null));
							} else {
								dlkPredicate.add(conjGuard);
							}
						}
						
						// MDL_EVT_WD and MDL_EVT_FIS
						IAction[] actions = event.getActions();
						
						// some preparations for MDL_EVT_INV
						ArrayList<Assignment> precBA = new ArrayList<Assignment>(actions.length);
						ArrayList<BecomesEqualTo> postBA = new ArrayList<BecomesEqualTo>(actions.length);
						HashSet<String> actionLL = new HashSet<String>(
							(cache.getSCCarrierSets().length + cache.getSCConstants().length + cache.getSCVariables().length) * 4 / 3 + 1);
						int numAssignedVars = 0;

						for(IAction action : actions) {
							Assignment assignment = cache.getAssignment(action.getContents(), fullTypeEnvironment);
							numAssignedVars += assignment.getAssignedIdentifiers().length;
							if(assignment instanceof BecomesEqualTo)
								postBA.add((BecomesEqualTo) assignment);
							else
								precBA.add(assignment);
							String varList = assignment.getAssignedIdentifiers()[0].getName();
							actionLL.add(assignment.getAssignedIdentifiers()[0].getName());
							for(int i=1; i<assignment.getAssignedIdentifiers().length; i++) {
								varList += "-" + assignment.getAssignedIdentifiers()[i].getName();
								actionLL.add(assignment.getAssignedIdentifiers()[i].getName());
							}
							Predicate wdPredicate = assignment.getWDPredicate(cache.getFactory());
							if(!wdPredicate.equals(btrue)) {
								poList.add(
										new ProofObligation(
												evtName + "/" + varList + "/WD",
												typeEnvironment,
												globalHypsetName,
												new ArrayList<ProofObligation.Form>(precGuards),
												new ProofObligation.PForm(wdPredicate),
												new HashMap<String, String>(0)
										)
								);
							}
							Predicate fisPredicate = assignment.getFISPredicate(cache.getFactory());
							if(!fisPredicate.equals(btrue)) {
								poList.add(
										new ProofObligation(
												evtName + "/" + varList + "/FIS",
												typeEnvironment,
												globalHypsetName,
												new ArrayList<ProofObligation.Form>(precGuards),
												new ProofObligation.PForm(fisPredicate),
												new HashMap<String, String>(0)
										)
								);
							}
						}
						
						// MDL_EVT_INV
						IInvariant[] invariants = cache.getNewInvariants();
						for(IInvariant invariant : invariants) {
							Predicate predicate = cache.getPredicate(invariant.getContents(), fullTypeEnvironment);
							FreeIdentifier[] freeIdentifiers = predicate.getFreeIdentifiers();
							HashSet<String> freeNames = new HashSet<String>(freeIdentifiers.length * 4 / 3 + 1);
							boolean idInt = false; // common identifiers?
							for(FreeIdentifier identifier : freeIdentifiers) {
								freeNames.add(identifier.getName());
								if(!idInt && actionLL.contains(identifier.getName())) {
									idInt = true;
								}
							}
							if(idInt) {
								ArrayList<ProofObligation.Form> prec = new ArrayList<ProofObligation.Form>(precGuards);
								ArrayList<BecomesEqualTo> post = new ArrayList<BecomesEqualTo>(postBA.size() + numAssignedVars);
								for(BecomesEqualTo bet : postBA) {
									if(freeNames.contains(bet.getAssignedIdentifiers()[0].getName()))
										post.add(bet);
								}
								for(Assignment ass : precBA) {
									FreeIdentifier[] left = ass.getAssignedIdentifiers();
									FreeIdentifier[] right = new FreeIdentifier[left.length];
									boolean rename = false;
									for(int i=0; i<left.length; i++) {
										if(!rename && freeNames.contains(left[i].getName()))
											rename = true;
										right[i] = cache.getFactory().makeFreeIdentifier(left[i].getName() + "'", null);
									}
									if(rename) {
										prec.add(new ProofObligation.PForm(ass.getBAPredicate(cache.getFactory())));
										post.add(cache.getFactory().makeBecomesEqualTo(left, right, null));
									}
								}
								ProofObligation.Form goal = new ProofObligation.PForm(predicate);
								for(int i=post.size()-1; i>=0; i--) {
									goal = new SForm(post.get(i), goal);
								}
								poList.add(
										new ProofObligation(
												evtName + "/" + invariant.getElementName() + "/INV",
												typeEnvironment,
												globalHypsetName,
												prec,
												goal,
												new HashMap<String, String>(0)
										)
								);
							}
						}

					}
					
					// MDL_DLK
					Predicate DLK = null;
					if(dlkPredicate.size() > 1)
						DLK = cache.getFactory().makeAssociativePredicate(Formula.LOR, dlkPredicate, null);
					else if(dlkPredicate.size() == 1)
						DLK = dlkPredicate.get(0);
					if (DLK != null)
						poList.add(
								new ProofObligation(
										"DLK", 
										cache.getFactory().makeTypeEnvironment(), 
										cache.getNewHypsetName(),
										new ArrayList<ProofObligation.Form>(0),
										new ProofObligation.PForm(DLK),
										new HashMap<String, String>(0)
								)
						);
					
					return poList;
				}
			}
	};
	
	public List<IPOGMachineRule> getRules() {
		return Arrays.asList(rules);
	}

}
