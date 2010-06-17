/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension.datatype;


/**
 * @author Nicolas Beauger
 * @since 2.0
 *
 */
public interface IDatatypeExtension {

	String getTypeName();
	
	// used as type constructor extension id
	String getId();
	
	// used as type constructor extension group id
	String getGroupId();
	
	void addTypeParameters(ITypeConstructorMediator mediator);
	
	void addConstructors(IConstructorMediator mediator);
	
	void addDestructors(IDestructorMediator mediator);
}
