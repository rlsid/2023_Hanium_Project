import numpy as np
import imutils
import cv2
from matplotlib import pyplot as plt
from sklearn.cluster import KMeans
from PIL import Image
from rembg import remove
from PIL import Image
import time
import socket

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(("192.168.35.73", 8081))
server_socket.listen()
print("듣는중")
client_sock, addr = server_socket.accept()
if addr:
    server_socket.close()
    print(addr)
    print("서버가 닫혔습니다")