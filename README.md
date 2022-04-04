# SisOpProject

**Nome dos integrantes:**

Gustavo G. Lottermann, Morgana Luiza Weber, Eduardo Berwanger


**Seção Implementação:**

Para fazer a compilação do código, é preciso ter instalado o JDK em sua máquina e um compilador a escolha. 
Caso queira editar o código e trabalhar nele, é necessário um editor de código a exemplo do Intellij, VSCode,
ou outro.

Para executar o código, deve ser executado o método _main()_ do arquivo de código _Sistema.java_.
Com isso, antes de colocar para executar, deve ser escolhido o programa que quer que seja executado, removendo o comentário relativo a ele.

```java
public static void main(String args[]) {
    Sistema s = new Sistema();
    s.roda(progs.pa);     //executa PA
    //s.roda(progs.pb);   //executa PB
    //s.roda(progs.pc);  // executa PC
}
```

**Seção Programas:**

Das entregas solicitadas foram implementados:
1. todas as instruções 
2. PA
3. PB
4. uma parte do PC (não está funcionando por completo)
5. interrupções de overflow, instrução inválida, endereço inválido e chamada de sistema 
6. foram criados testes para cada uma das interrupções 

**Seção Saídas:**

s.roda(progs.pa);
![image](https://user-images.githubusercontent.com/50406261/160927346-a186337b-dbc9-4526-80e7-963d977ace76.png)

s.roda(progs.testePA);
![image](https://user-images.githubusercontent.com/50406261/160927504-4ff29e19-f963-47b2-b19f-b31aafb088c2.png)


s.roda(progs.pb);
![image](https://user-images.githubusercontent.com/50406261/160927667-681d5f7d-4b54-4a9f-bf49-56c1b2d35da9.png)

s.roda(progs.testePB);
![image](https://user-images.githubusercontent.com/50406261/160927769-7e6c38cd-e29b-4aab-b5f6-f728c912315e.png)

s.roda(progs.pc);
**Vai entrar em looping**

s.roda(progs.testeOverflow);
![image](https://user-images.githubusercontent.com/50406261/160928435-61e4a7e9-a67d-4934-ab12-e690b6d34298.png)

s.roda(progs.testeEnderecoInvalido);
![image](https://user-images.githubusercontent.com/50406261/160928529-d8db2f7f-b4e2-4e9e-ba24-bfc69b5773be.png)

s.roda(progs.testeInstrucaoInvalida);
![image](https://user-images.githubusercontent.com/50406261/160928642-43f1366e-bca7-4c44-a7cd-5f8714acbfcc.png)

s.roda(progs.testaIn);
![image](https://user-images.githubusercontent.com/50406261/160928839-ed1da981-125f-4a51-a9e9-d797e34eed0f.png)

s.roda(progs.testaOut);
![image](https://user-images.githubusercontent.com/50406261/160930160-6f8a184f-0f0d-49aa-82f9-17ad87cf9ae4.png)

s.roda(progs.paInput); >>> para input=3
![image](https://user-images.githubusercontent.com/50406261/160930370-c224b8fc-45ba-4191-831a-333388335e16.png)

s.roda(progs.pbOutput);
![image](https://user-images.githubusercontent.com/50406261/160930545-fce0b229-ffd3-4774-9736-1051bd2ec8bb.png)
