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
package org.ogema.driver.xbee.manager.zcl;

import org.ogema.driver.xbee.Constants;
import org.ogema.driver.xbee.manager.Endpoint;

/**
 * 
 * @author puschas
 * see ZigBee Cluster Library pages 127-130.
 */
public class IdentifyCluster extends Cluster {
	public IdentifyCluster(Endpoint endpoint) {
		super((short) 0x0003, endpoint);
		setName("Identify");
		clusterId = 0x0003;
		this.endpoint = endpoint;

		clusterAttributes.put((short) 0x0000, new ClusterAttribute(this, (short) 0x0000, "IdentifyTime",
				Constants.READ_WRITE, Constants.MANDATORY));

		clusterCommands.put((byte) 0x00, new ClusterCommand(this, (byte) 0x00, "Identify", Constants.MANDATORY));
		clusterCommands.put((byte) 0x01, new ClusterCommand(this, (byte) 0x01, "IdentifyQuery", Constants.MANDATORY));
		// TODO: Receive IdentifyQueryResponse
	}
}
