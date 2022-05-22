package software;

import java.util.Scanner;

import hardware.VM;

public class Interface extends Thread {
    Scanner in = new Scanner(System.in);

    public void run(){
        //TODO: adicionar todas as opções
        System.out.println("1 - Fibonacci");
        System.out.println("2 - Factorial");
        System.out.println("Instructions: \n exec <id> - to run a program \n dump <id> - list all PCB content \n dumpM <start> <end> - list memory frames \n dealocate <id> - remove the process ");

        while(true){
            int command = in.nextInt();

            if(command != 1 || command != 2){
                System.out.println("Invalid program");
            }
            else if(command == 0){
                System.out.println("Ending system.");
                System.exit(0);
                in.close();
            }
            else{
                createProcess(command);
            }

        }
    }

    public void createProcess(int code){
        boolean allocated = false; 

        if(code == 1){
            allocated = VM.pm.createProcess(new Programs().fibonacci10);
        }
        else if(code == 2){
            allocated = VM.pm.createProcess(new Programs().fatorial);
        }
        if(!allocated){ System.out.println("It wasn't possible create the program."); }
    }
}
