import java.util.LinkedList;

/**
 * 
 * Answer object related to the Questions aksed to a person.
 * Contains:
 * 		- The id of the question
 * 		- The answer of the person
 * 		- The set of acceptable answers from answers to this question
 * 		- The importance representing how important it is to him how the other answers
 * 
 *
 */
public class Answer {
	
	// Irrelevant
	public static final int IMPORTANCE_0 = 0;
	// Little Important
	public static final int IMPORTANCE_1 = 1;
	// Somewhat Important
	public static final int IMPORTANCE_2 = 10;
	// Very Important
	public static final int IMPORTANCE_3 = 50;
	// Mandatory
	public static final int IMPORTANCE_4 = 250;

	public int 					questionId;
	public int 					myAnswer;
	public LinkedList<Integer> 	acceptableAnswers;
	public int 					importanceValue;
	
	public Answer(){}
	
	public Answer( int questionId, int myAnswer, LinkedList<Integer> acceptableAnswers, int importanceCode ){
		
		this.questionId	= questionId;
		this.myAnswer = myAnswer;
		
		this.acceptableAnswers	= acceptableAnswers;
		try {
			this.setImportance(importanceCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setImportance(int importanceCode) throws Exception{
		
		switch(importanceCode) {
			case 0 : this.importanceValue = IMPORTANCE_0; break;
			case 1 : this.importanceValue = IMPORTANCE_1; break;
			case 2 : this.importanceValue = IMPORTANCE_2; break;
			case 3 : this.importanceValue = IMPORTANCE_3; break;
			case 4 : this.importanceValue = IMPORTANCE_4; break;
			default : throw new Exception("Invalid Importance Values");
		}
		
	}
	
	public String toString(){
		return "Answer : QuestionID: " + Integer.toString(questionId) + 
				" MyAnswer " + Integer.toString(myAnswer) +
				" AcceptedAnswers " + this.acceptableAnswers.toString() +
				" Importance: " + Integer.toString(importanceValue);
	}
}