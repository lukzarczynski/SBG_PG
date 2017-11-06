import os
import re
import sys
import glob
import shutil
import subprocess


# 1. add 'out' directory to /src
if not os.path.exists("out"):  os.mkdir("out"); print('INFO: "out" directory created')
# 2. copy 'resources to \src
if not os.path.exists("resources"): shutil.copytree('../resources', 'resources'); print('INFO: "resource" directory copied')

#3. prepare list of all java files to recompile
with open('_sources.txt', 'w') as sourcelist:
            sourcelist.write('\n'.join([file for file in glob.glob('**/*.java', recursive=True)]))

if 0 != subprocess.call(["javac", "@_sources.txt"]): print('ERROR: Java compilation error'); sys.exit(1)
#if 0 != subprocess.call(["java", "com.lukzar.Main"]): print('ERROR: Java runtime error'); sys.exit(1)

print('Done.')

#What to do:

#1. run this script on my machine
#2. copy everything to remote
#3. nohup java com.lukzar.Main B > B.out &
