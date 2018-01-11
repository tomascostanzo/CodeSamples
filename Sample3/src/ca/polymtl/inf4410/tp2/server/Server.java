package ca.polymtl.inf4410.tp2.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Operations;
import ca.polymtl.inf4410.tp2.shared.Pair;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		int port = 0;
		String portArg = "";
		if (args.length > 0) {
			portArg = args[0];
		}		
		port = (int)Integer.parseInt(portArg);
		
		Server server = new Server();
		server.run(port);
	}

	public Server() {
		super();
		rand = new Random();
	}
	
	// Attributs
	Operations calculateur;
	int q = 0;
	int m = 0;
	Random rand;
	
	private void run(int port) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 5002);

			Registry registry = LocateRegistry.getRegistry(port);
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
		
		calculateur = new Operations();
	}
	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	public String test()throws RemoteException{
			return "Serveur active";
	}
	
	// Cette methode execute une liste d'operations et retoune une liste de reponses a chaque operation dans la liste de depart
	public ArrayList<Integer> calculer(ArrayList<Pair> operations)throws RemoteException{
		
		int Taux = tauxRefus(this.q,operations.size());
		if(Taux > 20){
			// ne peut pas traiter ce nombre d'operations
			return null;
		}
				
		ArrayList<Integer> resultats = new ArrayList<Integer>();
		
		if(operations != null){
			for (Pair paire : operations){
				
				int randomNum = rand.nextInt(101);
				// Si le serveur est malicieux on generee une mauvaise reponse avec m % de chances
				if(this.m != 0 && randomNum <= this.m){
						resultats.add(0);
				}
				else{
					Integer resultat = 0;
					if(paire.getOperation().equals("prime")){
						resultat = calculateur.prime(paire.getOperande());
					}
					else if(paire.getOperation().equals("pell")){
						resultat = calculateur.pell(paire.getOperande());
					}
					resultats.add(resultat);					
				}
			}
		}
		return resultats;
	}


	public void setQ(int q_) throws RemoteException{
			this.q = q_;
	}
	
	public void setM(int m_) throws RemoteException{
			this.m = m_;
	}
	
	public int tauxRefus( int nbOperations, int nbTaches){
		int T;
		T = ((nbTaches - nbOperations) / (5 * nbOperations)) * 100;
		return T;
	}
	
}
