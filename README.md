# Projet-Dev-Mobile
### Louka DOZ - Guillaume DESCROIX

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
- Lorsque les informations de l'utilisateurs sont changées, elles ne sont pas mises à jour automatiquement dans l'activité principale, il faut swipe tout en hautt pour rafraîchir les informations, contrairement aux tâches (raison inconnue)
- La photo de profil ne peut pas être changée plusieurs, pour une raison inconnue, elle se change la première fois, mais pas les suivante
