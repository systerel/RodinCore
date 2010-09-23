/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - introduction of generated elements
 *     Systerel - added unselected hyps attribute
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

/**
 * This class lists all attribute names used by the Event-B core plugin.
 * 
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 * @since 1.0
 */
public final class EventBAttributes {

	public static IAttributeType.String CONFIGURATION_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".configuration");

	public static IAttributeType.String LABEL_ATTRIBUTE = 
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".label");

	public static IAttributeType.Handle SOURCE_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".source");

	public static IAttributeType.String COMMENT_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".comment");
	
	@Deprecated
	public static IAttributeType.Boolean INHERITED_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".inherited");
	
	@Deprecated
	public static IAttributeType.Boolean FORBIDDEN_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".forbidden");
	
	@Deprecated
	public static IAttributeType.Boolean PRESERVED_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".preserved");
	
	public static IAttributeType.Boolean EXTENDED_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".extended");
	
	public static IAttributeType.Boolean ABSTRACT_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".abstract");
	
	public static IAttributeType.Boolean CONCRETE_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".concrete");
	
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
	
	public static IAttributeType.Boolean THEOREM_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".theorem");
	
	public static IAttributeType.Boolean GENERATED_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".generated");
	
	public static IAttributeType.String TARGET_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".target");

	public static IAttributeType.String IDENTIFIER_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".identifier");

	public static IAttributeType.Boolean ACCURACY_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".accurate");

	public static IAttributeType.Handle SCTARGET_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".scTarget");
	
	public static IAttributeType.String PODESC_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".poDesc");
	
	public static IAttributeType.String POROLE_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".poRole");
	
	public static IAttributeType.Handle POSELHINT_FST_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".poSelHintFst");
	
	public static IAttributeType.Handle POSELHINT_SND_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".poSelHintSnd");
	
	public static IAttributeType.Long POSTAMP_ATTRIBUTE =
		RodinCore.getLongAttrType(EventBPlugin.PLUGIN_ID + ".poStamp");
	
	public static IAttributeType.Handle PARENT_SET_ATTRIBUTE =
		RodinCore.getHandleAttrType(EventBPlugin.PLUGIN_ID + ".parentSet");

	// Attributes related to the PR and PS files
	
	// TODO : rename to .prConfidence
	public static IAttributeType.Integer CONFIDENCE_ATTRIBUTE =
		RodinCore.getIntegerAttrType(EventBPlugin.PLUGIN_ID + ".confidence");

	public static IAttributeType.Boolean PROOF_BROKEN_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".psBroken");
	
	// TODO : rename to .prManual
	public static IAttributeType.Boolean MANUAL_PROOF_ATTRIBUTE =
		RodinCore.getBooleanAttrType(EventBPlugin.PLUGIN_ID + ".psManual");
	
	public static IAttributeType.String GOAL_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prGoal");

	public static IAttributeType.String HYPS_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prHyps");
	
	/**
	 * @since 2.0
	 */
	public static IAttributeType.String UNSEL_HYPS_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prUnsel");
	
	public static IAttributeType.String INF_HYPS_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prInfHyps");
	
	public static IAttributeType.String RULE_DISPLAY_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prDisplay");
	
	public static IAttributeType.String STORE_REF_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prRef");
	
	public static IAttributeType.String STRING_VALUE_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prSValue");
	
	public static IAttributeType.String FRESH_IDENTIFIERS_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prFresh");
	
	public static IAttributeType.String PR_SETS_ATTRIBUTE =
		RodinCore.getStringAttrType(EventBPlugin.PLUGIN_ID + ".prSets");
	
	private EventBAttributes() {
		// Non-instantiable class
	}

}
