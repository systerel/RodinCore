/**
 * 
 */
package org.eventb.core.tests.pog;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author halstefa
 *
 */
public class TestMachineSeesContext extends EventBPOTest {
	
	public void testSees_00() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈S1"));
		
		con.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("V1", intType);
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "S1", "C1", "V1");
				
	}
	

}
