package led_control_classes;

import java.util.Observable;
import java.util.Observer;

public class TestObserver implements Observer {
	  public boolean hasStrips = false;
	  public void update(Observable registry, Object updatedDevice) {
	        System.out.println("Registry changed! test");
	        if (updatedDevice != null) {
	        	System.out.println("Device change: " + updatedDevice);
	        }
	        this.hasStrips = true;
	        System.out.println("Testobserver: " + this.hasStrips);
	  }
}
