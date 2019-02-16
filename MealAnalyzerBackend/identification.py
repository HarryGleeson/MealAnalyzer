import cv2
from darkflow.net.build import TFNet

''' identification.py:
This class uses the trained classifier to identify objects in the image passed to it
using YOLO object detection.
'''

def identify(img):

    options = {
        'model': 'cfg/tiny-yolo-voc-3c.cfg',
        'load': 6500,
        'threshold': 0.32,
        'gpu': 1.0
    }

    tfnet = TFNet(options)
    # use YOLO to predict the image
    result = tfnet.return_predict(img)
    img.shape

    return result