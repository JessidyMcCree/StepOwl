const mysql = require("mysql2");

const connection = mysql.createConnection({
    host:"ia8ezm.h.filess.io",
    user:"StepOwl_worryedge",
    password:"8b372de93641d9dbf4d33ec7fe66f7d3249e790c",
    port:61002,
    database:"StepOwl_worryedge"
});

connection.connect(err => {
    if (err) return console.error("Erro na conexão:", err);
    console.log("Conectou com sucesso!");
});

module.exports = connection;
