Poste Tracker
=============

(c) Mario Piccinelli <mario.piccinelli@gmail.com> 2014

Poste Tracker è un semplice programma in Java che permette di monitorare le 
spedizioni sotto forma di raccomandata internazionale (come i pacchetti di AliExpress,
per esempio) estraendo in maniera automatica le informazioni dal servizio DoveQuando
di Poste Italiane. Ovviamente il tracking è limitato alle informazioni fornite dal suddetto
servizio, il che significa che fino all'arrivo in Italia del pacchetto le informazioni disponibili
sono molto scarne.

Prerequisiti
------------
Il programma richiede l'ambiente Java (JRE) 8, all'ultima versione disponibile. Si può scaricare gratuitamente da
https://www.java.com/it/download/manual.jsp

Installazione
-------------
Per installare il programma è sufficiente scaricare l'ultima versione disponibile dal sito 
http://piccimario.github.io/PosteTracker/ ed estrarre il contenuto del file ZIP in una cartella qualsiasi.

Aggiornamento
-------------
Se hai usato il programma (e hai archiviato delle spedizioni che desideri conservare) puoi procedere ad
aggiornare il programma semplicemente andando a sovrascrivere i file preesistenti con quelli estratti dal 
nuovo ZIP. Come unica raccomandazione, devi conservare il file postetracker.db, che contiene le informazioni
relative alle tue spedizioni. Conviene sempre conservarne una copia. In ogni caso, se il file viene cancellato
il programma al successivo avvio lo ricreerà da zero.

Avvio
-----
Per avviare il programma da Windows è sufficiente fare doppio click sul file PosteTracker.jar. Se dovessero
verificarsi problemi in fase di avvio (Windows non riconosce l'estensione, oppure viene mostrato un errore e poi
il programma non viene avviato), probabilmente non si è installata l'ultima versione di Java 8. Vedi alla voce
"Prerequisiti".

Utilizzo
--------

### Per aggiungere un tracking code 
Premi il pulsante "Aggiungi" e nella finestra inserisci il codice e una breve descrizione.

### Per archiviare una spedizione
Seleziona la spedizione nell'elenco e premi il tasto "Archivia". Per vedere nella lista 
anche gli archiviati, attiva l'opzione "Mostra archiviati" (questi verranno mostrati in grigio). 
Per dis-archiviare una spedizione archiviata, selezionala e premi ancora il tasto "Archivia".

### Per aggiornare le informazioni
Basta premere il tasto "Aggiorna" per caricare eventuali aggiornamenti dal sito delle 
Poste per tutti i codici non archiviati (quelli archiviati non vengono analizzati). Se ci sono stati nuovi, questi vengono 
mostrati in una finestra di notifica. Inoltre, selezionando un elemento nella tabella posso leggere tutti i suoi status 
nella parte bassa della finestra.

### Devo avere il programma sempre attivo? 
Gli status vengono aggiornati solo quando si avvia il programma o quando si preme 
il tasto "Aggiorna", quindi si, il consiglio è avere il programma sempre attivo con l'aggiornamento automatico attivato 
(selezionando l'opzione "auto refresh" e impostando un tempo in minuti, vanno benissimo i 30 preimpostati). In alternativa 
conviene avviare il programma almeno una volta al giorno, per catturare eventuali aggiornamenti (tenete presente che sul 
sito si possono reperire solo il primo e l'ultimo status, quindi se non vado a leggere uno stato nuovo prima che questo 
venga rimpiazzato non potrò recuperarlo).
