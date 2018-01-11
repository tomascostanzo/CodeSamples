///////////////////////////////////////////////////////////////////////////
/// @file algo.java
/// @author Tomas Costanzo
///
/// Un algorithme pour resoudre un problème combinatoire avec une approche las Vegas.
/// Trois types de noeuds: entree, etapes, Vue(racine).
/// À chaque sentier possible reliant deux points d’intérêt est associé un coût.
/// Resultat : Tracé des chemins qui répond aux contraintes et qui a un coût total minimal.
///////////////////////////////////////////////////////////////////////////


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.util.*;
import java.util.LinkedList;



///////////////////////////////////////////////////////////////////////////
/// @class algo
/// @brief Classe qui represente l'algorithme de type vegas pour resoudre le problème combinatoire
/// @author Tomas Costanzo
/// @date 2017
///////////////////////////////////////////////////////////////////////////
public class algo {
	
	// Initialisation de attributs
	static Random rand = new Random();
	static int max = 0;
	static double CoutMax = 0;   
	static int nbrPoints = 0;
	static long startTime = 0;
	static boolean printTime = false;
	static double[][] matriceCouts = null; //Matrice des couts n*n
	static Integer[] typesArray = null;
	static ArrayList<Integer> noeuds = null;
	static int[] nombreMaximumArretesTemp = null;

	public static void main(String[] args) throws FileNotFoundException {
	  	
	  int[] nombreMaximumArretes = null;//Nombre maximum d'arretes par point	
	  int minLinks = 0;
	  //liste de types de noeud
	  ArrayList<Integer> Vues = new ArrayList<Integer>();
	  ArrayList<Integer> entrees = new ArrayList<Integer>();
	  ArrayList<Integer> etapes = new ArrayList<Integer>();
	  noeuds = new ArrayList<Integer>();
	    	
	  //Scanner
		Scanner s = new Scanner(new File(args[0]));
	
		if(s.hasNext()){
			nbrPoints = Integer.parseInt(s.next());// Nombre de points d'interet
		}
		
		typesArray = new Integer[nbrPoints];
		
		int index = 0;
		int type = 0;
		for(int i=0; i<nbrPoints; i++){ //Ici on reemplis les tableaux par type de noeud
			if(s.hasNext()){
				type = Integer.parseInt(s.next());
				typesArray[i] = type;
				switch (type) {
				
				case 1:
					Vues.add(index);
					break;
				case 2:
					entrees.add(index);
					break;
				case 3: 
					etapes.add(index);
					break;	
				}	
				noeuds.add(index);
				index++;
			}
		}
		
		max = entrees.size() + etapes.size() - 1;
		
		nombreMaximumArretes = new int[nbrPoints];
		for(int i=0; i<nbrPoints; i++){
			if(s.hasNext()){
				nombreMaximumArretes[i] = Integer.parseInt(s.next());//On reemplis le tableau qui contient les contraintes sur les nbr max de segments
			}
		}
		
		matriceCouts = null; //Matrice des couts n*n
		
		matriceCouts = new double[nbrPoints][nbrPoints];
		for(int i=0; i<nbrPoints; i++){
			for(int j=0; j<nbrPoints; j++){
				if(s.hasNext()){
					matriceCouts[i][j] = Double.parseDouble(s.next());
					CoutMax += matriceCouts[i][j];
				}	
			}
		}
		CoutMax /= 2; 
		
		s.close();
		
		//Debuter le calcul de temps
		startTime = System.nanoTime();
		nombreMaximumArretesTemp = nombreMaximumArretes.clone();
		
		final long tempsCalcul = System.nanoTime() - startTime;
		
		if(args.length > 1){//Pour savoir si on print le temps de calcul
			if(args[1].equals("-t")){
				printTime = true;
			}
			else if(args[1].equals("-p")){
				printTime = false;
			}
		}
		while(true){
			calculCheminMinimal(Vues, etapes, entrees);
			nombreMaximumArretesTemp = nombreMaximumArretes.clone(); //Faire calcul ici
		}
		//Fin algo	
	}  
	
	////////////////////////////////////////////////////////////////////////
	///
	/// @fn calculCheminMinimal(ArrayList<Integer> Vues, ArrayList<Integer> etapes, ArrayList<Integer> entrees)
	///
	/// Algorithme pour le calcul du graphe de poid minimum en respectant les contraintes. Type las Vegas
	///
	/// @param[in] ArrayList<Integer> Vues : les noeuds de type vues passe en reference
	/// @param[in] ArrayList<Integer> etapes : les noeuds de type etapes passe en reference
	/// @param[in] ArrayList<Integer> entrees : les noeuds de type entrees passe en reference
	///
	/// @return Aucun (fonction void).
	///
	////////////////////////////////////////////////////////////////////////
	public static void calculCheminMinimal(ArrayList<Integer> Vues, ArrayList<Integer> etapes, ArrayList<Integer> entrees){

		//Initialisation variables
		Graph g = new Graph(nbrPoints);
		ArrayList<Integer[]> couples = new ArrayList<Integer[]>();
		double currentCout = 0;
		int[] nombreMaximumArretes = nombreMaximumArretesTemp.clone();
		ArrayList<Integer> noeudsTemp = (ArrayList<Integer>) noeuds.clone();
		int randomNum = 0;
		int Tour = noeudsTemp.size();

		p: while (Tour > 0) {//Parcours des tous les noeuds
			
			//Verifier si l'algoritme a deja atteint sont nombre minimal (acceptable) de liens
			boolean finis = false;
			boolean valide = true;
			for(int i=0; i<noeuds.size(); i++){ //Parcourir tous les noeuds
				finis = false;
				int T = typesArray[i];
				switch (T) { //Selon le type de noeud on verifie si on a satisfait les contraintes minimales
				
				case 1:
					if(nombreMaximumArretes[i] - nombreMaximumArretesTemp[i] >= 1){
						finis = true;
					}
					break;
				case 2:
					if(nombreMaximumArretes[i] - nombreMaximumArretesTemp[i] >= 1){
						finis = true;
					}
					break;
				case 3: 
					if(nombreMaximumArretes[i] - nombreMaximumArretesTemp[i] >= 2){
						finis = true;
					}
					break;
				}
				
				if(!finis){
					valide = false;
					break;
				}
			}
			
			if(valide == true){
				break;//Si on a satisfait les contraintes on finis l'algo et on verifie si le graphe est correct
			}
			
			//Si on n'a pas atteint le nombre minimal de liens on continue	
			int randomIndex = rand.nextInt(((Tour - 1)) + 1);//Choisir un element random
			Integer noeud = noeudsTemp.get(randomIndex);
			
			switch(typesArray[noeud]) {//Verifier le type du noeud choisis
			
			case 1: //VUES
				
				if(nombreMaximumArretesTemp[noeud] == 0){
					noeudsTemp.remove(randomIndex);
					Tour--;
					continue p;
				}
		    	
			  randomNum = rand.nextInt((max) + 1); //entre 0 et max de entrees et etapes
			  ArrayList<Integer> NoeudsArrayTemp = new ArrayList<Integer>();
			  while(nombreMaximumArretesTemp[randomNum] <= 0){
			  	if(NoeudsArrayTemp.size() == max+1){
			  		noeudsTemp.remove(randomIndex);
			  		Tour--;
			  		continue p;		
			  	}
			  	randomNum = rand.nextInt((max) + 1);  	
			  	if(!NoeudsArrayTemp.contains(randomNum)){
			  		NoeudsArrayTemp.add(randomNum);
			  	}
			  }
			  nombreMaximumArretesTemp[randomNum] -= 1;
			  nombreMaximumArretesTemp[noeud] -= 1;
			  Integer[] pair = {noeud,randomNum};
				couples.add(pair);
				currentCout += matriceCouts[noeud][randomNum];
				if(currentCout > CoutMax){ //Si on depasse notre cout cible on arrete
					return;
				}
				g.addEdge(noeud, randomNum);
				g.addEdge(randomNum, noeud);
				
				if(nombreMaximumArretesTemp[noeud] == 0){
					noeudsTemp.remove(randomIndex);
					Tour--;
				}	
				break;
						
			case 3: //ETAPES
				
				if(nombreMaximumArretesTemp[noeud] == 0){
					noeudsTemp.remove(randomIndex);
					Tour--;
					continue p;
				}
				
			  randomNum = rand.nextInt((max + Vues.size()) + 1); //entre 0 et max de entrees et etapes
			  
		    Integer[] pairTest = new Integer[2];
		    pairTest[0] = noeud;
		    pairTest[1] = randomNum;
		    
		    Integer[] pairTest2 = new Integer[2];
		    pairTest2[0] = randomNum;
		    pairTest2[1] = noeud;
		    
		    ArrayList<Integer> test = new ArrayList<Integer>();

		    //On continue si on a pas trouve un bon candidat
			  while(nombreMaximumArretesTemp[randomNum] <= 0 || randomNum == noeud || containsSubArray(couples, pairTest) || containsSubArray(couples, pairTest2)){
			
			  	if(test.size() == max + Vues.size() + 1){
			  		noeudsTemp.remove(randomIndex);
			  		Tour--;
			  		continue p;		
			  	}
			  	
			  	randomNum = rand.nextInt((max + Vues.size()) + 1); 
			  	
			  	if(!test.contains(randomNum)){
			  		test.add(randomNum);
			  	}
			  	
			  	pairTest[0] = noeud;
			  	pairTest[1] = randomNum;
			  	
			  	pairTest2[0] = randomNum;
			  	pairTest2[1] = noeud;
			  }
			  nombreMaximumArretesTemp[randomNum] -= 1;
			  nombreMaximumArretesTemp[noeud] -= 1;
			  Integer[] pair2 = {noeud,randomNum};
				couples.add(pair2);
				currentCout += matriceCouts[noeud][randomNum]; 
				if(currentCout > CoutMax){ //Si on depasse notre cout cible on arrete
					return;
				}
				g.addEdge(noeud, randomNum);
				g.addEdge(randomNum, noeud);
				
		    
			if(nombreMaximumArretesTemp[noeud] == 0){
				noeudsTemp.remove(randomIndex);
				Tour--;
			}
		
		    break;
			    
			case 2: //ENTREES
				
				if(nombreMaximumArretesTemp[noeud] == 0){
					noeudsTemp.remove(randomIndex);
					Tour--;
					continue p;//Si l'entree a deja satisfait ces liens on arrete
				}
				
			  randomNum = rand.nextInt(((max + Vues.size()) - entrees.size()) + 1) + entrees.size(); //entre entrees max et max de entrees et etapes
		    Integer[] pairTest3 = new Integer[2];
		    pairTest3[0] = noeud;
		    pairTest3[1] = randomNum;
		    
		    Integer[] pairTest4 = new Integer[2];
		    pairTest4[0] = randomNum;
		    pairTest4[1] = noeud;
		    ArrayList<Integer> test2 = new ArrayList<Integer>();
		    
		    //On continue si on a pas trouve un bon candidat
			  while(nombreMaximumArretesTemp[randomNum] <= 0 || containsSubArray(couples, pairTest3) || containsSubArray(couples, pairTest4)){
		
			  	if(test2.size() == max + Vues.size() + 1 - entrees.size()){
			  		noeudsTemp.remove(randomIndex);
			  		Tour--;
			  		continue p;    		
			  	}
	
			  	randomNum = rand.nextInt(((max + Vues.size()) - entrees.size()) + 1) + entrees.size();
			  	
			  	if(!test2.contains(randomNum)){
			  		test2.add(randomNum);
			  	}
			  	
			  	pairTest3[0] = noeud;
			  	pairTest3[1] = randomNum;
			  	pairTest4[0] = randomNum;
			  	pairTest4[1] = noeud;
			  }
			  nombreMaximumArretesTemp[randomNum] -= 1;
			  nombreMaximumArretesTemp[noeud] -= 1;
			  Integer[] pair3 = {noeud,randomNum};
				couples.add(pair3);
				currentCout += matriceCouts[noeud][randomNum];
				if(currentCout > CoutMax){ //Si on depasse notre cout cible on arrete
					return;
				}
				g.addEdge(noeud, randomNum);
				g.addEdge(randomNum, noeud);
				
				if(nombreMaximumArretesTemp[noeud] == 0){
					Tour--;
					noeudsTemp.remove(randomIndex);
				}
				
				break;
				
			default:
				break;
			}
			
		}
		
		boolean lienMinimumValide = false;
		String MinimumValide = "valide";
		
		//Ici on verifie si chaque Etape contient un minimum de 2 liens
		for (Integer etape : etapes) {//ETAPES
			lienMinimumValide = false;
			if(nombreMaximumArretes[etape] - nombreMaximumArretesTemp[etape] >= 2){
				lienMinimumValide = true;
			}
			if(!lienMinimumValide){
				MinimumValide = "not valide";
				break;
			}		
		}
		
		boolean linked = false;
		String valide = "valide";
		
		//Ici on verifie si chaque Etape est connectee a au moins une entree
		for (Integer etape : etapes) {//ETAPES
			linked = false;
			for (Integer entree : entrees) {//ENTREES
				if(g.isReachable(entree, etape)){
					linked = true;
					break;	
				}			
			}	
			if(!linked){
				valide = "not valide";
				break;
			}
		}
		
		//Ici on verifie si chaque Vue est connectee a au moins une entree
		for (Integer Vue : Vues) {//VUES
			linked = false;
			for (Integer entree : entrees) {//ENTREES
				if(g.isReachable(entree, Vue)){
					linked = true;
					break;	
				}			
			}	
			if(!linked){
				valide = "not valide";
				break;
			}
		}	
		
		//Si notre resultat est valide et est le meilleur trouvee on le garde
		if(currentCout < CoutMax && valide == "valide" && MinimumValide == "valide"){	 
			CoutMax = currentCout;
			System.out.println("-----------------------------------------------");
			System.out.println("Cout: " + CoutMax);
			System.out.println("");
			int PrintCoupleGauche = 0;
			for(int i=0; i<couples.size(); i++){
				PrintCoupleGauche = couples.get(i)[0];
				String PrintCoupleGauchePrint = String.valueOf(PrintCoupleGauche);
				if(PrintCoupleGauche < 10){
					PrintCoupleGauchePrint += " ";
				}
				System.out.println(PrintCoupleGauchePrint + " " + couples.get(i)[1]);	
			}
			System.out.println("Fin");
			
			
			final long tempsCalcul = System.nanoTime() - startTime;
			
			if(printTime  == true){
				System.out.println("");
				System.out.println("Temps de Calcul (Nanosecondes) : " + tempsCalcul);
			}
			
			System.out.println("-----------------------------------------------");		
		}
		
		return;
	}
	
	public static boolean containsSubArray(ArrayList<Integer[]> couples2, Integer[] pairTest) {
		for ( Integer[] arr : couples2 ) {
			if (Arrays.equals(arr, pairTest)) {
				return true;
		    }
		}
		return false;
	}
}

//-------------------------------------------------------------

//This class represents a directed graph using adjacency list
//representation
class Graph
{
 	private int V;   // No. of vertices
 	private LinkedList<Integer> adj[]; //Adjacency List

 	//Constructor
 	Graph(int v)
 	{
    V = v;
    adj = new LinkedList[v];
    for (int i=0; i<v; ++i)
      adj[i] = new LinkedList();
 	}

 	//Function to add an edge into the graph
 	void addEdge(int v,int w)  {   adj[v].add(w);   }

 	//prints BFS traversal from a given source s
	Boolean isReachable(int s, int d)
 	{
    LinkedList<Integer>temp;

    // Mark all the vertices as not visited(By default set
    // as false)
    boolean visited[] = new boolean[V];

    // Create a queue for BFS
    LinkedList<Integer> queue = new LinkedList<Integer>();

    // Mark the current node as visited and enqueue it
    visited[s]=true;
    queue.add(s);

    // 'i' will be used to get all adjacent vertices of a vertex
    Iterator<Integer> i;
    while (queue.size()!=0)
    {
      // Dequeue a vertex from queue and print it
      s = queue.poll();

      int n;
      i = adj[s].listIterator();

      // Get all adjacent vertices of the dequeued vertex s
      // If a adjacent has not been visited, then mark it
      // visited and enqueue it
      while (i.hasNext())
      {
        n = i.next();

        // If this adjacent node is the destination node,
        // then return true
        if (n==d)
          return true;

        // Else, continue to do BFS
        if (!visited[n])
        {
          visited[n] = true;
          queue.add(n);
        }
      }
    }

    // If BFS is complete without visited d
    return false;
 }

}
//This code is contributed by Aakash Hasija


