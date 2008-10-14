package org.eventb.core.indexer;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.index.IDeclaration;

public class SymbolTable {

	private final Map<String, IDeclaration> table;
	private final SymbolTable prev;

	SymbolTable(SymbolTable prev) {
		this.table = new HashMap<String, IDeclaration>();
		this.prev = prev;
	}

	public IDeclaration lookup(String symbol) {
		final IDeclaration declaration = table.get(symbol);
		if (declaration == null && prev != null) {
			return prev.lookup(symbol);
		}
		return declaration;
	}

	/**
	 * Puts the given declaration in this SymbolTable.
	 * <p>
	 * It is possible that a declaration with the same name already exists. The
	 * value of the <code>override</code> parameter effects the resolution of
	 * such a conflict:
	 * <ul>
	 * <li><code>true</code> - in this case the declaration erases the
	 * previous one</li>
	 * <li><code>false</code> - in this case the declaration is not put and
	 * the previous one is removed.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param declaration
	 *            the declaration to add
	 * @param override
	 *            specify how to handle conflict is the same name already exists
	 */
	public void put(IDeclaration declaration, boolean override) {
		// TODO maybe return a boolean false if an association already exists
		final String name = declaration.getName();

		if (override || !table.containsKey(name)) {
			table.put(name, declaration);
		} else {
			table.remove(name);
		}
	}

	public void clear() {
		table.clear();
	}

	/**
	 * Extracts an IdentTable with only the given FreeIdentifiers.
	 * 
	 * @param idents
	 * @param identTable 
	 */
	public void addToIdentTable(FreeIdentifier[] idents, IdentTable identTable) {
		for (FreeIdentifier ident : idents) {
			if (ident.isPrimed()) {
				ident = ident.withoutPrime(FormulaFactory.getDefault());
			}
			final IDeclaration declaration = lookup(ident.getName());
			if (declaration != null) {
				identTable.put(ident, declaration);
			}
		}
	}

	/**
	 * @param abstSymbolTable
	 * @param override 
	 */
	public void putAll(SymbolTable abstSymbolTable, boolean override) {
		for (IDeclaration declaration : abstSymbolTable.table.values()) {
			this.put(declaration, override);
		}
		if (abstSymbolTable.prev != null) {
			putAll(abstSymbolTable.prev, override);
		}
	}
}
