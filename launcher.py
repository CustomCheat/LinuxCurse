import os
from pathlib import Path
from os.path import exists

file = open('modpacklist.txt', 'r')
read = file.read().split("\n")
if len(read) < 1 or read[0] == "" or read[0] == " " or read[0] == "\n":
    print("None modpacks installed install modpacks with main.py")
    exit()

print("Select Modpack: ")
count = 0
for modpack in read:
    if exists(modpack + "/") and modpack != "":
        count = count + 1
        print("[" + str(count) + "]" + " " + modpack)
    else:
        print("Invalid modpack: " + modpack)
if count == 0:
    print("No modpacks found")
    exit()
select = input("")
if int(select) - 1 < count and int(select) > 0:
    modpackname = read[int(select) - 1]
    os.system("minecraft-launcher --workDir " + str(Path().absolute()) + "/" + modpackname + "/.minecraft/")
else:
    print("Error: Invalid choice")




