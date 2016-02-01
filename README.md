# Automaattestimine_InvoiceRowGenerator
with mockito, junit tests

Iseseisev töö 2

Vajalik kood:
https://bitbucket.org/mkalmo/atex/src/master/src/main/java/invoice/

Mockito teek:
http://central.maven.org/maven2/org/mockito/mockito-all/1.10.19/mockito-all-1.10.19.jar

Kirjutage testidel põhinevalt klass InvoiceRowGenerator, mis genereerib osamakseid. 
Sisendiks on summa ja periood, millele see summa jagada tuleb.

Jagamine toimub järgmiste reeglite alusel:
  - esimene osamakse tuleb perioodi esimesel päeval;
  - ülejäänud osamaksed tulevad kuu esimesel päeval;
  - kui summa ei jagu Euro täpsusega, siis läheb vahe viimasse osamaksesse;
  - kahte osamakset ühele päevale sattuda ei või;
  - miinimum osamakse summa on 3 EUR-i.

Nt. kui summa on 10 ja periood on 2012-02-15 kuni 2012-04-02,
siis tulevad järgmised osamaksed (summa - kuupäev):

  3 - 2012-02-15
  3 - 2012-03-01
  4 - 2012-04-01 (jääk läheb viimasele osamaksele)

Vajalikud on vähemalt järgmised testid:

  - kuupäevad on õiged
      2012-01-01 kuni 2012-02-02 -> 2012-01-01, 2012-02-01
      2012-01-02 kuni 2012-03-01 -> 2012-01-02, 2012-02-01, 2012-03-01
  - summa jagatakse õigesti
      9 kolmeks -> 3, 3, 3
  - summa jagatakse õigesti (ei jagu täpselt)
      11 kolmeks -> 3, 4, 4
  - osamakse summa tuleks väiksem kui 3 EUR-i (pannakse teistega kokku)
      7 neljaks -> 3, 4
  - summa on alla 3 EUR-i, siis jääb üks vastava summaga osamakse
      2 kolmeks -> 2


InoviceRowGenerator.generateRowsFor() jagab summa perioodile ära ja kutsub iga osamakse
kohta välja InvoiceRowDao.save() meetodit. Meetodi generateRowsFor() õigsuses saab
veenduda kontrollides save() meetodi väljakutseid.

NB! väljakutsete järjekord on oluline.

Vältige duplikatsiooni. Kui mitu väga sarnast rida on järjest, siis võiks sellega midagi ette võtta.
Samas ei tohiks loetavus selle all kannatada.

Märkus: antud koodi parem disain oleks selline, et InvoiceRowGenerator ise salvestamisega ei
tegeleks ja tagastaks genereeritud osamaksed. Mock-imise harjutamise huvides on see
disain seekord selline.
