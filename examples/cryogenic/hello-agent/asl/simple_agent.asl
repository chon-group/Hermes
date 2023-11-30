/* Initial beliefs and rules */
day.

/* Initial goals */

!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("1", "192.168.0.105", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .connect("1");
    .setTrophicLevel(2);
    -day;
    .print("Hello world!!!");
    .send(agent1, tell, hello);
    .print("Preparing for cryonics!!!");
    .wait(200);
    .cryogenic;
    !start.

-!start: true <-
    .print("was cryogenated!!!").

