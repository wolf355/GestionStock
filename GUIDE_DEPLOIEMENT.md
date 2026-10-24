# Guide de déploiement — GestionStock MODEP

Objectif : obtenir un `.exe` que l'encadrant peut lancer d'un double-clic,
sans IntelliJ ni Java installé manuellement.

## Ce qu'il faut préparer AVANT le jour de la démo (sur TON PC, avec internet)

### 1. Télécharger le JavaFX SDK (jmods)
- Aller sur https://gluonhq.com/products/javafx/
- Choisir la version **21.0.2**, plateforme **Windows**, type **jmods**
- Dézipper, par exemple dans `C:\javafx-jmods-21.0.2`

### 2. Construire l'installeur
- Ouvrir un terminal dans le dossier du projet `GestionStock`
- Vérifier/adapter le chemin `JAVAFX_JMODS` dans `build-installer.bat`
- Lancer :
  ```
  build-installer.bat
  ```
- Résultat : un fichier `GestionStock-MODEP-1.0.exe` dans le dossier du projet.
  **C'est ce fichier qu'il faut mettre sur la clé USB** (pas le dossier du
  projet, pas IntelliJ).

Cet `.exe` contient déjà tout : ton code, JavaFX, le driver MySQL, PDFBox,
et même une petite version de Java (Runtime) intégrée. Le PC cible n'a
besoin de rien installer côté Java.

## Ce qu'il faut faire sur le PC de l'entreprise (le jour J)

### 1. Installer la base de données
Le seul vrai prérequis, car l'application se connecte à une base MySQL locale.
- Installer **XAMPP** (ou MySQL Server) sur le PC
- Démarrer MySQL depuis le panneau XAMPP
- Ouvrir phpMyAdmin → créer la base `gestion_stock` → **Importer** le fichier
  `.sql` que tu auras exporté depuis ta propre base (à faire avant, sur ta
  clé USB aussi)
- Comme `ConnexionBD.java` utilise `root` sans mot de passe (config XAMPP
  par défaut), ça doit fonctionner sans changer une ligne de code — sauf
  si le PC de l'entreprise a une config MySQL différente, auquel cas il
  faudra ajuster `USER`/`PASSWORD` dans `ConnexionBD.java` et refaire
  l'étape de build.

### 2. Installer l'application
- Copier `GestionStock-MODEP-1.0.exe` depuis la clé USB
- Double-clic → l'installeur Windows classique s'ouvre (suivant, suivant,
  terminer) → un raccourci est créé sur le Bureau / menu Démarrer
- L'encadrant clique sur le raccourci → l'application s'ouvre directement,
  sans terminal, sans IDE.

## Résumé du jour J
1. Installer XAMPP + importer la base `.sql`
2. Lancer MySQL depuis XAMPP
3. Installer `GestionStock-MODEP-1.0.exe`
4. Cliquer sur le raccourci → l'app s'ouvre
