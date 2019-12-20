import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;

public class DecTree extends Config{

	static boolean[] visAttributes;
	private static Scanner in = new Scanner(System.in);

	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
	}  

	public static void iniScreen(){				
		System.out.println(" __   ___  __     __     __          ___  __   ___  ___ ");
		System.out.println("|  \\ |__  /  ` | /__` | /  \\ |\\ |     |  |__) |__  |__  ");
		System.out.println("|__/ |___ \\__, | .__/ | \\__/ | \\|     |  |  \\ |___ |___ ");
		System.out.println("\n");  
	}

	public static void fileParser(){
		String file;
		String line;
		BufferedReader name = null;
		int rows = 0;

		System.out.println("Specify CSV file to open:");
		file = in.next();

		try{
			name = new BufferedReader(new FileReader(file));
			while((line = name.readLine()) != null){
				String[] words = line.split(",");

				for(int i=0; i<words.length; i++){
					data[rows][i] = words[i];
				}
				rows++;
				nCols = words.length;
			}
			nRows = rows;
		} catch(FileNotFoundException e){
      		e.printStackTrace();
    	} catch(IOException e){
     	 	e.printStackTrace(); 
		} finally{
			if(name != null){
				try{
					name.close();
				} catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}

	public static void printBlank(int size){
		for(int i=0; i<size; i++){
			System.out.print("\t");
		}
	}

	public static int checkMax(){
		int target = -1;
		double max = 0.0;
		Arrays.fill(gain,0.0);
		colGain(gain);

		for(int i=1; i<gain.length; i++){
			if(gain[i] > max && !visAttributes[i]){
				max = gain[i];
				target = i;
			}
		}

		if(target == -1)
			return -1;

		visAttributes[target] = true;
		return target;
	}

	public static LinkedList<String> ID3(int target, String[] examples, String[][] attributes, LinkedList<String> tree, int nIteracs){
		int nProb;
		nIteracs++;
		tree.addLast(attributes[0][target]);
		
		printBlank(nIteracs);
		System.out.println("<" + attributes[0][target] + ">");

		for(int i=0; i<diffClass.length; i++){
			nProb = 0;

			for(int j=0; j<examples.length; j++){
				if(partialProbability(target,i,examples[j]) == 1.0)
					nProb++;
			}
			if(examples.length == nProb){
				for(int k=0; k<examples.length; k++){
					printBlank(nIteracs);
					System.out.println(examples[k] + ": " + diffClass[i] + "-" + nProb);
				}
				return tree;
			}
		}
		boolean completeExamples[] = new boolean[examples.length];
		Arrays.fill(completeExamples,false);

		for(int i=0; i<diffClass.length; i++){
			for(int j=0; j<examples.length; j++){
				if(completeExamples[j] == false){
					if(partialProbability(target,i,examples[j]) == 1){
						int counter = getCounter(target,examples[j]);
						tree.addLast(diffClass[i]);
						completeExamples[j] = true;
						printBlank(nIteracs);
						System.out.println(examples[j] + ": " + diffClass[i] + " " + counter);
						isComplete(target,examples[j]);
					}
				}
			}
		}
		for(int i=0; i<examples.length; i++){
			if(completeExamples[i] == false){
				int nMax = checkMax();
				if(nMax == -1)
					return tree; 

				printBlank(nIteracs);
				System.out.println(examples[i]);
				ID3(nMax, differentVals(nMax), attributes, tree, nIteracs);
			}
		}
		return tree;
	}

	public static void main(String[] args){
		clearScreen();
		iniScreen();
		fileParser();
		clearScreen();
		System.out.println("GENERATED TREE:\n");

		gain = new double[nCols-1];
		visAttributes = new boolean[nCols-1];
		complete = new boolean[nRows];

		Arrays.fill(visAttributes,false);
		Arrays.fill(complete,false);

		diffClass = differentVals(nCols-1);
		gain = colGain(gain);

		int target = checkMax();
		if(target != -1)
			visAttributes[target] = true;

		LinkedList<String> tree = new LinkedList<String>();
		if(target == -1)
			ID3(1,differentVals(1),data,tree,-1);
		else
			ID3(target,differentVals(target),data,tree,-1);
	}
}