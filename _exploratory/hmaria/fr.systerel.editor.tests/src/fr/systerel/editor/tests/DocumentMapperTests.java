/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.tests;

import org.junit.Before;
import org.junit.Test;

import fr.systerel.editor.documentModel.DocumentMapper;

/**
 * @author Nicolas Beauger
 *
 */
public class DocumentMapperTests {
	private final DocumentMapper mapper = new DocumentMapper();

	@Before
	public void init() {
		mapper.reinitialize();
	}
	
	@Test
	public void testAddEditorSection() {
		
	}
}
