## This package contains the code for actually controlling the LEDs

We have 2 LED strips, so certain classes are designed to work with a specific strip. Each strip is controlled by its own thread which kicks off other threads.
This allows you to display multiple kinds of information on one strip at different times.

Written by David Cook for Jut.

### Classes

* LEDControlClasses.java - Contains the main method and kicks off the two threads. You must specify the location of the build results as an argument to the program.
* StripOne.java - Reads the result of the build results file, and kicks off the BuildStatusDisplay thread every minute.
* BuildStatusDisplay.java - If the build result is successful, then it turns the LEDs green; if it's red, the LEDs turn red. This thread runs for 1 minute. There's logic in the thread to light the LEDs one at a time; this is useful if you're using the strip to display multiple kinds of info. You can also tell the strip to fade the LEDs between bright and dark green/red by uncommenting the lines at the bottom of the file.
* ActiveUserRateDisplay.java - Displays the percentage of total users who are active. Behaves like a gauge.

* StripTwo.java - Kicks off the EventPulse thread every minute to send pulses up the strip in response to events.
* EventPulse.java - Extracts the last minute of specific events from Keen, parses them into Event objects, then triggers pulses for every event.
