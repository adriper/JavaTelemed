# JavaTelemed

## Requisitos:
* Java 1.8
* Postgresql 10 
* Maven 3.2 ou superior (compilação)

## Banco de dados:
Execute os scripts que estão no diretório "scripts"

## Antes de executar
### Mensagens.properties 
* Edite a propriedade br.ufsm.cpd.javatelemed.videoconferencia.url para apontar para o seu provedor de videoconferências (sugerimos a utilização do Meet Jitsi.org)
### application.properties 
* Edite as propriedades de conexão com o banco de dados (spring.datasource.url, spring.datasource.username, spring.datasource.password)
* Edite as propriedades para envio de email (smtp) - spring.mail.host, spring.mail.port, spring.mail.username, spring.mail.password
* Edite as prorpeidades referentes aos assuntos de envio de emails (iniciadas por br.ufsm.cpd.javatelemed.email)
* Indique a porta do servidor e o context-path nas respectivas propriedades (server.port, server.servlet.context-path)

### Setup.java
* Indique os dados do administrador do sistema - email, nome, sobrenome, cpf e senha. Esse arquivo pode ser apagado após a primeira execuço do sistema
* Será criado um estado inicial para o atendimento.

### CriptoConverter.java
* Neste arquivo, indique os valores para a criptografia dos dados no banco de dados.

## Compilação
Para compilação, utilize o Maven 3.2 ou superior.
* $ mvn clean install

## Execução
Para execução, é possível utilizar os comandos: 
* $ ./mvnw spring-boot:run
* $ java -jar target/javatelemed-1.0.jar

## Configuração
### Cidades de atendimento
* A primeira configuração a ser feita é em relação à(s) cidade(s) sede(s) de atendimento. Para tanto, busque a cidade na interface administrativa "Administração -> Cidades", vá em "Editar" a cidade, e indique que ela possui atendimento.
* Uma cidade-sede pode atender um conjunto de outras cidades. Um profissional vinculado a uma cidade-sede poderá atender os pacientes de qualquer cidade relacionada com essa sede

### Estados de atendimento
* Os estados de atendimento podem ser usados para configurar o "fluxo" do atendimento. Cada estado de atendimento pode ter uma ou mais propriedades

### Tipos de profissional
* É preciso cadastrar os tipos de profissionais do sistema (médicos, estudantes de internato de medicina, psicólogos, etc). Isso é feito na interface "Tipo de Profissional"
* O tipo de profissional pode acessar atendimentos que estejam em determinado estado. Assim, alguns tipos de profissional podem, por exemplo, acessar o início do atendimento, e outros o final. 
* Para o cadastro do tipos de profissionais "estudantes", *não* se deve indicar o conselho de classe. 

### Cadastro de profissional
* Um profissional deve ser cadastrado e deve ter um tipo (médico, estudante de medicina, psicólogo, etc). 
* Os usuários do sistema são os próprios profissionais. Seu nome de usuário é o seu e-mail.
* Um profissional (usuário do sistema) pode ter três tipos de perfil de acesso (papel): usuário, gestor de profissionais e administrador.
* Para acessar um atendimento, o profissional precisa estar vinculado à cidade-sede daquele atendimento; seu tipo de profissional precisa ter acesso liberado ao estado em que o atendimento se encontra; e, caso seja um estudante (tipo de profissional sem conselho de classe), deve estar vinculado a um profissional preceptor.
