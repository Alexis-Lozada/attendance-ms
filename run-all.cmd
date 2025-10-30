@echo off
echo === Iniciando todos los microservicios de Roster ===

start cmd /k "cd /d eureka-server && mvn spring-boot:run"
start cmd /k "cd /d api-gateway && mvn spring-boot:run"
start cmd /k "cd /d users-ms && mvn spring-boot:run"
start cmd /k "cd /d attendance-ms && mvn spring-boot:run"
start cmd /k "cd /d academic-ms && mvn spring-boot:run"
start cmd /k "cd /d auth-server && mvn spring-boot:run"
start cmd /k "cd /d chat-ms && mvn spring-boot:run"
start cmd /k "cd /d storage-ms && mvn spring-boot:run"

echo === Todos los microservicios se están iniciando ===
pause
