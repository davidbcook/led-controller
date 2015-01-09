## This package contains the code for actually controlling the LEDs

We have 2 LED strips, so certain classes are designed to work with a specific strip. Each strip is controlled by its own thread which kicks off other threads.
This allows you to display multiple kinds of information on one strip at different times.

### Classes

* LEDControlClasses.java - Contains the main method and kicks off the two threads. You must specify the location of the build results as an argument to the program.
* StripOne.java - Reads the result of the build results file, and kicks off the BuildStatusDisplay thread every minute.
* BuildStatusDisplay.java - If the build result is successful, then it turns the LEDs green; if it's red, the LEDs turn red. It runs for a minute then quits. There's logic in the thread to light the LEDs one at a time if you want the color change to be animated.