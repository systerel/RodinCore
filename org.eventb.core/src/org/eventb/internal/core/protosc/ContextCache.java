/**
 * 
 */
package org.eventb.internal.core.protosc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.core.ITheorem;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class ContextCache extends Cache<IContext> {

	private HashMap<String, ICarrierSet> oldCarrierSets; // the collection of carrier sets of the abstractions
	private HashSet<String> carrierSetConflictSet;
	private HashMap<String, String> carrierSetIdentMap;

	private HashMap<String, IConstant> oldConstants; // the collection of constants of the abstractions
	private HashSet<String> constantConflictSet = new HashSet<String>(3);
	private HashMap<String, String> constantIdentMap = new HashMap<String, String>(3);

	private ArrayList<IAxiom> oldAxioms; // the axioms of the abstractions
	private HashSet<String> axiomConflictSet = new HashSet<String>(3);
	private HashMap<String, String> axiomIdentMap = new HashMap<String, String>(3);

	private ArrayList<ITheorem> oldTheorems; // the theorems of the abstractions
	private HashSet<String> theoremConflictSet = new HashSet<String>(3);
	private HashMap<String, String> theoremIdentMap = new HashMap<String, String>(3);
	
	public final HashMap<String, ICarrierSet> newCarrierSets; // the collection of the new carrier sets of the context
	public final HashMap<String, IConstant> newConstants; // the collection of the new constants of the context
	public final ArrayList<IAxiom> newAxioms; // the new axioms of the context
	public final ArrayList<ITheorem> newTheorems; // the new theorems of the context

	public ContextCache(IContext context, ISCProblemList problemList) throws RodinDBException {
		super(context);
		
		carrierSetConflictSet = new HashSet<String>(3);
		carrierSetIdentMap = new HashMap<String, String>(getCarrierSets().length * 4 / 3 + 1);
		parseNames(getCarrierSets(), carrierSetIdentMap, carrierSetConflictSet, problemList);
		
		constantConflictSet = new HashSet<String>(3);
		constantIdentMap = new HashMap<String, String>(getConstants().length * 4 / 3 + 1);
		parseNames(getConstants(), constantIdentMap, constantConflictSet, problemList);
		
		axiomConflictSet = new HashSet<String>(3);
		axiomIdentMap = new HashMap<String, String>(getAxioms().length * 4 / 3 + 1);
		parseNames(getAxioms(), axiomIdentMap, axiomConflictSet, problemList);
		
		theoremConflictSet = new HashSet<String>(3);
		theoremIdentMap = new HashMap<String, String>(getTheorems().length * 4 / 3 + 1);
		parseNames(getTheorems(), theoremIdentMap, theoremConflictSet, problemList);
		
		newCarrierSets = new HashMap<String, ICarrierSet>(getCarrierSets().length * 4 / 3 + 1);
		newConstants = new HashMap<String, IConstant>(getConstants().length * 4 / 3 + 1);
		newAxioms = new ArrayList<IAxiom>(getAxioms().length);
		newTheorems = new ArrayList<ITheorem>(getTheorems().length);

		// TODO: get type environment of abstract context
		
		// TODO fill "old" fields properly
		
		oldCarrierSets = new HashMap<String, ICarrierSet>();
		oldConstants = new HashMap<String, IConstant>();
		oldAxioms =  new ArrayList<IAxiom>();
		oldTheorems =  new ArrayList<ITheorem>();

	}
	
	/**
	 * @return Returns the axiomConflictSet.
	 */
	public HashSet<String> getAxiomConflictSet() {
		return axiomConflictSet;
	}

	/**
	 * @return Returns the axiomIdentMap.
	 */
	public HashMap<String, String> getAxiomIdentMap() {
		return axiomIdentMap;
	}

	/**
	 * @return Returns the carrierSetConflictSet.
	 */
	public HashSet<String> getCarrierSetConflictSet() {
		return carrierSetConflictSet;
	}

	/**
	 * @return Returns the carrierSetIdentMap.
	 */
	public HashMap<String, String> getCarrierSetIdentMap() {
		return carrierSetIdentMap;
	}

	/**
	 * @return Returns the constantConflictSet.
	 */
	public HashSet<String> getConstantConflictSet() {
		return constantConflictSet;
	}

	/**
	 * @return Returns the constantIdentMap.
	 */
	public HashMap<String, String> getConstantIdentMap() {
		return constantIdentMap;
	}

	/**
	 * @return Returns the theoremConflictSet.
	 */
	public HashSet<String> getTheoremConflictSet() {
		return theoremConflictSet;
	}

	/**
	 * @return Returns the theoremIdentMap.
	 */
	public HashMap<String, String> getTheoremIdentMap() {
		return theoremIdentMap;
	}

	/**
	 * @return Returns the oldAxioms.
	 */
	public ArrayList<IAxiom> getOldAxioms() {
		return oldAxioms;
	}

	/**
	 * @return Returns the oldCarrierSets.
	 */
	public HashMap<String, ICarrierSet> getOldCarrierSets() {
		return oldCarrierSets;
	}

	/**
	 * @return Returns the oldConstants.
	 */
	public HashMap<String, IConstant> getOldConstants() {
		return oldConstants;
	}

	/**
	 * @return Returns the oldTheorems.
	 */
	public ArrayList<ITheorem> getOldTheorems() {
		return oldTheorems;
	}

	private IConstant[] constants = null;
	public IConstant[] getConstants() throws RodinDBException {
		if(constants == null) {
			constants = file.getConstants();
		}
		return constants;
	}
	
	private ICarrierSet[] carrierSets = null;
	public ICarrierSet[] getCarrierSets() throws RodinDBException {
		if(carrierSets == null) {
			carrierSets = file.getCarrierSets();
		}
		return carrierSets;
	}
	
	private IAxiom[] axioms = null;
	public IAxiom[] getAxioms() throws RodinDBException {
		if(axioms == null) {
			axioms = file.getAxioms();
		}
		return axioms;
	}
	
	private ITheorem[] theorems = null;
	public ITheorem[] getTheorems() throws RodinDBException {
		if(theorems == null) {
			theorems = file.getTheorems();
		}
		return theorems;
	}

	/**
	 * @return Returns the newAxioms.
	 */
	public ArrayList<IAxiom> getNewAxioms() {
		return newAxioms;
	}

	/**
	 * @return Returns the newCarrierSets.
	 */
	public HashMap<String, ICarrierSet> getNewCarrierSets() {
		return newCarrierSets;
	}

	/**
	 * @return Returns the newConstants.
	 */
	public HashMap<String, IConstant> getNewConstants() {
		return newConstants;
	}

	/**
	 * @return Returns the newTheorems.
	 */
	public ArrayList<ITheorem> getNewTheorems() {
		return newTheorems;
	}
	
}
