///////////////////////////////////////////////////////////////////////////
/// @file VisiteurSeelction.h
/// @author Tomas Costanzo
/// @date 2016-09-09
/// @version 1.0
/// @addtogroup inf2990 INF2990
///
///////////////////////////////////////////////////////////////////////////
#ifndef __ARBRE_VISITEUR_VISITEURSELECTION_H__
#define __ARBRE_VISITEUR_VISITEURSELECTION_H__

#include "VisiteurAbstrait.h"

class FacadeModele;

///////////////////////////////////////////////////////////////////////////
/// @class VisiteurSelection
/// @brief Cette Classe represente le visiteur qui permet de selectionner les objets dans la scene
///        
/// @author Tomas Costanzo
/// @date 2016
///////////////////////////////////////////////////////////////////////////

class VisiteurSelection : public VisiteurAbstrait
{

public:

	///Constructeur du visiteur de selection
	VisiteurSelection(glm::dvec3 pos, glm::dvec3 pos2, bool ctrl);

	///Applique la selection pour chaque type de noeud
	virtual void visiterNoeudAbstrait(NoeudAbstrait* elem);
	virtual void visiterNoeudComposite(NoeudComposite* elem);
	virtual void visiterNoeudPortail(NoeudPortail* elem);
	virtual void visiterNoeudBonus(NoeudBonus* elem);

	friend class VisiteurSelectionTest;

private:

	glm::dvec3 positionAnterieure;
	glm::dvec3 NouvellePosition;
	bool ctrl_;
	double dX;
	double dY;
	utilitaire::BoiteEnglobante boiteSelection;
	bool interieur(NoeudAbstrait* elem);
};

#endif //ARBRE_VISITEUR_VISITEURAJOUT_H__

///////////////////////////////////////////////////////////////////////////////
/// @}
///////////////////////////////////////////////////////////////////////////////