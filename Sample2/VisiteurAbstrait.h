///////////////////////////////////////////////////////////////////////////
/// @file AbstractVisiteur.h
/// @author Tomas Costanzo
/// @date 2016-09-09
/// @version 1.0
///
/// @{
///////////////////////////////////////////////////////////////////////////
#ifndef __ARBRE_VISITEUR_VISITEURABSTRAIT_H__
#define __ARBRE_VISITEUR_VISITEURABSTRAIT_H__

#include "../Noeuds/NoeudTypes.h"

///////////////////////////////////////////////////////////////////////////
/// @class VisiteurAbstrait
/// @brief Classe  abstraite qui represente le visiteur qui fait heriter 
///             tous les autres visiteurs
///        
///        
///        
///        
///             
///        
///
/// @author Tomas Costanzo
/// @date 2016
///////////////////////////////////////////////////////////////////////////


class VisiteurAbstrait
{
public:
	///Le visiteur abstrait et les fonctions pour visiter chaque type de noeud dans l'arbre
	virtual void visiterNoeudAbstrait(NoeudAbstrait* elem) {};
	virtual void visiterNoeudComposite(NoeudComposite* elem) {};
	virtual void visiterNoeudPortail(NoeudPortail* elem) {};
	virtual void visiterNoeudRondelle(NoeudRondelle* elem) {};
	virtual void visiterNoeudBut(NoeudBut* elem) {};
	virtual void visiterNoeudBonus(NoeudBonus* elem) {};
	virtual void visiterNoeudMuret(NoeudMuret* elem) {};
	virtual void visiterNoeudMaillet(NoeudMaillet* elem) {};
	virtual void visiterNoeudTable(NoeudTable* elem) {};
	virtual void visiterNoeudPointDeControle(NoeudPointDeControle* elem) {};
	void assignerTable(NoeudAbstrait* table) { table_ = table; }
	

protected:

	NoeudAbstrait* table_;


	////////////////////////////////////////////////////////////////////////
	///
	/// @fn bool PointDansTriangle(glm::vec3 s, glm::vec3 a, glm::vec3 b, glm::vec3 c)
	///
	/// Cette fonction permet de verifier si un point soumis se trouve dans un triangle
	/// 
	///
	/// @param[in] type  : le point ainsi que les sommets du triangle
	///                    
	///
	/// @return booleen.
	///
	////////////////////////////////////////////////////////////////////////


	bool PointDansTriangle(glm::vec3 s, glm::vec3 a, glm::vec3 b, glm::vec3 c)
	{

		int difX = s[0] - a[0];
		int difY = s[1] - a[1];

		bool difAB = (b[0] - a[0])*difY - (b[1] - a[1])*difX > 0;

		if ((c[0] - a[0])*difY - (c[1] - a[1])*difX > 0 == difAB) return false;

		if ((c[0] - b[0])*(s[1] - b[1]) - (c[1] - b[1])*(s[0] - b[0]) > 0 != difAB) return false;

		return true;

	}




	////////////////////////////////////////////////////////////////////////
	///
	/// @fn bool pointAppartientTable(glm::vec3 obj)
	///
	/// Cette fonction permet de verifier si un objet se trouve sur la table
	/// 
	///
	/// @param[in] type  : l'objet qu'on verifie
	///                    
	///
	/// @return booleen.
	///
	////////////////////////////////////////////////////////////////////////

	bool pointAppartientTable(glm::vec3 obj){

		glm::dvec3 m =  table_->obtenirPositionRelative();
		glm::dvec3 p1 = table_->getPointControle1()->obtenirPositionRelative();
		glm::dvec3 p2 = table_->getPointControle2()->obtenirPositionRelative();
		glm::dvec3 p3 = table_->getPointControle3()->obtenirPositionRelative();
		glm::dvec3 p4 = table_->getPointControle4()->obtenirPositionRelative();
		glm::dvec3 p5 = table_->getPointControle5()->obtenirPositionRelative();
		glm::dvec3 p6 = table_->getPointControle6()->obtenirPositionRelative();
		glm::dvec3 p7 = table_->getPointControle7()->obtenirPositionRelative();
		glm::dvec3 p8 = table_->getPointControle8()->obtenirPositionRelative();

		if (PointDansTriangle(obj, m, p1, p2))
			return true;

		else if (PointDansTriangle(obj, m, p2, p3))
			return true;

		else if (PointDansTriangle(obj, m, p3, p4))
			return true;

		else if (PointDansTriangle(obj, m, p4, p5))
			return true;

		else if (PointDansTriangle(obj, m, p5, p6))
			return true;

		else if (PointDansTriangle(obj, m, p6, p7))
			return true;

		else if (PointDansTriangle(obj, m, p7, p8))
			return true;

		else if (PointDansTriangle(obj, m, p8, p1))
			return true;

		else return false;

	}
	bool appartientTable(NoeudAbstrait* elem) {
		if (pointAppartientTable(elem->getBoite().coinMin)
			&& pointAppartientTable(elem->getBoite().coinMax)
			&& pointAppartientTable(elem->getBoite2().coinMin)
			&& pointAppartientTable(elem->getBoite2().coinMax)) {
			return true;
		}

		return false;

	}
	
};

#endif //ARBRE_VISITEUR_VISITEURABSTRAIT_H__

///////////////////////////////////////////////////////////////////////////////
/// @}
///////////////////////////////////////////////////////////////////////////////