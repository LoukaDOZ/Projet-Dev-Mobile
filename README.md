# Projet-Dev-Mobile
Authors: Louka DOZ - Guillaume DESCROIX

Android app for mobile phones - Manage your todolist and your account - Based on Todoist API

## Questions implémentées
Toutes les questions des 4 TPs ont été réalisées, mise à part la question 10 du TP 2 "Changements de configuration". En effet, celle-ci parle d'un soucis lorsque l'on passe le téléphone en mode paysage, or nous n'avons pas rencontré ce soucis lorsque nous sommes revenu à la question après avoir fini le TP 4. Nous n'avons donc pas vu l'utiité de l'implémenter.

## Travail complémentaire
Nous avons réalisé les points bonus suivants :
- Style graphique propre et s'inspirant des couleurs de Todoist
- Nom de l'application et icone changée pour correspondre à Todoist
- Les CompsableActivities sont scrollable quand ont passe en mode paysage
- SwipeRefreshLayout ajouté sur l'activité prnicipale pour permettre à l'utilisateur de se syncrhoniser avec l'API
- Possibilité de partager du texte depuis une autre application et de le mettre dans le champ description avec réellement la possibilité de sauvegarder une nouvelle tâche ensuite (passage par l'activité principale quand on partage pour que la sauvegarde soit rendue efficace)
- Possibilité de partager la description d'une tâche vers une autre application en restant appuyé longtemps sur la tâche
- Utilisation d'AlertDialogs dans le process de demandes de permissions
- Respect du process de demande de permissions proposé par Google
- Lorsque l'utilisateur refuse au moins 2x les permission, s'il redemande, il sera envoyé dans les options de l'application directement

## Problèmes non réglés
Nous avons encore des problèmes que nous avons pas pu régler, tels que :
- Lorsque les informations de l'utilisateurs sont changées, elles ne sont pas mises à jour automatiquement dans l'activité principale, il faut swipe tout en haut pour rafraîchir les informations, contrairement aux tâches (raison inconnue)
- La photo de profil ne peut pas être changée plusieurs fois, pour une raison inconnue, elle se change la première fois, mais pas les suivantes

## Identification utilisateur
Nous avons tenté de mettre en place une page de login permettant de s'authentifier directement sur l'application et eventuellement de changer d'utilisateur sans devoir changer le Token en dur dans le code. Toutefois nous avons rencontré un certain nombre de problème.
Initialement, nous pensions qu'il était possible de s'authentifier directement avec l'API en envoyant les crédentials (username et password) se qui n'était finalement pas le cas. Dans un second temps nous avons tenté d'utiliser AppAuth mais avons rencontré des difficultés que nous n'avons su identifié.
Enfin nous avons testé d'intercépter directement l'URL de redirection sans passé par AppAuth mais les résultats n'ont encore une fois pas été concluant. Vous pourrez toutefois consulté le code des tentatives sur les différentes branches du dépot.

## Screenshots
### App icon
![Screenshot_20240318_170704_One_UI_Home](https://github.com/LoukaDOZ/Projet-Dev-Mobile/assets/46566140/167455fb-5c23-4384-9f11-c74a7b453e78)

### Home page
![Screenshot_20240318_171446_Projet-DM](https://github.com/LoukaDOZ/Projet-Dev-Mobile/assets/46566140/62eb95a2-7036-481e-8ce4-1e269924c093)

### User profile
![Screenshot_20240318_171120_Projet-DM](https://github.com/LoukaDOZ/Projet-Dev-Mobile/assets/46566140/6876e413-e410-438f-9cda-ae7895ef99b3)

### Create task
![Screenshot_20240318_171041_Projet-DM](https://github.com/LoukaDOZ/Projet-Dev-Mobile/assets/46566140/e22c8ecf-8709-4825-8337-65c62de461c5)

### Edit task
![Screenshot_20240318_171452_Projet-DM](https://github.com/LoukaDOZ/Projet-Dev-Mobile/assets/46566140/6f02e51b-284c-4595-9abf-833e1fe72430)
