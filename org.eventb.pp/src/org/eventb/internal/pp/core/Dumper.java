/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.search.ResetIterator;

/**
 * This class is responsible for dumping data structures.
 * <p>
 * Either a {@link ResetIterator} or an {@link Object} might be given
 * to this dumper. A call to {@link #dump()} will then output the content
 * of these data structures to the console if the DEBUG flag is set.
 *
 * @author François Terrier
 *
 */
public final class Dumper {
	
	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private final List<Dumpable> dumpables = new ArrayList<Dumpable>(); 
	
	/**
	 * Adds a {@link ResetIterator} to the dumper.
	 * <p>
	 * Each time {@link #dump()} is called, all elements of the iterator
	 * will be output to the console.
	 * 
	 * @param name the name of this data structure
	 * @param iterator the content of this data structure
	 */
	public void addDataStructure(String name, ResetIterator<Clause> iterator) {
		dumpables.add(new DumpableDataStructure(name,iterator));
	}
	
	/**
	 * Adds an object to the dumper.
	 * 
	 * @param name the name of this object
	 * @param object the object to be output to the consule
	 */
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
		
		@Override
		public String getName() {
			return name;
		}

		@Override
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
		
		@Override
		public void output() {
			iterator.reset();
			while (iterator.hasNext()) {
				debug(iterator.next().toString());
			}
		}

		@Override
		public String getName() {
			return name;
		}
	}
	
	/**
	 * Dumps all registered data structures if {@value Dumper#DEBUG} is set to true.
	 */
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
