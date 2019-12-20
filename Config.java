import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;

public class Config{
	public static int nCols;
	public static int nRows;
	public static String[][] data = new String[300][300];
	public static String[] diffClass;
	public static double[] gain;
	public static boolean[] complete;

	public static double iniEntropy(){
		double entropy, plus, minus;
		int nYes = 0;
		int nNo = 0; 
		int total = 0;

		for(int i=1; i<nRows; i++){
			if(!complete[i])
				total++;
		}

		for(int i=1; i<nRows; i++){
			if(!complete[i])
				if (data[i][nCols-1].equals("No") || data[i][nCols-1].equals("no"))
       	 			nNo++;		
		}

		for(int i=1; i<nRows; i++){
			if(!complete[i])
				if (data[i][nCols-1].equals("Yes") || data[i][nCols-1].equals("yes"))
       	 			nYes++;		
		}

		plus = (double) nYes / (double) (nRows-1);
		minus = (double) nNo / (double) (nRows-1);
		entropy = -(plus * (Math.log(plus) / Math.log(2))) - (minus * (Math.log(minus) / Math.log(2)));
		
		if(Double.isNaN(entropy))
			return 0;
		else
			return entropy;
	}

	public static double colEntropy(String[] vals, int col, double iniEntropy){
		int[] nTypes = new int[vals.length];
		int nYes = 0;
		int nNo = 0;
		double total = 0;
		double gain, plus, minus;
		double staticEntropy = iniEntropy;
		double[] entropy = new double[vals.length];

		for(int i=0; i<nTypes.length; i++){
			for(int j=1; j<nRows; j++){
				if(!complete[j])
					if(data[j][col].equals(vals[i]))
						nTypes[i]++;
			}
		}

		for(int i=0; i<nTypes.length; i++){
			nYes = nNo = 0;
			plus = minus = 0;

			for(int j=1; j<nRows; j++){
				if(!complete[j])
					if(data[j][nCols-1].equals("No") || data[j][nCols-1].equals("no") && data[j][col].equals(vals[i]))
						nNo++;
			}

			for(int j=1; j<nRows; j++){
				if(!complete[j])
					if(data[j][nCols-1].equals("Yes") || data[j][nCols-1].equals("yes") && data[j][col].equals(vals[i]))
						nYes++;
			}
			plus = (double) nYes / (double) (nTypes[i]);
			minus = (double) nNo / (double) (nTypes[i]);
			entropy[i] = -(plus * (Math.log(plus) / Math.log(2))) - (minus * (Math.log(minus) / Math.log(2)));
			
			if(Double.isNaN(entropy[i]))
				entropy[i] = 0;
		}

		for(int i=0; i<nTypes.length; i++){
			total += ((double) nTypes[i] / (double) (nRows-1)) * entropy[i];
		}
		gain = iniEntropy - total;
		return gain;
	}

	public static void isComplete(int col, String val){
		for(int i=1; i<nRows; i++){
			if(data[i][col].equals(val))
				complete[i] = true;
		}
	}

	public static String[] clearString(String[] vals, int n){
		String[] aux = new String[n];
		for(int i=0; i<n; i++){
			aux[i] = vals[i];
		}
		return aux;
	}

	public static String[] differentVals(int col){
		String[] vals = new String[nRows-1];
		int nTypes = 0;
		int total = 0;
		int test = 0;

		Arrays.fill(vals,"null");
		for(int i=1; i<nRows; i++){
			for(int j=0; j<total+1; j++){
				if(!complete[i])
					if(data[i][col].equals(vals[j]))
						test = 1;
			}
			if(test == 0){
				vals[total] = data[i][col];
				total++;
			}
			test = 0;
		}
		String[] cleanVals = new String[total];
		cleanVals = clearString(vals,total);
		return cleanVals;
	}

	public static double[] colGain(double[] gain){
		String[] vals;
		double iniEntropy = iniEntropy();

		for(int i=1; i<nCols-1; i++){
			vals = differentVals(i);
			gain[i] = colEntropy(vals,i,iniEntropy);
		}
		return gain;
	}

	public static double partialProbability(int col, int val, String target){
		int nVals = 0;
		int nTargets = 0;
		
		for(int i=1; i<nRows; i++){
			if(!complete[i]){
				if(data[i][col].equals(target)){
					nTargets++;
					if(data[i][nCols-1].equals(diffClass[val]))
						nVals++;
				}
			}
		}
		return (double) nVals / (double) nTargets;
	}

	public static int getCounter(int col, String target){
		int counter = 0;

		for(int i=1; i<nRows; i++){
			if(!complete[i]){
				if(data[i][col].equals(target))
					counter++;
			}
		}
		return counter;
	}
}