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
package org.ogema.driver.xbee.serialconnection;

import org.ogema.driver.xbee.manager.InputHandler;
import org.slf4j.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * This class initializes the other classes in the serialconnection package and implements functions from the Interface.
 * 
 * @author puschas
 * 
 */
public class SerialConnection {
	private final SerialPort serialPort;
	private final SerialPortReaderAp1 serialPortReader;
	private final SerialPortWriter serialPortWriter;
	private static final int baudrate = SerialPort.BAUDRATE_9600;
	private static final int databits = SerialPort.DATABITS_8;
	private static final int stopbits = SerialPort.STOPBITS_1;
	private static final int parity = SerialPort.PARITY_NONE;
	private final Thread readerThread;
	private volatile boolean running = true;

	private final Logger logger = org.slf4j.LoggerFactory.getLogger("xbee-driver");

	public SerialConnection(String port, InputHandler ih) throws SerialPortException {
		this.serialPort = new SerialPort(port);

		try {
			this.serialPort.openPort();
			this.serialPort.setParams(baudrate, databits, stopbits, parity);
		} catch (SerialPortException e) {
			logger.error(String.format("Failed to open serial port %s \n %s", port, e.getMessage()));
			throw e;
		} catch (Throwable e) {
			throw new SerialPortException(port, "?", e.toString());
		}
		serialPortWriter = new SerialPortWriter(this.serialPort);
		serialPortReader = new SerialPortReaderAp1(this.serialPort, ih);
		this.readerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (running) {
					try {
						((SerialPortReaderAp1) serialPortReader).serialRead();
						Thread.sleep(10);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					} catch (Throwable e) {
						logger.warn("Exception in serial reader thread",e);
					}
				}
			}
		});
		readerThread.setName("xbee serial reader");
		readerThread.start();
	}

	/**
	 * @param frame
	 *            complete frame (including start delimiter and checksum)
	 */
	public void sendFrame(byte[] frame) {
		if (serialPortWriter == null) {
			logger.warn(
					String.format("Specified port %s to coordinator hardware couldn't be detected!\nZigBee driver will not work!",
							serialPort.getPortName()));
			return;
		}
		serialPortWriter.sendData(frame);
	}

	/**
	 * Closes the jSSC connection and stops the serialPortWriter Thread
	 */
	public void closeConnection() throws SerialPortException {
		if (serialPort.isOpened())
			serialPort.closePort();
		stop();
	}

	void stop() {
		running = false;
		readerThread.interrupt();
	}
}
