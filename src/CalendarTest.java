import java.net.URL;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;

public class CalendarTest {

	public static void main(String[] args) {
		try{
			CalendarService myService = new CalendarService("exampleCo-exampleApp-1.0");
			//put your google email address and password in these fields
			myService.setUserCredentials("heychrisaikens@gmail.com", "");

			URL feedUrl = new URL("http://www.google.com/calendar/feeds/default/allcalendars/full");
			CalendarFeed resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);

			System.out.println("Your calendars:");
			System.out.println();

			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEntry entry = resultFeed.getEntries().get(i);
				System.out.println("\t" + entry.getTitle().getPlainText());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
}