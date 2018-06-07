# Projet IMA3 SC 2019 TD2 P3

Github du groupe P3 TD2 en IM3 à Polytech Lille pour le projet SC de l'année 2019.

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

