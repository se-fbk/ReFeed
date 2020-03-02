package userfeedbacknlp.data;

import java.util.HashMap;
import java.util.Map;

public class Feedback {

	String id;
	String title;
	String description;
//	double score; //score in the similarity to the requirement
	Map<TypeProperty, Property> properties;
	double totalValueProperties; //Total value from properties
	
	public Feedback() {
		super();
		
		properties = new HashMap<TypeProperty, Property>();
	}
	
	public Feedback(String id, String title, String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
//		this.score = score;
		
		properties = new HashMap<TypeProperty, Property>();
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
	
	public Map<TypeProperty, Property> getProperties() {
		return properties;
	}
	public void setProperties(Map<TypeProperty, Property> properties) {
		this.properties = properties;
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
		if (obj instanceof Feedback) {
			Feedback other = (Feedback)obj;
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
