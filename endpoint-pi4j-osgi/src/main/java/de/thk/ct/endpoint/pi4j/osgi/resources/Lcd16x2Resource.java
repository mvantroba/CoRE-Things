package de.thk.ct.endpoint.pi4j.osgi.resources;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.i2c.I2CDevice;

import de.thk.ct.base.ActuatorType;

/**
 * The type of {@link ActuatorResource} that represents an LCD display with 16 x
 * 2 bytes. It uses I2C to control the device.
 * <p>
 * It is based on this python code: <a href=
 * "http://osoyoo.com/driver/i2clcdb.py">http://osoyoo.com/driver/i2clcdb.py</a>
 * 
 * @author Martin Vantroba
 *
 */
public class Lcd16x2Resource extends ActuatorResource {

	private static final Logger LOGGER = Logger.getLogger(Lcd16x2Resource.class.getName());

	private static final byte ENABLE = 0b00000100; // enable bit

	private static final int LINE_LENGTH = 16;
	private static final byte LINE_1_ADDRESS = (byte) 0x80; // RAM address
	private static final byte LINE_2_ADDRESS = (byte) 0xC0; // RAM address

	private static final byte MODE_CMD = 0; // sending command
	private static final byte MODE_DATA = 1; // sending data

	// On: 0x08
	// Off: 0x00
	private static final byte LCD_BACKLIGHT = 0x08;

	// Timing constants (nanoseconds).
	private static final int E_PULSE = 500;
	private static final int E_DELAY = 500;

	private static final String LINE_SEPARATOR = ";";

	private I2CDevice device;

	private String valueLine1 = "";
	private String valueLine2 = "";

	/**
	 * Constructs a {@link Lcd16x2Resource} which is represented by the given
	 * {@link I2CDevice}.
	 * 
	 * @param name
	 *            actuator name
	 * @param listener
	 *            actuator listener
	 * @param device
	 *            I2C device
	 */
	public Lcd16x2Resource(String name, DeviceResourceListener listener, I2CDevice device) {
		super(name, listener, ActuatorType.LCD16X2);
		this.device = device;
	}

	@Override
	public String toString() {
		String deviceAddress = String.format("0x%02X", device.getAddress());
		return "Lcd16x2Resource [deviceAddress=" + deviceAddress + ", actuatorType=" + actuatorType + ", name=" + name
				+ "]";
	}

	@Override
	public void toggle() {
		clearDisplay();
	}

	@Override
	public void setValue(String value) throws IllegalArgumentException {
		String[] lines = value.split(LINE_SEPARATOR);
		clearDisplay();
		try {
			writeString(lines[0], LINE_1_ADDRESS);
			valueLine1 = lines[0];
			if (lines.length > 1) {
				writeString(lines[1], LINE_2_ADDRESS);
				valueLine2 = lines[1];
			}
			onChanged(getValue());
		} catch (IOException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public void init() {
		try {
			initDisplay();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public void destroy() {
		clearDisplay();
	}

	@Override
	public String getValue() {
		return String.format("%s%s%s", valueLine1, LINE_SEPARATOR, valueLine2);
	}

	private void initDisplay() throws IOException, InterruptedException {
		writeByte((byte) 0x33, MODE_CMD); // 110011 initialize
		writeByte((byte) 0x32, MODE_CMD); // 110010 initialize
		writeByte((byte) 0x06, MODE_CMD); // 000110 cursor move direction
		writeByte((byte) 0x0C, MODE_CMD); // 001100 display on, cursor off,
											// blink off
		writeByte((byte) 0x28, MODE_CMD); // 101000 data length, number of
											// lines, font size
		writeByte((byte) 0x01, MODE_CMD); // 000001 clear display
		Thread.sleep(E_DELAY);
	}

	private void writeByte(byte bits, byte mode) throws IOException, InterruptedException {
		byte bitsHigh = (byte) (mode | (bits & 0xF0) | LCD_BACKLIGHT);
		byte bitsLow = (byte) (mode | ((bits << 4) & 0xF0) | LCD_BACKLIGHT);

		// High bits.
		device.write(bitsHigh);
		toggleEnable(bitsHigh);

		// Low bits.
		device.write(bitsLow);
		toggleEnable(bitsLow);
	}

	private void writeString(String message, byte line) throws IOException, InterruptedException {
		byte[] output = message.getBytes();
		writeByte(line, MODE_CMD);
		int length = output.length > LINE_LENGTH ? LINE_LENGTH : output.length;
		// Loop over all characters and send them to display.
		for (int i = 0; i < length; i++) {
			writeByte(output[i], MODE_DATA);
		}
	}

	private void toggleEnable(byte bits) throws IOException, InterruptedException {
		Thread.sleep(0L, E_DELAY);
		device.write((byte) (bits | ENABLE));
		Thread.sleep(0L, E_PULSE);
		device.write((byte) (bits | ~ENABLE));
		Thread.sleep(0L, E_DELAY);
	}

	private void clearDisplay() {
		try {
			writeByte((byte) 0x01, MODE_CMD);
			valueLine1 = "";
			valueLine2 = "";
		} catch (IOException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
}
