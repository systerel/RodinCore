package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.search.ResetIterator;

public class Dumper {
	
	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}
	
	private List<DumpableDataStructure> dataStructures = new ArrayList<DumpableDataStructure>(); 
	
	public void addDataStructure(String name, ResetIterator<IClause> iterator) {
		dataStructures.add(new DumpableDataStructure(name,iterator));
	}
	
	private static class DumpableDataStructure {
		private ResetIterator<IClause> iterator;
		private String name;
		
		private DumpableDataStructure(String name, ResetIterator<IClause> iterator) {
			this.name = name;
			this.iterator = iterator;
		}
	}
	
	public void dump() {
		debug("== Dumping datastructures ==");
		for (DumpableDataStructure ds : dataStructures) {
			debug("---- "+ds.name+" ----");
			ds.iterator.reset();
			while (ds.iterator.hasNext()) {
				debug(ds.iterator.next().toString());
			}
		}
		debug("======= End of dump ========");
	}
	
}
