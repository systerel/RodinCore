package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.IInvariant;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;

public class InvariantSectionComposite extends DefaultSectionComposite {

	@Override
	public void createContents() throws RodinDBException {
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = 1 + 3 * sectionRegistry
				.getNumColumns(IInvariant.ELEMENT_TYPE);

		map = new HashMap<IRodinElement, Collection<IEditComposite>>();

		GridLayout gridLayout = new GridLayout();
		// gridLayout.numColumns = numColumns;
		fComp.setLayout(gridLayout);
		Label label = fToolkit.createLabel(fComp, "INVARIANTS");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		label.setLayoutData(gd);

//		Composite tmpComp = fToolkit.createComposite(fComp);
//		gd = new GridData();
//		gd.heightHint = 0;
//		gd.widthHint = 0;
//		tmpComp.setLayoutData(gd);
//		String[] names = sectionRegistry
//				.getColumnNames(IInvariant.ELEMENT_TYPE);
//		for (String name : names) {
//			label = fToolkit.createLabel(fComp, name);
//			gd = new GridData(SWT.FILL, SWT.FILL, false, false);
//			label.setLayoutData(gd);
//		}

		IInvariant[] invariants;
		invariants = fInput.getChildrenOfType(IInvariant.ELEMENT_TYPE);

		for (IInvariant invariant : invariants) {
			Composite comp = fToolkit.createComposite(fComp);
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			gridLayout = new GridLayout();
			gridLayout.numColumns = numColumns + 1;
			comp.setLayout(gridLayout);
			createButtons(fInput, comp, invariant, IInvariant.ELEMENT_TYPE);

			map = sectionRegistry.createColumns(fForm, fToolkit, comp,
					invariant, map);
			fToolkit.paintBordersFor(comp);
		}

		Composite comp = fToolkit.createComposite(fComp);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns + 1;
		comp.setLayout(gridLayout);
		createButtons(fInput, comp, null, IInvariant.ELEMENT_TYPE); // The last
																	// null
																	// element

		fToolkit.paintBordersFor(fComp);
		fComp.getParent().pack();
		fForm.reflow(true);
	}

	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		changedElements = new HashSet<IRodinElement>();
		refresh = false;
		processDelta(delta, IInvariant.ELEMENT_TYPE);
		postRefresh();
	}

}
