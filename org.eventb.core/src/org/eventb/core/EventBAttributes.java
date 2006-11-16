/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

/**
 * This class lists all attribute names used by the Event-B core plugin.
 * 
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 */
public final class EventBAttributes {

	public static IAttributeType.String LABEL_ATTRIBUTE = 
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".label");

	public static IAttributeType.Handle SOURCE_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".source");

	public static IAttributeType.String COMMENT_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".comment");
	
	public static IAttributeType.Boolean INHERITED_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".inherited");
	
	public static IAttributeType.Boolean FORBIDDEN_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".forbidden");
	
	public static IAttributeType.Boolean PRESERVED_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".preserved");
	
	public static IAttributeType.Integer CONVERGENCE_ATTRIBUTE =
		RodinCore.getIntegerAttrType(EventBPlugin.PLUGIN_ID + ".convergence");
	
	public static IAttributeType.String PREDICATE_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".predicate");
	
	public static IAttributeType.String EXPRESSION_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".expression");
	
	public static IAttributeType.String ASSIGNMENT_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".assignment");
	
	public static IAttributeType.String TYPE_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".type");
	
	public static IAttributeType.String TARGET_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".target");

	public static IAttributeType.String IDENTIFIER_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".identifier");

	public static IAttributeType.Handle SCTARGET_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".scTarget");
	
	public static IAttributeType.String PODESC_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".poDesc");
	
	public static IAttributeType.String POROLE_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".poRole");
	
	public static IAttributeType.String POSTAMP_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".poStamp");
	
	public static IAttributeType.Handle PARENT_SET_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".parentSet");

	// Attributes related to the PR and PS files
	public static IAttributeType.Integer CONFIDENCE_ATTRIBUTE =
		RodinCore.getIntegerAttrType(EventBPlugin.PLUGIN_ID + ".confidence");

	public static IAttributeType.Boolean PROOF_VALIDITY_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".proofValid");

	public static IAttributeType.Boolean AUTO_PROOF_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".autoProof");
	
	private EventBAttributes() {
		// Non-instantiable class
	}

}
