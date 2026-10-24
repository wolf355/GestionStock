package utils;

import modele.Affectation;
import modele.Employe;
import modele.Materiel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Génération d'exports PDF (Matériel, Employés, Historique des
 * affectations) à partir d'un moteur de tableau générique et
 * paginé, écrit directement avec Apache PDFBox.
 */
public class ExportPDF {

    private static final float MARGE = 40;
    private static final float HAUTEUR_LIGNE = 20;
    private static final float TAILLE_POLICE = 9;
    private static final float TAILLE_TITRE = 16;
    private static final PDFont POLICE = PDType1Font.HELVETICA;
    private static final PDFont POLICE_GRAS = PDType1Font.HELVETICA_BOLD;
    private static final SimpleDateFormat FORMAT_DATE =
            new SimpleDateFormat("dd/MM/yyyy");

    // ================================================================
    // MOTEUR DE TABLEAU GENERIQUE (avec pagination automatique)
    // ================================================================

    /**
     * Génère un PDF au format A4 paysage contenant un titre et un
     * tableau. Le tableau change automatiquement de page quand il
     * atteint le bas de la page.
     */
    public static void genererTableau(String titre, String[] entetes,
                                      List<String[]> lignes, float[] largeursColonnes,
                                      File fichierDestination) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDRectangle format = new PDRectangle(
                    PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            float largeurUtile = format.getWidth() - 2 * MARGE;

            PageContext ctx = nouvellePage(document, format);
            ctx.y = ecrireEntete(ctx, titre, entetes, largeursColonnes,
                    format, largeurUtile);

            for (String[] ligne : lignes) {
                if (ctx.y - HAUTEUR_LIGNE < MARGE) {
                    ctx.flux.close();
                    ctx = nouvellePage(document, format);
                    ctx.y = ecrireEntete(ctx, titre, entetes,
                            largeursColonnes, format, largeurUtile);
                }
                ecrireLigne(ctx.flux, ligne, largeursColonnes, ctx.y,
                        POLICE, false);
                ctx.y -= HAUTEUR_LIGNE;
            }
            ctx.flux.close();

            document.save(fichierDestination);
        }
    }

    private static class PageContext {
        PDPageContentStream flux;
        float y;
    }

    private static PageContext nouvellePage(PDDocument document,
                                            PDRectangle format) throws IOException {
        PDPage page = new PDPage(format);
        document.addPage(page);
        PageContext ctx = new PageContext();
        ctx.flux = new PDPageContentStream(document, page);
        ctx.y = format.getHeight() - MARGE;
        return ctx;
    }

    private static float ecrireEntete(PageContext ctx, String titre,
                                      String[] entetes, float[] largeurs, PDRectangle format,
                                      float largeurUtile) throws IOException {

        float y = ctx.y;

        // Titre (seulement en haut de chaque page, pour le contexte)
        ctx.flux.beginText();
        ctx.flux.setFont(POLICE_GRAS, TAILLE_TITRE);
        ctx.flux.newLineAtOffset(MARGE, y);
        ctx.flux.showText(titre);
        ctx.flux.endText();
        y -= TAILLE_TITRE + 15;

        // Ligne d'en-tetes du tableau (fond gris clair)
        ctx.flux.setNonStrokingColor(0.90f, 0.90f, 0.90f);
        ctx.flux.addRect(MARGE, y - HAUTEUR_LIGNE + 5, largeurUtile,
                HAUTEUR_LIGNE);
        ctx.flux.fill();
        ctx.flux.setNonStrokingColor(0f, 0f, 0f);

        ecrireLigne(ctx.flux, entetes, largeurs, y, POLICE_GRAS, true);
        y -= HAUTEUR_LIGNE;

        return y;
    }

    private static void ecrireLigne(PDPageContentStream flux,
                                    String[] valeurs, float[] largeurs, float y, PDFont police,
                                    boolean gras) throws IOException {

        float x = MARGE;
        for (int i = 0; i < valeurs.length; i++) {
            String texte = valeurs[i] == null ? "" : valeurs[i];
            // Troncature simple si le texte deborde de la colonne
            float maxLargeur = largeurs[i] - 6;
            texte = tronquer(texte, police, maxLargeur);

            flux.beginText();
            flux.setFont(police, TAILLE_POLICE);
            flux.newLineAtOffset(x + 3, y - HAUTEUR_LIGNE + 6);
            flux.showText(texte);
            flux.endText();
            x += largeurs[i];
        }
    }

    private static String tronquer(String texte, PDFont police,
                                   float maxLargeur) throws IOException {
        while (police.getStringWidth(texte) / 1000 * TAILLE_POLICE
                > maxLargeur && texte.length() > 1) {
            texte = texte.substring(0, texte.length() - 1);
        }
        return texte;
    }

    private static String fmt(java.util.Date d) {
        return d == null ? "" : FORMAT_DATE.format(d);
    }

    // ================================================================
    // EXPORTS METIER
    // ================================================================

    public static void exporterEmployes(List<Employe> employes,
                                        File destination) throws IOException {
        String[] entetes = {"NAX", "Nom", "Prénom", "Email",
                "Téléphone", "Poste", "Département"};
        float[] largeurs = {60, 90, 90, 130, 80, 90, 100};

        List<String[]> lignes = new ArrayList<>();
        for (Employe e : employes) {
            lignes.add(new String[]{
                    e.getNax(),
                    e.getNom(),
                    e.getPrenom(),
                    e.getEmail(),
                    e.getTelephone(),
                    e.getPoste(),
                    e.getDepartement() != null
                            ? e.getDepartement().getNom() : ""
            });
        }
        genererTableau("Liste des employés", entetes, lignes, largeurs,
                destination);
    }

    public static void exporterMateriel(List<Materiel> materiels,
                                        File destination) throws IOException {
        String[] entetes = {"Désignation", "Marque", "Modèle",
                "N° Série", "Type", "Département", "Date achat", "État"};
        float[] largeurs = {110, 70, 90, 80, 90, 90, 70, 70};

        List<String[]> lignes = new ArrayList<>();
        for (Materiel m : materiels) {
            lignes.add(new String[]{
                    m.getDesignation(),
                    m.getMarque(),
                    m.getModele(),
                    m.getNumeroSerie(),
                    m.getTypeMateriel() != null
                            ? m.getTypeMateriel().getLibelle() : "",
                    m.getDepartement() != null
                            ? m.getDepartement().getNom() : "",
                    fmt(m.getDateAchat()),
                    m.getEtat()
            });
        }
        genererTableau("Liste du matériel", entetes, lignes, largeurs,
                destination);
    }

    public static void exporterHistorique(List<Affectation> affectations,
                                          File destination) throws IOException {
        String[] entetes = {"Employé", "Matériel", "N° Série",
                "Date affectation", "Date retour", "Statut", "Remarque"};
        float[] largeurs = {100, 100, 70, 80, 80, 60, 130};

        List<String[]> lignes = new ArrayList<>();
        for (Affectation a : affectations) {
            lignes.add(new String[]{
                    a.getEmploye() != null
                            ? a.getEmploye().getNom() + " "
                              + a.getEmploye().getPrenom() : "",
                    a.getMateriel() != null
                            ? a.getMateriel().getDesignation() : "",
                    a.getMateriel() != null
                            ? a.getMateriel().getNumeroSerie() : "",
                    fmt(a.getDateAffectation()),
                    fmt(a.getDateRetour()),
                    a.estEnCours() ? "En cours" : "Clôturée",
                    a.getRemarque()
            });
        }
        genererTableau("Historique des affectations", entetes, lignes,
                largeurs, destination);
    }
}