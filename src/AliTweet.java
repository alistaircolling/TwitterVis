import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class AliTweet {

	public static final int CIRCLE_MODE = 0;
	public static final int LINE_MODE = 0;
	
	public String status;
	public String createdAt;
	public String year;
	public String day;
	public String month;

	public float radius;
	public float theta;
	public float thetaSpeed = 0.1f;
	
	public float xPos;
	public float targetXPos;
	public float yPos;
	public float targetYPos;
	
	public int animMode = CIRCLE_MODE;
	public String date;
	
	
	public void update(){
		
	}
	
	public void render(){
		
	}

}
