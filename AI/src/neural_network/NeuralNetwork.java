package neural_network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jblas.*;

import training_algorithms.SBPImpl;

/**
 * 
 * @author braem
 *
 * Input/Hidden/Output Layered Neural Network
 * 
 */

public class NeuralNetwork implements SBPImpl, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DoubleMatrix Wji;		//Weight matrices
	private DoubleMatrix Wkj;
	private List<DoubleMatrix> Wjbias;
	private DoubleMatrix Wkbias;
	private List<DoubleMatrix> Wjs;
	private DoubleMatrix NETk;		//Nets stored in feedForward for SBP
	private List<DoubleMatrix> NETjs;
	private List<DoubleMatrix> ACTjs;
	private double A = 1.716;
	private double B = 0.667;
	private NeuralNetworkParams params;
	private DoubleMatrix error;
	
	public NeuralNetwork() { //initialize for basic XOR NN
		params = new NeuralNetworkParams();
		error = new DoubleMatrix(1, params.getOutputLayerSize());
		error = error.fill(Double.MAX_VALUE);
		NETjs = null;
		ACTjs = null;
	}
	
	public NeuralNetwork(NeuralNetworkParams params) {
		this.params = params;
		error = new DoubleMatrix(1, params.getOutputLayerSize());
		error = error.fill(Double.MAX_VALUE);
		NETjs = null;
		ACTjs = null;
	}
	
	/* FEED FORWARD */
	@Override
	public DoubleMatrix feedForward(DoubleMatrix inputVector) {
		if(NETjs != null)
			NETjs.clear();
		if(ACTjs != null)
			ACTjs.clear();
		double bias = params.getBias();
		
		/* HIDDEN LAYER */
		//Hidden Net Matrix = inputVector*Wji + Wjbias*bias
		List<DoubleMatrix> NETjs = new ArrayList<DoubleMatrix>(); //NET values of each hidden layer
		List<DoubleMatrix> ACTjs = new ArrayList<DoubleMatrix>(); //ACT values of each hidden layer
		for(int i=0; i<params.getHiddenLayerSizes().size(); i++) {
			DoubleMatrix hNetMatrix = new DoubleMatrix(1, params.getHiddenLayerSizes().get(i));
			DoubleMatrix hActMatrix = new DoubleMatrix(hNetMatrix.rows, hNetMatrix.columns);
			if(i==0)
				hNetMatrix = inputVector.mmul(Wji).add(Wjbias.get(i).mmul(bias));
			else	//get previous layers ACT values then *weights. Then add the bias
				hNetMatrix = ACTjs.get(i-1).mmul(Wjs.get(i-1)).add(Wjbias.get(i).mmul(bias));
			hActMatrix = applySigmoid(hNetMatrix);
			NETjs.add(hNetMatrix);
			ACTjs.add(hActMatrix);
		}
		this.NETjs = NETjs;
		this.ACTjs = ACTjs;
		
		/* OUTPUT LAYER */
		//Output Net Matrix = hiddenActMatrix*Wkj + Wkbias*bias
		DoubleMatrix outputNetMatrix = ACTjs.get(ACTjs.size()-1).mmul(Wkj).add(Wkbias.mmul(bias));
		NETk = outputNetMatrix;
		
		//Actual Output Matrix = tanh(outputNetMatrix*bias)*A
		DoubleMatrix outputActMatrix = applySigmoid(outputNetMatrix);
		
		//return actual output matrix
		return outputActMatrix;
	}
	
	/* sigmoid function application */
	@Override
	public DoubleMatrix applySigmoid(DoubleMatrix inc) {
		return MatrixFunctions.tanh(inc.mul(B)).mul(A);
	}
	@Override
	public DoubleMatrix applySigmoidDeriv(DoubleMatrix inc) {
		return MatrixFunctions.pow( MatrixFunctions.tanh(inc),2 ).mul(-1).add(1);
	}
	
	/* update application */
	@Override
	public void applyWkjUpdate (DoubleMatrix Wkj) { this.Wkj.addi(Wkj); }
	@Override
	public void applyWkbiasUpdate (DoubleMatrix Wkbias) { this.Wkbias.addi(Wkbias); }
	@Override
	public void applyWjiUpdate (DoubleMatrix Wji) { this.Wji.addi(Wji); }
	@Override
	public void applyWjbiasUpdate (List<DoubleMatrix> Wjbias) {
		for(int i=0; i<Wjbias.size(); i++)
			this.Wjbias.set(i, this.Wjbias.get(i).add(Wjbias.get(i)));
	}
	@Override
	public void applyWjsUpdate (List<DoubleMatrix> Wjs) {
		for(int i=0; i<Wjs.size(); i++)
			this.Wjs.set(i, this.Wjs.get(i).add(Wjs.get(i)));
	}

	//getters
	public int getOutputLayerSize() { return params.getOutputLayerSize(); }
	public NeuralNetworkParams getParams() { return params; }
	@Override
	public DoubleMatrix getNETk() { return NETk; }
	@Override
	public List<DoubleMatrix> getNETjs() { return NETjs; }
	@Override
	public DoubleMatrix getWkj() { return Wkj; }
	@Override
	public List<DoubleMatrix> getACTjs() { return ACTjs; }
	@Override
	public List<DoubleMatrix> getWjs() { return Wjs; }
	DoubleMatrix getWji() { return Wji; }
	List<DoubleMatrix> getWjbias() { return Wjbias; }
	DoubleMatrix getWkbias() { return Wkbias; }
	public DoubleMatrix getError() { return error; }
	
	//setters
	public void setParams(NeuralNetworkParams params) { this.params = params; }
	void setWji(DoubleMatrix Wji) { this.Wji = Wji; }
	void setWjbias(List<DoubleMatrix> Wjbias) { this.Wjbias = Wjbias; }
	void setWkj(DoubleMatrix Wkj) { this.Wkj = Wkj; }
	void setWkbias(DoubleMatrix Wkbias) { this.Wkbias = Wkbias; }
	void setWjs(List<DoubleMatrix> Wjs) { this.Wjs = Wjs; }

	/* Initialize this network */
	@Override
	public void init() {
		Random rand = new Random();
		
		List<Integer> hiddenLayerSizes = params.getHiddenLayerSizes();
		Wji = new DoubleMatrix(params.getInputLayerSize(), hiddenLayerSizes.get(0));
		Wkj = new DoubleMatrix(hiddenLayerSizes.get(hiddenLayerSizes.size()-1), params.getOutputLayerSize());
		Wjbias = new ArrayList<DoubleMatrix>();
		Wkbias = new DoubleMatrix(1, params.getOutputLayerSize()); //row vector
		Wjs = new ArrayList<DoubleMatrix>(); //will be empty if hidden layers = 1
		
		for(int i=0; i<hiddenLayerSizes.size(); i++) {
			DoubleMatrix hiddenLayerToBias = new DoubleMatrix(1, params.getHiddenLayerSizes().get(i));
			for(int j=0; j<hiddenLayerToBias.rows; j++)
				for(int k=0; k<hiddenLayerToBias.columns; k++)
					hiddenLayerToBias.put(j, k, (rand.nextDouble()%3)-1);
			Wjbias.add(hiddenLayerToBias);
		}
		for(int i=1; i<hiddenLayerSizes.size(); i++) {
			DoubleMatrix hiddenWeightsAtHi = new DoubleMatrix(hiddenLayerSizes.get(i-1), hiddenLayerSizes.get(i));
			for(int j=0; j<hiddenWeightsAtHi.rows; j++) //fill the hidden matrix
				for(int k=0; k<hiddenWeightsAtHi.columns; k++)
					hiddenWeightsAtHi.put(j, k, (rand.nextDouble()%3)-1);
			Wjs.add(hiddenWeightsAtHi);
		}
		
		//fill with initial edge weights
		for(int i=0; i<Wji.rows; i++)
			for(int j=0; j<Wji.columns; j++)
				Wji.put(i, j, (rand.nextDouble()%3)-1);
		for(int i=0; i<Wkj.rows; i++)
			for(int j=0; j<Wkj.columns; j++)
				Wkj.put(i, j, (rand.nextDouble()%3)-1);
		for(int i=0; i<Wkbias.rows; i++)
			for(int j=0; j<Wkbias.columns; j++)
				Wkbias.put(i, j, (rand.nextDouble()%3)-1);
	}
	
	@Override
	public void saveToDisk(DoubleMatrix error) {
		this.error = error;
		if(NeuralNetworkIO.isBestNetworkSoFar(error)) { //if best network so far, save it to disk
			NeuralNetworkIO.writeNetwork(this);
		}
	}
	
	@Override
	public void printAllEdges() {
		System.out.println("Wji"+Wji);
		System.out.println("Wjbias"+Wjbias);
		System.out.println("Wkj"+Wkj);
		System.out.println("Wkbias"+Wkbias);
		System.out.println("Wjs"+Wjs);
	}
}
