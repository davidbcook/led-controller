package led_control_classes;

import registry.DeviceRegistry;

public class StripTwo extends Thread{

	private TestObserver testObserver;
	private DeviceRegistry registry;
	static EventReplayer eventReplayerThread;

	public StripTwo(TestObserver observer, DeviceRegistry registry) {
		this.testObserver = observer;
		this.registry = registry;

	}

	public void run() {

		while (true) {

			try {
				eventReplayerThread = new EventReplayer(testObserver, registry);
				eventReplayerThread.start();


				// Slow the thread down so that it executes once a minute
				sleep(60000);
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			};
		}
	}
}
