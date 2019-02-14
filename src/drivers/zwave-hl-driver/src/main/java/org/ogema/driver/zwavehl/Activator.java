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
package org.ogema.driver.zwavehl;

import org.ogema.core.application.Application;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 * @author baerthbn
 * 
 */
public class Activator implements BundleActivator {

	private ServiceRegistration<?> serviceRegistration;
	private ZWaveHlDriver driver;
	public static volatile boolean bundleIsRunning = true;
	private ShellCommands sc;

	@Override
	public synchronized void start(BundleContext context) throws Exception {
		driver = new ZWaveHlDriver();
		Application application = driver;
		serviceRegistration = context.registerService(Application.class.getName(), application, null);
		sc = new ShellCommands(driver, context);
	}

	@Override
	public synchronized void stop(BundleContext context) throws Exception {
		bundleIsRunning = false;
		if (serviceRegistration != null)
			serviceRegistration.unregister();
		serviceRegistration = null;
		if (sc != null)
			sc.close();
		sc = null;
	}

}
