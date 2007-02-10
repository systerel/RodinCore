package org.eventb.ui.prover;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

public interface ITacticProvider {

	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input);

	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs);

	/**
	 * @deprecated Use
	 *             {@link #getApplicablePositions(IProofTreeNode,Predicate,String)}
	 *             instead
	 */
	@Deprecated
	public boolean isApplicable(IProofTreeNode node, Predicate hyp, String input);

	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position);

}
