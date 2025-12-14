//Import modules
const express = require("express")
const bodyParser = require("body-parser")
const session = require("express-session")
const database = require("./database")

//Initialize express
const app = express()

//Apply body parser to the incoming requests
app.use(bodyParser.urlencoded({extended: false}))

// Parses the body of the request as JSON

app.use(express.json())



app.post("/inventory/add", (req, res) => {
    const { playerId, items } = req.body;
    if (!playerId || !items) return res.status(400).json({ error: "Missing playerId or items" });

    items.forEach(item => {
        connection.query(
            `INSERT INTO inventory (playerId, itemId, quantity) VALUES (?, ?, ?)
             ON DUPLICATE KEY UPDATE quantity = quantity + ?`,
            [playerId, item.itemId, item.quantity, item.quantity]
        );
    });

    res.json({ success: true });
});

app.get("/inventory/:playerId", (req, res) => {
    const playerId = req.params.playerId;
    connection.query(
        "SELECT itemId, quantity FROM inventory WHERE playerId = ?",
        [playerId],
        (err, results) => {
            if (err) return res.status(500).json({ error: err });
            res.json(results);
        }
    );
});

app.listen(4000, () => console.log("🙌 Server running on port 4000"));