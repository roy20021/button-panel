# Description

As a joke between colleagues at work, the button panel idea has built up.
Each company has its specific tedious tasks. Sometimes suchs task could be automatised.

Button panel is, obliviously, a panel on which lays several switches and buttons with the aim of triggering certain tedious automatisable activities.

The architecture of the overall solution enables the panel to be re-used in order to write ad-hoc software according to the company and changes.
All the ad-hoc, custom code for the specific task is triggerable by a STOMP endpoint (via WebSocket).
As an example, a frontend reproducing the panel is available [here](https://github.com/roy20021/button-panel/tree/master/button-panel-frontend)

Sketch:
![Architecture Sketch](https://github.com/roy20021/button-panel/raw/master/images/Sketch.png)

## Button panel :smile:

Front:
![Button Panel Front](https://github.com/roy20021/button-panel/raw/master/images/Front.jpg)
Back:
![Button Panel Back](https://github.com/roy20021/button-panel/raw/master/images/Back.jpg)
