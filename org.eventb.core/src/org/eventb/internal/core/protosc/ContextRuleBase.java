/**
 * 
 */
package org.eventb.internal.core.protosc;

import java.util.Arrays;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class ContextRuleBase {

	private final IContextRule[] carrierSetRules = new IContextRule[] {
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getCarrierSetConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of carrier set.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getConstantIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Carrier set also declared as constant.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getOldCarrierSets().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of carrier set (occurence in refined context).", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldCarrierSets().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Multiple declaration of carrier set (occurence in abstract context).", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getOldConstants().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Constant redeclared as carrier set in refined context.", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldConstants().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Constant redeclared as carrier set in refined context.", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			}
	};
	
	private final IContextRule[] constantRules = new IContextRule[] {
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getConstantConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of constant.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getCarrierSetIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Constant also declared as carrier set.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getOldConstants().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of constant (occurence in refined context).", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldConstants().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Multiple declaration of constant (occurence in abstract context).", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getOldCarrierSets().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Carrier set redeclared as constant in refined context.", SCProblem.SEVERITY_ERROR);
						try {
							// TODO: instead of getContents, we should use a "handle identifier" attribute (as everywhere)
							IRodinElement abstractElement = RodinCore.create(cache.getOldCarrierSets().get(element.getElementName()).getContents());
							if(abstractElement == null) {
								if(ContextSC.DEBUG)
									System.out.println(getClass().getName() + ": Invalid handle identifier"); //$NON-NLS-1$
							} else
								problemList.addProblem(element, "Carrier set redeclared as constant in refined context.", SCProblem.SEVERITY_ERROR);
						} catch (RodinDBException e) {
							// nothing to do
						}
						return false;
					}
					return true;
				}
			}	
	};
		
	private final IContextRule[] axiomRules = new IContextRule[] {
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getAxiomConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of axiom.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getTheoremIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Axiom also declared as theorem.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			}
	};
	
	private final IContextRule[] theoremRules = new IContextRule[] {
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getTheoremConflictSet().contains(element.getElementName())) {
						problemList.addProblem(element, "Multiple declaration of theorem.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			},
			new IContextRule() {
				public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList) {
					if(cache.getAxiomIdentMap().containsKey(element.getElementName())) {
						problemList.addProblem(element, "Theorem also declared as axiom.", SCProblem.SEVERITY_ERROR);
						return false;
					}
					return true;
				}
			}
	};

	/**
	 * @return Returns the axiomRules.
	 */
	public List<IContextRule> getAxiomRules() {
		return Arrays.asList(axiomRules);
	}

	/**
	 * @return Returns the carrierSetRules.
	 */
	public List<IContextRule> getCarrierSetRules() {
		return Arrays.asList(carrierSetRules);
	}

	/**
	 * @return Returns the constantRules.
	 */
	public List<IContextRule> getConstantRules() {
		return Arrays.asList(constantRules);
	}

	/**
	 * @return Returns the theoremRules.
	 */
	public List<IContextRule> getTheoremRules() {
		return Arrays.asList(theoremRules);
	}
	
}
