# Android-LED-Controller\

### Final Project Implementation
### CSCI 4211
### Scott Clay and Devin Piner


Activities: 
 - Main activity
 - Favorites activity
 - Network test activity

Favorites and presets are stored on local sqlite database.


To see effects of running android application, demo can be found at:

[dpiner.com/mobile/index.html](http://www.dpiner.com/mobile.index.html)

Demo page represents LED lights that also use the the custom API to
update color, brightness, and on/off state.


**Extra info:**

LED light strips connected to an arduino and raspberry pi server were
used as "smart lights" for this project. The status is saved on an
online remote mysql database (we have hosted ourselves). The database is
accessed using a custom API we wrote with endpoints for retrieving and
saving the full status, brightness only, and status only. The demo is
hosted on the same server as the database, and is updated by checking for 
changes every 10 seconds.
