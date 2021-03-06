import json
import os
import sys
import requests
import wget
import zipfile
from os.path import exists
from pathlib import Path
import os
import re
def main():
    search = input("Enter a name of a modpack: ")
    modpacklink = "https://addons-ecs.forgesvc.net/api/v2/addon/search?categoryId=0&gameId=432&index=1&pageSize=0&searchFilter=" + search + "".replace(
        " ", "%20")
    modreq = requests.get(modpacklink, headers={
        'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64; rv:92.0) Gecko/20100101 Firefox/92.0'})
    jsonsearch = json.loads(modreq.text)
    modpacklist = []
    count = 0
    for modpack in jsonsearch:
        if search.lower() in str(modpack['name']).lower():

            modpackdownloadlink = modpack['latestFiles'][len(modpack['latestFiles']) - 1]['downloadUrl']
            modpackname = str(modpack['name']).replace(" ", "")

            modpacklist.append(modpackname + "A:W:B[]" + modpackdownloadlink)

            count = count + 1
    #modpackname = str(jsonsearch[0]['name']).replace(" ", "")
    modpackname = ""
    if count > 1:
      liststring = ""
      a = 0
      for i in modpacklist:
          a = a + 1
          liststring = liststring + "\n" + "[" + str(a) + "]" + i.split("A:W:B[]")[0]
      select = input("\nMultiple modpacks found write the number of the modpack: " + liststring + "\n")
      tryInt = int(select)
      c = 0
      for modpack in modpacklist:
          c = c + 1
          if tryInt == c:
                modpackname = modpack.split("A:W:B[]")[0].replace(" ", "")
                modpackdownloadlink = modpack.split("A:W:B[]")[1]

    if count != 0 and count != 1:
      confirm = input("Modpack name: " + modpackname + " is that right? type yes/no and press enter and the download will start: ")
    else:
        confirm = 'no'
        print('No modpacks found!! Try using some parts of the modpack like Better Minecraft [FABRIC] - 1.17.1 use Better Minecraft if this is still happening make a issue on the github page with the modpack name')
    if modpackname == "":
        confirm = 'no'
        print('No modpacks found!! Try using some parts of the modpack like Better Minecraft [FABRIC] - 1.17.1 use Better Minecraft if this is still happening make a issue on the github page with the modpack name')
    if(confirm == 'yes'):
        if not exists("modpacklist.txt"):
            fp = open('modpacklist.txt', 'w')
        modpackname = re.sub(r"[^a-zA-Z0-9 ]", "", modpackname)
        file = open('modpacklist.txt', 'r+')
        file.write(file.read() + modpackname + "\n")
        #modpackdownloadlink = jsonsearch[0]['latestFiles'][len(jsonsearch[0]['latestFiles']) - 1]['downloadUrl']
        #print(jsonsearch[0]['latestFiles'][len(jsonsearch[0]['latestFiles']) - 1]['downloadUrl'])
        jsonManifest = None
        if (exists("modpack.zip")):
            os.remove("modpack.zip")
        if(exists(modpackname + "/")):
            print(modpackname + " Already exists")
            exit()
        if (exists("unzip")):
            os.system("rm -rf unzip")
        print(
            "This program uses automated installation of forge please go support forge at https://www.patreon.com/LexManos/")
        # link = link.replace('https://download.curseforge.com/?', "").replace("addonId=", "").replace("fileId=", "").split("&");
        #link = "https://addons-ecs.forgesvc.net/api/v2/addon/" + addonId + "/file/" + fileId
        #req = requests.get("https://addons-ecs.forgesvc.net/api/v2/addon/" + addonId + "/file/" + fileId,
                           #headers={'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64; rv:92.0) Gecko/20100101 Firefox/92.0'})
        #req = requests.get(modpacklink,
                           # headers={'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64; rv:92.0) Gecko/20100101 Firefox/92.0'})

        #tojson = json.loads(req.text)
        print("\nTask: Downloading " + modpackdownloadlink)#jsonsearch[0]['latestFiles'][len(jsonsearch[0]['latestFiles']) - 1]['fileName'])
        wget.download(modpackdownloadlink, "modpack.zip")

        os.mkdir("unzip")
        os.mkdir(modpackname + "/")
        os.mkdir(modpackname + "/.minecraft")
        os.mkdir(modpackname + "/.minecraft/mods")
        os.mkdir(modpackname + "/.minecraft/versions")
        with zipfile.ZipFile("modpack.zip", "r") as zip_ref:
            zip_ref.extractall("unzip")
        if (exists("unzip/overrides")):
            os.system("cp -nR unzip/overrides/* " + modpackname +  "/.minecraft")
        if (exists("unzip/manifest.json")):
            manifest = open(r"unzip/manifest.json", "r+")

            jsonmanifest = json.loads(manifest.read())

            for i in jsonmanifest["files"]:

                projectID = i["projectID"]
                fID = i["fileID"]
                modlink = "https://addons-ecs.forgesvc.net/api/v2/addon/" + str(projectID) + "/file/" + str(fID)

                modreq = requests.get(modlink, headers={
                    'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64; rv:92.0) Gecko/20100101 Firefox/92.0'})
                modjson = None
                if modreq is not None and modreq.text is not None and modreq.text != "":
                  try:
                    modjson = json.loads(modreq.text)
                  except:
                      print("\nError downloading a mod this can cause problems when loading the modpack!")
                if (modjson != None):

                    if (len(modjson) > 1):
                        if exists(modpackname + "/.minecraft/mods/" + modjson["fileName"]):
                            print("Info: Skipping " + modjson["fileName"] + " because file already exists")
                        else:
                            print("\nTask: Downloading " + modjson["fileName"])
                            wget.download(modjson["downloadUrl"], modpackname + "/.minecraft/mods")

        # https://maven.minecraftforge.net/net/minecraftforge/forge/1.16.5-36.2.4/forge-1.16.5-36.2.4-universal.jar

        else:
            print("Error: unzip/manifest.json doesnt exist (Could be because of invalid modpack or cannot unzip)")


        manifest2 = open(r"unzip/manifest.json", "r+")

        jsonmanifest2 = json.loads(manifest2.read())
        if jsonmanifest2 != None:
            forgeversion = str(jsonmanifest2['minecraft']).split(",")[0].replace("{", "").replace("'", "").replace(" ", "").replace(":", "").replace("version", "")
            forgeid = str(jsonmanifest2['minecraft']).split(",")[1].replace("{", "").replace("'", "").replace(" ", "").replace(":", "").replace("[", "").replace("forge-", "").replace("modLoaders", "").replace("id", "")#.replace("forge-", "")

        print("Downloading Forge " + forgeversion + " " + forgeid)
        #forgelink = "https://maven.minecraftforge.net/net/minecraftforge/forge/" + forgeversion + "-" + forgeid + "/forge-" + forgeversion + "-" + forgeid + "-universal.jar"
        #wget.download(forgelink, "modpack/.minecraft/versions")
        forgelinkinstaller = "https://maven.minecraftforge.net/net/minecraftforge/forge/" + forgeversion + "-" + forgeid + "/forge-" + forgeversion + "-" + forgeid + "-installer.jar"
        wget.download(forgelinkinstaller)
        if exists("forge-" + forgeversion + "-" + forgeid + "-installer.jar"):
            os.system("java -jar " + "forge-" + forgeversion + "-" + forgeid + "-installer.jar")
        print(str(os.getenv("HOME")) + "/.minecraft/versions/" + "forge-" + forgeversion + "-" + forgeid)
        if exists(str(os.getenv("HOME")) + "/.minecraft/versions/" + forgeversion + "-" + "forge-" + forgeid + "/"):
            os.mkdir(modpackname + "/.minecraft/versions/" +forgeversion + "-forge-" + forgeid + "/")
            os.system("cp -R " + str(os.getenv("HOME")) + "/.minecraft/versions/" + forgeversion + "-forge-" + forgeid + "/*" + " " + modpackname +  "/.minecraft/versions/" + forgeversion + "-forge-" + forgeid + "/")
            print("Copying forge")
        else:
            print("Error: " + str(os.getenv("HOME")) + "/.minecraft/versions/" + forgeversion + "-" + "forge-" + forgeid + " does not exist")
        if exists(str(os.getenv("HOME")) + "/.minecraft/libraries/"):
            os.mkdir(modpackname + "/.minecraft/libraries/")
            os.mkdir(modpackname + "/.minecraft/libraries/net/")
            os.mkdir(modpackname + "/.minecraft/libraries/net/minecraftforge/")
            os.system("cp -R " + str(os.getenv("HOME")) + "/.minecraft/libraries/* " + modpackname + "/.minecraft/libraries/")
            print("Copying forge done!")
        else:
            print("Error: " + str(os.getenv("HOME")) + "/.minecraft/libraries/net/minecraftforge/" + " does not exist")
        os.system(str("rm -rf " + os.getenv("HOME")) + "/.minecraft/versions/" + forgeversion + "-" + "forge-" + forgeid + "/")
        if (exists("modpack.zip")):
            os.remove("modpack.zip")

        if (exists("unzip")):
            os.system("rm -rf unzip")
        if(exists(str(os.getenv("HOME")) + "/.minecraft/launcher_accounts.json")):
            os.system("cp -R " + str(os.getenv("HOME")) + "/.minecraft/launcher_accounts.json" + " " + modpackname + "/.minecraft/")
        print("Launching minecraft launcher (Make sure u have minecraft-launcher installed)")
        os.system("minecraft-launcher --workDir " + str(Path().absolute()) + "/" + modpackname + "/.minecraft/")
        exit()
    else:
        print("Please specify the name more if u think this is an issue report it on the github issue page")

main()
