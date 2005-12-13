/**
 * 
 */
package org.eventb.internal.core.ast;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Free identifier substitution.
 * 
 * @author halstefa
 *
 */
public class FreeReplacement extends Replacement {
	
	Map<FreeIdentifier, Info> map;
	
	/**
	 * During Substitution we use an expression with some extra information.
	 * Initialise the substitute for expression
	 * @param map The substitution
	 */
	public FreeReplacement(Map<FreeIdentifier, Expression> map) {
		this.map = new HashMap<FreeIdentifier, Info>(map.size() + map.size()/4 + 1);
		for(Map.Entry<FreeIdentifier, Expression> entry : map.entrySet()) {
			
			this.map.put(entry.getKey(), new Info(entry.getValue()));
		}
	}
	
	/**
	 * When substituting we must keep deBuijn indices up-to-date.
	 * This is only necessary, if there there are unmatched deBruijn indices
	 * in the expression.
	 * @param identifier The identifier of the free variable
	 * @return True, if there are no unmatched deBuijn indices in the expression
	 */
	public Info getInfo(FreeIdentifier identifier) {
		return map.get(identifier);
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

}
