**Nome dos integrantes:** Gustavo G. Lottermann, Morgana Luiza Weber

**Seção Implementação:**

O programa implementa todas as características solicitadas, tanto o escalonamento (Scheduler), quanto 
a fase final, na qual o IO é concorrente.

**Seção Testes**

FASE 6

Para ver o funcionamento do escalonamento, deve-se carregar em memória vários programas a partir do exec < id >.
Quando carregados 2 ou mais programas, o primeiro será executado, até um tempo Delta, que foi definido como 7
intruções executadas e após isso, sua execução será interrompida, tendo como saída a mensagem "Type Interruption: 
Max CPU cycle reached" para dar espaço para que o próximo programa da memória seja executado nesse mesmo número de ciclos de CPU. 
A partir da mensagem "Scheduling process with id = _", que dá antes da execução de cada um dos programas, dá para saber 
o id do programa que está em execução no momento. Assim, toda vez que um programa é interrompido pela interrupção do tipo clock,
é chamado outro programa salvo, até que todos processos da memória acabem e sejam desalocados do sistema. Quando um processo acaba, é printado
na tela a mensagem "Type Interruption: Program ended". Assim, se espera que todos programas sejam executados, escalonados, até que 
essa mensagem final apareça para todos os processos.


FASE 7

Para ver o funcionamento das interrupções por chamadas de sistemas (IO), deve ser chamado os programas PA Input ou PB Output.
Ao chamar o programa PB Output, ele vai chamar uma interrupção porque o programa precisa dar um output sobre um resultado e ao dar esse output,
ele vai continuar executando, até outro programa seja escalonado com ele, se espera que ao dar uma interrupção de Output, apareça
as mensagens:

              [Process with ID = _ - WRITE]
              
              [Output from process with ID = 0] 6"
              
Já aochamar o PA Input, vai dar uma interrupção para que o usuário faça um Input de um valor solicitado, e botará o processo na 
fila de bloqueados para que possam ser executados outros programas enquanto o sistema aguarda o input, para isso 
pode ser feito um exec < id > de outro programa e na linha abaixo adicionar o input para o programa PA Input, para que ele saia
da lista de bloqueados e seja executado pelo sistema.
