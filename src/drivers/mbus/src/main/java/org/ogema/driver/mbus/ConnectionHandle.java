/**
 * Copyright 2011-2019 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * This file is part of OGEMA.
 *
 *  OGEMA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3
 *  as published by the Free Software Foundation.
 *
 *  OGEMA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ogema.driver.mbus;

import org.openmuc.jmbus.MBusSap;

/**
 * Class representing an MBus Connection.<br>
 * This class will bind to the local com-interface.<br>
 * 
 */
public class ConnectionHandle {

	private int deviceCounter = 1;
	private final MBusSap mBusSap;
	private boolean connected = false;
	private final String deviceAddress;

	public ConnectionHandle(MBusSap mBusSap, String deviceAddress) {
		this.mBusSap = mBusSap;
		this.deviceAddress = deviceAddress;
	}

	public MBusSap getMBusSap() {
		return mBusSap;
	}

	public String getDeviceAddres() {
		return deviceAddress;
	}

	public void increaseDeviceCounter() {
		deviceCounter++;
	}

	public void decreaseDeviceCounter() {
		deviceCounter--;
	}

	public int getDeviceCounter() {
		return deviceCounter;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

}
