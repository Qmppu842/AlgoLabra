# Määrittelydokumentti

---
## Kielen valinta
Ajattelin tehdä projektin kotlinilla jos ei mitään isompaa outoutta tapahdu maailmassa.


## Algoritmien (algorytmien) valinta
Minimax ainakin alkuun, jos aika ja innostus riitää niin saatan harkita negamaxia tai muita minimaxin wikipediassa mainittuja kavereita.  
Ja minimaxiin meinasin toteuttaa vaaditut tehosteet kuten iteratiivinen syventäminen ja aplha/beta-karsinta, sekä jonkin tason heurestiikka funktion.

## Mihin sitten käytän sitä Minimaxia?
Ajattelin toteuttaa Connect Four (Connect 4, C4) "ratkaisijan"/tekoälyn/botin/vastuksen.

## Miten tätä sitten käytetään?
Tähtään graafiseen käyttöliittymään jotta pelaaminen on simppeliä ja nopeaa.

## Aika ja tilavaateet?
Minimax aika pitäisi olla O(b^d) missä d on tutkittu syvyys ja b on solmmujen määrä.   
Koska ongelma alue on määrätty pelin puolesta, maksimi d on 42 ja solmuja on (max) 7 per kerros, 7^42 =~3E35.  
Onneksi nämä kaikki ei ole sallittuja peli tilanteita.
Pelitilanteet rejoitettuna määrä pitäis olla "`states (total)    4,531,985,219,092`" [^1].  
Vaikka rajoitetumpi, ei siltikään mieluisa pelata brute froce ratkaisua vastaan, täten minimaxin käyttö.

Tilallisesti pitäisi tämän olla hieman hillitympi O(b*d) missä d on tutkittu syvyys ja b on solmmujen määrä.

## Harjoitustyön ydin:
Harjoitus työn ydin on minimaxin toteutus.  
Sen jälkeen siihen alpha/beta-karsinnan toteuttaminen.  
Ja sitten iteratiivisen syventämisen lisääminen.  
Kun nämä vaiheet ovat tehty ja jos aikaa ja tarvetta vielä on niin näiden tehostamista ja tuunaamista.


## Harjoitus työn sivu, asiat jotka ovat mukavia mutta eivät ole focus:
Käyttöliittymä ja sen ympärillä olevat asiat.
(Tämä osio on lähinnä muistuttamaan minua itseäni mihin ei kuulu upottaa liikaa aikaa.)


---
## Hallinnollinen osio:
Teen tällä hetkellä tietojenkäsittelytieteen kandidaatti tutkintoa (TKT).


## Ohjelmointi kielet
Kotlin ja java osaan mukavasti.
Python menee kanssa.
Kyllä varmaankin C/C++ tai C# myös toimivat.
Ja varmaan niitä muitakin menee kun vähän mietiskelee.

## Lähteitä
| Syntax | Description                                                            |
| ----------- |------------------------------------------------------------------------|
| [^1] | https://github.com/markus7800/Connect4-Strong-Solver                   |
| Minimax | https://en.wikipedia.org/wiki/Minimax                                  |
| Negamax | https://en.wikipedia.org/wiki/Negamax                                  |
| NegaScout | https://en.wikipedia.org/wiki/Principal_variation_search               |
| Alpha/beta-karsinta  | https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning               |
| Iteratiivinen syventäminen | https://en.wikipedia.org/wiki/Transposition_table                      |
| Ovela bittilauta  | https://github.com/denkspuren/BitboardC4/blob/master/BitboardDesign.md |
| SSS*  | https://en.wikipedia.org/wiki/SSS*                                     |
| Minimax aika ja tila | https://stackoverflow.com/a/9449563/5151160  <br/>     https://locall.host/is-minimax-algorithm-hard/                     |










