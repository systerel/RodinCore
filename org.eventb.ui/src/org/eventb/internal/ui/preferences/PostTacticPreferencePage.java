package org.eventb.internal.ui.preferences;

import java.util.ArrayList;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IProvingMode;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PostTacticPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	TwoListSelectionEditor tacticsEditor;
	
	public PostTacticPreferencePage() {
		super();
		setPreferenceStore(EventBUIPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for the Post Tactic apply after every tactic application");
	}

	@Override
	public void createFieldEditors() {
		tacticsEditor = new TwoListSelectionEditor(PreferenceConstants.P_PROVINGMODE,
						"&Tactics are run as post-tactics",
						getFieldEditorParent()) {
		
							@Override
							protected String createList(ArrayList<Object> objects) {
								return ProverUIUtils.toCommaSeparatedList(objects);
							}
		
							@Override
							protected ArrayList<Object> parseString(String stringList) {
								ArrayList<String> strings = ProverUIUtils.parseString(stringList);
								ArrayList<Object> result = new ArrayList<Object>(strings.size());
								for (String string : strings) {
									result.add(string);
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
	protected void performApply() {
		setProvingMode();
		super.performApply();
	}

	private void setProvingMode() {
		ArrayList<Object> objects = tacticsEditor.getSelectedObjects();
		ArrayList<String> tacticIDs = new ArrayList<String>(objects.size());
		for (Object object : objects) {
			tacticIDs.add((String) object);
		}
		IProvingMode provingMode = EventBPlugin.getDefault().getUserSupportManager().getProvingMode();
		provingMode.setPostTactics(tacticIDs);
	}

}