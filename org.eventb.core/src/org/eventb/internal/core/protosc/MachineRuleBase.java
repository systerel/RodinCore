/**
 * 
 */
package org.eventb.internal.core.protosc;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.IEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class MachineRuleBase {

	IMachineRule[] variableRules = new IMachineRule[] {
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getVariableConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of variable.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getOldCarrierSets().keySet().contains(element.getElementName())) {
						problemList.addProblem(element, "Carrier set redeclared as variable.", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldCarrierSets().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Carrier set redeclared as variable.", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getOldConstants().keySet().contains(element.getElementName())) {
						problemList.addProblem(element, "Constant redeclared as variable.", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldConstants().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Constant redeclared as variable.", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			}
	};
	
	IMachineRule[] invariantRules = new IMachineRule[] {
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getInvariantConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of invariant.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getTheoremIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Invariant also declared as theorem.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getEventIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Invariant also declared as event.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			}
	};
	
	IMachineRule[] theoremRules = new IMachineRule[] {
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getTheoremConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of theorem.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getInvariantIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Theorem also declared as invariant.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getEventIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Theorem also declared as event.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			}
	};
	
	IMachineRule[] eventRules = new IMachineRule[] {
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getEventConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of event.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getInvariantIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Event also declared as invariant.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getTheoremIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Event also declared as theorem.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(element.getElementName().equals("INITIALISATION")) {
						if(cache.getGuards((IEvent) element).length != 0) {
							problemList.addProblem(element, "Initialisation has a guard.", SCProblem.SEVERITY_ERROR);
							return false;
						}
						if(cache.getLocalVariableIdentMap((IEvent) element).size() != 0) {
							problemList.addProblem(element, "Initialisation has local variables.", SCProblem.SEVERITY_ERROR);
							return false;
						}
					}
					
					return true;
				}
			}
	};
	
	IMachineRule[] localVariableRules = new IMachineRule[] {
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getLocalVariableConflictSet((IEvent) element.getParent()).contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of local variable.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getVariableIdentMap().keySet().contains(element.getElementName())) {
						problemList.addProblem(element, "Variable redeclared as local variable.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getOldCarrierSets().keySet().contains(element.getElementName())) {
						problemList.addProblem(element, "Carrier set redeclared as local variable.", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldCarrierSets().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Carrier set redeclared as local variable.", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			},
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getOldConstants().keySet().contains(element.getElementName())) {
						problemList.addProblem(element, "Constant redeclared as local variable.", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldConstants().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Constant redeclared as local variable.", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			}
	};
	
	IMachineRule[] guardRules = new IMachineRule[] {
			new IMachineRule() {
				public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException {
					if(cache.getGuardConflictSet((IEvent) element.getParent()).contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of guard.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			}
	};
	
	IMachineRule[] actionRules = new IMachineRule[] {
	};
	
	/**
	 * @return Returns the variableRules.
	 */
	public List<IMachineRule> getVariableRules() {
		return Arrays.asList(variableRules);
	}

	/**
	 * @return Returns the variableRules.
	 */
	public List<IMachineRule> getInvariantRules() {
		return Arrays.asList(invariantRules);
	}

	/**
	 * @return Returns the theoremRules.
	 */
	public List<IMachineRule> getTheoremRules() {
		return Arrays.asList(theoremRules);
	}

	/**
	 * @return Returns the eventRules.
	 */
	public List<IMachineRule> getEventRules() {
		return Arrays.asList(eventRules);
	}

	/**
	 * @return Returns the actionRules.
	 */
	public List<IMachineRule> getActionRules() {
		return Arrays.asList(actionRules);
	}

	/**
	 * @return Returns the guardRules.
	 */
	public List<IMachineRule> getGuardRules() {
		return Arrays.asList(guardRules);
	}

	/**
	 * @return Returns the localVariableRules.
	 */
	public List<IMachineRule> getLocalVariableRules() {
		return Arrays.asList(localVariableRules);
	}

}
