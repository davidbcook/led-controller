package led_control_classes;

import java.util.List;

import registry.DeviceRegistry;
import devices.pixelpusher.Strip;

public class BuildStatusDisplay extends Thread {

	private String results;
	private TestObserver testObserver;
	private DeviceRegistry registry;
	//private boolean ascending = true; // used if the strip fades between off and on
	private byte red_intensity = 15;
	private long startTime;
	private boolean illuminated = true; // switch to false when strip one is used for multiple things
	private Strip buildStrip;

	public BuildStatusDisplay(TestObserver observer, DeviceRegistry registry, String results) {
		this.testObserver = observer;
		this.registry = registry;
		this.results = results;
		this.startTime = System.currentTimeMillis();
	}

	public void run() {

		// Run for 60 seconds then quit
		while (System.currentTimeMillis() < startTime + 62000) {
		
			try {
				// If strips can be found on the network, get the strip and start coloring the LEDs 
				if (testObserver.hasStrips) {
					registry.startPushing();
					registry.setAutoThrottle(true);
					registry.setAntiLog(true);

					List<Strip> strips = registry.getStrips();

					if (strips.size() > 0) {
						// The build strip is the second strip in the list
						buildStrip = strips.get(1);
						byte zero = 0;
						if (results.equals("Successful")) {
							byte b = 10;
							if (!illuminated) { // If the strip isn't already illuminated, gradually light each LED on the strip
								for (int stripx = 0; stripx < buildStrip.getLength(); stripx++) {
									buildStrip.setPixelGreen(b, stripx);
									buildStrip.setPixelRed(zero, stripx);
									sleep(15);
								}
//								illuminated = !illuminated;
							} else { // If it's already lit, then keep it lit
								for (int stripx = 0; stripx < buildStrip.getLength(); stripx++) {
									buildStrip.setPixelGreen(b, stripx);
									buildStrip.setPixelRed(zero, stripx);
								}
							}
						} else {
							if (!illuminated) { // If the strip isn't already illuminated, gradually light each LED on the strip
								for (int stripx = 0; stripx < buildStrip.getLength(); stripx++) {
									buildStrip.setPixelGreen(zero, stripx);
									buildStrip.setPixelRed(red_intensity, stripx);
									sleep(15);
								}
//								illuminated = !illuminated;
							} else {
								for (int stripx = 0; stripx < buildStrip.getLength(); stripx++) {
									buildStrip.setPixelGreen(zero, stripx);
									buildStrip.setPixelRed(red_intensity, stripx);
								}
							}
						}
					}
				}

				// Change the brightness of the red to make the strip fade between off and on. Turned off for now to preserve the sanity of people next to the strip.
//				if (ascending) {
//					red_intensity++;
//				} else {
//					red_intensity--;
//				}
//				if (red_intensity == 0 || red_intensity == 15) {
//					ascending = !ascending;
//				}

				// Slow the thread down
				sleep(30);
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			};
		}
		System.out.println("build status thread ending");
		// turn off all the LEDs; uncomment the next three lines if the strip is used to display other information as well
		//for (int stripx = 0; stripx < buildStrip.getLength(); stripx++) {
		//	buildStrip.setPixel(0, stripx);
		//}
	}
}
