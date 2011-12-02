package fr.systerel.editor.internal.actions.operations.extension;

import org.eclipse.ui.IEditorPart;
import org.eventb.core.IAxiom;
import org.eventb.core.IEventBRoot;

import fr.systerel.editor.actions.IEventBDialog;
import fr.systerel.editor.internal.actions.operations.OperationUtils;
import fr.systerel.editor.internal.dialogs.NewDerivedPredicateDialog;

public class AxiomMaker extends AbstractRodinEditorWizardElementMaker {

	public AxiomMaker(IEditorPart editor, IEventBRoot root) {
		super(editor, root);
	}

	@Override
	public void addValues(IEventBDialog dialog) {
		assert dialog instanceof NewDerivedPredicateDialog<?>;
		final NewDerivedPredicateDialog<IAxiom> ndp = (NewDerivedPredicateDialog<IAxiom>) dialog;
		final String[] names = ndp.getNewNames();
		final String[] contents = ndp.getNewContents();
		final boolean[] isTheorem = ndp.getIsTheorem();
		OperationUtils.executeAtomic(getHistory(), getUndoContext(),
				"Create Axiom", EventBOperationFactory.createAxiom(getRoot(),
						names, contents, isTheorem));
	}

}
