# Hermes
[![](https://jitpack.io/v/chon-group/Hermes.svg)](https://jitpack.io/#chon-group/Hermes)

Repositorio criado para o desenvolvimento da arquitetura de agentes Hermes capaz de se comunicar e transferir agentes entre diferentes SMA.

Os agentes Hermes foram criados generalizando as funcionalidades apresentadas pelos agentes *Comunicadores* como **comunicação** e **mobilidade** de agentes.
A inclusão de novos *middlewares* para comunicação de agentes a uma infraestrutura de rede foi generalizada seguindo os padrões de desenvolvimento GoF, assim como a inclusão de métodos de criptografia de mensagens. 

Na Figura a seguir podemos observar o ciclo de raciocinio do agente Hermes modificado e como podemos adicionar novos **middleware** e novos métodos de criptografia.

![Diagrama-Hermes-newMiddleware.png](files%2FDiagrama-Hermes-newMiddleware.png)

O **Security** apresentado na imagem anterior é uma interface Java com somente dois métodos para serem implementados **encrypt** e **decrypt** visando facilitar a inclusão de novos métodos de criptografia.

A arquitetura Hermes é um **pacote Jason** que pode ser utilizado em qualquer versão do *framework* Jason e possui as seguintes capacidades:
1. Gerenciamento de conexões de rede.
   - Configuração de conexões;
   - Conexão e desconexão da rede;
   - Autoconexão.
2. Habilidades Sociais
   - tell
   - untell
   - achieve
   - unachieve
   - askOne
   - askAll
   - askHow
   - tellHow
   - untellHow
3. Protocolos bioinspirados de transferencia de agentes.
   - Mutualismo;
   - Inquilinismo;
   - Predação;
     - Atualização de nível trófico.
   - Clonagem;
4. Criogenia.
5. Auto Localização.

## 1. Gerenciamento de conexões de rede

Com a arquitetura Hermes, os agentes podem se conectar a uma ou mais infraestrutura de redes. As funcionalidades da arquitetura Hermes estão disposiveis como ações internas.

### Configuração de conexão

Para criar uma configuração de uma nova conexão cada *middleware* disponível na arquitetura possui uma ação interna específica. 


O nome da ação interna para configuração de nova conexão segue o padrão: .<span style="color:green">*configure*</span><span style="color:orange">**NOME_DO_MIDDLEWARE**</span><span style="color:green">*Connection*</span>(<span style="color:blue">***IDENTIFICADOR_DA_CONEXÃO***</span>, <span style="color:blue">***...***</span>)</span>. Sendo assim:
- A ação interna se inicia com a palavra <span style="color:green">*configure*</span>;
- Seguida do nome do *middleware*;
- Finalizando com a palavra <span style="color:green">*Connection*</span>;

OBS: O primeiro parâmetro sempre deve ser o <span style="color:blue">***IDENTIFICADOR_DA_CONEXÃO***</span>.

No exemplo abaixo é possível observar a configuração de uma nova rede **ContextNet**.

```
.configureContextNetConnection(IDENTIFICADOR_DA_CONEXÃO, IP_DO_GATEWAY, PORT_DO_GATEWAY, UUID_DO_AGENTE);
```

```
.configureContextNetConnection("1", "192.168.15.102", 5500, "788b2b22-baa6-4c61-b1bb-01cff1f5f880");
```

### Conexão e desconexão da rede

Ao realizar a configuração de uma nova conexão, o agente **Hermes** ainda não está conectado a esta rede. Para realizar a conexão existe uma ação interna.

A ação interna de conexão segue o seguinte padrão: .<span style="color:green">*connect*</span>(<span style="color:blue">***IDENTIFICADOR_DA_CONEXÃO***</span>). Sendo assim:
- O nome da ação interna é <span style="color:green">*connect*</span>;
- E possui somente um parâmetro: <span style="color:blue">***IDENTIFICADOR_DA_CONEXÃO***</span>.

No exemplo abaixo é possível observar como o agente Hermes se conecta em uma nova rede.

```
.connect(IDENTIFICADOR_DA_CONEXÃO);
```

```
.connect("1");
```

Para realizar a **desconexão** de uma rede, o agente **Hermes** possui uma ação interna que segue o mesmo padrão da ação interna de conexão:

```
.disconnect(IDENTIFICADOR_DA_CONEXÃO);
```

```
.disconnect("1");
```

### Autoconexão

A autoconexão dos agentes **Hermes** é dada nos seguintes cados:
1. Caso a infraestrutura de rede que o agente esteja conectado fique indisponível e o agente seja desconectado, o agente **Hermes** se reconecta automaticamente quando a infraestrutura de rede estiver disponível novamente.
2. Caso um agente **Hermes** seja movido para outro SMA utilizando os protocolos bioinspirados de transferência de agentes, a arquitetura **Hermes** é capaz de verificar em qual/quais redes o agente estava conectado quando estava no SMA de Origem e realiza a uma reconexão automaticamente.
3. Caso o agente **Hermes** seja criogenado e no momento da criogenia o agente estivesse conectado em uma rede, quando esse agente **Hermes** for reanimado, a arquitetura **Hermes** é capaz de verificar em qual/quais redes o agente estava conectado e realiza a uma reconexão automaticamente.

## 2. Habilidades Sociais

As habilidades sociais dos agentes Hermes para a comunicação com agentes do mesmo SMA permanecem as mesmas. A arquitetura **Hermes** adiciona a capacidade de comunicação com agentes de outros SMA.

A sociabilidade entre agentes de diferentes SMA seguem o mesmo padrão utilizado para a ação interna **send**. 

A ação interna **send** segue o seguinte padrão: .<span style="color:green">*send*</span>(<span style="color:blue">***NOME_DO_AGENTE_DE_DESTINO***</span>, <span style="color:blue">***FORÇA_ILOCUCIONÁRIA***</span>, <span style="color:blue">***CONTEÚDO_DA_MENSAGEM***</span>). Sendo assim:
- O nome da ação interna é <span style="color:green">*send*</span>;
- E possui no mínimo três parâmetros (OBS: podendo ter mais dependendo da força ilocucionária utilizada):
  1. <span style="color:blue">***NOME_DO_AGENT_DE_DESTINO***</span>;
  2. <span style="color:blue">***FORÇA_ILOCUCIONÁRIA***</span>
  3. <span style="color:blue">***CONTEÚDO_DA_MENSAGEM***</span>

No exemplo abaixo é possível observar como um agente envia uma crença de que o dia está bonito para um outro agente chamado de bob.

```
.send(NOME_DO_AGENTE_DE_DESTINO, FORÇA_ILOCUCIONÁRIA, CONTEÚDO_DA_MENSAGEM);
```

```
.send(bob, tell, dia(bonito));
```

A ação interna para a comunicação com diferentes SMA é a **sendOut** e segue o mesmo padrão da **send**: .<span style="color:green">*sendOut*</span>(<span style="color:blue">***IDENTIFICADOR_DO_AGENTE_DE_DESTINO***</span>, <span style="color:blue">***FORÇA_ILOCUCIONÁRIA***</span>, <span style="color:blue">***CONTEÚDO_DA_MENSAGEM***</span>). Sendo assim:
- O nome da ação interna é <span style="color:green">*sendOut*</span>;
- E possui no mínimo três parâmetros (OBS: podendo ter mais dependendo da força ilocucionária utilizada):
   1. <span style="color:blue">***IDENTIFICADOR_DO_AGENT_DE_DESTINO***</span> (Ex: Em uma conexão *ContextNet* é o <span style="color:blue">***UUID_DO_AGENTE_DE_DESTINO***</span>. Pode ser visto como o último parâmentro da ação interna <span style="color:green">configureContextNetConnection</span>);
   2. <span style="color:blue">***FORÇA_ILOCUCIONÁRIA***</span>
   3. <span style="color:blue">***CONTEÚDO_DA_MENSAGEM***</span>

### tell

No exemplo abaixo é possível observar como um agente envia uma crença para um agente de outro SMA.

```
.sendOut(IDENTIFICADOR_DO_AGENTE_DE_DESTINO, FORÇA_ILOCUCIONÁRIA, CONTEÚDO_DA_MENSAGEM);
```

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tell, hello);
```

OBS: É possível também passar como **último** parâmetro o <span style="color:blue">***IDENTIFICADOR_DA_CONEXÃO***</span> para obrigar o agente utilizar uma conexão específica para realizar a comunicação. Caso esse paramêtro não seja preenchido (como no exemplo anterior) a arquitetura de agentes realizar a comunicação com a primeira conexão ativa que estiver disponível.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tell, hello, "1");
```

### untell

No exemplo abaixo é possível observar como um agente envia para um agente de outro SMA deixar de acreditar na crença.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", untell, beautiful);
```

### achieve

No exemplo abaixo é possível observar como um agente envia uma intenção para um agente de outro SMA.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", achieve, run);
```

### unachieve

No exemplo abaixo é possível observar como um agente envia para cancelar uma intenção para um agente de outro SMA.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", unachieve, run);
```

### askOne

No exemplo abaixo é possível observar como um agente pergunta para um agente de outro SMA sobre uma crença.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", askOne, beautiful(X));
```

### askAll

No exemplo abaixo é possível observar como um agente pergunta para um agente de outro SMA todas as crenças que satisfaz um predicado.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", askAll, beautiful(X));
```

Ou seja, o agente receptor vai receber todas as crenças que possui o predicado beautiful(ALGUM_VALOR).

### askHow

No exemplo abaixo é possível observar como um agente pede para um agente de outro SMA a implementação de um plano.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", askHow, {+!run});
```

OBS: O agente receber o pedido, já responde automaticamente. Sem precisar do uso do tellHow.

### tellHow

No exemplo abaixo é possível observar como um agente envia a implementação de um plano para um agente de outro SMA. E possui três formar deferentes de ser feita:

1. Forma padrão da notação do **Jason**.
```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tellHow, {+!run: true <- .print("I am Running with chave"); .wait(500); !run});
```

2. Utilizando a anotação do plano.

Caso o plano tenha uma anotação "@p__1" como no exemplo abaixo:

```
@p__1
+!run: true <-
    .print("I am Running with anotation plan");
    .wait(500);
    !run.
```

É possível enviar esse plano utilizando a anotação:

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tellHow, "@p__1");
```

3. Utilizando aspas.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", tellHow, "+!run: true <- .print(\"I am Running\"); .wait(500); !run.");
```

OBS: Nessa forma, todos os caracteres especiais utilizados na implementação do plano tem que vir com o caracter \ na frente. No exemplo anterior, é possível observar que possui um \" para inserir o caracter ***"*** na implementação do plano run.

### untellHow

No exemplo abaixo é possível observar como um agente envia para um agente de outro SMA para esquecer a implementação de um plano. As três formas utilizadas no tellHow funcionam também para o untellHow:

1. Forma padrão da notação do **Jason**.
```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", untellHow, {+!run: true <- .print("I am Running with chave"); .wait(500); !run});
```

2. Utilizando a anotação do plano.

Caso o plano tenha uma anotação "@p__1" como no exemplo abaixo:

```
@p__1
+!run: true <-
    .print("I am Running with anotation plan");
    .wait(500);
    !run.
```

É possível enviar esse plano utilizando a anotação:

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", untellHow, "@p__1");
```

3. Utilizando aspas.

```
.sendOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", untellHow, "+!run: true <- .print(\"I am Running\"); .wait(500); !run.");
```

OBS: A mesma observação sobre os caracteres especiais também valem para o untellHow.

## 3. Protocolos Bioinspirados para transferencia de agentes

Os protocolos bioinspirados permitem a transferencia de agentes entre SMA distintos baseado nos conceitos de relações ecologicas da biologia.

Para o acionamento dos protocolos bioinspirados existe a ação interna chamada **moveOut**:

A ação interna **moveOut** segue o seguinte padrão: .<span style="color:green">*moveOut*</span>(<span style="color:blue">***IDENTIFICADOR_DO_AGENTE_DE_DESTINO***</span>, <span style="color:blue">***NOME_DO_PROTOCOLO***</span>). Sendo assim:
- O nome da ação interna é <span style="color:green">*moveOut*</span>;
- E possui no mínimo dois parâmetros (OBS: podendo ter mais dependendo do protocolo utilizado):
    1. <span style="color:blue">***IDENTIFICADOR_DO_AGENTE_DE_DESTINO***</span>;
    2. <span style="color:blue">***NOME_DO_PROTOCOLO***</span>

OBS: Assim como na ação interna **sendOut** é possível também passar como **último** parâmetro o <span style="color:blue">***IDENTIFICADOR_DA_CONEXÃO***</span> para obrigar o agente utilizar uma conexão específica para realizar o protocolo bioinspirado especificado. Caso esse paramêtro não seja preenchido a arquitetura de agentes executa o protocolo com a primeira conexão ativa que estiver disponível.

Sendo assim, atualmente temos algumas implementações de protolocos bioinspirados:

### Mutualismo

O protocolo inspirado na relação de mutualismo permite ao SMA de origem enviar agentes para outro SMA com o intuito de adquirir/transmitir novos conhecimentos e depois voltar para o SMA de origem para compartilhar esses conhecimentos com os demais agentes.

O protocolo de mutualismo existem três maneiras de ser executado:

1. Enviando todos os agentes

No exemplo abaixo é possível observar como executar o protocolo de mutualismo enviando todos os agentes do SMA de origem para outro SMA de destino, ficando somente o agente **Hermes** que está ativando o protocolo no SMA de origem.

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", mutualism);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", mutualism, "1");
```

2. Enviando um agente específico

No exemplo abaico é possível observar como executar o protocolo mutualismo enviando um agente específico para outro SMA. 

OBS: O agente deve existir no SMA de origem para ser enviado. Ou seja, o SMA de origem deve conter um agente chamado "*agent1*" para poder enviá-lo para outro SMA.

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", mutualism, agent1);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", mutualism, agent1, "1");
```

3. Enviando uma lista de agentes

No exemplo abaixo é possível observar como executar o protocolo mutualismo enviando uma lista de agentes específicos para outro SMA.

OBS: Os agentes devem existir no SMA de origem para serem enviados. Ou seja, o SMA de origem deve conter os agentes chamados de "*agent1*", "*agent2*" para poder enviá-los para outro SMA.

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", mutualism, [agent1, agent2]);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", mutualism, [agent1, agent2], "1");
```

### Inquilinismo

O protocolo inspirado na relação de inquilinismo permite ao SMA de origem se transportar para um outro SMA com o objetivo de ficar somente como um inquilino. Os agentes transferidos utilizando este protocolo não podem interferir nas atividades do SMA de destino e chegam com todas as inteções removidas.

O protocolo de inquilinismo envia todos os agentes do SMA de origem para o SMA de destino. Foi criado com o intuito de preservar os conhecimentos do SMA de origem em caso de mal funcionamento do dispositivo físico do SMA embarcado.

No exemplo abaixo é possível observar o acionamento do protocolo de inquilinismo:

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", inquilinism);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", inquilinism, "1");
```

### Predação

O protocolo inspirado na relação de predação permite ao SMA de origem predar o SMA de Destino. Ou seja, transferir todos os agentes do SMA de origem para o SMA de destino com o intuito de dominar o SMA de destino. Para isso, todos os agentes do SMA de destino são deletados e somente ficam os agentes vindos do SMA de origem.

Assim como o inquilinismo, o protocolo de predação envia todos os agentes do SMA de origem para o SMA de destino. Também foi criado com o intuito de preservar os conhecimentos do SMA de origem em caso de mal funcionamento do dispositivo físico do SMA embarcodo, porém nesta solução o SMA transferido consegue continuar exercendo suas funções normalmente no novo dispositivo físico.

No exemplo abaixo é possível observar o acionamento do protocolo de predação:

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", predation);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", predation, "1");
```


Porém, como neste protocolo o SMA de destino é deletado e seus conhecimentos são perdidos, então foi necessário a criação de um mecanismo para determinar qual SMA pode ou não predar o outro. O mecanismo criado foi baseado nos conceitos de **cadeias alimentares** da biologia.

#### Atualização de nível trófico

As cadeias alimentares possuem um conceito chamado de **nível trófico** que determina qual posição o ser vivo está localizado na cadeia alimentar. 

Os principais **níveis tróficos** são: 
1. ***Produtores*** que são os organismos capazes de produzir seus próprios alimentos.
2. ***Consumidores*** que obtém energia se alimentando de outros seres vivos.
3. ***Decompositores*** que obtém energia fazendo a decomposição de restos de organismo mortos.

Esses níveis tróficos são usados para determinar se um SMA pode ou não predar outro da seguinte maneira:
- Atualmente temos os seguintes níveis tróficos:
  1. PRODUCER;
  2. PRIMARY_CONSUMER;
  3. SECONDARY_CONSUMER.
- Um SMA pode somente predar um outro SMA que possui um nível trófico ***menor que*** o dele.
  - Exemplo:
    - SECONDARY_CONSUMER predando um PRIMARY_CONSUMER resulta em ACEITE.
    - PRIMARY_CONSUMER predando um PRODUCER resulta em ACEITE.
    - SECONDARY_CONSUMER predando um SECONDARY_CONSUMER resulta em REJEIÇÃO.
    - PRIMARY_CONSUMER predando um SECONDARY_CONSUMER resulta em REJEIÇÃO.
  
Cada SMA que possui um agente Hermes é capaz de utilizar os protocolos bioinspirados. Os agentes **Hermes** de um SMA sempre vem como padrão configurados com o nível trófico mais baixo (PRODUCER). E para atualizar o nível trófico existe uma ação interna.

A ação interna é **setTrophicLevel** segue o seguinte padrão: .<span style="color:green">*setTrophicLevel*</span>(<span style="color:blue">***NÍVEL_TRÓFICO***</span>). Sendo assim:
- O nome da ação interna é <span style="color:green">*setTrophicLevel*</span>;
- E possui somente um parâmetro:
    1. <span style="color:blue">***NÍVEL_TRÓFICO***</span>.

No exemplo abaixo é possível observar como executar a atualização de nível trófico.

```
.setTrophicLevel("PRIMARY_CONSUMER");
```

Caso queira fazer a atualização de nível trófico por valor inteiro também é possível:

- O valor **0** corresponde ao PRODUCER;
- O valor **1** corresponde ao PRIMARY_CONSUMER;
- O valor **2** corresponde ao SECONDARY_CONSUMER.

```
.setTrophicLevel(1);
```

### Clonagem

O protocolo inspirado na relação de clonagem permite ao SMA de origem fazer uma cópia de um, um grupo ou todos os seus agentes e enviar para outro SMA.

Assim como o mutualismo, este protocolo possui três maneiras de ser acionado:

1. Enviando todos os agentes.

Neste método, ao enviar todos os agentes do SMA de origem, o protocolo parte do princípio que o SMA de destino deve se tornar uma cópia perfeita do SMA de origem, ou seja, um SMA clone. Então, o SMA de destino recebe a cópia de todos os agentes do SMA de origem e deleta todos os agentes já existentes no sistema. 

Os agentes **Hermes** recebidos assumem as conexões de rede do SMA de destino, visando não entrar em conflito com as conexões do SMA de origem.

No exemplo abaixo é possível observar o acionamento do protocolo de clonagem:

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", cloning);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", cloning, "1");
```

2. Enviando um agente específico.

Neste método, somente um agente específico é clonado e enviado para outro SMA. 

No exemplo abaixo é possível observar o acionamento do protocolo de clonagem especificando um único agente para a clonagem.

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", cloning, agent1);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", cloning, agent1, "1");
```

OBS: O agente deve existir no SMA de origem para ser clonado e enviado. Ou seja, o SMA de origem deve conter um agente chamado "*agent1*" para poder enviá-lo para outro SMA.

OBS 2: Caso o agente enviado seja um agente **Hermes** com uma configuração de rede ativa, a configuração de rede será analizada e caso a configuração possua um identificador único que possa dar conflito caso essa conexão seja ativa no SMA de destino. O protocolo gera um novo identificador de maneira randomica para essa conexão de rede.
- Exemplo, o UUID de um agente em uma conexão **ContextNet**. Se um agente **Hermes** com uma conexão **ContextNet** for enviado, o SMA de destino irá gerar um novo UUID randomico para este agente para não gerar conflito com o agente do SMA de origem.


3. Enviando uma lista de agentes.

Neste método, um conjunto de agentes específicos são clonados e enviados para outro SMA.

No exemplo abaixo é possível observar o acionamento do protocolo de clonagem especificando um grupo de agentes para a clonagem.

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", cloning, [agent1, agent2]);
```

Caso queira especificar a conexão para execução do protocolo, no exemplo abaixo ilustra como especificar uma conexão com o nome de "*1*":

```
.moveOut("788b2b22-baa6-4c61-b1bb-01cff1f5f880", cloning, [agent1, agent2], "1");
```

As observações levantadas do método anterior também são validas para esse. 

## 4. Criogenia

A criogenia é um método de preservação de conhecimentos do SMA que permiter parar a execução do SMA mantendo/salvando o estado mental atual de todos os agentes do SMA e posteriormente reiniciar o SMA e o mesmo voltar a ser executado exatamente do mesmo ponto que foi parado/criogenado.

No *framework* ***Jason*** atualmente se precionar o botão do *Stop* para parar a execução do SMA, todo o conhecimento adquirido pelos agentes são perdidos e ao iniciar novamente o SMA, os agentes são iniciados a partir dos seus códigos fontes iniciais.

A criogenia possui duas formas de ser acionada:

1. Por meio da ação interna.

A ação interna é a **cryogenic** e segue o seguinte padrão: .<span style="color:green">*cryogenic*</span>. Sendo assim:
- O nome da ação interna é <span style="color:green">*cryogenic*</span>;
- E não possui nenhum parâmetro.

No exemplo abaixo é possível observar o acionamento da criogenia.

```
.cryogenic;
```

**ATENÇÃO**: Cuidado com utilizar a ação interna de criogenia em um plano de crença, pois se a crença permanecer na mente do agente após a execução da criogenia, quando reiniar o SMA a crença que ativa esse plano de crença ainda estará na mente do agente e o plano de crença será ativado novamente criogenando o SMA novamente formando um ciclo.

**Orientação**: Utilizar essa ação interna de criogenia em planos de intenção e como última ação do plano, pois com isso a intenção será satisfeita e minimizará a probabilidade de ocorrer um ciclo.

2. Manualmente.

A execução da criogenia é responsabilidade do agente **Hermes**, no inicio do ciclo de raciocinio desse agentes, na fase de **checkMail** o agente verifica se existe um arquivo chamado de ***".cryogenic"*** na raiz do SMA que está sendo executado. Se existir o arquivo, o SMA deve ser criogenado, senão existir, o SMA deve seguir sua execução normalmente.

Ou seja, a ação interna cria o arquivo ***".cryogenic"*** na raiz do SMA para que no inicio do próximo ciclo o agente identificar que a criogenia deve ser realizada.

Para acionar a criogenia manualmente é necessário somente criar um arquivo de texto vazio com o nome ***".cryogenic"*** na raiz do SMA que está em execução que a criogenia será executada.

**OBS:** O arquivo ***".cryogenic"*** é apagado no final da execução da criogenia automaticamente pelo protocolo para não resultar em um ciclo de execução da criogenia.

## 5. Auto Localização

Como os protocolos bioinspirados permitem a transferencia de agentes entre diferentes SMA. Os agentes necessitam de um mecanismo para se localizarem e identificar em qual SMA está inseridos.

Essa responsabilidade de identificar o SMA ao qual estão inseridos é da arquitetura **Hermes**.

Sendo assim, quando um agente **Hermes** é iniciado, o mesmo identifica o SMA a qual está inserido e informa aos demais agentes do SMA. 

OBS: Quando um agente **Hermes** é enviado para outro SMA, esse agente **Hermes** identifica o SMA que está sendo inserido, porém não informa aos demais agentes para evitar uma redundância. 

OBS 2: Quando um agente que seja de uma arquitetura diferente da **Hermes** é enviado para outro SMA, o agente **Hermes** responsável por receber esse novo agente atualiza esse agente de qual SMA está sendo inserido.