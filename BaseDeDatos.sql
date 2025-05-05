create database burbuja;
use burbuja;

create table cambios_burbuja(
id_cambio			int auto_increment not null,
caracter			VARCHAR(60),
caracter_acomo		VARCHAR(60), 
primary key(id_cambio)
);

select * from cambios_burbuja;
