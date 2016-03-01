package training_algorithms;

public class SBPParams
{
	private int epochs;
	private int trainingIterations;
	private double errorThreshold;
	private double learningRate;
	private double momentumRate;
	
	public SBPParams() {
		setEpochs(5000);
		setTrainingIterations(3500);
		setErrorThreshold(0.0001);
		setLearningRate(0.30);
		setMomentumRate(0.30);
	}
	
	public SBPParams(int epochs, int trainingIterations, double errorThreshold, double learningRate, double momentumRate) {
		this.setEpochs(epochs);
		this.setTrainingIterations(trainingIterations);
		this.setErrorThreshold(errorThreshold);
		this.setLearningRate(learningRate);
		this.setMomentumRate(momentumRate);
	}

	public int getEpochs() {
		return epochs;
	}

	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}

	public int getTrainingIterations() {
		return trainingIterations;
	}

	public void setTrainingIterations(int trainingIterations) {
		this.trainingIterations = trainingIterations;
	}

	public double getErrorThreshold() {
		return errorThreshold;
	}

	public void setErrorThreshold(double errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getMomentumRate() {
		return momentumRate;
	}

	public void setMomentumRate(double momentumRate) {
		this.momentumRate = momentumRate;
	}
}