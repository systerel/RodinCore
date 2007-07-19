package org.eventb.internal.ui.preferences;

import java.util.ArrayList;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.ITacticContainer;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 */
public abstract class TacticContainerPreferencePage
	extends PreferencePage
	implements
		IWorkbenchPreferencePage {

	TwoListSelectionEditor tacticsEditor;

	String enableFieldName;
	
	String enableFieldDescription;

	String tacticsFieldName;
	
	String tacticsFieldDescription;

	ITacticContainer tacticContainer = null;
	
	public TacticContainerPreferencePage(String description,
			String enableFieldName, String enableFieldDescription,
			String tacticsFieldName, String tacticsFieldDescription) {
		super();
		this.enableFieldName = enableFieldName;
		this.enableFieldDescription = enableFieldDescription;
		this.tacticsFieldName = tacticsFieldName;
		this.tacticsFieldDescription = tacticsFieldDescription;
		setTacticContainer();
		setPreferenceStore(EventBUIPlugin.getDefault().getPreferenceStore());
		setDescription(description);
	}

	protected abstract void setTacticContainer();

	@Override
	public void createFieldEditors() {
		addField(
				new BooleanFieldEditor(
					enableFieldName,
					enableFieldDescription,
					getFieldEditorParent()));

		tacticsEditor = new TwoListSelectionEditor(
				tacticsFieldName,
				tacticsFieldDescription,
				getFieldEditorParent()) {

			@Override
			protected String createList(ArrayList<Object> objects) {
				return ProverUIUtils.toCommaSeparatedList(objects);
			}

			@Override
			protected ArrayList<Object> parseString(String stringList) {
				String [] tacticIDs = ProverUIUtils
						.parseString(stringList);
				ArrayList<Object> result = new ArrayList<Object>();
				for (String string : tacticIDs) {
					if (!SequentProver.getTacticRegistry().isRegistered(string)) {
						if (UIUtils.DEBUG) {
							System.out.println("Tactic " + string
									+ " is not registered.");
						}
					} else if (!tacticContainer.getTacticContainerRegistry()
							.isDeclared(string)) {
						if (UIUtils.DEBUG) {
							System.out.println("Tactic " + string
									+ " is not declared for using within this tactic container.");
						}
					}
					else {
						result.add(string);
					}
				}
				return result;
			}

			@Override
			protected String getLabel(Object object) {
				return SequentProver.getTacticRegistry().getTacticName(
						(String) object);
			}

			@Override
			protected String[] getDeclaredObjects() {
				return tacticContainer.getTacticContainerRegistry()
						.getTacticIDs();
			}

		};
		addField(tacticsEditor);

	}

	@Override
	public void init(IWorkbench workbench) {
		// Do nothing
	}

	@Override
	public boolean performOk() {
		setTactics();
		return super.performOk();
	}

	private void setTactics() {
		ArrayList<Object> objects = tacticsEditor.getSelectedObjects();
		String[] tacticIDs = new String[objects.size()];
		int i = 0;
		for (Object object : objects) {
			tacticIDs[i] = (String) object;
			++i;
		}
		
		tacticContainer.setTactics(tacticIDs);
	}

}