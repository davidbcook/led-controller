package led_control_classes;

/*
 *  This class displays the percentage of total users who have used the product in the last day.
 *  It uses the LED strip like a thermometer where visible_pixels is the number of pixels that 
 *  would be lit if 100% users are active.
 */

import java.util.List;

import registry.DeviceRegistry;
import devices.pixelpusher.Strip;

public class ActiveUserRateDisplay extends Thread {

	private double activeUserRate;
	private TestObserver testObserver;
	private DeviceRegistry registry;
	private long startTime;
	private boolean illuminated = false;
	private Strip buildStrip;

	public ActiveUserRateDisplay(TestObserver observer, DeviceRegistry registry, double activeUserRate) {
		this.testObserver = observer;
		this.registry = registry;
		this.activeUserRate = activeUserRate;
		this.startTime = System.currentTimeMillis();
	}

	public void run() {

		// Run the thread for 60 seconds then quit
		while (System.currentTimeMillis() < startTime + 60000) {
			try {
				// Get the strip and start pushing data to it
				if (testObserver.hasStrips) {
					registry.startPushing();
					registry.setAutoThrottle(true);
					registry.setAntiLog(true);

					List<Strip> strips = registry.getStrips();
					
					// If not all of the LEDs on the stip are visible
					int visible_pixels = 123;
					long lit_pixels = Math.round(visible_pixels * activeUserRate);

					if (strips.size() > 0) {
						buildStrip = strips.get(1);
						if (!illuminated) {
							// Slow the thread down so that it lights LEDs one by one the first time the thread is run
							for (int stripx = 0; stripx < lit_pixels; stripx++) {
								buildStrip.setPixel(0x6f2d00, stripx);
								sleep(15);
							}
							illuminated = !illuminated;
						} else {
							// Keep the LEDs lit
							for (int stripx = 0; stripx < lit_pixels; stripx++) {
								buildStrip.setPixel(0x6f2d00, stripx);
							}
						}
					}
				}

				// Slow the thread down
				sleep(30);
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			};
		}
		System.out.println("active user thread ending");
		// turn off all the LEDs
		for (int stripx = 0; stripx < buildStrip.getLength(); stripx++) {
			buildStrip.setPixel(0, stripx);
		}
	}
}
