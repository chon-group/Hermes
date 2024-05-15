/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */
/* 788b2b22-baa6-4c61-b1bb-01cff1f5f880 */
!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .hermes.configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .hermes.connect("1");
    .print("Hello world - tellHow!!!");
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tellHow, "+!run: true <- .print(\"I am Running\"); .wait(500); !run.");
    .wait(1000);
    .hermes.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", achieve, run).

@p__9
+!run: true <-
    .print("I am Running");
    .wait(500);
    !run.