/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .hermes.configureContextNetConnection("1", IP, PORT, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
    .hermes.connect("1");
    .print("Hello world - untell!!!").

+beautiful: true <-
    .print("I started to believe that I am beautiful").

-beautiful: true <-
    .print("I am not beautiful anymore!").