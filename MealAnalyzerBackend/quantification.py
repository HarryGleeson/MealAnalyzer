# import the necessary packages
from scipy.spatial import distance as dist
from imutils import perspective
from imutils import contours
import numpy as np
import argparse
import imutils
import cv2
import math

''' quantification.py:

** The code from lines 23-97 was inspired by Adrian Rosebrock's 'Measuring size of objects in an image with OpenCV',
** available at https://www.pyimagesearch.com/2016/03/28/measuring-size-of-objects-in-an-image-with-opencv/

This class determines the number of pixels in each dimension of each object in an image. The pixels
are converted into a real world measurement using the size of the reference object. The 2D area of
each object is calculated. Then the minimum distance between top-left and bottom-right points of each
identification bounding box and each quantification bounding box are found to determine which food is 
present in which quantification bounding box. If this bounding box does not represent the reference object 
or a bounding box which has already been assigned to another food, it is appended to the list to be returned.
'''
 
def midpoint(ptA, ptB):
    return ((ptA[0] + ptB[0]) * 0.5, (ptA[1] + ptB[1]) * 0.5)
 
def quantify(result, image): 
    # load the image, convert it to grayscale, and blur it slightly
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (7, 7), 0)
    width = 0.91535433071
    dimensions = [] 
    assigned = []
    mins = []
    weight = {"bacon":1.60323441212, "chicken dipper":4.49154031405, "fish finger":7.21068589273}

    # perform edge detection, then perform a dilation + erosion to
    # close gaps in between object edges
    edged = cv2.Canny(gray, 50, 100)
    edged = cv2.dilate(edged, None, iterations=1)
    edged = cv2.erode(edged, None, iterations=1)
 
    # find contours in the edge map
    cnts = cv2.findContours(edged.copy(), cv2.RETR_EXTERNAL,
        cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if imutils.is_cv2() else cnts[1]
 
    # sort the contours from left-to-right and initialize the
    # 'pixels per metric' calibration variable
    (cnts, _) = contours.sort_contours(cnts)
    pixelsPerMetric = None
    # loop over the contours individually
    for c in cnts:
        # if the contour is not sufficiently large, ignore it
        if cv2.contourArea(c) < 50:
            continue
 
        # compute the rotated bounding box of the contour
        orig = image.copy()
        box = cv2.minAreaRect(c)
        box = cv2.cv.BoxPoints(box) if imutils.is_cv2() else cv2.boxPoints(box)
        box = np.array(box, dtype="int")
 
        # order the points in the contour such that they appear
        # in top-left, top-right, bottom-right, and bottom-left
        # order, then draw the outline of the rotated bounding
        # box
        box = perspective.order_points(box)
        cv2.drawContours(orig, [box.astype("int")], -1, (0, 255, 0), 2)
 
        # loop over the original points and draw them
        for (x, y) in box:
            cv2.circle(orig, (int(x), int(y)), 5, (0, 0, 255), -1)
    # unpack the ordered bounding box, then compute the midpoint
        # between the top-left and top-right coordinates, followed by
        # the midpoint between bottom-left and bottom-right coordinates
        (tl, tr, br, bl) = box
        (tltrX, tltrY) = midpoint(tl, tr)
        (blbrX, blbrY) = midpoint(bl, br)
 
        # compute the midpoint between the top-left and top-right points,
        # followed by the midpoint between the top-righ and bottom-right
        (tlblX, tlblY) = midpoint(tl, bl)
        (trbrX, trbrY) = midpoint(tr, br)
 
    # compute the Euclidean distance between the midpoints
        dA = dist.euclidean((tltrX, tltrY), (blbrX, blbrY))
        dB = dist.euclidean((tlblX, tlblY), (trbrX, trbrY))
 
        # if the pixels per metric has not been initialized, then
        # compute it as the ratio of pixels to supplied metric
        # (in this case, inches)
        if pixelsPerMetric is None:
            pixelsPerMetric = dB / width               
        # compute the size of the object
        dimA = dA / pixelsPerMetric
        dimB = dB / pixelsPerMetric
 
        # draw the object sizes on the image
        area = dimA*dimB
        x = [tl, br, area]
        dimensions.append(x)   
     
     #Calculates minimum distance between corners of quantification and identification box
     #to determine which food corresponds to which identification box   
    for r in result:
        i=0
        minDistance = 1000
        minTracker = 1000
        for d in dimensions:
            distance = math.sqrt(math.pow(r["topleft"]['x']-d[0][0], 2)+math.pow(r["topleft"]['y']-d[0][1], 2)) + math.sqrt(math.pow(r["bottomright"]['x']-d[1][0], 2)+math.pow(r["bottomright"]['y']-d[1][1], 2))        
            if distance<minDistance:
                minDistance = distance
                minTracker = i
            
            i=i+1
        mins.append(minTracker)    
        if not(minTracker==0): #Ensures that the reference object is not used as a food
            if not(mins.count(minTracker)>1): #Ensures that the same object is not identified twice
                mass = dimensions[minTracker][2]*weight[r["label"]]            
                y=[r, mass]
                assigned.append(y)  
    
    return assigned