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
	private ArrayList<MonthsTweets> months = new ArrayList<MonthsTweets>();
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
	private int circleCounter = 0;
	private int rotationCount = -1;
	private int tweetCount = 0;
	

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
		
			String[] list = tweet.createdAt.split(" ");
			String year = list[list.length - 1];
			String month = list[1];
			String day = list[0];
			tweet.year = year;
			tweet.day = day;
			tweet.month = month;
			tweets.add(tweet);
		}
		counter = tweets.size() - 1;
		mm = new MovieMaker(this, 1300, 768, "mymovie" + day() + "_" + hour()
				+ "_" + minute() + "_" + second() + ".mov", 30,
				MovieMaker.JPEG, MovieMaker.MEDIUM);
		print("parsing complete:" + hour() + ":" + minute() + ":" + second());
		//showTweets();
		
		//sortTweets();
		
		recording = false;
	}

	private void sortTweets() {
	
		String currMonth = "";
		MonthsTweets month = new MonthsTweets();
		months = new ArrayList<MonthsTweets>();
		for (int i = tweets.size()-1; i>=0; i--) {
			AliTweet t = tweets.get(i);
			if (t.month!=currMonth){
				month = new MonthsTweets();
				month.monthName = t.month;
			}
			month.tweets.add(t);
			months.add(month);
			
		}
		
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
			println("curr month:"+currMonth+"  month:"+month+" counter:"+counter);
			if (!currMonth.contains(month)) {
				// write name of last month
				currMonth = month;
				println("rot up:"+rotationCount+"  tweetc:"+tweetCount);
				rotationCount++;
				tweetCount = 0;
				
				
				println(rotationCount);

				if (currMonth.contains("Jan")) {
					pushMatrix();
					rotate(radians(90));
					fill(100);
					textSize(18);
				//	text(currYear, 50, 0 - ((monthPos * 20) + 8), 1);
					popMatrix();
				}

				monthPos++;
			}
			
			//draw the tweet
			
			 pushMatrix();
			    translate(width/2, height/2);  
			    rotate(radians((rotationCount*10)+180));
			    pushMatrix();
				    // rotate(TWO_PI*.25);
				    translate(0,100+(tweetCount));
				    pushMatrix();
					    rotate(TWO_PI*.25f);
					//    text(currMonth, 30, 0);
					    colorMode(HSB, 100);
						fill(tweetCount, 100, 100);
						noStroke();
					    ellipse(0,0,3,3);
				    popMatrix();
				    //translate(0,100);
				popMatrix();
			    fill(255);
			 popMatrix();
			
			 tweetCount++;
			 counter--;
	
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

	private void drawCircleTweets() {
		
		String currentMonth = "Jan";
		for (int i = tweets.size()-1; i >=0; i--) {
			
			AliTweet tweet = tweets.get(i);
		//	println("month:"+tweet.month+"  cuurmonth:"+currentMonth);
			if (!tweet.month.contains(currentMonth)) {
				currentMonth = tweet.month;
				tweetCount = 0;
				rotationCount++;
				println("rot:"+rotationCount);
			}
			drawTweet();
			tweetCount++;
		}
		
	}

	private void drawTweet() {
	//	println("drawt");
		translate(width*.5f, height*.5f);
		pushMatrix();
		
		translate(0, -100);
		rotate(radians(rotationCount));
		
		fill(random(255),random(255),random(255));
		ellipse(0,0,5,5);
		
		popMatrix();
		
		
	}

	private AliTweet getTweet(int i) {
		int theCount = 0;
		for (int j = 0; j < months.size(); j++) {
			MonthsTweets monthsTweets = months.get(j);
			for (int k = 0; k < monthsTweets.tweets.size(); k++) {
				AliTweet tweet = monthsTweets.tweets.get(k);
				if (theCount==i){
					return tweet;
				}
				theCount++;
			}
		}
		
		return null;
	}
/*
	private void drawTweet() {

		colorMode(HSB, 100);

		// fill(tweetYPos, 100, 100);
		stroke(tweetYPos * .5f, 100, 100);

		ellipse((monthPos * 20) + 9, tweetYPos * .5f, 1, 1);
		tweetYPos++;

	}*/

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
				println("movie record complete");
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
