package org.eventb.internal.pp.core.tracing;

import java.util.Arrays;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;

public class ExtensionalityOrigin extends AbstractInferrenceOrigin {

	private Level level;
	
	public ExtensionalityOrigin(Clause parent) {
		super(parent);
		
		this.level = parent.getLevel();
	}
	
	public ExtensionalityOrigin(Clause parent1, Clause parent2) {
		super(Arrays.asList(new Clause[]{parent1,parent2}));
		
		this.level = parent1.getLevel().isAncestorOf(parent2.getLevel())?parent2.getLevel():parent1.getLevel();
	}

	public Level getLevel() {
		return level;
	}

}
