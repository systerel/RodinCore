/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.IJavaModelStatusConstants.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;

/**
 * Status codes used with Rodin database status objects.
 * <p>
 * This interface declares constants only; it is not intended to be implemented
 * or extended.
 * </p>
 * 
 * @see IRodinDBStatus
 * @see org.eclipse.core.runtime.IStatus#getCode()
 */
public interface IRodinDBStatusConstants {

	/**
	 * Status constant indicating a core exception occurred. Use
	 * <code>getException</code> to retrieve a <code>CoreException</code>.
	 */
	int CORE_EXCEPTION = 962;

	/**
	 * Status constant indicating one or more of the elements supplied are not
	 * of a valid type for the operation to process. The element(s) can be
	 * retrieved using <code>getElements</code> on the status object.
	 */
	int INVALID_ELEMENT_TYPES = 963;

	/**
	 * Status constant indicating that no elements were provided to the
	 * operation for processing.
	 */
	int NO_ELEMENTS_TO_PROCESS = 964;

	/**
	 * Status constant indicating that one or more elements supplied do not
	 * exist. The element(s) can be retrieved using <code>getElements</code>
	 * on the status object.
	 * 
	 * @see IRodinDBStatus#isDoesNotExist()
	 */
	int ELEMENT_DOES_NOT_EXIST = 965;

	/**
	 * Status constant indicating that a <code>null</code> path was supplied
	 * to the operation.
	 */
	int NULL_PATH = 966;

	/**
	 * Status constant indicating that a path outside of the project was
	 * supplied to the operation. The path can be retrieved using
	 * <code>getPath</code> on the status object.
	 */
	int PATH_OUTSIDE_PROJECT = 967;

	/**
	 * Status constant indicating that a relative path was supplied to the
	 * operation when an absolute path is required. The path can be retrieved
	 * using <code>getPath</code> on the status object.
	 */
	int RELATIVE_PATH = 968;

	/**
	 * Status constant indicating that a path specifying a device was supplied
	 * to the operation when a path with no device is required. The path can be
	 * retrieved using <code>getPath</code> on the status object.
	 */
	int DEVICE_PATH = 969;

	/**
	 * Status constant indicating that a string was supplied to the operation
	 * that was <code>null</code>.
	 */
	int NULL_STRING = 970;

	/**
	 * Status constant indicating that the operation encountered a read-only
	 * element. The element(s) can be retrieved using <code>getElements</code>
	 * on the status object.
	 */
	int READ_ONLY = 971;

	/**
	 * Status constant indicating that a naming collision would occur if the
	 * operation proceeded.
	 */
	int NAME_COLLISION = 972;

	/**
	 * Status constant indicating that a destination provided for a
	 * copy/move/rename operation is invalid. The destination element can be
	 * retrieved using <code>getElements</code> on the status object.
	 */
	int INVALID_DESTINATION = 973;

	/**
	 * Status constant indicating that a path provided to an operation is
	 * invalid. The path can be retrieved using <code>getPath</code> on the
	 * status object.
	 */
	int INVALID_PATH = 974;

	/**
	 * Status constant indicating there is an update conflict for a working
	 * copy. The compilation unit on which the working copy is based has changed
	 * since the working copy was created.
	 */
	int UPDATE_CONFLICT = 975;

	/**
	 * Status constant indicating that <code>null</code> was specified as a
	 * name argument.
	 */
	int NULL_NAME = 976;

	/**
	 * Status constant indicating that a name provided is not syntactically
	 * correct. The name can be retrieved from <code>getString</code>.
	 */
	int INVALID_NAME = 977;

	/**
	 * Status constant indicating that the specified contents are not valid.
	 */
	int INVALID_CONTENTS = 978;

	/**
	 * Status constant indicating that an <code>java.io.IOException</code>
	 * occurred.
	 */
	int IO_EXCEPTION = 979;

	/**
	 * Status constant indicating that the Rodin builder could not be
	 * initialized.
	 */
	int BUILDER_INITIALIZATION_ERROR = 980;

	/**
	 * Status constant indicating that the Rodin builder's last built state could
	 * not be serialized or deserialized.
	 */
	int BUILDER_SERIALIZATION_ERROR = 981;

	/**
	 * Status constant indicating that a sibling specified is not valid.
	 */
	int INVALID_SIBLING = 982;

	/**
	 * Status indicating that a Rodin element could not be created because the
	 * underlying resource is invalid.
	 * 
	 * @see RodinCore
	 */
	int INVALID_RESOURCE = 983;

	/**
	 * Status indicating that a Rodin element could not be created because the
	 * underlying resource is not of an appropriate type.
	 * 
	 * @see RodinCore
	 */
	int INVALID_RESOURCE_TYPE = 984;

	/**
	 * Status indicating that a Rodin element could not be created because the
	 * project owning underlying resource does not have the Rodin nature.
	 * 
	 * @see RodinCore
	 */
	int INVALID_PROJECT = 985;

	/**
	 * Status indicating that the corresponding resource has no local contents
	 * yet. This might happen when attempting to use a resource before its
	 * contents has been made locally available.
	 */
	int NO_LOCAL_CONTENTS = 986;

	/**
	 * Status indicating that a project is involved in a build path cycle.
	 */
	int BUILD_CYCLE = 987;

	/**
	 * Status indicating that an XML error was encountered while parsing a Rodin file.
	 */
	int XML_PARSE_ERROR = 988;

	/**
	 * Status indicating that a Rodin file, although conforming to the XML
	 * standard, doesn't abide by the constraints of the XML subset used by the
	 * Rodin database.
	 */
	int MALFORMED_FILE_ERROR = 989;

	/**
	 * Status indicating that an internal element could not be created because
	 * its type is not known by the database. The element type can be retrieved
	 * using <code>getString</code> on the status object.
	 */
	int INVALID_INTERNAL_ELEMENT_TYPE = 990;

	/**
	 * Status indicating that the number of renamings supplied to a copy or move
	 * operation does not match the number of elements that were supplied.
	 */
	int INDEX_OUT_OF_BOUNDS = 991;

	/**
	 * Status indicating that an XML error was encountered while saving a Rodin file.
	 */
	int XML_SAVE_ERROR = 992;

	/**
	 * Status indicating that an XML configuration error was encountered while
	 * parsing or saving a Rodin file.
	 */
	int XML_CONFIG_ERROR = 993;

	/**
	 * Status indicating that an attribute name specified by a client is not
	 * known to the Rodin database. The name can be retrieved from
	 * <code>getString</code>.
	 */
	int UNKNOWN_ATTRIBUTE = 994;
	
	/**
	 * Status indicating that an attribute name doesn't match the Java type used
	 * for manipulating it. For instance, a client tried to get or set a string
	 * attribute using a method for boolean attributes. The name of the
	 * attribute can be retrieved from <code>getString</code>.
	 */
	int INVALID_ATTRIBUTE_KIND = 995;

	/**
	 * Status indicating that an attribute value cannot be parsed by the
	 * database. The name of the attribute can be retrieved from
	 * <code>getString</code>.
	 */
	int INVALID_ATTRIBUTE_VALUE = 996;

	/**
	 * Status indicating that an attempt has been made to read an attribute
	 * which doesn't exist. The name of the attribute can be retrieved from
	 * <code>getString</code>.
	 */
	int ATTRIBUTE_DOES_NOT_EXIST = 997;
	
}
