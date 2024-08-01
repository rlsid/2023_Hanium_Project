import sys
sys.path.append('/home/ubuntu/./local/lib/python3.10/site-packages')
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
import re

host = ''
port = 5800
result = 200
############################## Socket Connection Code
def socket_connection(host, port):

    while True:
        server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_sock.bind((host, port))
        server_sock.listen()
        print("기다리는 중")
        client_sock, addr = server_sock.accept()

        inputBytes = bytearray(client_sock.recv(1024))
        print(inputBytes)
        len_bytes_string = inputBytes[2:]
        print(len_bytes_string)
        len_bytes = len_bytes_string.decode("utf-8")
        print("2", len_bytes)
        length = int(len_bytes)
        img_bytes = get_bytes_stream(client_sock, length)
        img_path = "./image/melon.jpg"
        
        with open(img_path, "wb") as writer:
            writer.write(img_bytes)
        print(img_path+" is saved")

        result = ripening_function()
        send_result(result, client_sock)

        client_sock.close()
        server_sock.close()
    
    
def get_bytes_stream(sock, length):
    buffer = b''
    try:
        remain = length
        cnt = 1
        while True:
            data = sock.recv(remain)
            buffer += data
            if len(buffer) == length:
                break
            elif len(buffer) < length:
                remain = length - len(buffer)
            # if cnt == 1:
            #     print(buffer)
            #     cnt += 1
    except Exception as e:
        print(e)
    return buffer[ :length]

def send_result(result, sock):
    sock.sendall((result).to_bytes(4, byteorder="big"))
    print("결과 전달 완료")


############## Ripening_functions
def centroid_histogram(clt):
    numLabels = np.arange(0, len(np.unique(clt.labels_)) + 1)
    (hist, _) = np.histogram(clt.labels_, bins=numLabels)
    
    #합이 1이 되도록 히스토그램 정규화
    hist = hist.astype("float")
    hist /= hist.sum()
    print("중심색 비율:", hist)
    hist *= 100

    return hist

def checkRGB(hist, centroids):
    max = 0
    max_2 = 0
    for percent in hist:
        if percent > max:
            max = percent
        for percent in hist:
            if (max > percent) & (percent > max_2):
                max_2 = percent
    
    rgb_colors = []
    print("중심색 RGB값:")
    for(percent, color) in zip(hist, centroids):
        print(color.astype("uint8").tolist())
        if((percent == max) | (percent == max_2)):
            rgb_colors.append(color.astype("uint8").tolist())
    
    print("주요 2가지 색 RGB:", rgb_colors)
    return rgb_colors

def rgb_to_hsv(rgb_colors):
    rgb_colors = np.array(rgb_colors)
    rgb_colors = rgb_colors[ : , :3]
    rgb_colors = rgb_colors / 255.0

    hsv_colors = []

    for rgb in rgb_colors:
        r =  rgb[0]
        g =  rgb[1]
        b =  rgb[2]

        h = 0
        s = 0
        v = max(rgb)


        if(v == 0):
            s = 0
            h = 0
        else:
            min_rgb = min(rgb)

            s = (1 - (min_rgb / v)) * 100

            if v == r:
                h = 60 * (g - b) / (v - min_rgb)
            elif v == g:
                h = 120 + (60 * (b - r)) / (v - min_rgb)
            elif v == b:
                h = 240 + (60 * (r - g)) / (v - min_rgb)
            if h < 0:
                h += 360
    
        v *= 100
        hsv = [int(h), int(s), int(v)]
        hsv_colors.append(hsv)

    print("주요 2가지 색 hsv값:", hsv_colors)
    return hsv_colors

def redefiningColors(hsv_colors):
    redefined_color = []
    
    for color in hsv_colors:
        if color[0] < 50:
            redefined_color.append('deep-yellow')
        elif color[0] > 65:
            redefined_color.append('green')
        else:
            if color[2] > 75:
                redefined_color.append('yellow')
            else:
                redefined_color.append('green-yellow')

    print("재정의 된 색:", redefined_color)
    return redefined_color

def ripening_degree(redefined_color):
    
    ripening_percent = 0

    if redefined_color == ['green' , 'green']:
        ripening_percent = 0
    elif redefined_color == ['green-yellow', 'green-yellow']:
        ripening_percent = 25
    elif (redefined_color == ['green', 'yellow']) | (redefined_color == ['yellow', 'green']):
        ripening_percent = 50
    elif (redefined_color == ['green-yellow', 'yellow']) | (redefined_color == ['yellow', 'green-yellow']):
        ripening_percent = 50
    elif redefined_color == ['yellow', 'yellow']:
        ripening_percent = 75
    elif redefined_color == ['deep-yellow', 'deep-yellow']:
        ripening_percent = 100
    elif (redefined_color == ['deep-yellow', 'yellow']) | (redefined_color == ['yellow', 'deep-yellow']):
        ripening_percent = 100
    else:  
        ripening_percent = 0
    
    return ripening_percent



def image_color_cluster(image, k=4):
    clt = KMeans(n_clusters = k)
    clt.fit(image)

    hist = centroid_histogram(clt)
    rgb_colors = checkRGB(hist, clt.cluster_centers_)
    hsv_colors = rgb_to_hsv(rgb_colors)
    redefined_color = redefiningColors(hsv_colors)
    result = ripening_degree(redefined_color)

    return result



def ripening_function():

    input_path = './image/melon.jpg'
    output_path = './image/melon.png'

    try: 
        img = cv2.imread(input_path)
        img = imutils.resize(img, width=600)
        cv2.imwrite(input_path, img) #opencv 이미지 저장될때 BGR순으로 저장
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        input_img = img
       
    except FileNotFoundError:
        print("cannot find image files.")
    except Exception as e:
        print(f'error: {e}')
    
    output = remove(input_img)
    output = Image.fromarray(output)

    output.save(output_path)
    print("이미지 배경 제거 완료")

    # 클러스터링 하기
    img = Image.open(output_path)
    plt.imshow(img)
    image = np.array([f for f in img.getdata() if f[3] > 200], np.uint8)
    result = image_color_cluster(image)

    return result

socket_connection(host, port)
