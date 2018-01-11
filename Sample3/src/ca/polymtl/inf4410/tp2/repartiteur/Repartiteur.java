package ca.polymtl.inf4410.tp2.repartiteur;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;


import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Pair;
import ca.polymtl.inf4410.tp2.shared.ServersConfig;

public class Repartiteur {
	public static void main(String[] args) {		
		Repartiteur repartiteur = new Repartiteur();
		
		String nomFichier = "";
		String mode = "";
		if (args.length >= 2) {
			nomFichier = args[0];
			mode = args[1];
			repartiteur.run(nomFichier,mode);
		}
		else{
			System.out.println("Inserer un nom de fichier a executer comme premier parametre et un mode d'execution comme second parametre");
		}
		
	}


	ArrayList<ServersConfig> serverList;
	ArrayList<ServerInterface> serverStubList;
	// Liste d'operations a effectuer
	ArrayList<Pair> operations;
	// Liste de reponses
	ArrayList<Integer> resultats = null;
		
	public Repartiteur() {
		super();
		
		serverList = new ArrayList<ServersConfig>();
		serverStubList = new ArrayList<ServerInterface>();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		// Lire fichier config pour obtenir les infos sur les serveurs
		updateServers();
		for (int counter = 0; counter < serverList.size(); counter++) { 
			ServerInterface rmiServerStub = loadServerStub(serverList.get(counter));
			serverStubList.add(rmiServerStub);
			try{
				rmiServerStub.setQ(serverList.get(counter).getQ());
				rmiServerStub.setM(serverList.get(counter).getMode());
			}
			catch (RemoteException e) {
				System.out.println("Erreur set Q: " + e.getMessage());
			}
		}   	
		

	}

	private void run(String nom, String mode) {
		
		
		long temps = 0;
		long start = System.nanoTime();
			
		// Lire le fichier avec les operations
		contenuFichier(nom);
		// Executer selon le mode d'execution
		if(mode.equals("n")){
			repartitionNonSecurisee();
		}
		else if(mode.equals("s")){
			repartitionSecurisee();
		}
		
		// Calculer temps d'execution
		long end = System.nanoTime();		
		temps += (end - start);	

		System.out.println("Temps écoulé: " + temps + " ns");
	}

	private ServerInterface loadServerStub(ServersConfig config) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(config.getIP(), config.getPort());
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}
	
	// Cette fonction lit les operations a partir d'un fichire txt et ajoutes ces operations dans un ArrayList
	public void contenuFichier(String nom){
		operations = new ArrayList<Pair>();
		try{
			InputStream ips=new FileInputStream("./Fichiers fournis TP2 INF4410/"+nom); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				String[] calcul = ligne.split(" ");
				int calcul1 = (int)Integer.parseInt(calcul[1]);
				Pair paire = new Pair(calcul[0],calcul1);
				operations.add(paire);
			}
			br.close(); 
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}	
	}

	// Cette methode lis le fichier de configuration pour savoir la configuration de serveurs de calcul 
	public void updateServers(){
		try{
			serverList.clear();
			InputStream ips=new FileInputStream("./fichierConfig"); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			String firstLine = br.readLine();
			while ((ligne=br.readLine())!=null){
				String[] info = ligne.split("\t");
				int info1 = (int)Integer.parseInt(info[1]);
				int info2 = (int)Integer.parseInt(info[2]);
				int info3 = (int)Integer.parseInt(info[3]);
				ServersConfig serverConfig = new ServersConfig(info[0],info1,info2,info3);
				serverList.add(serverConfig);
			}
			br.close(); 
			}		
			catch (Exception e){
			System.out.println(e.toString());
			}	
		}
	
	// Cette methode execute un calcul en mode securisee
	public void repartitionSecurisee(){
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<threadServeur> threadsClass = new ArrayList<threadServeur>();
		int nombreDeTachesParServeur = operations.size()/serverStubList.size();
	    int finalResult = 0;
		Boolean tachesNonfinies = true; 
		 
		// On continue s'il reste des taches a faire
		while(tachesNonfinies){
			tachesNonfinies = false;
			int i = 0;	
			int j = 1;
			threadsClass.clear();
			threads.clear();
			nombreDeTachesParServeur = operations.size()/serverStubList.size();
			// Creer un thread par serveur disponible
			for (ServerInterface stub : serverStubList){
				ArrayList<Pair> taches = new ArrayList<Pair>();
				int offset = 0;
				if(j == serverStubList.size() && serverStubList.size() != 1){
					offset = operations.size() % serverStubList.size();
				}
				// Repartir equitablement le nombre de taches par le nombre de serveurs
				for(;i < (nombreDeTachesParServeur*j) + offset; i++){
					taches.add(operations.get(i));
				}
				threadServeur threadClass = new threadServeur(taches,stub,j-1);
				threadsClass.add(threadClass);
				Thread thread = new Thread(threadClass);
				threads.add(thread);
				thread.start();
				j++;
			}
			operations.clear();
			
			try {
				int l = 0;
				for (Thread thread : threads){ 
					thread.join();
					// Si un serveur a crash on repartit ses taches avec les autres serveurs
					if(threadsClass.get(l).getResult() == -1){
						tachesNonfinies = true;
						operations.addAll(threadsClass.get(l).Taches);
						serverStubList.remove(threadsClass.get(l).index);
					}
					// Si le serveur a reussi
					else{
						finalResult = (finalResult + threadsClass.get(l).getResult()) % 4000;
					}
					l++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Final result = " + finalResult);
	}
	
	// Cette methode execute un calcul en mode non securisee
	public void repartitionNonSecurisee(){
		
	ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<threadServeur> threadsClass = new ArrayList<threadServeur>();
		ArrayList<ArrayList<Pair>> ListeTaches = new ArrayList<ArrayList<Pair>>();
		ArrayList<Integer> resultatsFinaux = new ArrayList<Integer>();
		int nombreDeOperationsParTache;
		int nombreDeTaches;
	    int finalResult = 0;
		Boolean tachesNonfinies = true; 
		Boolean ServeurCrash = false; 
		nombreDeTaches = serverStubList.size()-2;
		nombreDeOperationsParTache = operations.size()/nombreDeTaches;
		// On continue s'il reste des taches a faire
		while(tachesNonfinies){
			tachesNonfinies = false;
			int j = 1;
			threadsClass.clear();
			threads.clear();
			nombreDeTaches = serverStubList.size()-2;
			//S'il n'y a que deux serveurs fonctionnels, ce mode ne peut etre utilise car nous n'auront aucun moyen de verifier si un des deux serveurs ne sont pas malicieux
			if(serverStubList.size() == 2){
				System.out.println(" \n\t!!!! Un de vos serveurs a crash !!!!\n!!!! Ce mode ne fonctionne pas s'il n'y a que deux serveurs operationnels !!!! \n");
				return;
			}
			nombreDeOperationsParTache = operations.size()/nombreDeTaches;
			int offset = operations.size() % nombreDeTaches;
			// S'il y a un serveur qui a crash, la liste des taches est effacee pour ne pas garder les anciennes taches
			if(ServeurCrash){
				ListeTaches.clear();
			}
			for(int indexTache = 0; indexTache < nombreDeTaches; indexTache++){				
				ArrayList<Pair> temp;
				if(indexTache == nombreDeTaches - 1){
					temp = new ArrayList<Pair>(operations.subList(indexTache * nombreDeOperationsParTache, (indexTache * nombreDeOperationsParTache) + nombreDeOperationsParTache + offset));
					ListeTaches.add(temp);
				}
				else{
					temp = new ArrayList<Pair>(operations.subList(indexTache * nombreDeOperationsParTache, (indexTache * nombreDeOperationsParTache) + nombreDeOperationsParTache));
					ListeTaches.add(temp);
				}	
			}
			// Creer un thread par serveur disponible
			for(int index = 0; index < nombreDeTaches; index++){
				
				for(int i = index; i < index+3; i++){
					ArrayList<Pair> tache = new ArrayList<Pair>(ListeTaches.get(index));
					threadServeur threadClass = new threadServeur(tache,serverStubList.get(i),i);
					threadsClass.add(threadClass);
					Thread thread = new Thread(threadClass);
					threads.add(thread);
					thread.start();		
				}
			}
			operations.clear();
			
			try {
				int l = 0;
				for (Thread thread : threads){ 
					thread.join();
					// Si un serveur a crash on repartit ses taches avec les autres serveurs
					if(threadsClass.get(l).getResult() == -1){
						tachesNonfinies = true;
						operations.addAll(threadsClass.get(l).Taches);
						serverStubList.remove(threadsClass.get(l).index);
					}
					l++;
				}
				
				
				for (int indexclass = 0; indexclass < threadsClass.size(); indexclass+=3){ 
					int resultatTache = 0;
					//Verification du bon fonctionnement de tous les serveurs
					if((threadsClass.get(indexclass).getResult() == -1) ||
					 (threadsClass.get(indexclass+1).getResult() == -1) ||
					 (threadsClass.get(indexclass+2).getResult() == -1)){
						ServeurCrash = true;
						continue;					
					}
					else{
										
						if(threadsClass.get(indexclass).getResult() == threadsClass.get(indexclass+1).getResult()){
							resultatTache = threadsClass.get(indexclass).getResult();
						}
						else{
							resultatTache = threadsClass.get(indexclass+2).getResult();
						}
						finalResult = (finalResult + resultatTache) % 4000;
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Final result = " + finalResult);
	}
	
	// Cette classe sera attibue a chaque thread de serveur de calcul
	public class threadServeur implements Runnable {
		
	  public ArrayList<Pair> Taches;
	  private int TachesParBlock = 6;
	  private int finalResult = 0;
	  private ServerInterface stub;
	  public int index;
	  

	  public threadServeur(ArrayList<Pair> taches, ServerInterface stub, int index){
		this.Taches = taches;
		this.stub = stub;
		this.index = index;
		this.finalResult = 0;		
	  }
	  
	  public int getResult(){
		  return this.finalResult;
	  }	  
	  
	  public void run() {
		while(this.Taches.size() != 0){
			// Creer un block pour envoyer au serveur		
			ArrayList<Pair> block = new ArrayList<Pair>();
			// Reemplir le block de operations			
			for(int i = 0 ; i < this.TachesParBlock; i++){
				if(i < this.Taches.size()){
					block.add(this.Taches.get(i));
				}	
			}	
			// Envoyer calcul au serveur 
			ArrayList<Integer> results = null;		
			try{
				results = stub.calculer(block);		
			}
			catch (RemoteException e) {
				System.out.println("Erreur: " + e.getMessage());
				this.finalResult = -1;
				return;
			}
						
						
			if(results == null){
				// Si le serveur n'est pas capable de traiter autant de taches, il va reduire le nombre de taches par block
				this.TachesParBlock--;	
			}
			else if(results != null){
				// On est capable de traiter encore plus de taches par block (peut-etre)
				this.TachesParBlock++;
				
				for (Integer result : results){
					this.finalResult = (this.finalResult + result) % 4000;
				}
				Taches.removeAll(block);
			}		
		}	
	  }                       
	}               
  
}



