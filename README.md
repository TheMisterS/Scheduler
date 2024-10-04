# Introduction

Scheduler is a small personal project to practise PostgreSQL and JAVA. <br>

It consists of a PostgreSQL database and an interface built with Java and the JDBC library. <br>


## Requirements

PostgreSQL 11 or later <br>
Java (JDK) <br>
Git Bash (optional)
## Installation
Step 1. To setup the database run the following script <br>
```bash
./init_db.sh
```
On Windows you may need to make it executable first<br>
```bash
chmod +x init_db.sh
./init_db.sh
```
You may also verify the DB setup by connecting to it and listing the tables.
```bash
psql -U postgres -d scheduler_db
\dt scheduler.*
```
Step 2. Compile and run the Java program with the JDBC library
```bash
javac -cp ".;postgresql-42.7.4.jar" Main.java
```

```bash
java -cp ".;postgresql-42.7.4.jar" Main
```
## Usage
1. Login/Register: <br>
The program starts by asking if you want to register or login as a user.
2. Program Menu: <br>
Once authenticated, you will have access to the following functionalities:
- Schedule a new task. <br>
-   Add a new tag.<br>
-   Schedule a reminder.<br>
-   View data (tasks, tags, reminders).<br>
-   Edit a task.<br>
-   Remove a tag.<br>
-   Cancel a reminder.<br>
-   Tag a task.<br>
-   Search for a user (admin functionality).<br>
-   Help (for more information on usage).<br>
-   Logout to exit the application.<br>



## License

[MIT](https://choosealicense.com/licenses/mit/)