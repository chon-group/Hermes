// Agente A

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <-
	.print("Sou o agente A4");
	.send(agente_b, tell, crenca_de_a_para_b);
	+crencaA.