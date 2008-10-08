package org.eventb.core.indexer;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.index.IDeclaration;

public class SymbolTable {

	public static class IdentTable {
		private final Map<FreeIdentifier, IDeclaration> table;
		
		public IdentTable() {
			this.table = new HashMap<FreeIdentifier, IDeclaration>();
		}
		
		public void put(FreeIdentifier ident, IDeclaration declaration) {
			table.put(ident, declaration);
		}
		
		public IDeclaration get(FreeIdentifier ident) {
			return table.get(ident);
		}
		
		public boolean contains(FreeIdentifier ident) {
			return table.containsKey(ident);
		}
		
		public boolean isEmpty() {
			return table.isEmpty();
		}
	}
	
	
	private final Map<String, IDeclaration> table;
	
	SymbolTable() {
		this.table = new HashMap<String, IDeclaration>();
	}
	
//	public IDeclaration get(String symbol) {
//		return table.get(symbol);
//	}
	
	public void put(IDeclaration declaration) {
		// TODO maybe return a boolean false if an association already exists
		final String name = declaration.getName();
		final IDeclaration previous = table.put(name, declaration);
		if (previous != null) {
			table.remove(name);
			// TODO maybe store both in a list of duplicate symbols
		}
	}
	
	public void clear() {
		table.clear();
	}
	
	public IdentTable extractIdentTable(FreeIdentifier[] idents) {
		final IdentTable result = new IdentTable();
		for (FreeIdentifier ident : idents) {
			final IDeclaration declaration = table.get(ident.getName());
			if (declaration != null) {
				result.put(ident, declaration);
			}
		}
		return result;
	}
}
