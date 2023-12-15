/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}
Hermes::contextNetConfiguration("1","192.168.1.121",3273,"788b2b22-baa6-4c61-b1bb-01cff1f5f880","true","NoSecurity").

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .print("Hello world - autoConnection!!!").
