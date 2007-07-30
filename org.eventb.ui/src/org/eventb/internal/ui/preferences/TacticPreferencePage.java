package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 */
public abstract class TacticPreferencePage
	extends PreferencePage
	implements
		IWorkbenchPreferencePage {

	TwoListSelectionEditor tacticsEditor;

	String enableFieldName;
	
	String enableFieldDescription;

	String tacticsFieldName;
	
	String tacticsFieldDescription;

	IAutoTacticPreference tacticPreference = null;
	
	public TacticPreferencePage(String description,
			String enableFieldName, String enableFieldDescription,
			String tacticsFieldName, String tacticsFieldDescription) {
		super();
		this.enableFieldName = enableFieldName;
		this.enableFieldDescription = enableFieldDescription;
		this.tacticsFieldName = tacticsFieldName;
		this.tacticsFieldDescription = tacticsFieldDescription;
		setTacticPreference();
		setPreferenceStore(EventBUIPlugin.getDefault().getPreferenceStore());
		setDescription(description);
	}

	protected abstract void setTacticPreference();

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
				ArrayList<Object> tacticIDs = new ArrayList<Object>();
				for (Object obj : objects) {
					assert obj instanceof ITacticDescriptor;
					tacticIDs.add(((ITacticDescriptor) obj).getTacticID());
				}
				return ProverUIUtils.toCommaSeparatedList(tacticIDs);
			}

			@Override
			protected ArrayList<Object> parseString(String stringList) {
				String [] tacticIDs = ProverUIUtils
						.parseString(stringList);
				ArrayList<Object> result = new ArrayList<Object>();
				for (String tacticID : tacticIDs) {
					IAutoTacticRegistry tacticRegistry = SequentProver.getAutoTacticRegistry();
					if (!tacticRegistry.isRegistered(tacticID)) {
						if (UIUtils.DEBUG) {
							System.out.println("Tactic " + tacticID
									+ " is not registered.");
						}
						continue;
					}
					
					ITacticDescriptor tacticDescriptor = tacticRegistry
							.getTacticDescriptor(tacticID);
					if (!tacticPreference.isDeclared(tacticDescriptor)) {
						if (UIUtils.DEBUG) {
							System.out
									.println("Tactic "
											+ tacticID
											+ " is not declared for using within this tactic preference.");
						}
					}
					else {
						result.add(tacticDescriptor);
					}
				}
				return result;
			}

			@Override
			protected String getLabel(Object object) {
				return ((ITacticDescriptor) object).getTacticName();
			}

			@Override
			protected Collection<Object> getDeclaredObjects() {
				Collection<ITacticDescriptor> declaredDescriptors = tacticPreference
						.getDeclaredDescriptors();
				Collection<Object> result = new ArrayList<Object>(
						declaredDescriptors.size());
				result.addAll(declaredDescriptors);
				return result;
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
		List<ITacticDescriptor> tacticDescs = new ArrayList<ITacticDescriptor>(
				objects.size());
		for (Object object : objects) {
			tacticDescs.add((ITacticDescriptor) object);
		}
		
		tacticPreference.setSelectedDescriptors(tacticDescs);
	}

}