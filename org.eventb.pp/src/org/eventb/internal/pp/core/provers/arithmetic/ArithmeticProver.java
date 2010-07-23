package org.eventb.internal.pp.core.provers.arithmetic;

import java.util.Set;

import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;

public final class ArithmeticProver implements IProverModule {

	@Override
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProverResult contradiction(Level oldLevel, Level newLevel,
			Set<Level> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProverResult next(boolean force) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerDumper(Dumper dumper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeClause(Clause clause) {
		// TODO Auto-generated method stub

	}

}
