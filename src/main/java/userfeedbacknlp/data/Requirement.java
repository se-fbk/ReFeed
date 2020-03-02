package userfeedbacknlp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requirement {

	String id;
	String title;
	String description;
	Map<Feedback, Double> relatedFeedback;
	Map<String, Property> aggregatedProperties;
	double totalValueProperties; //Total value from properties
	
	public Requirement() {
		super();
		
		relatedFeedback = new HashMap<Feedback, Double>();
		aggregatedProperties = new HashMap<String, Property>();
	}
	
	public Requirement(String id, String title, String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		
		relatedFeedback = new HashMap<Feedback, Double>();
		aggregatedProperties = new HashMap<String, Property>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<Feedback, Double> getRelatedFeedback() {
		return relatedFeedback;
	}
	public void setRelatedFeedback(Map<Feedback, Double> relatedFeedback) {
		this.relatedFeedback = relatedFeedback;
	}
	public Map<String, Property> getAggregatedProperties() {
		return aggregatedProperties;
	}
	public void setAggregatedProperties(Map<String, Property> aggregatedProperties) {
		this.aggregatedProperties = aggregatedProperties;
	}
	public double getTotalValueProperties() {
		return totalValueProperties;
	}
	public void setTotalValueProperties(double totalValueProperties) {
		this.totalValueProperties = totalValueProperties;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof Requirement) {
			Requirement other = (Requirement)obj;
			if (getId().equalsIgnoreCase(other.getId())) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
