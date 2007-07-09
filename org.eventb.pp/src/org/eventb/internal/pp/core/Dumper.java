package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.search.ResetIterator;

public class Dumper {
	
	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private List<DumpableDataStructure> dataStructures = new ArrayList<DumpableDataStructure>(); 
	private List<DumpableObject> objects = new ArrayList<DumpableObject>();
	
	public void addDataStructure(String name, ResetIterator<Clause> iterator) {
		dataStructures.add(new DumpableDataStructure(name,iterator));
	}
	
	public void addObject(String name, Object object) {
		objects.add(new DumpableObject(name,object));
	}
	
	private static class DumpableObject {
		Object object;
		String name;
		
		DumpableObject(String name, Object object) {
			this.name = name;
			this.object = object;
		}
	}
	
	private static class DumpableDataStructure {
		ResetIterator<Clause> iterator;
		String name;
		
		DumpableDataStructure(String name, ResetIterator<Clause> iterator) {
			this.name = name;
			this.iterator = iterator;
		}
	}
	
	public void dump() {
		if (DEBUG) {
			debug("== Dumping datastructures ==");
			for (DumpableDataStructure ds : dataStructures) {
				debug("---- "+ds.name+" ----");
				ds.iterator.reset();
				while (ds.iterator.hasNext()) {
					debug(ds.iterator.next().toString());
				}
			}
			for (DumpableObject object : objects) {
				debug("---- "+object.name+" ----");
				debug(object.object.toString());
			}
			debug("======= End of dump ========");
		}
	}
	
}
