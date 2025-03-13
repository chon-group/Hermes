/* Initial beliefs and rules */
skill(chef).
skill(youtuber).
skill(writer).
menu(["Lamb rack with sprout salad", "Popcorn chicken with basil", "Potato tortilla", "Butter chicken moussaka", "Matt Preston's ice cream brioche rolls"]).
lastOrder(0).

/* Plans */
+!order(Product,Qtd)[source(Client)]: lastOrder(Last) <-
	OrderNumber=(Last+1);
	-+lastOrder(OrderNumber);
	.print("Order:",OrderNumber," - Preparing ",Qtd," ",Product);
	.send(Client,tell,foodArrived(OrderNumber,Product,Qtd)).