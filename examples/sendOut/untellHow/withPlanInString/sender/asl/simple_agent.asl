/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */
/* 788b2b22-baa6-4c61-b1bb-01cff1f5f880 */
!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("1");
    .print("Hello world - untellHow - withPlanInString!!!");
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tellHow, "+!run: true <- .print(\"I am Running\"); .wait(500); !run.");
    .wait(1000);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", achieve, run);
    .wait(3000);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", untellHow, "+!run: true <- .print(\"I am Running\"); .wait(500); !run.");
    .print("untellHow done!").

@p__1
+!run: true <-
    .print("I am Running");
    .wait(500);
    !run.