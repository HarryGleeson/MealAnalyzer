from quantification import quantify
from identification import identify
from get_nutritional_info import nutrients
import cv2
import argparse

''' main.py:
This class is used to run the back end classes: identification, quantification and get nutritional information
Values are returned to the server by being printed. All identified foods followed by the total fat and the number 
of foods identified are printed. The number of foods identified is used to return the foods to the Android application.
'''

ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required = True, help = "Path to the image")
args = vars(ap.parse_args())

img = cv2.imread(args["image"])

fatPerGram = []
mass = []
   
result = identify(img)
if not result: #If there are no foods identified
    print("No Foods were identified in this image")
    print(0)
    print(0)
  
mass = quantify(result, img)
for m in mass: #Each foods corresponding fat per gram is added to the list
    fatPerGram.append(nutrients(m[0]['label']))
i=0
fat = 0
for f in fatPerGram:
    print(mass[i][0]["label"],": Mass =",round(mass[i][1],2),"g")
    fat = fat+f*mass[i][1]
    i=i+1
print(fat)
print(len(mass)) 