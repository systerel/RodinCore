/**
 * 
 */
package org.eventb.internal.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 * 
 */
public class RodinHandleTransfer extends ByteArrayTransfer {

	private static RodinHandleTransfer _instance = new RodinHandleTransfer();

	private static final String TYPE_NAME = "rodin-element"; //$NON-NLS-1$

	private static final int TYPE_ID = registerType(TYPE_NAME);

	/**
	 * Returns the singleton instance of the RodinElementTransfer class.
	 * 
	 * @return the singleton instance of the RodinElementTransfer class
	 */
	public static RodinHandleTransfer getInstance() {
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	/*
	 * Method declared on Transfer.
	 */
	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}

	protected IRodinElement[] fromByteArray(byte[] bytes) {
		DataInputStream in = new DataInputStream(
				new ByteArrayInputStream(bytes));

		try {
			/* read number of gadgets */
			int n = in.readInt();
			/* read gadgets */
			IRodinElement[] elements = new IRodinElement[n];
			for (int i = 0; i < n; i++) {
				IRodinElement element = readHandle(null, in);
				if (element == null) {
					return null;
				}
				elements[i] = element;
			}
			return elements;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Reads and returns a single gadget from the given stream.
	 */
	private IRodinElement readHandle(IParent parent, DataInputStream dataIn)
			throws IOException {
		/**
		 * Gadget serialization format is as follows: (String) name of gadget
		 * (int) number of child gadgets (Gadget) child 1 ... repeat for each
		 * child
		 */
		String id = dataIn.readUTF();
		IRodinElement element = RodinCore.valueOf(id);
		return element;
		// int n = dataIn.readInt();
		//
		// // Gadget newParent = new Gadget(parent, name);
		// for (int i = 0; i < n; i++) {
		// readGadget(newParent, dataIn);
		// }
		// return newParent;
	}

	/*
	 * Method declared on Transfer.
	 */
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (!checkRodinElement(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		byte[] bytes = toByteArray((IRodinElement[]) object);
		if (bytes != null)
			super.javaToNative(bytes, transferData);
	}

	private boolean checkRodinElement(Object object) {
		if (object == null || !(object instanceof IRodinElement[])
				|| ((IRodinElement[]) object).length == 0)
			return false;
		return true;
	}

	protected byte[] toByteArray(IRodinElement[] elements) {
		/**
		 * Transfer data is an array of gadgets. Serialized version is: (int)
		 * number of gadgets (Gadget) gadget 1 (Gadget) gadget 2 ... repeat for
		 * each subsequent gadget see writeGadget for the (Gadget) format.
		 */
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);

		byte[] bytes = null;

		try {
			/* write number of markers */
			out.writeInt(elements.length);

			/* write markers */
			for (int i = 0; i < elements.length; i++) {
				writeHandle(elements[i], out);
			}
			out.close();
			bytes = byteOut.toByteArray();
		} catch (IOException e) {
			// when in doubt send nothing
		}
		return bytes;
	}

	/**
	 * Writes the given gadget to the stream.
	 */
	private void writeHandle(IRodinElement element, DataOutputStream dataOut)
			throws IOException {
		/**
		 * Gadget serialization format is as follows: (String) name of gadget
		 * (int) number of child gadgets (Gadget) child 1 ... repeat for each
		 * child
		 */
		dataOut.writeUTF(element.getHandleIdentifier());
		// Should dump the complete content of the element here (including its
		// children)
		// Gadget[] children = element.getChildren();
		// dataOut.writeInt(children.length);
		// for (int i = 0; i < children.length; i++) {
		// writeGadget(children[i], dataOut);
		// }
	}

}
