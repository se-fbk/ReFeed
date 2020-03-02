package userfeedbacknlp.properties;

import userfeedbacknlp.data.Property;

public class SeverityProperty extends Property{
	private double valueSeverity;
	private double totalLength;
	private double negativeLength;

	public double getValueSeverity() {
		return valueSeverity;
	}
	
	public SeverityProperty(double totalLength, double negativeLength) {
		super();
		this.totalLength = totalLength;
		this.negativeLength = negativeLength;
	}

	@Override
	public void calculateValues() {
		this.valueSeverity = (negativeLength/totalLength);
	}

}