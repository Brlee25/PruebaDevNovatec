# PruebaDevNovatec

PruebaDevNovatec es una api de tipo Rest para la compañia Bank Inc que permite asignar a sus clientes una tarjeta débito o crédito para realizar compras en los comercios asociados. En la actualidad se soportan las siguientes funcionalidades:


	- Manejo de mensajeria tipo JSON para la comunicación con los clientes externos.
	- Generación de tarjetas.
	- Activación de tarjetas.
	- Recarga de saldo.
	- Consulta de saldo.
	- Bloqueo de tarjeta.
	- Transacción de Compra.
	- Consulta de Transacción.
	- Anulación de Compra.
	
## Transacciones financieras

PruebaDevNovatec procesa la información recibida por el endpoint consumido usando mensajeria en formato JSON, realiza validaciones de datos y posteriormente procede a insertar en base de datos la información.

## Flujo Normal
	1. Se recibe la petición.
	2. Se realizan las validaciones necesarias según el endpoint.
	3. Se procesa la petición.
	4. Se actualiza información en base de datos.
	5. Se envía una respuesta. 
	
## Flujo de excepción
	- Paso 1: En caso de recibirse un request con formato invalido se responderá con HttpStatus 400 - BadRequest.
	- Paso 2: En caso de recibirse campos con formatos invalidos se responderá con HttpStatus 400 - BadRequest.
	- Paso 3: En caso de recibirse información no encontrada en base de datos se responderá con HttpStatus 404 - NotFound.
	
##Base de Datos

Actualmente este componente se conecta a un BD en memoria con el nombre <code>bank_inc_db</code> para:
	
	- Almacenar nuevas tarjetas emitidas.
	- Consultar Tarjetas previamente emitidas.
	- Consultar y actualizar estado de tarjetas emitidas.
	- Activar tarjetas emitidas.
	- Cargar y consultar saldos en tarjetas emitidas.
	- Insertar, consultar y anular transacciones de compras.
	
	
## Instalación

El proceso de instalación de PruebaDevNovatec se realiza en Docker, por lo que es necesario en un server que cuente con este servicio realizar los siguientes pasos:


	1. Clonar el respositorio: https://github.com/Brlee25/PruebaDevNovatec.git
		git clone [-b main] https://github.com/Brlee25/PruebaDevNovatec.git
	2. Generar la imagen, (Se debe estar ubicado sobre la raiz del proyecto previamente clonado)
		docker build -t <urlbase>/bank-inc/prueba-dev-novatec:pruebadev-0.0.1-SNAPSHOT.B1 .
	3. Crear un servicio
		docker service create --name prueba-dev-novatec
	4. Asociar la imagen al servicio
		docker service update --image <urlbase>/bank-inc/prueba-dev-novatec:pruebadev-0.0.1-SNAPSHOT.B1 prueba-dev-novatec
	5. Crear replicas
		docker servce scale prueba-dev-novatec=1
	
	
	

