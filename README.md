# GestionStock-MODEP

## Description

GestionStock-MODEP est une application de bureau développée en **Java 21** avec **JavaFX** permettant la gestion du matériel informatique d'une entreprise.

L'application permet d'administrer les employés, les départements, les matériels, leurs types ainsi que les affectations entre employés et équipements.

---

# Fonctionnalités

- Authentification utilisateur
- Gestion des employés
- Gestion des départements
- Gestion des matériels
- Gestion des types de matériel
- Gestion des affectations
- Export PDF
- Interface graphique JavaFX
- Connexion à une base de données MySQL

---

# Technologies utilisées

- Java 21
- JavaFX 21
- Maven
- MySQL
- JDBC
- Apache PDFBox

---

# Structure du projet

```
src/
│
├── connexion/
│     Connexion à MySQL
│
├── dao/
│     Couche d'accès aux données
│
├── modele/
│     Classes métier
│
├── vue/
│     Interfaces JavaFX
│
├── utils/
│     Export PDF
│
└── resources/
      Images
      CSS
```

---

# Architecture

Le projet suit une architecture de type **DAO (Data Access Object)** :

```
Vue (JavaFX)
        │
        ▼
DAO
        │
        ▼
Base de données MySQL
```

---

# Base de données

L'application nécessite une base MySQL.

Pensez à modifier les paramètres de connexion dans :

```
src/connexion/ConnexionBD.java
```

avant l'exécution.

---

# Compilation

Compiler le projet :

```bash
mvn clean package
```

Le fichier généré se trouve dans :

```
target/
```

---

# Génération de l'installateur Windows

Lancer :

```
build-installer.bat
```

Le script :

- compile le projet ;
- génère le JAR ;
- crée le runtime Java ;
- construit l'installateur Windows avec **jpackage**.

---

# Lancement depuis IntelliJ

Classe de démarrage :

```
Launcher.java
```

Cette classe appelle :

```
Main.java
```

qui démarre l'application JavaFX.

---

# Dépendances Maven

- JavaFX Controls
- JavaFX FXML
- JavaFX Graphics
- MySQL Connector/J
- Apache PDFBox

---



# Auteur

**Aimrane Abbou**

Élève ingénieur en Génie Informatique

École Mohammadia d'Ingénieurs (EMI)

---

# Licence

Projet académique réalisé dans le cadre des études à l'École Mohammadia d'Ingénieurs.