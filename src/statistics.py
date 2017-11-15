import os
import re
import sys
import glob
import shutil
import subprocess


#### TODO

# - z zadanego folderu czytać N plików txt ze statystykami
# - podliczać te statystyki i tworzyć mapę nazwafolderu -> statystyka


def calculateandshow(d, name):
  print('='*20+'\nStatistic ' + name+'\n'+20*'='+'\n')
  print ('Stats per combination:')
  for k,v in d.items():
    avg =  0 if len(v)==0 else sum(v) / len(v)
    print ('{} - {} entries, average: {}'.format(k, len(v), avg))
  print ()  
  print ('Stats per algorithm variant:')

  for ver in ['evolver', 'picker', 'independent']:
    data = []
    for k,v in d.items():
      if ver not in k: continue
      data.extend(v)
    avg =  0 if len(data)==0 else sum(data) / len(data)
    print ('{} - {} entries, average: {}'.format(ver, len(data), avg))

  print()


#print ( glob.glob('**/*.txt', recursive=True))
dirs = [dir.strip('\\') for dir in glob.glob("*/")]
astats = {}
bstats = {}
for d in dirs:
  astats[d]=[]
  bstats[d]=[]
  
print('='*20+'\nFittestFiles\n'+20*'='+'\n')
for d in dirs:
  bestm = 0.0
  bestfile = ''
  files = glob.glob(d+'/*.txt')
  #todo check quantity
  num = 0
  for f in files:
    num +=1
    lines = open(f).readlines()
    #print (lines)
    metrics = list(map(float,lines[0].split()))
    if len(metrics):
      if sum(metrics)/len(metrics) > bestm:
        bestm = sum(metrics)/len(metrics)
        bestfile = f
    if num <= 10:
      astats[d].extend(metrics)
      bstats[d].extend(map(float,lines[1].split()))
  print ('{} -> {} ({})'.format(d, bestfile, bestm))
   
    #print (astats)
    #print (bstats)
    #sys.exit()


    
print() 
calculateandshow(astats, 'a)')
calculateandshow(bstats, 'b)')
sys.exit()
