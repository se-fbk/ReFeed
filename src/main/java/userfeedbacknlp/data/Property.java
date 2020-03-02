package userfeedbacknlp.data;

import java.util.Map;

public abstract class Property {

	protected String sourceText;
	
	//This variable contains a sentence, a annotated value of the property, a numerical value of the property
	// <sentence, <string value, numerical value or frequency>>
	protected Map<String, Map<String, Double>> values;
	
	abstract public void calculateValues();
}
