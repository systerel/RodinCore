/*******************************************************************************
 * Copyright (c) 2008, 2015 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation;

import static fr.systerel.editor.EditorPlugin.PLUGIN_ID;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.CONTENT_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.DEFAULT_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.HANDLE_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IDENTIFIER_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.LABEL_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.PRESENTATION_DEBUG_BG;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.rodinp.core.IAttributeType;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * A customization of the SourceViewerConfiguration to provide syntax coloring.
 */
public class RodinConfiguration extends SourceViewerConfiguration implements
		IPropertyChangeListener {

	/**
	 * The formula foreground color definition used across both Event-B editor
	 * and the Rodin Editor.
	 */
	private static final String EVENTB_TEXT_COLOR = "org.eventb.ui.colorDefinition.text.foreground";

	/**
	 * The comment foreground color definition used across both Event-B editor
	 * and the Rodin Editor.
	 */
	private static final String EVENTB_COMMENT_COLOR = "org.eventb.ui.colorDefinition.comment.foreground";

	/**
	 * Debug option to activate the visualization of content types using colored
	 * backgrounds
	 */
	public static boolean DEBUG = false;

	private static final String IMPLICIT_FREFIX = "__implicit_";

	public static final String LEFT_PRESENTATION_TYPE = "__left_presentation";
	public static final String PRESENTATION_TYPE = "__presentation";
	public static final String IMPLICIT_PRESENTATION_TYPE = "__implicit_presentation";
	public static final String KEYWORD_TYPE = "__keyword";
	public static final String SECTION_TYPE = "__section";
	public static final String COMMENT_HEADER_TYPE = "__comment_header";
	public static final String HANDLE_TYPE = "__handle";
	public static final String IMPLICIT_HANDLE_TYPE = "__implicit_handle";
	public static final String LABEL_TYPE = "__label";
	public static final String IMPLICIT_LABEL_TYPE = "__implicit_label";
	public static final String BOLD_LABEL_TYPE = "__bold_label";
	public static final String IMPLICIT_BOLD_LABEL_TYPE = "__implicit_bold_label";
	public static final String IDENTIFIER_TYPE = "__identifier";
	public static final String IMPLICIT_IDENTIFIER_TYPE = "__implicit_identifier";
	public static final String COMMENT_TYPE = "__comment";
	public static final String IMPLICIT_COMMENT_TYPE = "__implicit_comment";
	public static final String FORMULA_TYPE = "__formula";
	public static final String IMPLICIT_FORMULA_TYPE = "__implicit_formula";
	public static final String ATTRIBUTE_TYPE = "__attribute";
	public static final String IMPLICIT_ATTRIBUTE_TYPE = "__implicit_attribute";

	private static final List<String> contentNames = new ArrayList<String>();

	private static final Map<String, IAttributeType> attributeNames = new HashMap<String, IAttributeType>();

	private static final Map<String, RGB> debugBackgrounds = new HashMap<String, RGB>();

	static {
		contentNames.add(LEFT_PRESENTATION_TYPE);
		contentNames.add(PRESENTATION_TYPE);
		contentNames.add(KEYWORD_TYPE);
		contentNames.add(SECTION_TYPE);
		contentNames.add(HANDLE_TYPE);
		contentNames.add(COMMENT_HEADER_TYPE);

		attributeNames.put(IDENTIFIER_TYPE, IDENTIFIER_ATTRIBUTE);
		attributeNames.put(LABEL_TYPE, LABEL_ATTRIBUTE);
		attributeNames.put(COMMENT_TYPE, COMMENT_ATTRIBUTE);
		attributeNames.put(BOLD_LABEL_TYPE, LABEL_ATTRIBUTE);

		debugBackgrounds.put(LEFT_PRESENTATION_TYPE, PRESENTATION_DEBUG_BG);
		debugBackgrounds.put(PRESENTATION_TYPE, PRESENTATION_DEBUG_BG);
		debugBackgrounds.put(HANDLE_TYPE, HANDLE_DEBUG_BG);
		debugBackgrounds.put(IMPLICIT_HANDLE_TYPE, HANDLE_DEBUG_BG);
		debugBackgrounds.put(IDENTIFIER_TYPE, IDENTIFIER_DEBUG_BG);
		debugBackgrounds.put(IMPLICIT_IDENTIFIER_TYPE, IDENTIFIER_DEBUG_BG);
		debugBackgrounds.put(LABEL_TYPE, LABEL_DEBUG_BG);
		debugBackgrounds.put(IMPLICIT_LABEL_TYPE, LABEL_DEBUG_BG);
		debugBackgrounds.put(BOLD_LABEL_TYPE, LABEL_DEBUG_BG);
		debugBackgrounds.put(IMPLICIT_BOLD_LABEL_TYPE, LABEL_DEBUG_BG);
		debugBackgrounds.put(FORMULA_TYPE, CONTENT_DEBUG_BG);
		debugBackgrounds.put(IMPLICIT_FORMULA_TYPE, CONTENT_DEBUG_BG);
	}

	public static final ContentType FORMULA_CONTENT_TYPE = new AttributeContentType(
			FORMULA_TYPE, null);

	public static final ContentType IMPLICIT_FORMULA_CONTENT_TYPE = new AttributeContentType(
			IMPLICIT_FORMULA_TYPE, null);

	public static final ContentType ATTRIBUTE_CONTENT_TYPE = new AttributeContentType(
			ATTRIBUTE_TYPE, null);

	public static final ContentType IMPLICIT_ATTRIBUTE_CONTENT_TYPE = new AttributeContentType(
			IMPLICIT_ATTRIBUTE_TYPE, null);

	private static final ContentType[] defaultTypes = new ContentType[] {
			FORMULA_CONTENT_TYPE, IMPLICIT_FORMULA_CONTENT_TYPE,
			ATTRIBUTE_CONTENT_TYPE, IMPLICIT_ATTRIBUTE_CONTENT_TYPE };

	private RodinEditor editor;

	private IAnnotationHover annotationHover;

	private ITextHover textHover;

	public static Color getForegroundColor(String name) {
		final ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		Color color;
		final String base = name.replaceFirst(IMPLICIT_FREFIX, "__");
		if (COMMENT_TYPE.equals(base)) {
			color = colorRegistry.get(EVENTB_COMMENT_COLOR);
		} else if (FORMULA_TYPE.equals(base)) {
			color = colorRegistry.get(EVENTB_TEXT_COLOR);
		} else {
			final String colorId = base.replaceFirst("__", PLUGIN_ID
					+ ".colorDefinition.")
					+ ".foreground";
			color = colorRegistry.get(colorId);
		}
		if (color == null) {
			color = colorRegistry.get(EVENTB_TEXT_COLOR);
		}
		if (name.startsWith(IMPLICIT_FREFIX)) {
			return ColorManager.getDefault().getImplicitColor(color);
		}
		return color;
	}

	public static Color getBackgroundColor(String name) {
		RGB rgb = IRodinColorConstant.BACKGROUND;
		if (DEBUG) {
			rgb = debugBackgrounds.get(name);
			if (rgb == null) {
				rgb = DEFAULT_DEBUG_BG;
			}
		}
		return ColorManager.getDefault().getColor(rgb);
	}

	/**
	 * TODO warning this method is unsafe and shall not be used
	 * <p>
	 * DO NOT USE THIS METHOD
	 * </p>
	 */
	public static ContentType getContentType(String name) {
		if (name.startsWith(IMPLICIT_FREFIX)) {
			final String base = name.replace(IMPLICIT_FREFIX, "__");
			if (contentNames.contains(base)) {
				return new ContentType(name);
			} else if (attributeNames.containsKey(base)) {
				return new AttributeContentType(name, attributeNames.get(base));
			}
		}
		if (contentNames.contains(name)) {
			return new ContentType(name);
		}
		if (attributeNames.keySet().contains(name)) {
			return new AttributeContentType(name, attributeNames.get(name));
		}
		if (name.equals(FORMULA_TYPE))
			return FORMULA_CONTENT_TYPE;
		if (name.equals(IMPLICIT_FORMULA_TYPE))
			return IMPLICIT_FORMULA_CONTENT_TYPE;
		if (name.equals(IMPLICIT_ATTRIBUTE_TYPE)) {
			return IMPLICIT_ATTRIBUTE_CONTENT_TYPE;
		}
		return ATTRIBUTE_CONTENT_TYPE;
	}

	public static ContentType getAttributeContentType(IAttributeType type,
			boolean implicit) {
		final String id = (implicit) ? IMPLICIT_ATTRIBUTE_TYPE : ATTRIBUTE_TYPE;
		return new AttributeContentType(id, type);
	}

	public static ContentType getFormulaContentType(IAttributeType attrType,
			boolean implicit) {
		final String id = (implicit) ? IMPLICIT_FORMULA_TYPE : FORMULA_TYPE;
		return new AttributeContentType(id, attrType);
	}

	public RodinConfiguration(RodinEditor editor) {
		this.editor = editor;
		JFaceResources.getColorRegistry().addListener(this);
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return getContentTypeNames();
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		final PresentationReconciler reconciler = new PresentationReconciler();
		for (ContentType contentType : getContentTypes()) {
			final RodinDamagerRepairer rdr = new RodinDamagerRepairer(editor,
					createTextAttribute(contentType));
			reconciler.setDamager(rdr, contentType.getName());
			reconciler.setRepairer(rdr, contentType.getName());
		}
		return reconciler;
	}

	private static List<ContentType> getContentTypes() {
		final List<ContentType> result = new ArrayList<ContentType>();
		for (String name : contentNames) {
			result.add(getContentType(name));
			result.add(getContentType(IMPLICIT_FREFIX
					+ name.replaceFirst("__", "")));
		}
		for (String name : attributeNames.keySet()) {
			result.add(getContentType(name));
			result.add(getContentType(IMPLICIT_FREFIX
					+ name.replaceFirst("__", "")));
		}
		for (ContentType type : defaultTypes) {
			result.add(type);
		}
		return result;
	}

	private static TextAttribute createTextAttribute(ContentType type) {
		return new TextAttribute(type.getColor(),
				getBackgroundColor(type.getName()), type.getStyles());
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		if (annotationHover == null)
			annotationHover = new RodinProblemAnnotationHover();
		return annotationHover;
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		if (textHover == null)
			textHover = new RodinProblemTextHover(sourceViewer);
		return textHover;
	}

	public static String[] getContentTypeNames() {
		final Set<String> result = new HashSet<String>();
		result.addAll(contentNames);
		result.addAll(attributeNames.keySet());
		for (ContentType t : defaultTypes) {
			result.add(t.getName());
		}
		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * Deactivates the undo manager from the Rodin Editor
	 */
	@Override
	public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() instanceof ColorRegistry) {
			editor.getViewer().configure(this);
		}
	}

	public static class ContentType {

		private final String name;
		private final boolean isEditable;
		private final boolean isImplicit;
		private final Color color;

		public ContentType(String contentName) {
			this(contentName, false);
		}

		public ContentType(String contentName, boolean isEditable) {
			this.name = contentName;
			this.isEditable = isEditable;
			this.isImplicit = contentName.startsWith(IMPLICIT_FREFIX);
			this.color = getForegroundColor(contentName);
		}

		public String getName() {
			return name;
		}

		public boolean isEditable() {
			return isEditable;
		}

		public Color getColor() {
			return color;
		}

		public boolean isImplicit() {
			return isImplicit;
		}

		public boolean isKindOfEditable() {
			return false;
		}

		public int getStyles() {
			int styles = SWT.NONE;
			if (name.toUpperCase().contains("BOLD")) {
				styles = styles | SWT.BOLD;
			}
			if (name.toUpperCase().contains("ITALIC")) {
				styles = styles | SWT.ITALIC;
			}
			return styles;
		}

	}

	public static class AttributeContentType extends ContentType {

		// note: the attribute type may be null when unknown
		private final IAttributeType attributeType;

		public AttributeContentType(String contentName,
				IAttributeType attributeType) {
			super(contentName, !contentName.startsWith(IMPLICIT_FREFIX));
			this.attributeType = attributeType;
		}

		/**
		 * Returns the attribute type, or <code>null</code> when it is unknown.
		 * 
		 * @return an attribute type, or <code>null</code>
		 */
		public IAttributeType getAttributeType() {
			return attributeType;
		}

		@Override
		public boolean isKindOfEditable() {
			return true;
		}

	}

}
