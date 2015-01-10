package led_control_classes;

/*
 *  This class retrieves events from Keen and parses them into Event objects.
 *  Then it sends a white pulse up the LED strip for every successful event.
 *  Every failed event triggers a red pulse.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import registry.DeviceRegistry;
import devices.pixelpusher.Strip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventReplayer extends Thread {

	private TestObserver testObserver;
	private DeviceRegistry registry;
	private LEDPulse pulseThread;
	private URL event_one;
	private URL event_two;
	private URL event_three;

	public EventReplayer(TestObserver observer, DeviceRegistry registry) {
		this.testObserver = observer;
		this.registry = registry;
	}

	public void run() {

		ArrayList<Event<Calendar,Boolean,String,String>> eventList = new ArrayList<Event<Calendar,Boolean,String,String>>();
		ArrayList<URL> urlList = new ArrayList<URL>();
		ArrayList<String> responseList = new ArrayList<String>();
		// since keen only lets you retrieve events for one event collection at a time, we have to create different URLs for every event
		try {
			event_one = new URL("https://api.keen.io/3.0/projects/<REST OF YOUR KEEN URL>");
			event_two = new URL("https://api.keen.io/3.0/projects/<REST OF YOUR OTHER KEEN URL>");
			event_three = new URL("https://api.keen.io/3.0/projects/<REST OF YOUR OTHER OTHER KEEN URL>");
			urlList.add(event_one);
			urlList.add(event_two);
			urlList.add(event_three);
		} catch (MalformedURLException e1) {
			System.out.println("Your URL is malformed");
			e1.printStackTrace();
		}

		// for each event collection (event name) we need to extract the results and write them to a file
		try {
			for (int u = 0; u < urlList.size(); u++) {
				responseList.add(getRunData(urlList.get(u)));
			}

			for (int r = 0; r < responseList.size(); r++) {
				JSONArray runs = new JSONObject(responseList.get(r).toString()).getJSONArray("result");
				System.out.println("length = " + runs.length());
				System.out.println("result = " + runs);
				// For each event, extract the event, timestamp, user_id, and outcome to create an Event object then add each object to eventList
				for (int x = 0; x < runs.length(); x++) {
					Calendar timestamp = new GregorianCalendar();
					String timestring = ((JSONObject) runs.getJSONObject(x).get("keen")).getString("timestamp");
					// These three fields can represent whatever you want. 
					// You'll have to extract the necessary data from the Keen data extraction to populate them
					String user_id = null;
					String event = null;
					String success = true;

					// Parse the timestring into a Calendar object.
					System.out.println("timestring " + timestring);
					String date = timestring.split("T")[0];
					String time = timestring.split("T")[1];
					int year = Integer.parseInt(date.split("-")[0]);
					int month = Integer.parseInt(date.split("-")[1]) - 1; // subtract 1 from month because January is represented by 0 in Java Calendar objects
					int day = Integer.parseInt(date.split("-")[2]);
					int hour = Integer.parseInt(time.split(":")[0]) - 8; // subtract 8 from hour to adjust for timezones and daylight savings
					int minute = Integer.parseInt(time.split(":")[1]) + 1; // Since this event already happened, we need to add 1 to minute so we can replay that minute
					String secondAndMilli = time.split(":")[2];
					secondAndMilli = secondAndMilli.replace(".", "-"); //i for some reason I can't split on .
					int second = Integer.parseInt(secondAndMilli.split("-")[0]);
					int milliseconds = Integer.parseInt(secondAndMilli.split("-")[1].substring(0, 2));
					timestamp.set(year, month, day, hour, minute, second);
					timestamp.set(Calendar.MILLISECOND, milliseconds);

					eventList.add(new Event<Calendar, Boolean, String, String>(timestamp, success, user_id, event));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// sort the arraylist so that we fire the pulses in order and don't skip over any of them
		Collections.sort(eventList);

		int pulseCount = 0;
		forLoop: for (int x = 0; x < eventList.size(); x++) {
			try {
				// If there are strips, get the pulse strip and start coloring the LEDs 
				if (testObserver.hasStrips) {
					registry.startPushing();
					registry.setAutoThrottle(true);
					registry.setAntiLog(true);

					List<Strip> strips = registry.getStrips();

					Strip pulseStrip = strips.get(0);
					long eventTime = eventList.get(x).getTimestring().getTimeInMillis();

					System.out.println("pulses index: " + x + " pulses sent: " + pulseCount + " event time: " + eventTime + " system time: " + System.currentTimeMillis()); 

					// If the adjusted time of the event hasn't happened yet, then we haven't sent a pulse yet
					if (eventTime > System.currentTimeMillis()) {
						// While the time of the event is greater than the current time, wait and keep checking to see if the time of the event ~= the current time
						while (eventTime > System.currentTimeMillis()) {
							// If the eventTime is within 10 milliseconds of system time, that's close enough, so send the pulse 
							// As soon as we start the pulse thread, we move on to the next event in the list to avoid sending multiple pulses for the same event
							if (eventTime - System.currentTimeMillis() <= 10) {
								// Look at properties of the event object to determine the color and speed of each pulse
								if (eventList.get(x).getEvent() == "<EVENT NAME>" && eventList.get(x).getOutcome()) {
									System.out.println("sending white pulse");
									pulseThread = new LEDPulse(pulseStrip, 0xffffff, 10);
									pulseThread.start();
									pulseCount++;
									continue forLoop;
								} else if (eventList.get(x).getEvent() == "<EVENT NAME>" && !eventList.get(x).getOutcome()) {
									System.out.println("sending red pulse");
									pulseThread = new LEDPulse(pulseStrip, 0xff0000, 10);
									pulseThread.start();
									pulseCount++;
									continue forLoop;
								}
							}
						} 
					} 
					// if the time of the event is less than the current time, then we should've already sent the pulse, so move onto the next event in the list
					else {
						System.out.println("already sent pulse");
						continue;
					}
				}

				// Slow the thread down
				sleep(20);
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			};
		}
		// We've gone through every event in the list
		System.out.println("pulse thread done sending pulses");
		interrupt();
	}

	// Establishes a connection to the URL provided and returns the response as a string.
	private String getRunData(URL url) {
		StringBuffer response = new StringBuffer();
		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.toString();
	}
}
