import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of OkCupid's matching algorithm. Given a JSON input it calculates how much the
 * people match each other.
 *
 * The output JSON represents each profiles scores towards any other profile
 *
 */
public class OkCupid {
	

	/**
	 * Given two profile's answers, calculates the matching score of the two people according to the PDF file description.
	 * Uses two Profile objects to calulate the matchScore.
	 * 
	 * @param hisAnswers - Profile object that is used to claculate the matchScore.
	 * @param herAnswers - The other Profile object with respect to which the matchScore is calculated and vice-versa.
	 * @return - The match score in float with the Error Margins considered with the scores.
	 */
	private float calculateTrueMatchScore(Profile his, Profile her) {
		HashMap<Integer,Answer> hisAnswers = his.profileAnswerMap;
		HashMap<Integer,Answer> herAnswers = her.profileAnswerMap;

		int commonQuestionsForHim = 0;
		int commonQuestionsForHer = 0;

		//calculating match percentage from his perspective.
		int pointsPossibleHim = 0;
		int pointsEarnedByHim = 0;
		for( Integer questionId : hisAnswers.keySet() ){

			Answer herAnswer = herAnswers.get(questionId);
			Answer hisAnswer = hisAnswers.get(questionId);

			pointsPossibleHim += hisAnswer.importanceValue;
			if ( herAnswer != null ) {
				commonQuestionsForHim++;
				if( hisAnswer.acceptableAnswers.contains(herAnswer.myAnswer) ){
					pointsEarnedByHim += hisAnswer.importanceValue;
				}
			}
		}
		
		// calculating match percentage from her perspective.
		int pointsPossibleHer = 0;
		int pointsEarnedByHer = 0;
		for( Integer questionId : herAnswers.keySet() ){

			Answer herAnswer	= herAnswers.get(questionId);
			Answer hisAnswer		= hisAnswers.get(questionId);
			
			pointsPossibleHer += herAnswer.importanceValue;
			if ( hisAnswer != null ) {
				commonQuestionsForHer++;
				if( herAnswer.acceptableAnswers.contains(hisAnswer.myAnswer) ){
					pointsEarnedByHer += herAnswer.importanceValue;
				}
			}
		}
		// considering the average common questions for both Him and Her.
		int commonQuestions = (commonQuestionsForHim + commonQuestionsForHer) / 2; 
	 	
		float calculatedMatch = (float)Math.sqrt(
								( (float)pointsEarnedByHim  / (float)pointsPossibleHim)
								*
								( (float)pointsEarnedByHer / (float)pointsPossibleHer )
								);

		float marginOfError = 1 / commonQuestionsForHim;

		return (calculatedMatch - marginOfError);
	}
	
	/**
	 * Reads input JSON file to and converts it to Profiles.
	 * 		- An ArrayList of Profiles
	 * 		- A Profile represents a person, consists of its id and a HashMap of Answers
	 * 		- An Answer represents a Question, the persons answer, the expected set of answers and the
	 * 			importance
	 * 
	 * @param inputPath - The path of the input JSON file
	 * @return - ArrayList of Profiles.
	 */
	private ArrayList<Profile> readInput(String inputPath){
		
		ArrayList<Profile> profileList = new ArrayList<Profile>();
		try {
			// Reading input JSON file
			String str;
			StringBuilder inputJSONtext = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(inputPath));
			System.out.println("Reading file...");
			while ( (str = br.readLine()) != null ){
				inputJSONtext.append(str);
			}
			
			
			JSONObject inputJSON = new JSONObject(inputJSONtext.toString());
			
			//iterate through profiles in JSON adding them to an ArrayList  "profiles"
			JSONArray profilesJSON = inputJSON.getJSONArray("profiles");
			for( int i = 0 ; i< profilesJSON.length() ; i++ ) {
				
				JSONObject profileJSON = profilesJSON.getJSONObject(i);
				int profileId = profileJSON.getInt("id");
				HashMap<Integer,Answer> profileAnswerMap = new HashMap<Integer,Answer>();

				//iterate all answers in the profile
				JSONArray answersJSON =profileJSON.getJSONArray("answers");
				for( int j = 0 ; j< answersJSON.length() ; j++ ) {
					
					try {
						JSONObject answerJSON = answersJSON.getJSONObject(j);
						LinkedList<Integer> acceptableAnswerList = new LinkedList<Integer>();
						JSONArray acceptableAnswersJSON =answerJSON.getJSONArray("acceptableAnswers");
						
						for( int k = 0 ; k< acceptableAnswersJSON.length() ; k++ ) {
							acceptableAnswerList.add(acceptableAnswersJSON.getInt(k));
						}
						
						Answer answer = new Answer(	answerJSON.getInt("questionId"), 
												answerJSON.getInt("answer"), 
												acceptableAnswerList, 
												answerJSON.getInt("importance")
											);
						profileAnswerMap.put(answer.questionId, answer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				profileList.add(new Profile(profileId,profileAnswerMap));
			}
			
			br.close();
			return profileList;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	/**
	 * Calculates the scores for one person to every other.
	 * The output is a HashMap:
	 * 		- The Key is Pair object that contains the ID's of two Profiles that it wraps.
	 * 		- The Value is the score in float
	 * 
	 * @param profiles - The ArrayList of profiles(from the input JSON) obtained from 'readInput' method.
	 * @param numOfMatechesReqd - The number of matches for a particular profile required given at STDIN.
	 * @return - The HashMap of scores keyed by their corresponding Profile ID Pair.
	 */
	private HashMap<Pair, Float> generateProfileScores(ArrayList<Profile> profiles, int numOfMatchesReqd){

		HashMap<Pair, Float> results = new HashMap<Pair, Float>();
		// compare each profile with every other and also eliminating dupicate pair(profile A & profile B == profile B & profile A)
		for( int i=0 ; i<profiles.size() - 1 ; i++ ) {
			for( int j = i+1 ; j<profiles.size() ; j++ ){
				if ( i!=j ){
					Profile p1 = profiles.get(i);
					Profile p2 = profiles.get(j);
					float matchScore = calculateTrueMatchScore(p1, p2);
					Pair keys = new Pair(p1.profileId,p2.profileId);
					results.put(keys, matchScore);
				}
			}
		}
        // return closest matches for all profile pairs.
		return findTopMatches(results, numOfMatchesReqd);
	}
    
    /**
    * Finds the top 'k' matches ranked by the match score. Uses a PrirityQueue ordered by values to achieve this.
    * The output is a HashMap: 
    *		- The Key is Pair of Proile ID's.
    *		- The Value is the true match score associated with the profile pairs.
    * @param allResults - HashMap that represents all the Paris and their associated scores.
    * @param noOfClosestMatches - The number of matches required per Profile.
    * @return closestMatches - HashMap with the the 'k' most closest matches where k = noOfClosestMatches.
    */
    private HashMap<Pair, Float> findTopMatches(HashMap<Pair, Float> allResults , int noOfClosestMatches ) {
        
        Comparator<Map.Entry<Pair,Float>> comparator = new Comparator<Map.Entry<Pair,Float>>() {
            @Override
            public int compare(Map.Entry<Pair,Float> e1, Map.Entry<Pair,Float> e2) {
                Float f1 = e1.getValue();
                Float f2 = e2.getValue();
                return f1.compareTo(f2);
            }
        };
        
        PriorityQueue<Map.Entry<Pair,Float>> closestProfiles = new PriorityQueue<Map.Entry<Pair,Float>>(comparator);
        // insert all the entries into a PrirityQueue ordered by the values and not the keys.
        for(Map.Entry<Pair,Float> e : allResults.entrySet()) {
            closestProfiles.offer(e);
        }
        // System.out.println(closestProfiles.size());
        
        HashMap<Pair,Float> closestMatches = new HashMap<Pair,Float>();
        for(int count = 0; count < noOfClosestMatches; count++) {
            Map.Entry<Pair,Float> e = closestProfiles.poll();
            closestMatches.put(e.getKey(),e.getValue());
        }
        return closestMatches;
    }
	

	private JSONObject createJSONresult( Map<Pair, Float> results ){
		
		JSONObject outputJSON = new JSONObject();
		try {
			JSONArray resultsArrayJSON = new JSONArray();

			// Goes through all ProfileId - ProfileId pairs, insterting the scores
			for ( Pair keyPair : results.keySet() ){
				
				HashSet<Pair> uniqueProfiles = new HashSet<Pair>();
				JSONObject resultJSON = new JSONObject();
				
				// The profile is not in the uniqueProfiles reuslt set add it else continue.
				if(!uniqueProfiles.contains(keyPair)){
					uniqueProfiles.add(keyPair);
					resultJSON = new JSONObject();
					resultJSON.put("profileId", keyPair.idA);
					JSONArray scoresArrayJSON = new JSONArray();
					resultJSON.put("matches", scoresArrayJSON);
					resultsArrayJSON.put(resultJSON);

					JSONObject score = new JSONObject();
					score.put("profileId", keyPair.idB);
					score.put("score", (double)results.get(keyPair));
					scoresArrayJSON.put(0, score);
				}
			}
			outputJSON.put("results", resultsArrayJSON);
			// System.out.println(resultsArrayJSON.length());
			// Printing the generated JSON.
			System.out.println("\n" + outputJSON.toString() );

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return outputJSON;
	}
	
	
	public static void main(String[] args) {
		
		OkCupid okc = new OkCupid();
		// Scanner sc = new Scanner(System.in);
		// System.out.println("Enter number of mathces required");
		// int kMatches = sc.nextInt();
		ArrayList<Profile> profiles = okc.readInput("input.json");
		// Hard-Coding the number of Matches to 10 as required. Can be changed by using the commented code above.
		HashMap<Pair, Float> mresults = okc.generateProfileScores(profiles, 10);
        okc.createJSONresult(mresults);
	}

}
