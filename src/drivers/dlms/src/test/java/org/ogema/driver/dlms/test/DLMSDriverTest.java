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
package org.ogema.driver.dlms.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ogema.core.channelmanager.driverspi.ChannelLocator;
import org.ogema.core.channelmanager.driverspi.DeviceLocator;
import org.ogema.core.channelmanager.driverspi.SampledValueContainer;
import org.ogema.driver.dlms.DlmsDriver;

/**
 * For testing with a Kamstrup 382 device
 * 
 * @author pweiss
 * 
 */

public class DLMSDriverTest {

	private static List<SampledValueContainer> sampledValueContainer = new ArrayList<SampledValueContainer>();

	public static void main(String[] args) {

		DeviceLocator device = new DeviceLocator("dlms-driver", "", "hdlc:/dev/ttyUSB0:16:1",
				"baudrate=9600;referencing=LN;usehandshake=false");

		// Syntax for addressing: ClassID:OBISCODE:AttributeID
		ChannelLocator channel = new ChannelLocator("1:1.1.96.1.0.255:2", device);
		ChannelLocator channel1 = new ChannelLocator("1:0.0.42.0.0.255:2", device);
		ChannelLocator channel2 = new ChannelLocator("3:1.1.31.25.0.255:2", device);
		ChannelLocator channel3 = new ChannelLocator("3:1.1.32.25.0.255:2", device);

		SampledValueContainer sample = new SampledValueContainer(channel);
		SampledValueContainer sample1 = new SampledValueContainer(channel1);
		SampledValueContainer sample2 = new SampledValueContainer(channel2);
		SampledValueContainer sample3 = new SampledValueContainer(channel3);

		sampledValueContainer.add(sample);
		sampledValueContainer.add(sample1);
		sampledValueContainer.add(sample2);
		sampledValueContainer.add(sample3);

		DlmsDriver dlms = new DlmsDriver();

		dlms.channelAdded(channel);
		dlms.channelAdded(channel1);
		dlms.channelAdded(channel2);
		dlms.channelAdded(channel3);

		try {
			dlms.readChannels(sampledValueContainer);
			dlms.channelRemoved(channel);
			dlms.channelRemoved(channel1);

		} catch (UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}

	}
}
