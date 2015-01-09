package led_control_classes;

import java.io.File;

import registry.*;

/*
 *  This is the main class that controls the program.
 */ 

public class LEDControlClasses {

	private static TestObserver observer;
	static DeviceRegistry registry;
	static StripOne stripOne;
	static StripTwo stripTwo;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// This code sets up the objects that look for and communicate with PixelPushers
		registry = new DeviceRegistry();
		observer = new TestObserver();
		registry.addObserver(observer);

		// We pass in the location of the file where the results of the bamboo tests are written to.
		File result_file = new File(args[0]);

		// Each strip has its own thread that controls what data is displayed on the strips
		// The first strip is used to display the results of our Bamboo builds
		stripOne = new StripOne(observer, registry, result_file);
		stripOne.start();

		// The second strip is used to send pulses in response to events
		stripTwo = new StripTwo(observer, registry);
		stripTwo.start();

	}
}
