
#include "VisiteurSelection.h"
#include "../ArbreRenduINF2990.h"
#include "../Noeuds/NoeudTypes.h"
#include "FacadeModele.h"
#include "iostream"
#include <glm/gtc/type_ptr.hpp>

////////////////////////////////////////////////////////////////////////
///
/// @fn VisiteurSelection::VisiteurSelection(glm::dvec3 posDebut, glm::dvec3 posFin, bool ctrl, utilitaire::BoiteEnglobante boiteEnglobante)
///
/// 
///
/// @return Aucune (constructeur).
///
////////////////////////////////////////////////////////////////////////

VisiteurSelection::VisiteurSelection(glm::dvec3 posDebut, glm::dvec3 posFin, bool ctrl, utilitaire::BoiteEnglobante boiteEnglobante)
{
	positionAnterieure_ = posDebut;
	NouvellePosition_ = posFin;
	ctrl_ = ctrl;
	boiteSelection_ = boiteEnglobante;
	dX_ = abs(posDebut.x - posFin.x);
	dY_ = abs(posDebut.y - posFin.y);
}


////////////////////////////////////////////////////////////////////////
///
/// @fn VisiteurSelection::visiterNoeudAbstrait(NoeudAbstrait* elem)
///
/// Fonction pour selectionner un noeudAbstrait, pas de fonctionnalite
///
/// @param[in] NoeudAbstrait* elem : le noeud passe en reference
///
/// @return Aucun (fonction void).
///
////////////////////////////////////////////////////////////////////////

void VisiteurSelection::visiterNoeudAbstrait(NoeudAbstrait* elem) {}

////////////////////////////////////////////////////////////////////////
///
/// @fn VisiteurSelection::visiterNoeudComposite(NoeudComposite* elem)
///
/// Fonction pour selectionner un composite, pas de fonctionnalite
///
/// @param[in] NoeudComposite* elem : le noeud passe en reference
///
/// @return Aucun (fonction void).
///
////////////////////////////////////////////////////////////////////////
void VisiteurSelection::visiterNoeudComposite(NoeudComposite* elem) {}

////////////////////////////////////////////////////////////////////////
///
/// @fn VisiteurSelection::visiterNoeudPortail(NoeudPortail* elem)
///
/// Fonction pour selectionner un portail
///
/// @param[in] NoeudPortail* elem : le noeud passe en reference
///
/// @return Aucun (fonction void).
///
////////////////////////////////////////////////////////////////////////

void VisiteurSelection::visiterNoeudPortail(NoeudPortail* elem) {
	// Si l'element est selectioneee par la souris
	if (interieur(elem)) {
		// La touche control inverse la selection
		if (ctrl_) {
			elem->inverserSelection();
		}
		else {
			elem->assignerSelection(true);
		}
	}
	else if (elem->estSelectionne()) {

		if (!ctrl_) {
			elem->assignerSelection(false);
		}
	}

}

////////////////////////////////////////////////////////////////////////
///
/// @fn VisiteurSelection::visiterNoeudBonus(NoeudBonus* elem)
///
/// Fonction pour selectionner un NoeudBonus
///
/// @param[in] NoeudBonus* elem : le noeud passe en reference
///
/// @return Aucun (fonction void).
///
////////////////////////////////////////////////////////////////////////

void VisiteurSelection::visiterNoeudBonus(NoeudBonus* elem) {

	if (interieur(elem)) {
		elem->updateBoite();
		elem->assignerSelection(true);
	}
	else if (elem->estSelectionne()) {
		elem->assignerSelection(false);
	}
}

////////////////////////////////////////////////////////////////////////
///
/// @fn VisiteurSelection::interieur(NoeudAbstrait* elem) 
///
/// Fonction pour verifier si un click est a l'interieur de l'element 
///
/// @param[in] NoeudAbstrait* elem : le noeud passe en reference
///
/// @return Bool.
///
////////////////////////////////////////////////////////////////////////

bool VisiteurSelection::interieur(NoeudAbstrait* elem) {

	utilitaire::BoiteEnglobante boite = elem->getBoite();
	utilitaire::BoiteEnglobante boite3 = elem->getBoite2();
	utilitaire::BoiteEnglobante boite4 = elem->getBoite2();

	// Selection simple
	if (dX_ <= 3 && dY_ <= 3) {
		if (elem->obtenirType() == "muret") {
			// définir une matrice identité
			glm::mat4 mtc = glm::mat4(1.0);

			glm::vec3 axes_ = { 0.0, 0.0, 1.0 };

			// multiplication par une matrice de translation de (x,y,z)
			mtc = glm::translate(mtc, glm::vec3((0.0, 0.0, 0.0)));
			// multiplication par une matrice de rotation autour de (x,y,z)
			mtc = glm::rotate(mtc, -elem->getAngle(), axes_);
			// multiplication par une matrice de mise à l'échelle par (x,y,z) 
			mtc = glm::scale(mtc, glm::vec3(1.0, 1.0, 1.0));
			
			double dArray[16] = { 0.0 };	
			const float *pSource = (const float*)glm::value_ptr(mtc);
			for (int i = 0; i < 16; ++i)
				dArray[i] = pSource[i];
			
			glm::dvec3 positionAnterieureTemp = utilitaire::appliquerMatrice(positionAnterieure_, dArray);

			glm::mat4 mtc2 = glm::mat4(1.0);
			// multiplication par une matrice de translation de (x,y,z)
			mtc2 = glm::translate(mtc2, glm::vec3((0.0, 0.0, 0.0)));
			// multiplication par une matrice de rotation autour de (x,y,z)
			mtc2 = glm::rotate(mtc2, -elem->getAngle(), axes_);
			// multiplication par une matrice de mise à l'échelle par (x,y,z) 
			mtc2 = glm::scale(mtc2, glm::vec3(elem->obtenirScale()[0], elem->obtenirScale()[1], elem->obtenirScale()[2]));

			double dArray2[16] = { 0.0 };
			const float *pSource2 = (const float*)glm::value_ptr(mtc2);
			for (int i = 0; i < 16; ++i)
				dArray2[i] = pSource2[i];

			boite.coinMax = utilitaire::appliquerMatrice(boite.coinMax, dArray);
			boite.coinMin = utilitaire::appliquerMatrice(boite.coinMin, dArray);
			boite4.coinMax = utilitaire::appliquerMatrice(boite4.coinMax, dArray);
			boite4.coinMin = utilitaire::appliquerMatrice(boite4.coinMin, dArray);

			if (utilitaire::DANS_LIMITESXY(positionAnterieureTemp.x, boite.coinMin.x, boite.coinMax.x, positionAnterieureTemp.y, boite.coinMin.y, boite.coinMax.y) && elem->estSelectionnable()
				|| utilitaire::DANS_LIMITESXY(positionAnterieureTemp.x, boite4.coinMin.x, boite4.coinMax.x, positionAnterieureTemp.y, boite4.coinMin.y, boite4.coinMax.y)
				&& elem->estSelectionnable()) {
				return true;
			}
			else if (elem->estSelectionne()) {
				return false;
			}
			return false;
		}

		else {
			boite3 = elem->getBoite();
			boite = elem->getBoite2();
			if (utilitaire::DANS_LIMITESXY(positionAnterieure_.x, boite3.coinMin.x, boite3.coinMax.x, positionAnterieure_.y, boite3.coinMin.y, boite3.coinMax.y)
				|| utilitaire::DANS_LIMITESXY(positionAnterieure_.x, boite3.coinMin.x, boite.coinMin.x, positionAnterieure_.y, boite3.coinMin.y, boite.coinMin.y)
				|| utilitaire::DANS_LIMITESXY(positionAnterieure_.x, boite.coinMin.x, boite.coinMax.x, positionAnterieure_.y, boite.coinMin.y, boite.coinMax.y)
				|| utilitaire::DANS_LIMITESXY(positionAnterieure_.x, boite3.coinMax.x, boite.coinMax.x, positionAnterieure_.y, boite3.coinMax.y, boite.coinMax.y)
				|| utilitaire::DANS_LIMITESXY(positionAnterieure_.x, boite3.coinMax.x, boite.coinMin.x, positionAnterieure_.y, boite3.coinMax.y, boite.coinMin.y)
				|| utilitaire::DANS_LIMITESXY(positionAnterieure_.x, boite3.coinMin.x, boite.coinMax.x, positionAnterieure_.y, boite3.coinMin.y, boite.coinMax.y)
				&& elem->estSelectionnable()) {
				return true;
			}
			else if (elem->estSelectionne()) {
				return false;
			}
			return false;
		}
	}
	// Selection Multiple
	else {
		utilitaire::BoiteEnglobante boite2 = elem->getBoite2();
		double position3 = (boite2.coinMax.x + boite2.coinMin.x) / 2;
		double position4 = (boite2.coinMax.y + boite2.coinMin.y) / 2;
		double positionX = abs(boite.coinMax.x - boite.coinMin.x);
		double positionY = abs(boite.coinMax.y - boite.coinMin.y);
		double pente;

		if (utilitaire::DANS_LIMITESXY(boite.coinMin.x, boiteSelection_.coinMin.x, boiteSelection_.coinMax.x, boite.coinMin.y, boiteSelection_.coinMin.y, boiteSelection_.coinMax.y) ||
			utilitaire::DANS_LIMITESXY(boite.coinMax.x, boiteSelection_.coinMin.x, boiteSelection_.coinMax.x, boite.coinMax.y, boiteSelection_.coinMin.y, boiteSelection_.coinMax.y) ||
			utilitaire::DANS_LIMITESXY(boite2.coinMin.x, boiteSelection_.coinMin.x, boiteSelection_.coinMax.x, boite2.coinMin.y, boiteSelection_.coinMin.y, boiteSelection_.coinMax.y) ||
			utilitaire::DANS_LIMITESXY(boite2.coinMax.x, boiteSelection_.coinMin.x, boiteSelection_.coinMax.x, boite2.coinMax.y, boiteSelection_.coinMin.y, boiteSelection_.coinMax.y) ||
			utilitaire::DANS_LIMITESXY(position3, boiteSelection_.coinMin.x, boiteSelection_.coinMax.x, position4, boiteSelection_.coinMin.y, boiteSelection_.coinMax.y) && elem->estSelectionnable()) {
			return true;
		}

		glm::dvec3 longMAX = boite.coinMax;
		glm::dvec3 longMIN = boite.coinMin;

		if (boite.coinMax.x < boite.coinMin.x) {
			boite.coinMax = longMIN;
			boite.coinMin = longMAX;
		}

		if (boite.coinMin.y > boite.coinMax.y) {
			pente = -(positionY / positionX);
		}
		else {
			pente = positionY / positionX;
		}

		//Calcul de y = inclinaison*X pour trouver des points sur la ligne
		double deltaX = positionX / 30;
		double y = 0;
		double x = 0;

		for (int i = 0; i < 30; i++) {
			//pente de la lige
			y = (pente * x);
			if (utilitaire::DANS_LIMITESXY(x + boite.coinMin.x, boiteSelection_.coinMin.x, boiteSelection_.coinMax.x, y + boite.coinMin.y, boiteSelection_.coinMin.y, boiteSelection_.coinMax.y) && elem->estSelectionnable()) {
				return true;
			}
			x += deltaX;
		}
		if (elem->estSelectionne()) {
			return false;
		}
		return false;
	}
}



