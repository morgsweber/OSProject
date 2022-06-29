# Operational System Project #

##### About the project

This repository was created to develop a university work on Operational Systems. 
It was developed: 
- some programs with low-level instructions
    - the programs are defined in Programs.java and the 'pc' is one incompleted program
- interruptions
    - you can find the interruptions created on Interruptions.java
- system calls
    - in this part, it was implemented the TRAP instruction
- memory manager
    - this memory manager implements memory pagination 
- process manager
    - the process has three states: running, ready, or blocked (by IO request)
- scheduler
    - the process is scheduled by a number of instructions, each process run 7 instructions then schedule with other 
- IO
    - when a process needs an IO input, for example, the process is blocked until the user provides the input

![image](https://user-images.githubusercontent.com/50406261/176444423-c993cf71-9f40-401e-88d4-f6613dc6bae4.png)

You can find more information about the project in the folder "guidelines" in this repository. 

##### Requirements
* [Java](https://www.oracle.com/java/technologies/downloads/)

##### Running the project

First, you need compile the project using:

```
cd src
javac Main.java
```

After, run the project with: 

`java Main`

Then, you will see some instructions to exec or deallocate a process, dump the memory, and exit. If you exec some process, you will see on the terminal the instructions running and the scheduler happening.

##### Observations
This project has some bugs, but the main features are implemented and functional.

