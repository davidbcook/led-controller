package led_control_classes;

import java.util.List;

import devices.pixelpusher.Strip;
import registry.DeviceRegistry;

/* 
 *  This class can be used to turn off all the LEDs in a strip.
 */

public class OffThread extends Thread {

	private TestObserver testObserver;
	private DeviceRegistry registry;
	
	public OffThread(TestObserver observer, DeviceRegistry registry) {
		this.testObserver = observer;
		this.registry = registry;
	}
	
	public void run() {
		int run_count = 0;
		
		// This is run 500 times to make sure the LEDs turn off. Sometimes the PixelPusher doesn't receive a message
		while (run_count < 500) {
			if (testObserver.hasStrips) {
				registry.startPushing();
				registry.setAutoThrottle(true);
				registry.setAntiLog(true);

				List<Strip> strips = registry.getStrips();

				if (strips.size() > 0) {
					for(Strip strip : strips) {
						for (int stripx = 0; stripx < strip.getLength(); stripx++) {
							strip.setPixel(0, stripx);
						}
					}
				}
			}
			run_count += 1;
		}
	}
}
