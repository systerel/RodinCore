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
	
	private List<Dumpable> dumpables = new ArrayList<Dumpable>(); 
	
	public void addDataStructure(String name, ResetIterator<Clause> iterator) {
		dumpables.add(new DumpableDataStructure(name,iterator));
	}
	
	public void addObject(String name, Object object) {
		dumpables.add(new DumpableObject(name,object));
	}
	
	private static interface Dumpable {
		public String getName();
		public void output();
	}
	
	private static class DumpableObject implements Dumpable {
		Object object;
		String name;
		
		DumpableObject(String name, Object object) {
			this.name = name;
			this.object = object;
		}
		
		public String getName() {
			return name;
		}

		public void output() {
			debug(object.toString());
		}
	}
	
	private static class DumpableDataStructure implements Dumpable {
		ResetIterator<Clause> iterator;
		String name;
		
		DumpableDataStructure(String name, ResetIterator<Clause> iterator) {
			this.name = name;
			this.iterator = iterator;
		}
		
		public void output() {
			iterator.reset();
			while (iterator.hasNext()) {
				debug(iterator.next().toString());
			}
		}

		public String getName() {
			return name;
		}
	}
	
	public void dump() {
		if (DEBUG) {
			debug("== Dumping datastructures ==");
			for (Dumpable ds : dumpables) {
				debug("---- "+ds.getName()+" ----");
				ds.output();
			}
			debug("======= End of dump ========");
		}
	}
	
}
