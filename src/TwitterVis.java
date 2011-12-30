import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;
import processing.video.MovieMaker;
import processing.xml.XMLElement;
import toxi.color.TColor;
import toxi.geom.Vec2D;
import toxi.math.InterpolateStrategy;
import toxi.math.MathUtils;
import toxi.math.SigmoidInterpolation;
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
	private int startTweetsY = 200;
	private int totalTweets = 31;
	private float degreesRot = .7f;
	private int radius = 1000;
	private int startingRot = 257;
	private int startingX = -950;
	private int startingY = 430;
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
	private String currYear = "----";
	private String currMonth = "Jan";
	private String currDay = "----";
	private int monthPos = 0;
	private int tweetYPos = 0;
	private int showCount = 0;
	private int circleCounter = 0;
	private int rotationCount = 0;
	private int tweetCount = 0;

	// keepers of transition state & target
	private float transition, transTarget;

	// use a S-Curve to achieve an ease in/out effect
	InterpolateStrategy is = new SigmoidInterpolation(1);
	private int mouseMoverX;
	private float targetZoom = 3;
	private float currentZoom = 3;
	private int mouseMoverY;
	private int movee = 0;

	public void setup() {
		frameRate(30);
		background(255);
		size(1300, 768);
		font = loadFont("PFRondaSeven-12.vlw");
		smooth();
		textFont(font, 24);

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
			tweet.date = list[2];
			tweets.add(tweet);
		}
		counter = tweets.size() - 1;
		recording = false;
		if (recording) {
			mm = new MovieMaker(this, 1300, 768, "mymovie" + day() + "_"
					+ hour() + "_" + minute() + "_" + second() + ".mov", 30,
					MovieMaker.JPEG, MovieMaker.MEDIUM);
		}
		print("parsing complete:" + hour() + ":" + minute() + ":" + second());
		// showTweets();

		// sortTweets();

	}

	private void sortTweets() {

		String currMonth = "";
		MonthsTweets month = new MonthsTweets();
		months = new ArrayList<MonthsTweets>();
		for (int i = tweets.size() - 1; i >= 0; i--) {
			AliTweet t = tweets.get(i);
			if (t.month != currMonth) {
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
		//println("tra" + transition);
		background(30);
		float w2 = width * 0.5f;
		float h2 = height * 0.5f;

		translate(w2, h2);
		// update transition
		// transition += (transTarget - transition) * 0.01f;
		transition += (transTarget - transition) * 0.05f;
		Vec2D normUp = new Vec2D(0, -1);
		// define a color container using HSV
		TColor col = TColor.newHSV(0, 1, 1);

		String currYear = "--";
		for (int i = tweets.size() - 1; i >= 0; i--) {
			AliTweet tweet = tweets.get(i);
			// 360
			// 2700 / 360
			float size = tweets.size();
			float diff = 360.0f / tweets.size();
			float thisTheta = i * diff;
			float theta = radians(thisTheta);
			// theta = theta/6.72f;
			// println("i:"+i+" theta:"+theta);

			if (tweet.month.contains("Jan")) {
				col.setHue(1.0f / 12f);
			}
			if (tweet.month.contains("Feb")) {
				col.setHue(2.0f / 12f);
			}
			if (tweet.month.contains("Mar")) {
				col.setHue(3.0f / 12f);
			}
			if (tweet.month.contains("Apr")) {
				col.setHue(4.0f / 12f);
			}
			if (tweet.month.contains("May")) {
				col.setHue(5.0f / 12f);
			}
			if (tweet.month.contains("Jun")) {
				col.setHue(6.0f / 12f);
			}
			if (tweet.month.contains("Jul")) {
				col.setHue(7.0f / 12f);
			}
			if (tweet.month.contains("Aug")) {
				col.setHue(8.0f / 12f);
			}
			if (tweet.month.contains("Sep")) {
				col.setHue(9.0f / 12f);
			}
			if (tweet.month.contains("Oct")) {
				col.setHue(10.0f / 12f);
			}
			if (tweet.month.contains("Nov")) {
				col.setHue(11.0f / 12f);
			}
			if (tweet.month.contains("Dec")) {
				col.setHue(12.0f / 12.0f);
			}

			// create a polar coordinate
			Vec2D polar = new Vec2D(100, theta);
			// col.setHue((polar.y / TWO_PI) % 1);
			// also use theta to manipulate line length
			float len = map(i, tweets.size(), 0, 0, 400);
			// convert polar coord into cartesian space (to obtain position on a
			// circle)
			Vec2D circ = polar.copy().toCartesian();
			// create another coord splicing the circle at the top and using
			// theta difference as position on a line

			currentZoom = (targetZoom - currentZoom) * 0.001f;
			movee += mouseMoverX;

			int vecX = round((MathUtils.THREE_HALVES_PI - (polar.y * currentZoom))
					* w2 / PI)
					- (2 * mouseMoverX);
			vecX += (200 * mouseMoverX);
			// println("mouseMover:"+mouseMoverX+" vecX:"+vecX);
			// vecX += movee;
			// if (transTarget==1){
			// vecX = round((MathUtils.THREE_HALVES_PI - (polar.y*currentZoom))
			// * w2 / PI);
			// }
			int vecY = (int) map(mouseMoverY, 0 - (width * .5f), width * .5f,
					-360, 400);
			Vec2D linear = new Vec2D(vecX, vecY);
			// interprete circular position as normal/direction vector
			Vec2D dir = circ.getNormalized();
			// interpolate both position & normal based on current transition
			// state
			circ.interpolateToSelf(linear, transition, is);
			dir.interpolateToSelf(normUp, transition, is).normalizeTo(len);
			// apply color & draw line
			stroke(col.toARGB());
			line(circ.x, circ.y, circ.x + dir.x, circ.y + dir.y);
			fill(255);
			if (transition > 0) {
				if (mouseX <= circ.x + 6 && mouseX >= circ.x - 6) {
					pushMatrix();
					// rotate(radians(90));
					textSize(14);
					text(tweet.status, linear.y-550, 0 - linear.x);
					// if (!tweet.year.contains(currYear)) {
					rotate(radians(90));
					textSize(24);
					text(tweet.year + " " + tweet.month + " " + tweet.date,
							linear.y - 200, 550 - linear.x );
					currYear = tweet.year;

					// }
					popMatrix();

				}
			}

			// pushMatrix();
			// rotate(radians(90));
			// translate(dir.x, 0);
			// textSize(24);
			// fill(255);
			// text("hello");
			// popMatrix();

		}

		try {
			if (recording) {
				mm.addFrame();
			}
		} catch (Exception e) {
			// println("cant record!");
		}

	}

	private void drawCircleTweets() {

		String currentMonth = "Jan";
		for (int i = tweets.size() - 1; i >= 0; i--) {

			AliTweet tweet = tweets.get(i);
			// println("month:"+tweet.month+"  cuurmonth:"+currentMonth);
			if (!tweet.month.contains(currentMonth)) {
				currentMonth = tweet.month;
				tweetCount = 0;
				rotationCount++;
				println("rot:" + rotationCount);
			}
			// drawTweet(tweet.status);
			tweetCount++;
		}

	}

	private void drawTweet(String msg, int col) {
		colorMode(RGB, 255);
		// fill(tweetCount, 100, 100);
		fill(30);
		rect(0, height - 100, width, 100);

		fill(255);
		textSize(16);
		float wid = textWidth(msg);
		text(msg, (.5f * width) - (.5f * wid), height - 80);

	}

	private AliTweet getTweet(int i) {
		int theCount = 0;
		for (int j = 0; j < months.size(); j++) {
			MonthsTweets monthsTweets = months.get(j);
			for (int k = 0; k < monthsTweets.tweets.size(); k++) {
				AliTweet tweet = monthsTweets.tweets.get(k);
				if (theCount == i) {
					return tweet;
				}
				theCount++;
			}
		}

		return null;
	}

	/*
	 * private void drawTweet() {
	 * 
	 * colorMode(HSB, 100);
	 * 
	 * // fill(tweetYPos, 100, 100); stroke(tweetYPos * .5f, 100, 100);
	 * 
	 * ellipse((monthPos * 20) + 9, tweetYPos * .5f, 1, 1); tweetYPos++;
	 * 
	 * }
	 */
	public void mouseMoved(MouseEvent e) {
		// mouseMoverX = round(e.getX()-(width*.5f));
		mouseMoverX = (int) map(e.getX(), 0, width, -5, 214);
		mouseMoverY = round(e.getY() - (height * .5f));
		//println("mouseMoverY:" + mouseMoverY);
	}

	public void mousePressed() {
		println("mouse pressed");
	}

	public void mouseDragged() {
		Vec2D m = new Vec2D(mouseX, mouseY);
		Vec2D center = new Vec2D(width * .5f, height * .5f);
		// calculate rotation
		println("mouse:" + m.toString() + "  center:" + center);
		float ang = center.angleBetween(m);
		println("angle:" + ang);
		println("mouse dragged");
	}

	public void mouseReleased() {
		println("mouse released");
	}

	public void keyPressed() {
		if (key == '1') {
			targetZoom = 3;
			println("zoom set to 1");
		}
		if (key == '2') {
			targetZoom = 10;
			println("zoom set to 5");
		}
		if (key == '3') {
			targetZoom = 20;
			println("zoom set to 20");
		}
		if (key == '4') {
			targetZoom = 100;
			println("zoom set to 20");
		}
		if (key == '5') {
			targetZoom = 300;
			println("zoom set to 20");
		}
		if (key == '6') {
			targetZoom = 500;
			println("zoom set to 20");
		}
		if (key == '7') {
			targetZoom = 5000;
			println("zoom set to 20");
		}

		if (key == '8') {
			targetZoom = 30000;
			println("zoom set to 20");
		}

		if (key == 'a') {
			println("animating...");
			transTarget = (++transTarget % 2);
		}

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
