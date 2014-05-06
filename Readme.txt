==================
CS 425 Spring 2014 
	  MP 3
==================

------------------------
Netids: cting4, reziapo1
------------------------

-----------
Compilation
-----------
	- IDE 
		Import the project into a Java IDE (Eclipse or Intelli J)
		Make or build the project in the IDE
	- Manual
        find . -name "*.java" -print | xargs javac

------------------
Config File Format
------------------
	For each line:
		[peer name][id] [ip] [port]

	Example:
        node-1,1,127.0.0.1,8001
        node-2,2,127.0.0.1,8002
        node-3,3,127.0.0.1,8003
        node-4,4,127.0.0.1,8004

------------------
Testing Sample Command
------------------
	For each line:
		java NoSQL [config file] [local id] [number of replicas] "[peer id: delay(ms),peer id:delay(ms),peer id: delay(ms)]"

	Example:
        java NoSQL ../config 1 3 "2:1000,3:16000,4:32000"
		java NoSQL ../config 2 3 "3:1000,4:1000,1:1000"
		java NoSQL ../config 3 3 "4:4000,1:8000,2:2000"
        java NoSQL ../config 4 3 "1:1000,2:2000,3:4000"
	
----------------------------
Running / Command line usage
----------------------------
	java NoSQL [configFile] [selfId] [number of replicas] [peer delay list]
    
        configFile - the path to the configuration file
		selfId - the id of the chat client in configuration file
        number of replicas - the number of replicas for each operation
        peer delay list - "[peer id: delay(ms),peer id:delay(ms),peer id: delay(ms)]"
            example: "2:1000,3:16000,4:32000"


----------
Algorithms
----------
(See report for details)
    Read repair:
        whether a read request is fired, after received all responses from differet replicas, the coordinator will
        check whether all responsed tuples have same timestamp.
        If some tuples' timestamp is smaller than the largest one, then the read Repair will start by sending repair
        request to the replicas that return tuple with small timestamp.
        If all tuples have the same timestamp, the we do not need to repair.

