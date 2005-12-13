/**
 * 
 */
package org.eventb.internal.core.ast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;

/**
 * This substitution is "pre-logic" it is used during parsing to turn parsed indentifiers
 * into bound identifiers under the scope of quantifiers.
 * 
 * @author halstefa
 *
 */
public class BindReplacement extends Replacement {

	private Map<String, Integer> map;
	
	public BindReplacement(Collection<BoundIdentDecl> identsToBind) {
		final int hashSize = (int) (identsToBind.size() / 0.75);
		map = new HashMap<String, Integer>(hashSize);
		// The identifiers are indexed in reverse order.
		int index = identsToBind.size() - 1;
		for (BoundIdentDecl ident : identsToBind) {
			map.put(ident.getName(), index);
			--index;
		}
	}
	
	public Integer getIndex(FreeIdentifier identifier) {
		return map.get(identifier.getName());
	}
	
	public int size() {
		return map.size();
	}
	
}
