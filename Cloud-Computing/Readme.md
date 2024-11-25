## API Documentation

**BASE URL** http://localhost:9000

#

## Register Endpoint:

```text
/api/auth/register
```

Request Body (JSON):

```json
{
  "username": "User 1",
  "email": "user@gmail.com",
  "password": "admin123"
}
```

Success Response:

```json
{
  "status": true,
  "message": "Registered Successfully"
}
```

Error Response

```json
{
  "status": false,
  "message": "Email is already registered"
}
```

```json
{
  "status": false,
  "message": "Password min 8 characters"
}
```

#

## Login Endpoint:

```text
/api/auth/login
```

Request Body (JSON):

```json
{
  "email": "user@gmail.com",
  "password": "admin123"
}
```

Success Response:

```json
{
  "status": true,
  "message": "Login Successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjQsImVtYWlsIjoibXVraW1haGZ1ZGFAZ21haWwuY29tIiwiaWF0IjoxNzMyNDIyMjEyLCJleHAiOjE3MzI1OTUwMTJ9.jm6Wfi6VUtfpSIEnaITB2cJ83KAmeSMgzafxKEh6w"
}
```

Error Response

```json
{
  "status": false,
  "message": "Email is not registered"
}
```

```json
{
  "status": false,
  "message": "Password invalid"
}
```

#

## Logout Endpoint:

```text
api/auth/logout
```

Headers:

```
Authorization: Bearer {token}
```

Success Response:

```json
{
  "status": true,
  "message": "Logout successful"
}
```

Error Response

```json
{
  "status": false,
  "message": "Token invalid"
}
```

