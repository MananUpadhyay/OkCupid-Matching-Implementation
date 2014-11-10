import java.util.HashMap;

/**
 * Profile represents each person in the input okCupid input JSON
 * 
 * Contains a HashMap of answers in the format of: QuestionId -> Answer
 * and the id of the person's profile
 *
 */
public class Profile {
    
	private static final long serialVersionUID = 1L;
	public int profileId;
	public HashMap<Integer, Answer> profileAnswerMap;

    public Profile () {}

    public Profile (int profileId, HashMap<Integer, Answer> profileAnswerMap) {
    	this.profileId = profileId;
    	this.profileAnswerMap = profileAnswerMap;
    }

  	public int getID (int profileId) {
  		return this.profileId;
  	}
}