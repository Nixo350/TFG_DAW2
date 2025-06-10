PASOS PARA DESPLIEGE EN RAILWAY

1. PREPARACION DEL CODIGO
	1.1 SPRING BOOT
		-CAMBIAR VARIABLES DE CONEXION BBDD A VARIABLES DE ENTORNO 
			spring.datasource.url=${MYSQL_URL}
			spring.datasource.username=${MYSQL_USERNAME}
			spring.datasource.password=${MYSQL_PASSWORD}
		-GENERAR .jar con "mvn clean install"
	1.2 ANGULAR
		-CREAR CARPETA ENVIROMENTS y ARCHIVO ENVIROMENT.PROD.TS
			export const environment = {
  			production: true,
  			apiUrl: 'URL_TEMPORAL_DEL_BACKEND/api'
			};
		-CAMBIAR EN SERVICIOS A ENVIROMENT.apiUrl