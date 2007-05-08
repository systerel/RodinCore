package org.eventb.internal.pp.core.tracing;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;

public class ClauseOrigin extends AbstractInferrenceOrigin {

	public ClauseOrigin(List<IClause> parents) {
		super(parents);
	}

	Level level = null;
	public Level getLevel() {
		if (level != null) return level;
		
		Level result = null;
		for (IClause clause : parents) {
			if (result == null || result.isAncestorOf(clause.getLevel())) {
				result = clause.getLevel();
			}
		}
		level = result;
		return result;
	}
	
	
}
