package led_control_classes;

/*
 *  This class sends a pulse of the desired color up the LED strip.
 *  Each pulse is its own thread so multiple pulses can move up the strip at once.
 */

import devices.pixelpusher.Strip;

public class LEDPulse extends Thread {

	private Strip strip;
	private int color;
	private int speed;

	public LEDPulse(Strip strip, int color, int speed) {
		this.strip = strip;
		this.color = color;
		this.speed = speed;
	}

	public void run() {
		try {
			for (int stripx = 0; stripx < strip.getLength(); stripx++) {
				strip.setPixel(color, stripx);
				if (stripx > 2) { // Once the pulse is 3 LEDs long, start turning the LEDs off behind the pulse
					strip.setPixel(0, stripx - 3);
				}
				sleep(speed);
			}
			// After the pulse has gone up the whole strip, make sure the last LEDs are off.
			for (int stripx = strip.getLength() - 4; stripx < strip.getLength(); stripx++) {
				strip.setPixel(0, stripx);
			}
		} catch (InterruptedException e) {
			System.err.println("pulse interrupted");
		};

	}
}
