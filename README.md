# Chat System

## About the project

Chat System is a decentralized communication software created as part of the 4 IR program at INSA Toulouse.
It enables users to communicate with each other on the same network. It is developed in Java and is designed to be deployed on a closed corporate network: no Internet connection is required.

## Features

- [x] Discover the network to get connected users.
- [x] Connection to the system
- [x] Change username
- [x] Disconnection
- [x] Messaging
- [x] History of messages
- [ ] Extra

## How to use the program

You will need Maven and Java (version >=17).

### Easy way with GitHub Release

**Download** the latest release on [GitHub](https://github.com/insa-4ir-chatsystem/chatsystem-bonnet-cazeneuve/releases/)

**Launch** the program either by double-clicking on it, or **run** `java -jar Chat-System-Release-{version}.jar`

### Compile it yourself
#### Install Maven on Linux machines

```shell
mkdir -p ~/bin
cd ~/bin
wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz -O maven.tar.gz
tar xf maven.tar.gz
echo 'export PATH=~/bin/apache-maven-3.9.5/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

#### Clone the repository

Clone the repository to get the source code:
```shell
git clone https://github.com/insa-4ir-chatsystem/chatsystem-bonnet-cazeneuve.git
```

You can also download the ZIP file and unzip it.

#### Compilation
To **compile** it, you will use Maven, run the following command:
```bash
mvn clean package
```
#### Run the program

The program uses Swing, any X environment variable should be set.

To **run** it, execute the following command:
```bash
java -jar target/chatsystem-bonnet-cazeneuve-1.1-jar-with-dependencies.jar
```

_Change the `1.1` in the command with the current version of the project visible in `pom.xml`_.

## Documentation and reports

You can find reports on the development process we have followed and UML diagrams in the [docs folder](docs):
- [Management and CI/CD](docs/Rapport_chatsystem_management.pdf)
- [UML diagrams](docs/Rapport_chatsystem_UML.pdf)

You can know more about the tech stack, testing policy and highlights in [java-report.md](java-report.md).

## Authors
- [Ronan Bonnet](https://github.com/BloodFutur)
- [Anna Cazeneuve](https://github.com/annzzza)