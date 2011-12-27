import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;

import processing.core.PApplet;
import processing.core.PFont;
import processing.video.MovieMaker;
import processing.xml.XMLElement;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterVis extends PApplet {

	private Twitter twitter;
	private List<Status> statuses;
	private static final String consumer_key = "sDa99LdJYCdTvo4kKj34bg";
	private static final String consumer_secret = "qJCLH8GFigTTKlrg13wQokL3XTWHkZvXbxCEK2rj74";
	private static final String accessToken = "16739856-51DM9962nElCXSunewW5CufpkcE4c1kPv4BG82Bwy";
	private static final String accessSecret = "MmqD8yCac4xdGhFJbK4OFJYMMegShoNAW8OHtVBeU";
	private static final int START_TWEETS_Y = 200;
	private static final int TOTAL_TWEETS = 31;
	private AppConfig config;
	private XMLElement xml;
	private ArrayList<AliTweet> tweets = new ArrayList<AliTweet>();
	private boolean displayTweets = false;
	private PFont font;
	private int counter = 0;
	private MovieMaker mm;
	private boolean recording = false;
	private int currHeight = 0;
	private String currYear = "";
	private String currMonth = "";
	private String currDay;
	private int monthPos = 0;
	private int tweetYPos = 0;
	private int showCount = 0;

	public void setup() {
		frameRate(100);
		background(30);
		size(1300, 768);
		font = loadFont("PFRondaSeven-12.vlw");
		textFont(font, 14);

		// text("parsing started....." + hour() + ":" + minute() + ":" +
		// second());
		xml = new XMLElement(this, "alidrongo.xml");
		XMLElement[] statuses = xml.getChildren();
		// println("total elements:" + statuses.length);
		for (int i = 0; i < statuses.length; i++) {
			AliTweet tweet = new AliTweet();
			XMLElement text = statuses[i].getChild("text");
			XMLElement created_at = statuses[i].getChild("created_at");
			tweet.status = text.getContent();
			tweet.createdAt = created_at.getContent();
			tweets.add(tweet);
		}
		counter = tweets.size() - 1;
		mm = new MovieMaker(this, 1300, 768, "mymovie" + day() + "_" + hour()
				+ "_" + minute() + "_" + second() + ".mov", 30,
				MovieMaker.JPEG, MovieMaker.MEDIUM);
		print("parsing complete:" + hour() + ":" + minute() + ":" + second());
		showTweets();
		recording = true;
	}

	private void showTweets() {
		// TODO Auto-generated method stub
		displayTweets = true;
	}

	private void setupTwitter() {

		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(consumer_key);
		cb.setOAuthConsumerSecret(consumer_secret);
		//
		// RequestToken requestToken;
		// requestToken = twitter.getOAuthRequestToken();
		cb.setOAuthAccessToken(accessToken);
		cb.setOAuthAccessTokenSecret(accessSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();

		try {
			statuses = twitter.getHomeTimeline();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Query query = new Query("cats");
			query.setRpp(100);
			QueryResult result;
			result = twitter.search(query);
			tweets = (ArrayList) result.getTweets();
			for (int i = 0; i < tweets.size(); i++) {
				Tweet t = (Tweet) tweets.get(i);
				String user = t.getFromUser();
				String msg = t.getText();
				Date d = t.getCreatedAt();
				// println("Tweet by " + user + " at " + d + ": " + msg);
			}
			;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// println("statuses" + statuses.toString());

	}

	// fill the screen then scroll up
	public void draw() {

		// if (displayTweets) {
		// println("counter:" + counter);
		// for (int i = counter; i < counter+50; i++) {

		if (counter >= 0) {
			AliTweet t = tweets.get(counter);
			// get the year
			String created = t.createdAt;
			String[] list = t.createdAt.split(" ");
			String year = list[list.length - 1];
			String month = list[1];
			String day = list[0];

			if (year != currYear) {
				currYear = year;
			}
			if (!currMonth.contains(month)) {
				// write name of last month

				currMonth = month;

				pushMatrix();
				rotate(radians(90));
				fill(255);
				textSize(12);
				text(currMonth, 0, 0 - ((monthPos * 20) + 15), 1);
				popMatrix();

				tweetYPos = 0;
				// println("mpnth pos+");

				if (currMonth.contains("Jan")) {
					pushMatrix();
					rotate(radians(90));
					fill(100);
					textSize(18);
					text(currYear, 50, 0 - ((monthPos * 20) + 8), 1);
					popMatrix();
				}

				monthPos++;
			}
			if (day != currDay) {
				fill(30);
				noStroke();
				rect(width - 320, 0, 320, 50);
				currDay = day;
				fill(255);
				textSize(40);
				text(currMonth + " " + currYear, width - 215, 40);

			}

			float ratio = mouseY / height;
			float realHeight = (tweets.size() * 18) * ratio;
			textSize(16);
			if (counter == tweets.size() - 1) {
				fill(255);
			} else {
				fill(random(255), random(255), random(255));
			}

			float yPos = ((currHeight % TOTAL_TWEETS) * 18) + START_TWEETS_Y
					+ 18;

			text(t.status, 25, yPos);
			// text("hi", 30,yPos);
			// println("showing:" + t.status + "  ypos:" + yPos +
			// "  cuurheight:"
			// + currHeight);

			if (currHeight % TOTAL_TWEETS == 0 && currHeight > 0) {
				fill(30);
				noStroke();
				rect(0, START_TWEETS_Y, width, height - START_TWEETS_Y);

			}

			// draw month

			

			// only increment after the first tweet has shown 100 times
			if (counter == tweets.size() - 1 && showCount < 100) {
				showCount++;
			} else {
				drawTweet();
				currHeight++;
				counter--;
			}

			colorMode(RGB, 255);
			stroke(255);
			line(0, START_TWEETS_Y, width, START_TWEETS_Y);

		}

		try {
			if (recording) {
				mm.addFrame();
			}
		} catch (Exception e) {
			// println("cant record!");
		}

		//
		// }
		// println("displayed all--");

	}

	private void drawTweet() {

		colorMode(HSB, 100);

		// fill(tweetYPos, 100, 100);
		stroke(tweetYPos * .5f, 100, 100);

		ellipse((monthPos * 20) + 9, tweetYPos * .5f, 1, 1);
		tweetYPos++;

	}

	public void mousePressed() {
		println("mouse pressed");
	}

	public void mouseDragged() {
		println("mouse dragged");
	}

	public void mouseReleased() {
		println("mouse released");
	}

	public void keyPressed() {
		println("key pressed:" + key);
		if (key == 't') {
			displayTweets = !displayTweets;
		}
		if (key == 'm') {
			println("key is m");
			if (recording) {
				println("we are recording");
				mm.finish();
				println("movie record compolete");
				recording = false;
			} else {
				println("we are not recording");
				mm = new MovieMaker(this, 1300, 768, "mymovie" + day() + "_"
						+ hour() + "_" + minute() + "_" + second() + ".mov",
						30, MovieMaker.JPEG, MovieMaker.LOW);
				println("movie making....");
				recording = true;
			}
			// println(treeness);
		}
	}

}
