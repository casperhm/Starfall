# Starfall

I recommend using the Square font

Map files must be rectanglular.

The first line of a map file is the message that will print when in that map, or that fight.

The game does not support tall terminals. The terminal is HIGHLY RECCOMENDED to be no more that 2/3 the length in verticality, weird stuff happens if you ignore this.

chestData files keep a log of all the chests that have been opened in a room. chestData_-1,-1 is the main map, and so on. Each line will be the coordinates of one empty chest.


SAVE FILE SETUP

This shows how the save files work

playerX
playerY
health
maxHealth
coins
XP
enterX - these will be -1 if on the main map, and the two parts of the room map file name if in a room
enterY


CONFIG FILE SETUP

This just shows how to set up the config file - basicly the default save.

HEALTH
MAXHEALTH
CAPHEALTH
COINS
PLAYERX
PLAYERY
XP
