# Istruzioni per l’avvio #
Il Server, così come il Client, viene avviato tramite un sistema di virtualizzazione a container attraverso l’ausilio della piattaforma open-source Docker.
A tale scopo, è stato definito un file docker-compose.yml.
Per avviare l’applicazione VirtualLabs, è necessario lanciare i seguenti comandi nella CLI all’interno della cartella principale VirtualLabs:

- `docker-compose build`: si occupa di scaricare le immagini specificate nel docker-compose file

- `docker-compose up`: per avviare l’esecuzione dei contenitori

Il client è accessibile aprendo il browser all’indirizzo *http://localhost:4200*.

Durante l’utilizzo dell’applicazione è necessario avere accesso a degli account e-mail per completare le procedure di registrazione e conferma/rifiuto adesione ad un team. Di conseguenza, sono stati creati due account gmail, il primo utilizzato per inviare le e-mail e il secondo per riceverle. Vengono di seguito fornite le credenziali per accedere al secondo account:

email: *testaivirtuallabs@gmail.com*

password: *testai2020!*

# Configurazione iniziale #
Al fine di testare il funzionamento dell’applicazione sono stati inseriti i seguenti utenti:

- d1@polito.it	password: 01234567
- d2@polito.it	password: 01234567
- s266556@studenti.polito.it	  password: 01234567
- s267543@studenti.polito.it	  password: 01234567
- s267560@studenti.polito.it	  password: 01234567
- s267782@studenti.polito.it	  password: 01234567
- s1@studenti.polito.it	password: 01234567
- s3@studenti.polito.it	password: 01234567

È stato inoltre aggiunto il corso “Applicazioni Internet” avente come titolari i docenti “d1” e “d2”, tutti gli studenti presenti nel sistema sono stati aggiunti al corso e sono state inoltre inserite due consegne.

È stato creato un team, denominato “Team1”, avente come membri gli studenti s267543, s266556, s267560 e due VM “VM1” e “VM2”.

Lo studente s267543 ha caricato una versione dell’elaborato e il docente ha caricato due revisioni.

