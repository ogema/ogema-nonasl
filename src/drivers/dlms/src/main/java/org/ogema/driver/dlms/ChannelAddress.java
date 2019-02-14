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
package org.ogema.driver.dlms;

import org.openmuc.jdlms.client.GetRequest;
import org.openmuc.jdlms.client.ObisCode;

public class ChannelAddress {

	private final int classId;
	private final ObisCode instanceId;
	private final String printableInstanceId;
	private final int attributeId;

	public ChannelAddress(int classId, String instanceId, int attributeId) {
		this.classId = classId;
		printableInstanceId = instanceId;
		this.instanceId = parseInstanceId(instanceId);
		this.attributeId = attributeId;
	}

	public ChannelAddress(int classId, byte[] instanceId, int attributeId) {
		this.classId = classId;
		printableInstanceId = parseInstanceId(instanceId);
		this.instanceId = parseInstanceId(printableInstanceId);
		this.attributeId = attributeId;
	}

	public static ChannelAddress parse(String input) {
		String[] tokens = input.split("/");
		if (tokens.length != 3) {
			throw new IllegalArgumentException("Channel address must be of format 'classId/logicalName/attributeId'");
		}

		int classId = Integer.parseInt(tokens[0]);
		int attributeId = Integer.parseInt(tokens[2]);
		String instanceId = tokens[1];

		return new ChannelAddress(classId, instanceId, attributeId);
	}

	private static ObisCode parseInstanceId(String idString) {
		ObisCode result = null;

		String tokens[] = idString.split("\\.");

		if (tokens.length == 1 && idString.length() == 12) {
			byte instanceId[] = new byte[6];
			for (int i = 0; i < idString.length(); i += 2) {
				instanceId[i / 2] = (byte) Integer.parseInt(idString.substring(i, i + 2), 16);
			}
			result = new ObisCode(instanceId[0], instanceId[1], instanceId[2], instanceId[3], instanceId[4],
					instanceId[5]);
		}
		else if (tokens.length == 6) {
			result = new ObisCode(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]),
					Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer
							.parseInt(tokens[5]));
		}
		else {
			throw new IllegalArgumentException("Reduced ID codes are not supported");
		}

		return result;
	}

	private static String parseInstanceId(byte[] instanceId) {
		StringBuilder result = new StringBuilder();

		result.append(Integer.toString(instanceId[0] & 0xff));
		for (int i = 1; i < instanceId.length; i++) {
			result.append(".").append(Integer.toString(instanceId[i] & 0xff));
		}

		return result.toString();
	}

	public int getClassId() {
		return classId;
	}

	public ObisCode getInstanceId() {
		return instanceId;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public String getPrintableInstanceID() {
		return printableInstanceId;
	}

	public GetRequest createGetRequest() {
		return new GetRequest(classId, instanceId, attributeId);
	}

	@Override
	public String toString() {
		return classId + "/" + printableInstanceId + "/" + attributeId;
	}
}
