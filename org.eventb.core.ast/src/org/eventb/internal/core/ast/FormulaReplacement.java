package org.eventb.internal.core.ast;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.QuantifiedPredicate;

/**
 * A formula replacement carries out an unbind replacement on the specified
 * predicate, using the substitution specified by <code>map</code> (see constructor).
 * 
 * @author halstefa
 *
 */
public class FormulaReplacement extends Replacement {
	
	private final QuantifiedPredicate predicate;
	private final Map<Integer, Expression> map;
	
	public FormulaReplacement(QuantifiedPredicate predicate, Map<Integer, Expression> map) {
		this.predicate = predicate;
		this.map =map;
	}

	/**
	 * @return Returns the map.
	 */
	public Map<Integer, Expression> getMap() {
		return map;
	}

	/**
	 * @return Returns the predicate.
	 */
	public QuantifiedPredicate getPredicate() {
		return predicate;
	}

}
