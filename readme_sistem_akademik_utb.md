# Sistem Akademik

## Deskripsi

Aplikasi Sistem Informasi Akademik berbasis Java Swing + MySQL menggunakan NetBeans 8.2 untuk mengelola data mahasiswa, mata kuliah, dosen, KRS, nilai, dan user login.

## Teknologi

- Java SE
- Java Swing
- MySQL
- JDBC Connector
- NetBeans 8.2
- XAMPP

## Struktur Project

```text
SistemAkademik/
├── src/akademik/
│   ├── DBConnection.java
│   ├── LoginFrame.java
│   ├── MainFrame.java
│   ├── FormMahasiswa.java
│   ├── FormOthers.java
│   └── FormSoalUTS.java
└── akademik_db.sql
```

## Fitur

- Login admin/user
- CRUD Mahasiswa
- CRUD Mata Kuliah
- CRUD Dosen
- CRUD User
- Input KRS
- Input Nilai
- Form Hitung Nilai
- Change Password
- Logout

## Setup Database

1. Jalankan XAMPP dan start MySQL.
2. Buka phpMyAdmin
3. Import file `akademik_db.sql`.

## Konfigurasi DBConnection

- Host: localhost
- Port: 3306
- Database: akademik\_db
- User: root
- Password: kosong

## Cara Menjalankan

1. Buka project di NetBeans.
2. Tambahkan mysql-connector-java.jar ke Libraries.
3. Run `LoginFrame.java`.

## Login Default

- admin / admin123
- user1 / user123

