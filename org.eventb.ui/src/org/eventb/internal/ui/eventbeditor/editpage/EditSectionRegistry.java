package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

public class EditSectionRegistry {

	public static EditSectionRegistry instance = null;

	private EditSectionRegistry() {
		// Singleton to hide contructor
	}

	public static EditSectionRegistry getDefault() {
		if (instance == null)
			instance = new EditSectionRegistry();
		return instance;
	}

	// Map from editor to list of sections
	private Map<String, SectionsInfo> sectionRegistry = null;

	private static final String EDITSECTIONS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editSections";

	class SectionsInfo {
		List<SectionInfo> sections;

		public SectionsInfo() {
			sections = new ArrayList<SectionInfo>();
		}

		public ISectionComposite[] createSections(EditPage page,
				FormToolkit toolkit, ScrolledForm form, Composite parent) {
			Collection<ISectionComposite> sectionComps = new ArrayList<ISectionComposite>();
			for (SectionInfo section : sections) {
				ISectionComposite sectionComp = section.createSection(page,
						toolkit, form, parent);
				if (sectionComp != null)
					sectionComps.add(sectionComp);
			}
			return sectionComps.toArray(new ISectionComposite[sectionComps
					.size()]);
		}

		public void addSection(SectionInfo info) {
			sections.add(info);
		}

		public void sortSections() {
			boolean sorted = false;
			int size = sections.size();
			while (!sorted) {
				sorted = true;
				for (int i = 0; i < size - 1; i++) {
					SectionInfo curr = sections.get(i);
					SectionInfo next = sections.get(i + 1);
					if (curr.getPriority() > next.getPriority()) {
						// Swap element
						sections.set(i, next);
						sections.set(i + 1, curr);
						sorted = false;
					}
				}
			}
		}
	}

	private class SectionInfo {
		IConfigurationElement config;

		private final String id;

		private int priority;

		private static final int DEFAULT_PRIORITY = 10000;

		public SectionInfo(IConfigurationElement config) {
			this.config = config;
			// TODO check that id is present.
			this.id = config.getAttribute("id");
			priority = readPriority();
		}

		private int readPriority() {
			String priorityValue = config.getAttribute("priority");
			if (priorityValue == null) {
				UIUtils.log(null, "Missing priority attribute (using default),"
						+ " for editor page extension " + id);
				return DEFAULT_PRIORITY;
			}
			try {
				return Integer.parseInt(priorityValue);
			} catch (NumberFormatException e) {
				UIUtils.log(e, "Illegal priority " + priorityValue
						+ ", using default instead,"
						+ " for editor page extension " + id);
				return DEFAULT_PRIORITY;
			}
		}

		public int getPriority() {
			return this.priority;
		}

		public ISectionComposite createSection(EditPage page,
				FormToolkit toolkit, ScrolledForm form, Composite parent) {
			try {
				ISectionComposite sectionComp;
				sectionComp = (ISectionComposite) config
						.createExecutableExtension("class");
				return sectionComp.create(page, toolkit, form, parent);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	private synchronized void loadSectionRegistry() {
		if (sectionRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		sectionRegistry = new HashMap<String, SectionsInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(EDITSECTIONS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			if (!configuration.getName().equals("section"))
				continue;
			SectionInfo info = new SectionInfo(configuration);
			IConfigurationElement[] targets = configuration
					.getChildren("target");
			for (IConfigurationElement target : targets) {
				String targetID = target.getAttribute("id");
				addSection(targetID, info);
			}
		}

		sortSections();
	}

	private void sortSections() {
		assert sectionRegistry != null;

		for (String targetID : sectionRegistry.keySet()) {
			SectionsInfo infos = sectionRegistry.get(targetID);
			infos.sortSections();
		}
	}

	// private void addColumn(String sId, IConfigurationElement info) {
	// for (IConfigurationElement sInfo : sectionRegistry) {
	// if (sInfo.getAttribute("id").equals(sId)) {
	// Collection<IConfigurationElement> cInfos = map.get(sInfo);
	// if (cInfos == null) {
	// cInfos = new ArrayList<IConfigurationElement>();
	// map.put(sInfo, cInfos);
	// }
	// cInfos.add(info);
	//
	// String message = "Registered configuration for column "
	// + info.getAttribute("id") + " with section " + sId;
	// if (UIUtils.DEBUG) {
	// System.out.println(message);
	// }
	// return;
	// }
	// }
	// String message = "Configuration for section " + sId
	// + " cannot be found, ignore the column configuration "
	// + info.getAttribute("id");
	// if (UIUtils.DEBUG) {
	// System.out.println(message);
	// }
	//
	// }

	public String getSectionName(IConfigurationElement section) {
		return section.getAttribute("id");
	}

	public ISectionComposite[] createSections(EditPage page,
			FormToolkit toolkit, ScrolledForm form, Composite parent) {
		if (sectionRegistry == null)
			loadSectionRegistry();

		IEventBEditor editor = (IEventBEditor) page.getEditor();
		SectionsInfo info = sectionRegistry.get(editor.getEditorId());
		if (info == null) {
			return new ISectionComposite[0];
		}
		return info.createSections(page, toolkit, form, parent);

	}

	private synchronized void addSection(String targetID, SectionInfo info) {
		assert sectionRegistry != null;

		SectionsInfo infos = sectionRegistry.get(targetID);
		if (infos == null) {
			infos = new SectionsInfo();
			sectionRegistry.put(targetID, infos);
		}
		infos.addSection(info);
	}

	private Map<IElementType, ColumnsInfo> columnRegistry;

	class ColumnsInfo {
		List<ColumnInfo> columns;

		public ColumnsInfo() {
			columns = new ArrayList<ColumnInfo>();
		}

		public int getNumColumns() {
			return columns.size();
		}

		public void addColumn(ColumnInfo info) {
			columns.add(info);
		}

		public IEditComposite[] createColumns(ScrolledForm form,
				FormToolkit toolkit, Composite parent, IRodinElement element) {
			IEditComposite[] result = new IEditComposite[columns.size()];
			for (int i = 0; i < columns.size(); ++i) {
				result[i] = columns.get(i).createColumn(toolkit, form, parent,
						element);
			}
			return result;
		}

		public String[] getColumnNames() {
			List<String> names = new ArrayList<String>();
			for (ColumnInfo column : columns) {
				String name = column.getColumnName();
				names.add(name);
			}
			return names.toArray(new String[names.size()]);
		}
	}

	private class ColumnInfo {
		private IConfigurationElement config;

		public ColumnInfo(IConfigurationElement config) {
			this.config = config;
		}

		public String getColumnName() {
			return config.getAttribute("name");
		}

		public IEditComposite createColumn(FormToolkit toolkit,
				ScrolledForm form, Composite parent, IRodinElement element) {
			try {
				IEditComposite editComposite;
				String prefix = config.getAttribute("prefix");
				if (prefix == null)
					prefix = "";
				Label label = toolkit.createLabel(parent, " " + prefix + " ");
				GridData gridData = new GridData();
				gridData.verticalAlignment = SWT.TOP;
				label.setLayoutData(gridData);
				// label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));

				editComposite = (IEditComposite) config
						.createExecutableExtension("class");
				editComposite.setForm(form);
				editComposite.setElement(element);
				editComposite.createComposite(toolkit, parent);
				editComposite.setFillHorizontal(config.getAttribute(
						"horizontalExpand").equalsIgnoreCase("true"));
				String postfix = config.getAttribute("postfix");
				if (postfix == null)
					postfix = "";
				label = toolkit.createLabel(parent, " " + postfix + " ");
				gridData = new GridData();
				gridData.verticalAlignment = SWT.TOP;
				label.setLayoutData(gridData);
				// label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				return editComposite;
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private synchronized void loadColumnRegistry() {
		if (columnRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		columnRegistry = new HashMap<IElementType, ColumnsInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(EDITSECTIONS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			if (!configuration.getName().equals("column"))
				continue;
			ColumnInfo info = new ColumnInfo(configuration);
			IConfigurationElement[] types = configuration.getChildren("type");
			for (IConfigurationElement type : types) {
				String id = type.getAttribute("id");
				IElementType elementType;
				try {
					elementType = RodinCore.getElementType(id);
				} catch (IllegalArgumentException e) {
					String message = "Illegal element type " + id
							+ ", ignore this configuration";
					UIUtils.log(e, message);
					if (UIUtils.DEBUG) {
						System.out.println(message);
					}
					continue;
				}
				addColumn(elementType, info);
			}
		}
	}

	private void addColumn(IElementType elementType, ColumnInfo info) {
		assert columnRegistry != null;

		ColumnsInfo infos = columnRegistry.get(elementType);
		if (infos == null) {
			infos = new ColumnsInfo();
			columnRegistry.put(elementType, infos);
		}
		infos.addColumn(info);
	}

	public synchronized int getNumColumns(IElementType type) {
		if (columnRegistry == null)
			loadColumnRegistry();

		ColumnsInfo info = columnRegistry.get(type);
		if (info == null) {
			return 0;
		}

		return info.getNumColumns();
	}

	public synchronized IEditComposite[] createColumns(ScrolledForm form,
			FormToolkit toolkit, Composite parent, IRodinElement element) {
		if (columnRegistry == null)
			loadColumnRegistry();

		ColumnsInfo info = columnRegistry.get(element.getElementType());
		if (info == null) {
			return new IEditComposite[0];
		}

		return info.createColumns(form, toolkit, parent, element);
	}

	public synchronized String[] getColumnNames(IElementType type) {
		if (columnRegistry == null)
			loadColumnRegistry();

		ColumnsInfo info = columnRegistry.get(type);
		if (info == null) {
			return new String[0];
		}

		return info.getColumnNames();
	}

	private Map<IElementType, ActionsInfo> actionRegistry;

	class ActionsInfo {
		Map<String, ActionInfo> actions;

		public ActionsInfo() {
			actions = new LinkedHashMap<String, ActionInfo>();
		}

		public void addAction(String id, ActionInfo info) {
			actions.put(id, info);
		}

		public String[] getActions() {
			Set<String> actionIDs = actions.keySet();
			return actionIDs.toArray(new String[actionIDs.size()]);
		}

		public void run(String actionID, IEventBEditor editor,
				IInternalParent parent, IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			ActionInfo info = actions.get(actionID);
			info.run(editor, parent, element, type);
		}

		public String getToolTip(String actionID) {
			ActionInfo info = actions.get(actionID);
			return info.getToolTip();
		}

		public String getName(String actionID) {
			ActionInfo info = actions.get(actionID);
			return info.getName();
		}

		public boolean isApplicable(String actionID, IInternalParent parent,
				IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			ActionInfo info = actions.get(actionID);
			return info.isApplicable(parent, element, type);
		}

	}

	private synchronized void loadActionRegistry() {
		if (actionRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		actionRegistry = new HashMap<IElementType, ActionsInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(EDITSECTIONS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			if (!configuration.getName().equals("action"))
				continue;
			String actionID = configuration.getAttribute("id");
			ActionInfo info = new ActionInfo(configuration);
			IConfigurationElement[] types = configuration.getChildren("type");
			for (IConfigurationElement type : types) {
				String id = type.getAttribute("id");
				IElementType elementType;
				try {
					elementType = RodinCore.getElementType(id);
				} catch (IllegalArgumentException e) {
					String message = "Illegal element type " + id
							+ ", ignore this configuration";
					UIUtils.log(e, message);
					if (UIUtils.DEBUG) {
						System.out.println(message);
					}
					continue;
				}
				addAction(elementType, actionID, info);
			}
		}
	}

	private class ActionInfo {
		private IConfigurationElement config;

		private IEditAction editAction = null;

		public ActionInfo(IConfigurationElement config) {
			this.config = config;
		}

		public boolean isApplicable(IInternalParent parent,
				IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			if (editAction == null)
				editAction = (IEditAction) config
						.createExecutableExtension("class");
			return editAction.isApplicable(parent, element, type);
		}

		public String getName() {
			return config.getAttribute("name");
		}

		public String getToolTip() {
			return config.getAttribute("tooltip");
		}

		public void run(IEventBEditor editor, IInternalParent parent,
				IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			if (editAction == null)
				editAction = (IEditAction) config
						.createExecutableExtension("class");
			editAction.run(editor, parent, element, type);
		}

		public String getId() {
			return config.getAttribute("id");
		}

	}

	public synchronized String[] getActions(IElementType type) {
		if (actionRegistry == null)
			loadActionRegistry();

		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return new String[0];
		}

		return info.getActions();
	}

	private synchronized void addAction(IElementType type, String id,
			ActionInfo info) {
		assert actionRegistry != null;

		ActionsInfo infos = actionRegistry.get(type);
		if (infos == null) {
			infos = new ActionsInfo();
			actionRegistry.put(type, infos);
		}
		infos.addAction(id, info);
	}

	public void run(String actionID, IEventBEditor editor,
			IInternalParent parent, IInternalElement element,
			IInternalElementType<IInternalElement> type) throws CoreException {
		if (actionRegistry == null)
			loadActionRegistry();

		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return;
		}

		info.run(actionID, editor, parent, element, type);
	}

	public synchronized String getToolTip(String actionID, IElementType type) {
		if (actionRegistry == null)
			loadActionRegistry();
		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return null;
		}

		return info.getToolTip(actionID);
	}

	public synchronized String getName(String actionID, IElementType type) {
		if (actionRegistry == null)
			loadActionRegistry();
		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return null;
		}

		return info.getName(actionID);
	}

	public synchronized boolean isApplicable(String actionID,
			IInternalParent parent, IInternalElement element,
			IInternalElementType<IInternalElement> type) throws CoreException {
		if (actionRegistry == null)
			loadActionRegistry();
		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return false;
		}

		return info.isApplicable(actionID, parent, element, type);
	}

}
