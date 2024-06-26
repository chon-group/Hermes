/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f889");
    .connect("1");
    .print("Hello world - autoConnection - tell - Inquilinism!!!");
    !!enviar;
    !enviardois;
    .stopMAS.

+!enviar : true <-
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tell, hello);
    .print("Mensagem 1 enviada");
    .wait(500);
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f881", tell, hello);
    .print("Mensagem 1 enviada");
    .wait(500);
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f881", tell, hello);
    .print("Mensagem 1 enviada");
    .wait(500).


+!enviardois : true <-
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f884", tell, hellodois);
    .print("Mensagem 2 enviada");
    .wait(500);
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f882", tell, hellodois);
    .print("Mensagem 2 enviada");
    .wait(500);
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f882", tell, hellodois);
    .print("Mensagem 2 enviada");
    .wait(500).