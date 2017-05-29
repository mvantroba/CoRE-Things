package de.thk.ct.endpoint.pi4j.osgi.resources;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thk.ct.endpoint.device.osgi.resources.SensorType;

/**
 * Implementation of DS18B20 temperature sensor which reads temperature value
 * from the directory "/sys/bus/w1/devices/".
 * 
 * @author Martin Vantroba
 *
 */
public class DS18B20Resource extends SensorResource {

	private static final Logger LOGGER = Logger.getLogger(DS18B20Resource.class.getName());

	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	private static final Long READ_PERIOD = 1l; // 1 second

	private static final String W1_DEVICES_PATH = "/sys/bus/w1/devices";
	private static final String W1_SLAVE = "w1_slave";

	private String filename;
	private ScheduledFuture<?> scheduledFuture;

	private double value = -999.0;
	private double lastListenerValue = -999;
	private double threshold = 0.1;

	/**
	 * Constructs a {@link DS18B20Resource} which will read the temperature from
	 * the given file. Threshold specifies at which rate the listeners will be
	 * informed about value changes.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param filename
	 *            file from which temperature value will be obtained
	 * @param threshold
	 *            value threshold for listeners
	 */
	public DS18B20Resource(String name, DeviceResourceListener listener, String filename, double threshold) {
		super(name, listener, SensorType.DS18B20);
		this.filename = filename;
		if (threshold > 0) {
			this.threshold = threshold;
		}
	}

	/**
	 * Constructs a {@link DS18B20Resource} which will read the temperature from
	 * the given file. Default threshold will be used to notify listeners about
	 * value changes.
	 * 
	 * @param name
	 *            resource name
	 * @param listener
	 *            resource listener
	 * @param filename
	 *            file from which temperature value will be obtained
	 */
	public DS18B20Resource(String name, DeviceResourceListener listener, String filename) {
		this(name, listener, filename, -1.0);
	}

	@Override
	public String toString() {
		return "DS18B20Resource [filename=" + filename + ", threshold=" + threshold + ", sensorType=" + sensorType
				+ ", name=" + name + "]";
	}

	@Override
	public void init() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
		scheduledFuture = SCHEDULER.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				value = readTemperature();
				if (Math.abs(value - lastListenerValue) > threshold) {
					onChanged(String.valueOf(value));
					lastListenerValue = value;
				}
			}
		}, 0L, READ_PERIOD, TimeUnit.SECONDS);
	}

	@Override
	public void destroy() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
	}

	@Override
	public String getValue() {
		return String.valueOf(value);
	}

	private Path getFullPathToDevice() {
		return FileSystems.getDefault().getPath(String.format("%s/%s/%s", W1_DEVICES_PATH, filename, W1_SLAVE));
	}

	private double readTemperature() {
		Path path = getFullPathToDevice();
		int beginIndex;
		int endIndex;
		String tempIdentifier = "t=";
		double value = -999;
		try {
			for (String line : Files.readAllLines(path, Charset.defaultCharset())) {
				if (line.contains(tempIdentifier)) {
					beginIndex = line.indexOf(tempIdentifier) + 2;
					endIndex = line.length();
					value = Double.parseDouble(line.substring(beginIndex, endIndex)) / 1000;
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Cannot read temperature from file. {0}", new Object[] { e.getMessage() });
		}
		return value;
	}
}
