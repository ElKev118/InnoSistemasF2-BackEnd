Para ejecutar en local con docker, ejecutar el siguiente script:
```
.\setup-monitoring.sh
```

- Prometheus: ```http://localhost:9090```
- Grafana: ```http://localhost:3000``` (usuario: admin, contraseña: admin)
- Aplicación Spring Boot: ```http://localhost:8080```
- Métricas de Spring Boot: ```http://localhost:8080/actuator/prometheus```

Para ejecutar el servicio alojado en la nube, el link para hacer las peticiones es:```https://innosistemasfeature-2.onrender.com```

Para acceder a la documentación de la API

- En local: ``` http://localhost:8080/swagger-ui/index.html#/```
- En la nube: ``` https://innosistemasfeature-2.onrender.com/swagger-ui/index.html#/ ```
