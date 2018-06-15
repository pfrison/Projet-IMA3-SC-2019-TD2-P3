# Projet IMA3 SC 2019 TD2 P3

Github du groupe P3 TD2 en IM3 à Polytech Lille pour le projet SC de l'année 2019.

## Codes Arduino

La carte Arduino est utilisée pour récupérer les valeurs de photorésistance par le biais de ponts diviseurs connectés à chacune de ces entrées analogiques. La tension à une entrée analogique est donc fonction de et uniquement de la valeur des photorésistances.

Pour relier plusieurs capteurs, il nous a fallu utiliser un multiplexage réalisé avec des transistors. Ainsi, 5 capteurs sont branchés en parallèle et les ports digitaux de l'Arduino permettent de contrôler le passage du courant dans chaque capteur.

Le premier programme "Camera_test" nous a servi de tester notre carte électronique. Il transmet une visualisation avec des caractères ASCII de l'image reconstituée par l'Arduino. Le texte ASCII représentant l'image est envoyé à l'ordinateur par liaison série. Pour une meilleure visualisation, la programme ajuste le contraste de l'image : il définit le pixel le plus clair comme la valeur la plus haute enregistré depuis le lancement du programme (réciproquement, le noir correspond à la valeur la plus basse depuis le lancement).

Le second programme "Camera_serial" envoie les informations brutes des capteurs. Ce sera à la Raspberry ou tout autre appareil branché au port série de faire son propre calcul du contraste. Les informations sont envoyées selon le format suivant (toutes les valeurs (captXX) sont des entiers de 0 à 1024) :

    capt00-capt01-capt02-capt03-capt04-capt10-capt11-...-capt44   //image à t = 0
    capt00-capt01-capt02-capt03-capt04-capt10-capt11-...-capt44   //image à t = 1
    capt00-capt01-capt02-capt03-capt04-capt10-capt11-...-capt44   //image à t = 2

## Moteur graphique 3D

Un bon moyen de visualisation des données reçues de la caméra est un graphique en 3D. De cette manière, on pourrait théoriquement visualiser le profil de l'objet devant la caméra.

Les données envoyées au moteur pour le rendu sont :

* une liste de points avec trois dimensions
* une liste de lignes avec 2 points par ligne
* une liste de polygones avec trois ou plus points par polygone.

Pour le rendu, le script en Javascript applique trois transformations angulaires correspondant aux deux angles du repère sphérique, plus une autre rotation autour de l'axe de la caméra. Les trois rotations sont appelées : rotTheta, rotPhi, rotPsi.

Le script applique ensuite une translation des points suivant l'axe de la caméra. translation appelée transRho.

Notre scène est toujours en trois dimensions et ne peut pas être affichée sur un écran qui n'a que deux dimensions. Le script va alors réaliser une projection en perspective (une simple division : plus le point est loin de la caméra plus il sera proche du centre). Cette projection tiens comptes d'une distance appelée tirageMecanique qui est la distance entre l'écran et le point de perspective.

Une fois les coordonnées X et Y des points projetés sur l'écran, le moteur dessine les points, les lignes et les polygones sur l'écran.

En répétant l'opération régulièrement et en modifiant les coordonnées des points ou les angles ou la distance de la caméra à la scène, on obtient une animation.

## Controleur audio Android

La caméra pourrait servir à contrôler divers appareils. Pour en faire la démonstration, nous nous somme aventuré à la création d'une application Android qui serait capable de lire des données affichées sur une page web générée par la Raspberry et d'interpréter ces données en commande pour le lecteur audio d'Android.

Nous avons pensé à trois commandes basiques :

* lecture et pause lorsqu'un objet (une main par exemple) obscurcit brièvement la vision au centre de la caméra
* lecture de la chanson suivante lorsqu'un objet obscurcit la caméra de la gauche vers la droite
* lecture de la chanson précédente lorsqu'un objet obscurcit la caméra de la droite vers la gauche

L'application démarre sur un panneau de débogage, unique écran (Activity) de l'application, le reste étant géré dans un processus en arrière-plan (Service). Dans le premier champ, il faut y rentrer l'URL où Android pourra récupérer les informations brutes sur le serveur web de la Raspberry. Si l'application arrive à se connecter, elle affichera dans l'écran de débogage diverses informations : valeurs lues en brute, image reconstituée, etc.

Une fois l'application ouverte, l'utilisateur peut la "fermer" : la fermeture "normale" (différente de la fermeture forcée dans le gestionnaire des applications) détruit simplement l'Activity (l'écran de débogage) et non le Service qui va continuer à interroger la Raspberry et à contrôler le lecteur audio.

Comme énoncé précédemment, le Service est une tâche exécutée en fond qui s'occupe du téléchargement des données brutes, de l'interprétation de l'image (non différente des autres systèmes, c'est à dire un calcul de contraste) et du contrôle du lecteur audio d'Android.

## Communication série

La communication entre l'Arduino et la Raspberry PI est réalisée par la liaison série. Après la réception de données dans le format de chaîne de caractères, l'application web va transformer la chaîne de caractères reçue vers un tableau 5x5 avec toutes les données en entier. Ces données vont être affichées sur des blocs de 5x5, et chaque donnée correspond à une valeur de teinte. Ces 25 blocs nous permettent de visualiser le résultat détecté par les 25 photorésistances. 
