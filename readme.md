# Algo labra

Updates are loading...

## Documentit:

[Määrittelydokumentti](documents/Määrittely.md)

## Viikkoraportit

- [Viikko 1](documents/viikkoraportit/viikko1.md)
- [Viikko 2](documents/viikkoraportit/viikko2.md)
- [Viikko 3](documents/viikkoraportit/viikko3.md)
- [Viikko 4](documents/viikkoraportit/viikko4.md)


## Koodi

Täällä asustaa itse koodi:   [koodin pikalinkki](code/composeApp/src/desktopMain/kotlin/io/qmpu842/labs)  
Täällä asustaa minimax koodi:   [minimax pikalinkki](code/composeApp/src/desktopMain/kotlin/io/qmpu842/labs/logic/profiles/MiniMaxV3Profile.kt)


## Testit

Täällä asustaa testit:   [testit pikalinkki](code/composeApp/src/desktopTest/kotlin/io/qmpu842/labs)  
Testailuun liittyviä asioita: [lisäkansio](documents/testaus)


## Ajaminen

Tällä hetkellä pitäisi pystyä ajamaan jos menet [koodin kansioon](code)  
Ja ajat komennon ```./gradlew code:composeApp:run```  
  
Tai voit suoraa myös ajaa tämän komennon tässä ```./code/gradlew --project-dir=code :composeApp:run```

## Extraa
On suunnitelmissa että ui:sta voi tehdä nämä mutta ei vielä ole ollut ehtimistä toteuttaa:
https://github.com/Qmppu842/AlgoLabra/blob/06e37e0dbfd2b1e2b5dca175d51cf1a368332aae/code/composeApp/src/desktopMain/kotlin/io/qmpu842/labs/App2.kt#L42  
Tuossa kohtaa olevaa gameHolder muuttujan sisäisiä arvoja muuttamalla voi vaihtaa asioit kuten laudan koko, voittoon vaadittu määrä ja pelaajia.  
Esim jos ei jaksa itse pelata voi laittaa kaksi eri syvyyden minimaxia pelaamaan vastakkain laudalla jonka koko on 50x50
