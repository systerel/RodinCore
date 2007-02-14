package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;

public class VariableSectionComposite extends DefaultSectionComposite {

	@Override
	public void createContents() throws RodinDBException {
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = 1 + 3 * sectionRegistry.getNumColumns(IVariable.ELEMENT_TYPE);

		map = new HashMap<IRodinElement, Collection<IEditComposite>>();

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		fComp.setLayout(gridLayout);
		
		Label label = fToolkit.createLabel(fComp, "VARIABLES");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = numColumns;
		label.setLayoutData(gd);

//		Composite comp = fToolkit.createComposite(fComp);
//		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		gridLayout = new GridLayout();
//		gridLayout.numColumns = numColumns + 1;
//		comp.setLayout(gridLayout);
//		
//		Composite tmpComp = fToolkit.createComposite(comp);
//		gd = new GridData();
//		gd.heightHint = 0;
//		gd.widthHint = 0;
//		tmpComp.setLayoutData(gd);
//		String[] names = sectionRegistry.getColumnNames(IVariable.ELEMENT_TYPE);
//		for (String name : names) {
//			label = fToolkit.createLabel(comp, name);
//			gd = new GridData(SWT.FILL, SWT.FILL, false, false);
//			gd.horizontalSpan = 3;
//			label.setLayoutData(gd);
//		}
//
		IVariable[] variables;
		variables = ((IMachineFile) fInput).getVariables();

		for (IVariable variable : variables) {
			Composite comp = fToolkit.createComposite(fComp);
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			gridLayout = new GridLayout();
			gridLayout.numColumns = numColumns + 1;
			comp.setLayout(gridLayout);
			createButtons(fInput, comp, variable, IVariable.ELEMENT_TYPE);
			
			map = sectionRegistry.createColumns(fForm, fToolkit, comp,
					variable, map);
			fToolkit.paintBordersFor(comp);
		}
		
		Composite comp = fToolkit.createComposite(fComp);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns + 1;
		comp.setLayout(gridLayout);
		createButtons(fInput, comp, null, IVariable.ELEMENT_TYPE); // The last null element
		
		fToolkit.paintBordersFor(fComp);
		fComp.getParent().pack();
		fForm.reflow(true);
	}

	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		changedElements = new HashSet<IRodinElement>();
		refresh = false;
		processDelta(delta, IVariable.ELEMENT_TYPE);
		postRefresh();
	}

}