package led_control_classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import registry.DeviceRegistry;

public class StripOne extends Thread{

	private String buildResults;
	private File result_file;
	private TestObserver testObserver;
	private DeviceRegistry registry;
	static BuildStatusDisplay buildStatusThread;

	public StripOne(TestObserver observer, DeviceRegistry registry, File result_file) {
		this.testObserver = observer;
		this.registry = registry;
		this.result_file = result_file;
	}

	public void run() {

		// This thread will run forever
		while (true) {
			
			// Get the status of the builds on the first draw and every 60 seconds after that
			try {
				BufferedReader reader = new BufferedReader(new FileReader(result_file));
				buildResults = reader.readLine();
				reader.close();
			} catch (FileNotFoundException e) {
				System.out.println(e);
			} catch (IOException i) {
				System.out.println(i);
			}
			
			
			try {
				// Create a new thread to color the LEDs based on the results from the result_file
				System.out.println("starting build status thread");
				buildStatusThread = new BuildStatusDisplay(testObserver, registry, buildResults);
				buildStatusThread.start();

				// Slow the thread down so that it executes once a minute
				sleep(60000);
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			};
		}
	}
}
