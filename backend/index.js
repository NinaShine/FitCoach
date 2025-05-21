const express = require("express");
const axios = require("axios");
require("dotenv").config();

const app = express();

app.get("/getSpotifyToken", async (req, res) => {
  const auth = Buffer.from(
    `${process.env.SPOTIFY_CLIENT_ID}:${process.env.SPOTIFY_CLIENT_SECRET}`
  ).toString("base64");

  try {
    const response = await axios.post(
      "https://accounts.spotify.com/api/token",
      new URLSearchParams({ grant_type: "client_credentials" }),
      {
        headers: {
          Authorization: `Basic ${auth}`,
          "Content-Type": "application/x-www-form-urlencoded",
        },
      }
    );

    res.json(response.data);
  } catch (err) {
    console.error("Erreur Spotify", err.response.data);
    res.status(500).json({ error: "Spotify token error" });
  }
});

module.exports = app;
