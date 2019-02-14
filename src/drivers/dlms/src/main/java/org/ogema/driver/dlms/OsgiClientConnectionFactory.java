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

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.client.ClientConnectionSettings;
import org.openmuc.jdlms.client.hdlc.impl.HdlcClientLayerFactory;
import org.openmuc.jdlms.client.impl.ClientConnectionFactory;
import org.openmuc.jdlms.client.impl.ILowerLayerFactory;
import org.openmuc.jdlms.client.ip.impl.TcpClientLayerFactory;
import org.openmuc.jdlms.client.ip.impl.UdpClientLayerFactory;

/**
 * Implementation of {@link ILowerLayerFactory} statically loading all available LowerLayerFactories.
 * 
 * @author Karsten Mueller-Bier
 */
public final class OsgiClientConnectionFactory extends ClientConnectionFactory {

	private final List<ILowerLayerFactory> factories;

	public OsgiClientConnectionFactory() {
		factories = new ArrayList<ILowerLayerFactory>(3);
		factories.add(new HdlcClientLayerFactory());
		factories.add(new UdpClientLayerFactory());
		factories.add(new TcpClientLayerFactory());
	}

	@Override
	protected ILowerLayerFactory getLowerLayerFactory(
			@SuppressWarnings("rawtypes") Class<? extends ClientConnectionSettings> settingsClass) {
		for (ILowerLayerFactory factory : factories) {
			if (factory.accepts(settingsClass)) {
				return factory;
			}
		}

		return null;
	}
}
