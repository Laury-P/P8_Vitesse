# 📱 Vitesse – Application Android RH  
*(Projet éducatif – OpenClassrooms)*

## 📌 Contexte du projet
Ce projet correspond au **Projet 8 du parcours "Développeur d’Application Android" chez OpenClassrooms**, intitulé :

> **Créer une application Android en MVVM de A à Z**

L’objectif était de concevoir une application Android complète **from scratch**, dans un contexte proche du monde professionnel, en respectant des **User Stories**, un **Kanban**, un **Tech Radar** et une **Definition of Done**.

L’application est développée pour l’entreprise fictive **Vitesse**, spécialisée dans l’automobile, afin d’aider le service **Ressources Humaines** à gérer les candidats.

👉 Projet **réalisé seule**, **soutenu et validé**, avec des axes d’amélioration identifiés.

---

## 🎯 Objectif métier
Faciliter la gestion des candidats par les RH :
- créer, modifier et supprimer des candidats
- consulter le détail d’un candidat
- marquer des candidats en favoris
- gérer des informations comme le salaire attendu avec conversion de devise

---

## ⚙️ Fonctionnalités principales
- Liste des candidats
- Gestion des favoris
- Ajout / modification / suppression d’un candidat
- Validation des champs (obligatoires, email valide)
- Conversion de salaire via une API de taux de change
- Persistance locale des données
- Application disponible en **français** et en **anglais**

---

## 🧱 Architecture & choix techniques

### Architecture
- **Clean Architecture + MVVM**
- Séparation claire des responsabilités :
  - **View** : Fragments + UI XML
  - **ViewModel** : logique métier et état de l’UI
  - **Repository** : point d’entrée unique pour les données
  - **Data** : Room (local) et API distante

### Stack technique
- **Langage** : Kotlin  
- **UI** : XML  
- **Architecture** : MVVM  
- **Base de données locale** : Room  
- **API distante** : Retrofit (taux de change)  
- **Asynchronisme** : Coroutines + Flow  
- **Injection de dépendances** : Hilt  
- **Tests** : JUnit, MockK  
- **Outils** : Git / GitHub, Jira  

---

## 🔍 Gestion des données
- **Room**
  - Entités, DAO, mapping clair
  - CRUD complet
  - Tests d’intégration instrumentalisés
- **API distante**
  - Récupération des taux de change via Retrofit
  - Appels réseau asynchrones (Coroutines)

Les données locales et distantes sont **indépendantes**, conformément aux consignes du projet.

---

## 🧪 Tests & qualité
Les tests font partie intégrante de la **Definition of Done** du projet.

- Tests unitaires sur :
  - ViewModels
  - Repository
- Tests d’intégration :
  - Base de données Room
- Mocking avec **MockK**

👉 Le niveau de tests est volontairement **supérieur au minimum attendu**.

---

## 🧠 Ce que ce projet démontre
- Conception d’une application Android complète
- Bonne compréhension de l’architecture MVVM
- Autonomie sur un projet long et structuré
- Gestion des données locales et distantes
- Importance accordée aux tests et aux bonnes pratiques
- Organisation proche d’un contexte professionnel

---

## 🚀 Axes d’amélioration
Ce projet est éducatif et peut encore évoluer :
- migration de l’UI XML vers **Jetpack Compose**
- amélioration de l’**accessibilité** (sujet approfondi dans le projet suivant)
- optimisation de l’expérience utilisateur
- enrichissement fonctionnel

---

## 📎 À propos
Projet réalisé dans le cadre du parcours  
**Développeur d’Application Android – OpenClassrooms**

---
