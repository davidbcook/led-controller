package led_control_classes;

import java.util.Calendar;

/* 
 * This class represents an event. It has the name, time, outcome and user_id of the person who trigger the event.
 * You can easily retrofit this class to represent any event you want.
 */

public class Event<C extends Calendar, B, S1, S2> implements Comparable<Event<C,B,S1, S2>>{
	private C timestring;
	private B outcome;
	private S1 user_id;
	private S2 event_name;
	private String company = "unkown";
	private String[] employees = {"<LIST OF EMPLOYEE IDs>"};

	public Event(C timestring, B outcome, S1 user_id, S2 event_name) {
		super();
		this.timestring = timestring;
		this.outcome = outcome;
		this.user_id = user_id;
		this.event_name = event_name;
		// If the user_id is in the list of employees, then set the company to Jut (we use this to distinguish Jut employees from all other users)
		for (int i = 0; i < employees.length; i++) {
			if (user_id.equals(jutEmployees[i])) {
				this.company = "Jut";
			}
		}
	}

	public int hashCode() {
		int hashtimestring = timestring != null ? timestring.hashCode() : 0;
		int hashoutcome = outcome != null ? outcome.hashCode() : 0;

		return (hashtimestring + hashoutcome) * hashoutcome + hashtimestring;
	}

	public boolean equals(Object other) {
		if (other instanceof Event) {
			Event<?, ?, ?, ?> otherPair = (Event<?, ?, ?, ?>) other;
			return 
					((  this.timestring == otherPair.timestring ||
					  ( this.timestring != null && otherPair.timestring != null &&
					    this.timestring.equals(otherPair.timestring))) &&
					 (	this.outcome == otherPair.outcome ||
					  ( this.outcome != null && otherPair.outcome != null &&
					    this.outcome.equals(otherPair.outcome))) );
		}

		return false;
	}

	public String toString()
	{ 
		return "(" + timestring + ", " + outcome + ")"; 
	}

	public C getTimestring() {
		return timestring;
	}

	public void setTimestring(C timestring) {
		this.timestring = timestring;
	}

	public B getOutcome() {
		return outcome;
	}

	public void setOutcome(B outcome) {
		this.outcome = outcome;
	}
	
	public S1 getUserID() {
		return user_id;
	}

	public void setUserID(S1 user_id) {
		this.user_id = user_id;
	}
	
	public S2 getEvent() {
		return this.event;
	}

	public void setEvent(S2 event) {
		this.event = event;
	}
	
	public String getCompany() {
		return company;
	}

	@Override
	public int compareTo(Event<C, B, S1, S2> o) {
		return (getTimestring()).compareTo(o.getTimestring());
	}
}
