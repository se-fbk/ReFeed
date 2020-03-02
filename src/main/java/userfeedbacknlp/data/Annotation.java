package userfeedbacknlp.data;

public class Annotation {

	//Attributes 
	private String name;
	private Double priority;
	private Double weight;
	
	//Constructors
	public Annotation(String name, Double priority, Double weight) {
		super();
		this.name = name;
		this.priority = priority;
		this.weight = weight;
	}

	// get and set methods
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
}
