package software;
import java.util.Scanner;
import hardware.VM;

public class Interface {
    Scanner in = new Scanner(System.in);

    public void run(){
        //TODO: adicionar todas as opções
        System.out.println("##################################################");
        System.out.println("----------- Operation System Simulator -----------");
        System.out.println("----------- 1 - Fibonacci              -----------");
        System.out.println("----------- 2 - Factorial              -----------");
        System.out.println("-----------                            -----------");
        System.out.println("###########     INSTRUCTIONS:          ###########");
        System.out.println("----------- exec <id>                  -----------");
        System.out.println("----------- dump <id>                  -----------");
        System.out.println("----------- dumpM 0                    -----------");
        System.out.println("----------- dealocate <id>             -----------");
        System.out.println("----------- exit 0                     -----------");
        System.out.println("##################################################");

        while(true){
            String readLine = in.nextLine();
            String command = readLine.split(" ")[0];
            int id = Integer.parseInt(readLine.split(" ")[1]);

            //TODO: adicionar outros comandos no if
            if(id != 0 && id != 1  && id != 2){
                System.out.println("Invalid program id");
            }
            else{
                switch(command){
                    case "exec":
                        createProcess(id);
                        break;
                    case "dump":
                        VM.pm.dump(id);
                        break;
                    case "dumpM":
                        VM.pm.dumpM();
                        break;
                    case "dealocate":
                        VM.pm.deallocateProcess(id);
                        break;
                    case "exit":
                        System.out.println("Ending system");
                        System.exit(0);
                        in.close();
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;
                }
            }
        }
    }

    public void createProcess(int code){
        boolean allocated = false; 
        //TODO: colocar outros ifs com outros programas
        if(code == 1){
            allocated = VM.pm.createProcess(new Programs().fibonacci10);
        }
        else if(code == 2){
            allocated = VM.pm.createProcess(new Programs().fatorial);
        }
        if(!allocated){ System.out.println("Memory unavailable to create process"); }
    }
}
