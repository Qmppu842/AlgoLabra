# Viikko 2

## Mitä olen tehnyt tällä viikolla?

Tällä viikolla tein käytöliitymän hyväksytävään kuntoon.  
Lisäsin laudan ja voiton tarkistuksen testejä etenkin aina kun huomasin jotain outoa.  
Nyt olen aika tyytyväinen sen tilaan tosin vielä peli crashää jos lauta täyttyy ja sallittuja siirtoja ei ole enää
jäljellä.



## Miten ohjelma on edistynyt?

Tällä hetkellä pystyy pelaamaan vastaan tai katsoa kun pienet testini pelailevat vastakkain.

Tämän heken profiili mahdollisuudet ovat:

- Random, täysin random, valitsee vain satunnaisen sallitun siirron.
- Human, ihmis profiili joka tarjoaa mahdollisuuden satunnaiselle pudotukselle.
- SimpleHeuristicGuyProfile, we dont talk about simpleHeuristicGuyProfile
- SimpleOpportunisticProfile (SOP) joka pohjautuu muutamaan kevyeen heurestiikka sääntöön mitä pohdin.
- DFSProfile, ikään kuin naivi depth first search joka lähinnä yrittää valita voittavia siirtoja, ei exhaustive

Jotain tilastoja kun hetken katselin:

- Random v Random on ~50/50 voittojen kanssa
- Random v DFSProfile on ~2/3 pelistä DFS:ssälle  
  Ja hauskinta oli nähdä että:
- SimpleOpportunisticProfile v DFSProfile niin SOP voittaa ~2/3-3/4 ajasta


## Mitä opin tällä viikolla / tänään?

Aikaa vain kuluu yllättävän paljon vaikka kuinka yrittää.  
Ja opin taas jälleen että manuaalinen testaaminen ei vaan riitä, etenkin jos visualisaatio on puuteellinen vielä.


## Mikä jäi epäselväksi tai tuottanut vaikeuksia?

Joku gradle juttu on vieläkin vinossa ja en saa sen takia generoitua mukavia testien kattavuus raportteja.  
Saan ne generoitua mutta se on vähän tyhmä systeemi tällä hetkellä...  
Ja ne reportit mitä oli tarkoitus käyttää, generoituu kyllä mutta ne eivät suostu ottamaan yhtään testejä huomioon.

Myös ajatuksellisesti vaikuttaa vähän tuottavan vieläkin haasteita miten olisi järkevintä toteuttaa ihmisen
osallistuminen kun on mahdollisuus vaihtaa tietokone haastajien ja ihmisen välillä.

Tämän lisäksi myös TDD ja "muun kehityksen" raja on häiritsevä.  
Haluaisin tehdä TDD tyylillä ja oppia elämään sen puitteissa.  
Se alkaa aina hyvin mutta sitten tulee joku kohta missä ajatukseni menevät eri suuntaan kuin mitä onnistun testaamaan ja
sitten TDD lipsuu.  
Joskus hyvässä ja joskus pahassa mutta en oikein tiedä miten sitä tasapainottaa tai oppia kunnolla.

## Mitä teen seuraavaksi?
Vaikka tällä viikolla piti jo alotaa Minimax niin harhauduin vähän hutiin.  
En koe että aika oli hukkaan heitettyä kuitenkaan sillä sain käyttiksen mukavaan kuntoon, sekä hahmotin omaa tilannettani lisää.  
Näillä opeilla on hyvä alottaa seuraava viikko tehokkaasti Minimax focusilla.


## Ajan käyttö:

| Päivä    | aika | syy                              |
|----------|------|----------------------------------|
| 14/07    | 4h   | Ui pohdintaa ja rakentelua       |
| 15/07    | 6h   | Uix hiontaa                      |
| 16/07    | 12h  | Voiton tarkistus ja heuristiikka |
| 17/07    | 12h  | Pohdintaa ja sekoilua            |
| 18/07    | 13h  | Varmistaa oikeellisutta          |
| 19/07    | 16h  | Dfs testailu                     |
| 20/07    | 1h   | Dokumentaatio                    |
| 14-19/07 | 62h  | This weeks total                 |
| 10-19/07 | 77h  | Total Total                      |
