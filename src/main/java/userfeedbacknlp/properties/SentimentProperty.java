package userfeedbacknlp.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import userfeedbacknlp.data.Property;
import userfeedbacknlp.util.Preprocessing;

public class SentimentProperty extends Property{

	//Attributes
	private StanfordCoreNLP pipeline;
	private int numberPositiveSentences;
	private int numberNegativeSentences;
	private int numberNeutralSentences;
	private double positiveSentiment;
	private double negativeSentiment;
	private double avgTotalSentiment;
	private int lenghtNegative;
	
	//Properties get
	public int getNumberPositiveSentences() {
		return numberPositiveSentences;
	}

	public int getNumberNegativeSentences() {
		return numberNegativeSentences;
	}

	public int getNumberNeutralSentences() {
		return numberNeutralSentences;
	}

	public double getPositiveSentiment() {
		return positiveSentiment;
	}

	public double getNegativeSentiment() {
		return negativeSentiment;
	}

	public double getAvgTotalSentiment() {
		return avgTotalSentiment;
	}

	public int getLenghtNegative() {
		return lenghtNegative;
	}

	//Contructors
	public SentimentProperty(String strLine, StanfordCoreNLP pipeline) {
		super();
		numberPositiveSentences = 0;
		numberNegativeSentences = 0;
		positiveSentiment = 0;
		negativeSentiment = 0;
		avgTotalSentiment = 0;
		lenghtNegative = 0;
		
		/*// Setting the annotator 
		Properties props = new Properties();
		
		if(annotators!= null && !annotators.equals("")){
			props.put("annotators", annotators);
		}else{
			props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
			
		}
		//pipeline = new StanfordCoreNLP(props);*/
		this.pipeline = pipeline;
		
		// Setting the source text after pre-processing
		this.sourceText = Preprocessing.preProcessingText(strLine);
		sourceText = sourceText.replaceAll(" \\?", "? ");
		sourceText = sourceText.replaceAll("  \\?", "? ");
		sourceText = sourceText.replaceAll("  ", " ");
		sourceText = sourceText.replaceAll(" \\. ", "\\. ");
		sourceText = sourceText.replaceAll("\n", " ");
		sourceText = sourceText.replaceAll("\t", " ");
		
		// Initialize the values
		//this.values = new HashMap<String, Map<Double, String>>();
		this.values = new HashMap<String, Map<String, Double>>();
	}
	
	// Methods
	@Override
	public void calculateValues() {
				
		// Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
		Annotation annotation;
		annotation = new Annotation(this.sourceText);
		
		// run all the selected Annotators on this text
		pipeline.annotate(annotation);

		// An Annotation is a Map with Class keys for the linguistic analysis types.
		// You can get and use the various analyses individually.
		// For instance, this gets the parse tree of the first sentence in the text.
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && ! sentences.isEmpty()) 
		{			
			for(CoreMap sentence: sentences){
				Map<String, Double> obj = new HashMap<String, Double>();
				obj.put(sentence.get(SentimentCoreAnnotations.SentimentClass.class), 
						Double.valueOf((sentence.get(SentimentCoreAnnotations.SentimentClass.class).equals("Very negative"))? -1: //-5
							((sentence.get(SentimentCoreAnnotations.SentimentClass.class).equals("Negative"))? -0.4: //-2
								((sentence.get(SentimentCoreAnnotations.SentimentClass.class).equals("Neutral"))? 0:
									((sentence.get(SentimentCoreAnnotations.SentimentClass.class).equals("Positive"))? 0.4: //2
										((sentence.get(SentimentCoreAnnotations.SentimentClass.class).equals("Very positive"))? 1: 0)))))//5
						);
				this.values.put(sentence.toString(), obj);
			}
			
			// Calculate the aggregate values related to the user feedback
			// Calculate positive and negative sentiment in a separated way
			numberPositiveSentences=0;
			numberNegativeSentences=0;
			positiveSentiment=0;
			negativeSentiment=0;
			
			// The total sentiment is calculated using an aggregated value: sum (sentiment * weight) where weight is length/total length
			System.out.println("===========================================");
			System.out.println("Source text: " + sourceText);
			System.out.println("Total length: " + sourceText.length());
			
			double sentiment=0;
			for(String key: values.keySet()){
				System.out.println("Sentence: " + key + "\n length: " + key.length() 
					+ "\n sentiment: " + values.get(key).keySet().iterator().next()
					+ "\n value sentiment: " + values.get(key).values().iterator().next().toString());
			
				sentiment = values.get(key).values().iterator().next().doubleValue();
				if(sentiment > 0){
					numberPositiveSentences ++;
					positiveSentiment += sentiment;
				} else if (sentiment < 0){
					numberNegativeSentences ++;
					negativeSentiment += sentiment;
					lenghtNegative += key.length();
				} else{
					numberNeutralSentences ++;
				}
				//avgTotalSentiment += (sentiment * (key.length()/sourceText.length()));
				avgTotalSentiment += sentiment;
			}
			avgTotalSentiment = avgTotalSentiment / sentences.size();
			// We calculate the average value
			positiveSentiment = (numberPositiveSentences!=0)? positiveSentiment / numberPositiveSentences: 0;
			negativeSentiment = (numberNegativeSentences!=0)? negativeSentiment / numberNegativeSentences : 0;
			
			System.out.println("Average total sentiment: " + avgTotalSentiment);
			System.out.println("# neutral sentences: " + numberNeutralSentences);
			System.out.println("Average positive sentiment: " + positiveSentiment + " #positive: " + numberPositiveSentences);
			System.out.println("Average negative sentiment: " + negativeSentiment  + " #negative: " + numberNegativeSentences);
		}
	}
}