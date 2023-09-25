/* Initial beliefs and rules */
day.

/* Initial goals */
/* 788b2b22-baa6-4c61-b1bb-01cff1f5f880 */
!start.

/* Plans */

+!start: day <-
    .configureContextNetConnection("1", "192.168.0.106", 3273, "788b2b22-baa6-4c61-b1bb-01cff1f5f881");
    .connect("1");
    .print("Hello world!!!");
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tellHow, {@p__3[source("788b2b22-baa6-4c61-b1bb-01cff1f5f881")] +!run: true <- .print("I am Running with chave"); .wait(500); !run});
    .wait(1000);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", achieve, run);
    .wait(3000);
    .sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", untellHow, {@p__3[source("788b2b22-baa6-4c61-b1bb-01cff1f5f881")] +!run : true <- .print("I am Running with chave"); .wait(500); !run});
    .print("untellHow done!").

@p__1
+!run: true <-
    .print("I am Running");
    .wait(500);
    !run.