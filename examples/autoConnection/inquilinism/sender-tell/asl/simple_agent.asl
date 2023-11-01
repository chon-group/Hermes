/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("1", "169.254.7.146", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f889");
    .connect("1");
    .print("Hello world - tell - Inquilinism!!!");
    !!enviar;
    !enviardois;
    .stopMAS.

+!enviar : true <-
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tell, hello);
    .print("Mensagem 1 enviada");
    .wait(500);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f881", tell, hello);
    .print("Mensagem 1 enviada");
    .wait(500);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f881", tell, hello);
    .print("Mensagem 1 enviada");
    .wait(500).


+!enviardois : true <-
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f884", tell, hellodois);
    .print("Mensagem 2 enviada");
    .wait(500);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f882", tell, hellodois);
    .print("Mensagem 2 enviada");
    .wait(500);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f882", tell, hellodois);
    .print("Mensagem 2 enviada");
    .wait(500).