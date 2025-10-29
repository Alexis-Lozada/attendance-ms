@echo off
echo === Iniciando todos los microservicios de Roster ===

start cmd /k "cd /d D:\Projects\Roster\attendance-ms\eureka-server && mvn spring-boot:run"
start cmd /k "cd /d D:\Projects\Roster\attendance-ms\api-gateway && mvn spring-boot:run"
start cmd /k "cd /d D:\Projects\Roster\attendance-ms\users-ms && mvn spring-boot:run"
start cmd /k "cd /d D:\Projects\Roster\attendance-ms\attendance-ms && mvn spring-boot:run"
start cmd /k "cd /d D:\Projects\Roster\attendance-ms\academic-ms && mvn spring-boot:run"
start cmd /k "cd /d D:\Projects\Roster\attendance-ms\auth-server && mvn spring-boot:run"
start cmd /k "cd /d D:\Projects\Roster\attendance-ms\chat-ms && mvn spring-boot:run"
start cmd /k "cd /d D:\Projects\Roster\attendance-ms\storage-ms && mvn spring-boot:run"

echo === Todos los microservicios se están iniciando ===
pause
