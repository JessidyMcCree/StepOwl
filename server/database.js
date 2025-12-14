const mysql = require("mysql2");

const connection = mysql.createConnection({
    host: process.env.DB_HOST || "ia8ezm.h.filess.io",
    user: process.env.DB_USER || "StepOwl_worryedge",
    password: process.env.DB_PASSWORD || "8b372de93641d9dbf4d33ec7fe66f7d3249e790c",
    port: process.env.DB_PORT || 61002,
    database: process.env.DB_NAME || "StepOwl_worryedge"
});

connection.connect(err => {
    if (err) {
        console.error("nao consigo a db:", err);
        return;
    }
    console.log("✅ Conectado ao MySQL remoto!");
});

module.exports = connection;
