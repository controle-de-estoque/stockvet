const express = require('express');
const path = require('path');
const app = express();

// Serve the static files from the Angular build directory
// Note: Check your local 'dist' folder. If your files are in 'dist/frontend/browser', update the path below accordingly.
app.use(express.static(__dirname + '/dist/frontend'));

// Route all traffic to the index.html file so Angular can handle routing
app.get('/*', function(req,res) {
    res.sendFile(path.join(__dirname+'/dist/frontend/index.html'));
});

// Start the app by listening on the default Heroku port
app.listen(process.env.PORT || 8080);
