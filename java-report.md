## tech stack
**UDP**: used to create and maintain the list of connected users: discovery, new connected user, disconnection, change of username...

**TCP**: used for message exchange. The Server is always listening (to receive messages from any user) 
while the Client creates a session each time a new chat is selected (to send messages to this specific user).

**SQLite**: SQL database engine used in the project to maintain, access and write the history of messages (DB). It is cross-platforms, which allows great modularity. 
However, problems occur when multiple threads try to write in the DB at once.

**Java Swing**: GUI library provided by the standard JDK, used here to provide an experience similar to that of a regular chat system. 
Even though this library was highly recommended by the professors, we still think it is not ideal and JavaFX could have been a far better choice, especially in implementing **MVC**. 
Due to the semi-deprecation of Swing, we encountered strange errors (X11 DISPLAY).

**Default Java Logger**: used with a LogFormatter, we could display messages during testing or execution. It helps giving full understanding of the code and its potential issues and helps with debugging. 
By formatting, we can custom it to our specific use.


## testing policy

We used JUnit testing to provide unit tests for the major part of our code. 
The database, user and network packages are fairly covered. The GUI package, however, is harder to test.
Overall, the global **coverage** of our application is around **70%**.

Unit testing helps us identify the weaknesses in our implementation and give us clear information on what to do better. 
With good **logging** we can easily understand and fix code issues. 

We did not use Test-driven Development, mainly by lack of expertise with Java, but we have acknowledged its potential and will use it in a future project. 

## highlights

- testing -> 
  - network.tcp.**TCPTests**
  - users.**ConnectedUserListTest**
- code structure -> gui.view.**ContactView**: Singleton class implementing Observer on ConnectedUserList, creates a graphical representation of the list of connected users.
- database -> database.**Database** Singleton design pattern to solve the issues faced with SQLite and multi thread access.