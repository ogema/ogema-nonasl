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
package org.ogema.driver.xbee.frames;

import java.nio.ByteBuffer;

/**
 * This class represents a Remote AT ClusterCommand message. Use the message and the XBeeFrameFactory to generate the
 * Remote AT ClusterCommand frame.
 * 
 * @author puschas
 * @see <a href="http://ftp1.digi.com/support/utilities/digi_apiframes.htm">Digi API Packet Generator for ZB (ZigBee)
 *      Networks</a>
 */
public class RemoteAtCommand extends AbstractAtCommand {
	private final byte frameType = 0x17;
	private long address64Bit;
	private short address16Bit;
	private byte remoteCmdOptions;

	/**
	 * all parameters without predefined values.
	 * 
	 * @param frameId
	 * @param address64Bit
	 * @param address16Bit
	 * @param remoteCmdOptions
	 * @param atCommand
	 * @param parameterValueArray
	 */
	public RemoteAtCommand(byte frameId, long address64Bit, short address16Bit, byte remoteCmdOptions, short atCommand,
			byte[] parameterValueArray) {
		this.frameId = frameId;
		this.address64Bit = address64Bit;
		this.address16Bit = address16Bit;
		this.remoteCmdOptions = remoteCmdOptions;
		this.atCommand = atCommand;
		if (null != parameterValueArray)
			dataArray = ByteBuffer.wrap(parameterValueArray);
		else
			dataArray = ByteBuffer.wrap(new byte[0]);
		message = generateMessage();
	}

	/**
	 * @param payload
	 *            The complete content of the message (without frame start delimiter, length and checksum).
	 * @throws WrongFormatException
	 */
	public RemoteAtCommand(byte[] payload) throws WrongFormatException {
		ByteBuffer bb = ByteBuffer.wrap(payload);
		if (bb.get() != frameType) {
			throw new WrongFormatException();
		}
		frameId = bb.get();
		address64Bit = bb.getLong();
		address16Bit = bb.getShort();
		remoteCmdOptions = bb.get();
		atCommand = bb.getShort();
		byte[] temp = new byte[bb.capacity() - 15];
		bb.get(temp, 0, temp.length);
		dataArray = ByteBuffer.wrap(temp);
		message = generateMessage();
	}

	public long getAddress64Bit() {
		return address64Bit;
	}

	public void setAddress64Bit(long address64Bit) {
		this.address64Bit = address64Bit;
		message.position(2);
		message.putLong(address64Bit);
	}

	public short getAddress16Bit() {
		return address16Bit;
	}

	public void setAddress16Bit(short address16Bit) {
		this.address16Bit = address16Bit;
		message.position(10);
		message.putShort(address16Bit);
	}

	public byte getRemoteCmdOptions() {
		return remoteCmdOptions;
	}

	public void setRemoteCmdOptions(byte remoteCmdOptions) {
		this.remoteCmdOptions = remoteCmdOptions;
		message.position(12);
		message.put(remoteCmdOptions);
	}

	private ByteBuffer generateMessage() {
		ByteBuffer bb = ByteBuffer.allocate(15 + dataArray.limit());
		bb.put(frameType);
		bb.put(frameId);
		bb.putLong(address64Bit);
		bb.putShort(address16Bit);
		bb.put(remoteCmdOptions);
		bb.putShort(atCommand);
		bb.put(dataArray.array());
		return bb;
	}

	@Override
	void setAtCommand(short atCommand) {
		this.atCommand = atCommand;
		message.position(13);
		message.putShort(atCommand);
	}

	@Override
	void setDataArray(byte[] dataArray) {
		this.dataArray = ByteBuffer.wrap(dataArray);
		message = generateMessage(); // Because the size could have changed
	}
}
