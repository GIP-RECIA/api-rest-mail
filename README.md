# API-REST-Mail

API permettant d'interroger l'imap ENT pour récupérer le nombre de mail non lu et l'entête des derniers mails reçus d'une personne.

Versions :
- Java `11`
- Spring-boot `2.7.18`
- Fait pour tourner sur tomcat `9`

## Liste des routes

- GET `/api/email/summary` : retourne un JSON de type `MailFolderSummaryForWidget` contenant la liste des entête des derniers mails, le nom du dossier de mail,  et le nombre de mails non lu ;
- GET `/health-check` : retourne 200 OK.

## Securité

L'API est conçue pour utiliser des sessions CAS, et contact l'imap avec des CAS Proxy Tickets.

La session CAS doit contenir les attributs suivants :
- L'uid de l'utilisateur ;
- L'établissement courant de l'utilisateur.

Ces deux informations seront utilisées pour requêter l'imap associé à l'utilisateur.

## Appels API

Pour récupérer le résumé des mails de l'utilisateur on a besoin de faire un  appel API :
- Un appel à l'API paramuseretab. 

## Caches

Afin de ne pas faire de requêtes inutiles plusieurs caches sont mis en place :
- Un cache au niveau des requêtes à l'API paramuseretab (etablisement courrant <-> domaine imap)
- Un cache au niveau des requêtes à l'imap (uid <-> `MailFolderSummaryForWidget`)

## Déploiement

- Pour faire tourner en local : `mvn clean package spring-boot:run`
- Pour pousser sur le nexus : `mvn clean package deploy`

### Commandes pour notice et license

- `mvn notice:check`
- `mvn notice:generate`
- `mvn license:check`
- `mvn license:format`
- `mvn license:remove`