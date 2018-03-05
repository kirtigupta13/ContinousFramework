/**
 * 
 */
package com.cerner.devcenter.education.utils;

/**
 * Tracks a resource status for a user
 * 
 * @author Asim Mohammed (AM045300)
 *
 */
public enum Status {
	INCOMPLETE(0), COMPLETE(1);

	private int status;

	Status(int statusValue) {
		status = statusValue;
	}

	public int getStatusValue() {
		return status;
	}
}
