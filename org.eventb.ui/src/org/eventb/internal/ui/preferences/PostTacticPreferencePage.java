package org.eventb.internal.ui.preferences;

import java.util.ArrayList;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IProvingMode;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 */
public class PostTacticPreferencePage
	extends PreferencePage
	implements
		IWorkbenchPreferencePage {

	TwoListSelectionEditor tacticsEditor;

	public PostTacticPreferencePage() {
		super();
		setPreferenceStore(EventBUIPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for the Post Tactic apply after every tactic application");
	}

	@Override
	public void createFieldEditors() {
		addField(
				new BooleanFieldEditor(
					PreferenceConstants.P_PROVING_MODE,
					"Enable &post-tactic for proving",
					getFieldEditorParent()));

		tacticsEditor = new TwoListSelectionEditor(
				PreferenceConstants.P_POSTTACTIC_DETAILS,
				"&Tactics are run as post-tactics", getFieldEditorParent()) {

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
					} else if (!EventBPlugin.getDefault()
							.getPostTacticRegistry().isDeclared(string)) {
						if (UIUtils.DEBUG) {
							System.out.println("Tactic " + string
									+ " is not declared as a post tactic.");
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

		};
		addField(tacticsEditor);

	}

	@Override
	public void init(IWorkbench workbench) {
		// Do nothing
	}

	@Override
	public boolean performOk() {
		setProvingMode();
		return super.performOk();
	}

	private void setProvingMode() {
		ArrayList<Object> objects = tacticsEditor.getSelectedObjects();
		String[] tacticIDs = new String[objects.size()];
		int i = 0;
		for (Object object : objects) {
			tacticIDs[i] = (String) object;
			++i;
		}
		IProvingMode provingMode = EventBPlugin.getDefault()
				.getUserSupportManager().getProvingMode();
		provingMode.setPostTactics(tacticIDs);
	}

}