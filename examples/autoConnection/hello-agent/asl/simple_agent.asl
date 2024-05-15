/* Initial beliefs and rules */
day.
{include("examples/contextnetAgent.asl")}
hermes::contextNetConfiguration("1","uff.bot.chon.group",5500,"788b2b22-baa6-4c61-b1bb-01cff1f5f880","true","NoSecurity").

/* Initial goals */

!start.

/* Plans */

+!start: contextnetServer(IP, PORT) <-
    .print("Hello world - autoConnection!!!").
