# UNSW-learning
## comp6733-Internet-of-Things-Design-Studio
*lab 1-6

compile the source code

```
make TARGET=srf06-cc26xx BOARD=sensortag/cc2650
```

flash the chips
```
./uniflash_3.4/eclipse/uniflash 
```

load the hex files to device

```
make prog
```

*start rph-board-router at specific serial port
```
sudo ~/contiki-git/tools/tunslip6 -B 115200 -s /dev/ttyACM<2>  aaaa::1/64
```


*group project

