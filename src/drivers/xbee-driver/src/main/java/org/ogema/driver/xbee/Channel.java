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
package org.ogema.driver.xbee;

import java.io.IOException;

import org.ogema.core.channelmanager.driverspi.ChannelLocator;
import org.ogema.core.channelmanager.driverspi.ChannelUpdateListener;
import org.ogema.core.channelmanager.driverspi.SampledValueContainer;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;

/**
 * Each channel represents a ZigBee Cluster Command/Attribute
 * 
 * @author puschas
 * 
 */
abstract class Channel {
	protected ChannelLocator locator;
	protected Device device;

	protected Channel(ChannelLocator locator) {
		this.locator = locator;
	}

	public static Channel createChannel(ChannelLocator locator, Device dev) {
		String[] splitAddress = locator.getChannelAddress().split(":");

		switch (splitAddress[1]) {
		case Constants.XBEE:
			return new XBeeChannel(locator, splitAddress, dev);
		case Constants.COMMAND:
			return new CommandChannel(locator, splitAddress, dev);
		case Constants.ATTRIBUTE:
			return new AttributeChannel(locator, splitAddress, dev);
		case Constants.MANUFACTURER_SPECIFIC:
			return new ManufacturerChannel(locator, splitAddress, dev);
		default:
			break;

		}
		throw new NullPointerException("could not create Channel with Address " + locator.getChannelAddress());
	}

	public ChannelLocator getChannelLocator() {
		return locator;
	}

	abstract public SampledValue readValue(Connection connection) throws IOException, UnsupportedOperationException;

	abstract public void writeValue(Connection connection, Value value) throws IOException,
			UnsupportedOperationException;

	abstract public void setUpdateListener(SampledValueContainer container, ChannelUpdateListener listener)
			throws IOException, UnsupportedOperationException;

	abstract public void removeUpdateListener() throws IOException, UnsupportedOperationException;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
}
