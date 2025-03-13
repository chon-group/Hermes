/* Initial beliefs and rules */
battery(100).

/* Initial goals */

!start.

/* Plans */

+!start: true <-
    .print("Hello world - I am the energy agent of the Optimus Prime MAS!!!");
    .wait(3000);
    .print("I will monitor energy resources!");
    .wait(3000);
    !monitor.

+!monitor : battery(X) & X > 10 <-
    .print("battery[",X,"] Energy resources are ok!");
    -+battery(X-9);
    .wait(3000);
    !monitor.

-!monitor : battery(X) <-
    .print("battery[",X,"] Energy resources are critical!");
    .wait(3000);
    .print("battery[",X,"] So, I have to notify the system agent Hermes to cryogenate the MAS!");
    .wait(3000);
    .send(simple_agent, achieve, energyCritical).
