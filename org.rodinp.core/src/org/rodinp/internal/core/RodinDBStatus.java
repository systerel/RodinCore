/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.JavaModelStatus
 *     Systerel - added more messages
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.util.Messages;

/**
 * @see IRodinDBStatus
 */
public class RodinDBStatus extends Status implements IRodinDBStatus,
		IRodinDBStatusConstants, IResourceStatus {

	/**
	 * The elements related to the failure, or <code>null</code> if no
	 * elements are involved.
	 */
	protected IRodinElement[] elements = new IRodinElement[0];

	/**
	 * The path related to the failure, or <code>null</code> if no path is
	 * involved.
	 */
	protected IPath path;

	/**
	 * The <code>String</code> related to the failure, or <code>null</code>
	 * if no <code>String</code> is involved.
	 */
	protected String string;

	/**
	 * Empty children
	 */
	protected final static IStatus[] NO_CHILDREN = new IStatus[] {};

	protected IStatus[] children = NO_CHILDREN;

	/**
	 * Singleton OK object
	 */
	public static final IRodinDBStatus VERIFIED_OK = new RodinDBStatus(OK, OK,
			Messages.status_OK);

	/**
	 * Constructs a Rodin model status with no corresponding elements.
	 */
	public RodinDBStatus() {
		// no code for an multi-status
		super(ERROR, RodinCore.PLUGIN_ID, 0, "RodinDBStatus", null); //$NON-NLS-1$
	}

	/**
	 * Constructs a Rodin model status with no corresponding elements.
	 */
	public RodinDBStatus(int code) {
		super(ERROR, RodinCore.PLUGIN_ID, code, "RodinDBStatus", null); //$NON-NLS-1$
		this.elements = RodinElement.NO_ELEMENTS;
	}

	/**
	 * Constructs a Rodin model status with the given corresponding elements.
	 */
	public RodinDBStatus(int code, IRodinElement... elements) {
		super(ERROR, RodinCore.PLUGIN_ID, code, "RodinDBStatus", null); //$NON-NLS-1$
		this.elements = elements;
		this.path = null;
	}

	/**
	 * Constructs a Rodin model status with no corresponding elements.
	 */
	public RodinDBStatus(int code, String string) {
		this(ERROR, code, string);
	}

	/**
	 * Constructs a Rodin model status with the given corresponding element and
	 * string.
	 */
	public RodinDBStatus(int severity, int code, IRodinElement element,
			String string) {
		super(severity, RodinCore.PLUGIN_ID, code, "RodinDBStatus", null); //$NON-NLS-1$
		this.elements = new IRodinElement[] { element };
		this.path = null;
		this.string = string;
	}

	/**
	 * Constructs a Rodin model status with no corresponding elements.
	 */
	public RodinDBStatus(int severity, int code, String string) {
		super(severity, RodinCore.PLUGIN_ID, code, "RodinDBStatus", null); //$NON-NLS-1$
		this.elements = RodinElement.NO_ELEMENTS;
		this.path = null;
		this.string = string;
	}

	/**
	 * Constructs a Rodin model status with no corresponding elements.
	 */
	public RodinDBStatus(int code, Throwable throwable) {
		super(ERROR, RodinCore.PLUGIN_ID, code, "RodinDBStatus", throwable); //$NON-NLS-1$
		this.elements = RodinElement.NO_ELEMENTS;
	}

	/**
	 * Constructs a Rodin model status with no corresponding elements.
	 */
	public RodinDBStatus(int code, IPath path) {
		super(ERROR, RodinCore.PLUGIN_ID, code, "RodinDBStatus", null); //$NON-NLS-1$
		this.elements = RodinElement.NO_ELEMENTS;
		this.path = path;
	}

	/**
	 * Constructs a Rodin model status with the given corresponding element.
	 */
	public RodinDBStatus(int code, IRodinElement element) {
		this(code, new IRodinElement[] { element });
	}

	/**
	 * Constructs a Rodin model status with the given corresponding element and
	 * string
	 */
	public RodinDBStatus(int code, IRodinElement element, String string) {
		this(code, new IRodinElement[] { element });
		this.string = string;
	}

	/**
	 * Constructs a Rodin model status with the given corresponding element and
	 * path
	 */
	public RodinDBStatus(int code, IRodinElement element, IPath path) {
		this(code, new IRodinElement[] { element });
		this.path = path;
	}

	/**
	 * Constructs a Rodin model status with the given corresponding element,
	 * path and string
	 */
	public RodinDBStatus(int code, IRodinElement element, IPath path,
			String string) {
		this(code, new IRodinElement[] { element });
		this.path = path;
		this.string = string;
	}

	/**
	 * Constructs a Rodin model status with no corresponding elements.
	 */
	public RodinDBStatus(CoreException coreException) {
		super(ERROR, RodinCore.PLUGIN_ID, CORE_EXCEPTION,
				"RodinDBStatus", coreException); //$NON-NLS-1$
		elements = RodinElement.NO_ELEMENTS;
	}

	protected int getBits() {
		int severity = 1 << (getCode() % 100 / 33);
		int category = 1 << ((getCode() / 100) + 3);
		return severity | category;
	}

	/**
	 * @see IStatus
	 */
	@Override
	public IStatus[] getChildren() {
		return children;
	}

	/**
	 * @see IRodinDBStatus
	 */
	@Override
	public IRodinElement[] getElements() {
		return elements;
	}

	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	@Override
	public String getMessage() {
		Throwable exception = getException();
		if (exception == null) {
			switch (getCode()) {
			case CORE_EXCEPTION:
				return Messages.status_coreException;

			case BUILDER_INITIALIZATION_ERROR:
				return Messages.build_initializationError;

			case BUILDER_SERIALIZATION_ERROR:
				return Messages.build_serializationError;

			case DEVICE_PATH:
				return Messages.bind(Messages.status_cannotUseDeviceOnPath,
						getPath().toString());

			case ELEMENT_DOES_NOT_EXIST:
				return Messages.bind(Messages.element_doesNotExist,
						((RodinElement) elements[0]).toStringWithAncestors());

			case INVALID_CONTENTS:
				return Messages.status_invalidContents;

			case INVALID_DESTINATION:
				return Messages.bind(Messages.status_invalidDestination,
						((RodinElement) elements[0]).toStringWithAncestors());

			case INVALID_ELEMENT_TYPES:
				StringBuffer buff = new StringBuffer(
						Messages.operation_notSupported);
				for (int i = 0; i < elements.length; i++) {
					if (i > 0) {
						buff.append(", "); //$NON-NLS-1$
					}
					buff.append(((RodinElement) elements[i])
							.toStringWithAncestors());
				}
				return buff.toString();

			case INVALID_NAME:
				return Messages.bind(Messages.status_invalidName, string);

			case INVALID_PATH:
				if (string != null) {
					return string;
				} else {
					return Messages.bind(Messages.status_invalidPath,
							getPath() == null ? "null" : getPath().toString() //$NON-NLS-1$
					);
				}

			case INVALID_PROJECT:
				return Messages.bind(Messages.status_invalidProject, string);

			case INVALID_RESOURCE:
				return Messages.bind(Messages.status_invalidResource, string);

			case INVALID_RESOURCE_TYPE:
				return Messages.bind(Messages.status_invalidResourceType,
						string);

			case INVALID_SIBLING:
				if (string != null) {
					return Messages
							.bind(Messages.status_invalidSibling, string);
				} else {
					return Messages.bind(Messages.status_invalidSibling,
							((RodinElement) elements[0])
									.toStringWithAncestors());
				}

			case IO_EXCEPTION:
				return Messages.status_IOException;

			case NAME_COLLISION:
				if (string != null) {
					return string;
				} else {
					return Messages.bind(Messages.status_nameCollision,
							((RodinElement) elements[0])
							.toStringWithAncestors());
				}
			case NO_ELEMENTS_TO_PROCESS:
				return Messages.operation_needElements;

			case NULL_NAME:
				return Messages.operation_needName;

			case NULL_PATH:
				return Messages.operation_needPath;

			case NULL_STRING:
				return Messages.operation_needString;

			case PATH_OUTSIDE_PROJECT:
				return Messages.bind(Messages.operation_pathOutsideProject,
						string, ((RodinElement) elements[0])
								.toStringWithAncestors());

			case READ_ONLY:
				IRodinElement element = elements[0];
				String name = element.getElementName();
				return Messages.bind(Messages.status_readOnly, name);

			case RELATIVE_PATH:
				return Messages.bind(Messages.operation_needAbsolutePath,
						getPath().toString());

			case UPDATE_CONFLICT:
				return Messages.status_updateConflict;

			case NO_LOCAL_CONTENTS:
				return Messages.bind(Messages.status_noLocalContents, getPath()
						.toString());
				
			case ATTRIBUTE_DOES_NOT_EXIST:
				return Messages.bind(Messages.status_attribute_doesNotExist,
						((RodinElement) elements[0]).toStringWithAncestors(),
						string);

			case XML_PARSE_ERROR:
			case XML_SAVE_ERROR:
			case XML_CONFIG_ERROR:
			case INVALID_MARKER_LOCATION:
			case CONVERSION_ERROR:
				assert false; // always has an exception attached.

			case INVALID_RENAMING:
				return Messages.status_invalidRenaming;

			case INVALID_ATTRIBUTE_KIND:
				return Messages.bind(Messages.status_invalidAttributeKind,
						string);

			case INVALID_ATTRIBUTE_VALUE:
				return Messages.bind(Messages.status_invalidAttributeKind,
						string);

			case INVALID_VERSION_NUMBER:
				return Messages.bind(Messages.status_invalidVersionNumber,
						elements[0].getPath(), string);

			case FUTURE_VERSION:
				return Messages.bind(Messages.status_futureVersionNumber,
						elements[0].getPath(), string);

			case PAST_VERSION:
				return Messages.bind(Messages.status_pastVersionNumber,
						elements[0].getPath(), string);

			case ROOT_ELEMENT:
				return Messages.bind(Messages.status_rootElement,
						((RodinElement) elements[0]).toStringWithAncestors());
				
			case INVALID_CHILD_TYPE:
				final IRodinElement parent = elements[0];
				return Messages.bind(Messages.status_invalidChildType, string,
						elements[0].getPath() + " of type "
								+ parent.getElementType().getId());

			case INVALID_ATTRIBUTE_TYPE:
				final IRodinElement parent1 = elements[0];
				return Messages.bind(Messages.status_invalidAttributeType,
						string, elements[0].getPath() + " of type "
								+ parent1.getElementType().getId());

			case INDEXER_ERROR:
				return Messages.bind(Messages.status_indexerError,
						elements[0].getPath(), string);

			}
			if (string != null) {
				return string;
			} else {
				return ""; // //$NON-NLS-1$
			}
		} else {
			String message = exception.getMessage();
			if (message != null) {
				return message;
			} else {
				return exception.toString();
			}
		}
	}

	/**
	 * @see IRodinDBStatus#getPath()
	 */
	@Override
	public IPath getPath() {
		return path;
	}

	/**
	 * @see IStatus#getSeverity()
	 */
	@Override
	public int getSeverity() {
		if (children == NO_CHILDREN)
			return super.getSeverity();
		int severity = -1;
		for (int i = 0, max = children.length; i < max; i++) {
			int childrenSeverity = children[i].getSeverity();
			if (childrenSeverity > severity) {
				severity = childrenSeverity;
			}
		}
		return severity;
	}

	/**
	 * @see IRodinDBStatus#isDoesNotExist()
	 */
	@Override
	public boolean isDoesNotExist() {
		return getCode() == ELEMENT_DOES_NOT_EXIST;
	}

	/**
	 * @see IStatus#isMultiStatus()
	 */
	@Override
	public boolean isMultiStatus() {
		return children != NO_CHILDREN;
	}

	/**
	 * @see IStatus#isOK()
	 */
	@Override
	public boolean isOK() {
		return getCode() == OK;
	}

	/**
	 * @see IStatus#matches(int)
	 */
	@Override
	public boolean matches(int mask) {
		if (!isMultiStatus()) {
			return matches(this, mask);
		} else {
			for (int i = 0, max = children.length; i < max; i++) {
				if (matches((RodinDBStatus) children[i], mask))
					return true;
			}
			return false;
		}
	}

	/**
	 * Helper for matches(int).
	 */
	protected boolean matches(RodinDBStatus status, int mask) {
		int severityMask = mask & 0x7;
		int categoryMask = mask & ~0x7;
		int bits = status.getBits();
		return ((severityMask == 0) || (bits & severityMask) != 0)
				&& ((categoryMask == 0) || (bits & categoryMask) != 0);
	}

	/**
	 * Creates and returns a new <code>IRodinDBStatus</code> that is a a
	 * multi-status status.
	 * 
	 * @see IStatus#isMultiStatus()
	 */
	public static IRodinDBStatus newMultiStatus(IRodinDBStatus[] children) {
		RodinDBStatus rds = new RodinDBStatus();
		rds.children = children;
		return rds;
	}

	/**
	 * Returns a printable representation of this exception for debugging
	 * purposes.
	 */
	@Override
	public String toString() {
		if (this == VERIFIED_OK) {
			return "RodinDBStatus[OK]"; //$NON-NLS-1$
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("Rodin Database Status ["); //$NON-NLS-1$
		buffer.append(getMessage());
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}
}
