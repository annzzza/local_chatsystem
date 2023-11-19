# Chat System

## À propos du projet

Chat System est un logiciel de communication décentralisé créé dans le cadre de la 4 IR à l'INSA Toulouse.
Il permet à des utilisateurs de communiquer ensemble dans même réseau. Il est développé en Java et est conçu pour être déployé sur un réseau d'entreprise fermé: pas de connexion à l'Internet nécessaire.

## How to run the program

You will need Maven and Java 19.

To **compile** it, run the following command:
```bash
mvn clean package
```

To **run** it, execute the following command:
```bash
java -jar target/chatsystem-bonnet-cazeneuve-1.0-SNAPSHOT.jar
```

## Carbonara recipe
For one lonely person:
- 2 eggs
- 50g parmesan cheese
- freshly grounded black pepper
- rigatoni (~110 g)
- 2 lices of guanciale
  
Fry the guanciale cut into dices in a pan. Keep the fat in the pan and put the pan aside.
Put the water to a boil. In the meantime mix the two egg yolks, the black pepper and the parmesan cheese in a bowl.
Add the pasta into the salted water, stir. After a couple minute, take some pasta water out of the pot and mix it in the egg and cheese mixture.
When the pasta is done (but not overcooked!), add it to the greasy pan and fry it for a minute. Cut power off, light up a candle and add the egg mix to the pasta.
Serve immediately.

## Identify yourself

Before anything, **complete the `metadata.yml` with your own information.** 

This is the file that we will use to identify the teacher responsible for the repository and assign the grades to the participating students.

## Take ownership ( I took it)

As long as you make sure to keep the `metadata.yml` file at the root of this repository, you are free to do anything. Our suggestion would be to have it organized into something like the following:

    .gitignore
    metadata.yml
    pom.xml
    README.md
    src/
      main/
      test/
    doc/
      uml/
      report.pdf

In particular, you will soon have to replace this README with something that describes your own project.
