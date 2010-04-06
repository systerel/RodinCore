package org.eventb.internal.core.pm;

import java.util.ArrayList;

import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerDelta;

public class UserSupportManagerDelta implements IUserSupportManagerDelta {

	IUserSupportDelta[] affectedUserSupports = emptyUserSupports;

	/**
	 * Empty array of IUserSupportDelta
	 */
	protected static IUserSupportDelta[] emptyUserSupports = new IUserSupportDelta[] {};

	public IUserSupportDelta[] getAddedUserSupports() {
		return getUserSupportDeltaOfType(IUserSupportDelta.ADDED);
	}

	public IUserSupportDelta[] getRemovedUserSupports() {
		return getUserSupportDeltaOfType(IUserSupportDelta.REMOVED);
	}

	public IUserSupportDelta[] getChangedUserSupports() {
		return getUserSupportDeltaOfType(IUserSupportDelta.CHANGED);
	}

	private IUserSupportDelta[] getUserSupportDeltaOfType(int type) {
		int length = affectedUserSupports.length;
		if (length == 0) {
			return new IUserSupportDelta[] {};
		}
		ArrayList<IUserSupportDelta> children = new ArrayList<IUserSupportDelta>(
				length);
		for (int i = 0; i < length; i++) {
			if (affectedUserSupports[i].getKind() == type) {
				children.add(affectedUserSupports[i]);
			}
		}

		IUserSupportDelta[] childrenOfType = new IUserSupportDelta[children
				.size()];
		children.toArray(childrenOfType);

		return childrenOfType;
	}

	public IUserSupportDelta[] getAffectedUserSupports() {
		return affectedUserSupports;
	}

	public void addAffectedProofState(IUserSupport userSupport, ProofStateDelta affectedState) {
		UserSupportDelta delta = (UserSupportDelta) this.getDeltaForUserSupport(userSupport);
		delta.addAffectedProofState(affectedState);
		return;
	}

	protected void addAffectedUserSupport(IUserSupportDelta affectedUserSupport) {
		if (affectedUserSupports.length == 0) {
			affectedUserSupports = new IUserSupportDelta[] { affectedUserSupport };
			return;
		}

		IUserSupportDelta existingUserSupport = null;
		int existingUserSupportIndex = -1;
		if (affectedUserSupports != null) {
			for (int i = 0; i < affectedUserSupports.length; i++) {
				if (affectedUserSupports[i].getUserSupport() == affectedUserSupport
						.getUserSupport()) {
					existingUserSupport = affectedUserSupports[i];
					existingUserSupportIndex = i;
					break;
				}
			}
		}

		if (existingUserSupport == null) { // new affected child
			affectedUserSupports = growAndAddToArray(affectedUserSupports,
					affectedUserSupport);
		} else {
			switch (existingUserSupport.getKind()) {
			case IUserSupportDelta.ADDED:
				switch (affectedUserSupport.getKind()) {
				case IUserSupportDelta.ADDED:
					// user support was added then added -> it is added
				case IUserSupportDelta.CHANGED:
					// user support was added then changed -> it is added
					return;
				case IUserSupportDelta.REMOVED:
					// user support was added then removed -> noop
					affectedUserSupports = this.removeAndShrinkArray(
							affectedUserSupports, existingUserSupportIndex);
					return;
				}
				break;
			case IUserSupportDelta.REMOVED:
				switch (affectedUserSupport.getKind()) {
				case IUserSupportDelta.ADDED:
					// user support was removed then added -> it is changed
					((UserSupportDelta) affectedUserSupport)
							.setKind(IUserSupportDelta.CHANGED);
					((UserSupportDelta) affectedUserSupport)
							.setFlags(IUserSupportDelta.F_CURRENT
									| IUserSupportDelta.F_STATE);
					affectedUserSupports[existingUserSupportIndex] = affectedUserSupport;
					return;
				case IUserSupportDelta.CHANGED:
					// user support was removed then changed -> it is removed
				case IUserSupportDelta.REMOVED:
					// user support was removed then removed -> it is removed
					return;
				}
				break;
			case IUserSupportDelta.CHANGED:
				switch (affectedUserSupport.getKind()) {
				case IUserSupportDelta.ADDED:
					// user support was changed then added -> it is added
					((UserSupportDelta) affectedUserSupport).clearInformation();
					break;
				case IUserSupportDelta.REMOVED:
					// user support was changed then removed -> it is removed
					affectedUserSupports[existingUserSupportIndex] = affectedUserSupport;
					((UserSupportDelta) affectedUserSupport).clearInformation();
					return;
				case IUserSupportDelta.CHANGED:
					// user support was changed then changed -> it is changed
					IProofStateDelta[] proofStateDeltas = affectedUserSupport
							.getAffectedProofStates();
					for (int i = 0; i < proofStateDeltas.length; i++) {
						((UserSupportDelta) existingUserSupport)
								.addAffectedProofState(proofStateDeltas[i]);
					}

					// update flags
					int existingFlags = existingUserSupport.getFlags();

					((UserSupportDelta) existingUserSupport)
							.setFlags(existingFlags
									| affectedUserSupport.getFlags());
					// append information
					((UserSupportDelta) existingUserSupport).addInformationAll(
							affectedUserSupport.getInformation());

					return;
				}
				break;
			default:
				// unknown -> existing user support becomes the user support
				// with the existing child's flags
				int existingFlags = existingUserSupport.getFlags();
				affectedUserSupports[existingUserSupportIndex] = affectedUserSupport;
				((UserSupportDelta) affectedUserSupport)
						.setFlags(affectedUserSupport.getFlags() | existingFlags);
				// append information
				((UserSupportDelta) existingUserSupport).addInformationAll(
						affectedUserSupport.getInformation());
			}
		}

	}

	/**
	 * Adds the new element to a new array that contains all of the elements of
	 * the old array. Returns the new array.
	 */
	private IUserSupportDelta[] growAndAddToArray(IUserSupportDelta[] array,
			IUserSupportDelta addition) {
		IUserSupportDelta[] old = array;
		array = new IUserSupportDelta[old.length + 1];
		System.arraycopy(old, 0, array, 0, old.length);
		array[old.length] = addition;
		return array;
	}

	/**
	 * Removes the element from the array. Returns the a new array which has
	 * shrunk.
	 */
	protected IUserSupportDelta[] removeAndShrinkArray(IUserSupportDelta[] old,
			int index) {
		IUserSupportDelta[] array = new IUserSupportDelta[old.length - 1];
		if (index > 0)
			System.arraycopy(old, 0, array, 0, index);
		int rest = old.length - index - 1;
		if (rest > 0)
			System.arraycopy(old, index + 1, array, index, rest);
		return array;
	}

	public IUserSupportDelta getDeltaForUserSupport(IUserSupport userSupport) {
		for (IUserSupportDelta delta : affectedUserSupports) {
			if (delta.getUserSupport() == userSupport)
				return delta;
		}
		UserSupportDelta newDelta = new UserSupportDelta(userSupport);
		affectedUserSupports = growAndAddToArray(affectedUserSupports, newDelta);
		return newDelta;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IUserSupportDelta delta : affectedUserSupports) {
			if (sep)
				builder.append('\n');
			builder.append(delta);
			sep = true;
		}

		return builder.toString();
	}

}
