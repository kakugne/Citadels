package com.montaury.citadelles.tour;

import com.montaury.citadelles.faux.FauxControlleur;
import com.montaury.citadelles.faux.PersonnageAleatoirePrevu;
import com.montaury.citadelles.joueur.Joueur;
import com.montaury.citadelles.personnage.Personnage;
import com.montaury.citadelles.tour.PhaseDeSelectionDesPersonnages;
import com.montaury.citadelles.tour.TourDeJeu;
import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;

import static com.montaury.citadelles.CitésPredefinies.citéVide;
import static com.montaury.citadelles.joueur.JoueursPredefinis.unJoueur;
import static org.assertj.core.api.Assertions.assertThat;

public class PhaseDeSelectionDesPersonnagesDevrait {

    @Before
    public void setUp() {
        List<Personnage> selectionPreparee = List.of(Personnage.EVEQUE, Personnage.ARCHITECTE, Personnage.MAGICIEN, Personnage.ROI, Personnage.CONDOTTIERE, Personnage.ASSASSIN, Personnage.VOLEUR, Personnage.MARCHAND);
        phaseDeSelectionDesPersonnages = new PhaseDeSelectionDesPersonnages(new PersonnageAleatoirePrevu(selectionPreparee));
    }

    @Test
    public void demander_a_un_joueur_de_choisir_son_personnage() {
        FauxControlleur controlleur = new FauxControlleur();
        controlleur.prechoisirPersonnage(Personnage.MARCHAND);
        Joueur joueur = new Joueur("Toto", 20, citéVide(), controlleur);

        TourDeJeu tourDeJeu = phaseDeSelectionDesPersonnages.faireChoisirPersonnages(List.of(joueur));

        assertThat(controlleur.personnagesDisponibles).isNotNull();
        assertThat(tourDeJeu.joueurAssocieAuPersonnage(Personnage.MARCHAND)).containsExactly(joueur);
    }

    @Test
    public void ecarter_un_personnage_face_cachee() {
        FauxControlleur controlleur = new FauxControlleur();

        phaseDeSelectionDesPersonnages.faireChoisirPersonnages(List.of(unJoueur(controlleur), unJoueur("Joueur2"), unJoueur("Joueur3"), unJoueur("Joueur4")));

        assertThat(controlleur.personnagesDisponibles)
                .hasSize(5)
                .doesNotContain(Personnage.EVEQUE);
    }

    @Test
    public void ecarter_zero_personnage_face_visible_pour_une_partie_de_7_joueurs() {
        FauxControlleur controlleur = new FauxControlleur();
        List<Joueur> joueurs = List.of(unJoueur(controlleur), unJoueur("Joueur2"), unJoueur("Joueur3"), unJoueur("Joueur4"), unJoueur("Joueur5"), unJoueur("Joueur6"));

        phaseDeSelectionDesPersonnages.faireChoisirPersonnages(joueurs);

        assertThat(controlleur.personnagesEcartesFaceVisible)
                .isEmpty();
    }

    @Test
    public void ecarter_un_personnage_face_visible_pour_une_partie_de_5_joueurs() {
        FauxControlleur controlleur = new FauxControlleur();
        List<Joueur> joueurs = List.of(unJoueur(controlleur), unJoueur("Joueur2"), unJoueur("Joueur3"), unJoueur("Joueur4"), unJoueur("Joueur5"));

        phaseDeSelectionDesPersonnages.faireChoisirPersonnages(joueurs);

        assertThat(controlleur.personnagesEcartesFaceVisible)
                .containsExactly(Personnage.ARCHITECTE);
    }

    @Test
    public void ecarter_deux_personnages_face_visible_pour_une_partie_de_4_joueurs() {
        FauxControlleur controlleur = new FauxControlleur();

        phaseDeSelectionDesPersonnages.faireChoisirPersonnages(List.of(unJoueur(controlleur), unJoueur("Joueur2"), unJoueur("Joueur3"), unJoueur("Joueur4")));

        assertThat(controlleur.personnagesEcartesFaceVisible)
                .containsExactly(Personnage.ARCHITECTE, Personnage.MAGICIEN);
    }

    @Test
    public void ne_pas_proposer_un_personnage_deja_pris_par_un_autre_joueur() {
        FauxControlleur controlleurJoueur1 = new FauxControlleur();
        controlleurJoueur1.prechoisirPersonnage(Personnage.ARCHITECTE);
        FauxControlleur controlleurJoueur2 = new FauxControlleur();

        phaseDeSelectionDesPersonnages.faireChoisirPersonnages(List.of(unJoueur(controlleurJoueur1), unJoueur("Joueur2", controlleurJoueur2), unJoueur("Joueur3"), unJoueur("Joueur4")));

        assertThat(controlleurJoueur2.personnagesDisponibles)
                .doesNotContain(Personnage.ARCHITECTE);
    }

    @Test
    public void ne_pas_reveler_le_roi_face_visible() {
        FauxControlleur controlleurJoueur1 = new FauxControlleur();
        List<Joueur> joueurs = List.of(unJoueur(controlleurJoueur1), unJoueur("Joueur2"), unJoueur("Joueur3"), unJoueur("Joueur4"));
        PhaseDeSelectionDesPersonnages phaseDeSelectionDesPersonnages = new PhaseDeSelectionDesPersonnages(new PersonnageAleatoirePrevu(List.of(Personnage.EVEQUE, Personnage.ROI, Personnage.MAGICIEN, Personnage.ARCHITECTE, Personnage.CONDOTTIERE, Personnage.ASSASSIN, Personnage.VOLEUR, Personnage.MARCHAND)));

        phaseDeSelectionDesPersonnages.faireChoisirPersonnages(joueurs);

        assertThat(controlleurJoueur1.personnagesEcartesFaceVisible)
                .doesNotContain(Personnage.ROI);
    }

    @Test
    public void permettre_au_septieme_joueur_de_choisir_la_carte_ecartee_face_cachee() {
        FauxControlleur controlleurJoueur7 = new FauxControlleur();
        List<Joueur> joueurs = List.of(unJoueur("Joueur1"), unJoueur("Joueur2"), unJoueur("Joueur3"), unJoueur("Joueur4"), unJoueur("Joueur5"), unJoueur("Joueur"), unJoueur(controlleurJoueur7));

        phaseDeSelectionDesPersonnages.faireChoisirPersonnages(joueurs);

        assertThat(controlleurJoueur7.personnagesDisponibles)
                .hasSize(2)
                .containsExactly(Personnage.CONDOTTIERE, Personnage.EVEQUE);
    }

    private PhaseDeSelectionDesPersonnages phaseDeSelectionDesPersonnages;
}