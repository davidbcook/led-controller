## This package contains the code for actually controlling the LEDs

We have 2 LED strips, so certain classes are designed to work with a specific strip. Each strip is controlled by its own thread which kicks off other threads.
This allows you to display multiple kinds of information on one strip at different times.

Written by David Cook for Jut.

### Classes

* LEDControlClasses.java - Contains the main method and kicks off the two threads. You must specify the location of the file that get_build_status.py writes the build results to as an argument to the program.

* StripOne.java - Reads the result of the build results file, and kicks off the BuildStatusDisplay thread every minute. Extends Thread.
* BuildStatusDisplay.java - If the build result is successful, then it turns the LEDs green; if it's red, the LEDs turn red. This thread runs for 1 minute. There's logic in the thread to light the LEDs one at a time; this is useful if you're using the strip to display multiple kinds of info. You can also tell the strip to fade the LEDs between bright and dark green/red by uncommenting the lines at the bottom of the file. Extends Thread.
* get_build_status.py - Writes 'Successful' to a file if all of the specified builds have passed and failed if at least one of them did not pass. You should run this script every minute or two on your computer using something like cron.
* ActiveUserRateDisplay.java - Displays the percentage of total users who are active. Behaves like a thermometer/gauge. Extends Thread.

* StripTwo.java - Kicks off the EventReplayer thread every minute to send pulses up the strip in response to events. Extends Thread.
* EventReplayer.java - Extracts the last minute of specific events from Keen, parses them into Event objects, then triggers pulses for every event. Extends Thread.
* Event.java - Represents an event. Has fields for an event's time, name, status, and userID.
* LEDPulse.java - Sends a pulse down a strip based on the speed and color passed into it. Extends Thread.
