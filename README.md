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
