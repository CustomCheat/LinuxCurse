import os
from pathlib import Path
from os.path import exists

file = open('modpacklist.txt', 'r')
read = file.read().split("\n")
print("Select Modpack: ")
count = 0
for modpack in read:
    if exists(modpack + "/") and modpack != "":
        count = count + 1
        print("[" + str(count) + "]" + " " + modpack)
select = input("")
if int(select) - 1 < count:
    modpackname = read[int(select) - 1]
    os.system("minecraft-launcher --workDir " + str(Path().absolute()) + "/" + modpackname + "/.minecraft/")
else:
    print("Error: Invalid choice")


