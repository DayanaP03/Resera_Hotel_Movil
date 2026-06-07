

# 🏨 Sistema de Gestión de Reservas de Hotel

**API (Django REST) + Aplicación Móvil (Android/Kotlin)**

---
<p align="center">
  <img src="https://ute.edu.ec/wp-content/uploads/2021/08/LogoUteTrans.png" width="300">
</p>

## 📌 Descripción del Proyecto

Sistema integral para la gestión automatizada de un hotel, permitiendo el control eficiente de clientes, habitaciones, reservas y procesos de facturación/pago.

### 🛠 Tecnologías Utilizadas

* **Backend:** Django REST Framework.
* **Autenticación:** JWT (SimpleJWT).
* **App Móvil:** Android (Kotlin + Patrón MVVM).
* **Base de Datos:** PostgreSQL / SQLite.
* **Despliegue:** VPS con Gunicorn + Nginx.

---

## 🌐 Enlaces del Sistema

* **API Principal:** [Ver API](https://pisco-hotel.uaeftt-ute.site/api/)
* **Panel de Administración:** [Django Admin](https://pisco-hotel.uaeftt-ute.site/admin)


> **Usuario de Prueba:**
> * **Email:** `cliente@gmail.com`
> * **Password:** `123456`
> 
> **Contraseña del Backend**
> **admin123@gmail.com**
> **admin**


---

## 🧩 Entidades Principales

El sistema se estructura bajo los siguientes modelos:

* `Usuario` • `Cliente` • `Habitación` • `Reserva` • `Servicio` • `Factura` • `Pago`

---

## 📲 Estructura de la App Android

La aplicación móvil cuenta con las siguientes funcionalidades:

* **Autenticación:** Splash, Login y Registro.
* **Gestión:** Home, Listado de habitaciones y Detalle.
* **Reservas:** Crear nueva, Historial (Mis reservas) y Gestión de estados.
* **Finanzas:** Facturas y Perfil de usuario.

---

## 🔌 Referencia de la API

Todas las peticiones protegidas requieren el token JWT en el header: `Authorization: Bearer <token>`.

### Autenticación

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/auth/login/` | Iniciar sesión |
| `POST` | `/auth/registro/` | Registrar usuario |
| `GET` | `/auth/perfil/` | Obtener datos usuario |

### Gestión de Datos

* **Habitaciones:** `GET /habitaciones/` y `GET /habitaciones/{id}/`
* **Reservas:** `GET`, `POST`, `DELETE` en `/reservas/`
* **Pagos/Facturas:** `GET` y `POST` en `/pagos/` y `/facturas/`

### Ejemplo de Login (Request)

```json
POST /api/auth/login/
{
  "email": "cliente@gmail.com",
  "password": "123456"
}

```

---

## 🚀 Guía de Instalación (Backend)

1. **Clonar el repositorio:**
```bash
git clone <tu-url-del-repositorio>
cd Reserva_Hotel_Backend

```


2. **Entorno Virtual:**
```bash
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate
pip install -r requirements.txt

```


3. **Migraciones:**
```bash
python manage.py makemigrations
python manage.py migrate

```


4. **Ejecución:**
```bash
python manage.py runserver

```



---

## 🏗 Arquitectura y Despliegue

* **Arquitectura:** API REST desacoplada que permite la comunicación fluida con la App móvil mediante arquitectura MVVM.
* **Producción (VPS):**
* Reiniciar Gunicorn: `sudo systemctl restart gunicorn`
* Reiniciar Nginx: `sudo systemctl restart nginx`



---

*Desarrollado con ❤️ para la gestión hotelera.*

---

