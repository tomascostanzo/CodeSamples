Pour ajouter des nouveaux serveurs de calcul ou pour modifier les configurations de chaque serveur existant il faut faire les étapes suivantes:

	1_ Ouvrir le fichier fichierConfig et modifier/ajouter les configurations. A noter que les différentes configurations doivent être separées par des tabulations (TABs).
	2_ Pour lancer un nouveau serveur: Il faut lancer le serveur avec "./server numPort", avec numPort etant le numero du port du serveur (le même qui a été déclaré dans le fichierCondig du repartiteur). Ce numéro de port est aussi celui donné au registre rmi lors de son lancement (rmiregistry numPort &).

Pour lancer le répartiteur il faut exécuter la ligne de commande suivante: "./repartiteur nomFichier mode" Avec nomFichier étant le nom du fichier avec les opérations, et mode le mode d'exécution. m doit être égal à "n" pour une execution en mode non-securisée et égal à "s" pour une exécution en mode securisée. 


Attention, il faut compiler le code avant le test.
