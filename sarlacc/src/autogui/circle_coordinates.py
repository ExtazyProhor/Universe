from math import sqrt


import keyboard
from threading import Thread, Lock

import pyautogui
import pyttsx3

def speak_russian(text):
    engine = pyttsx3.init()
    voices = engine.getProperty('voices')
    for v in voices:
        if "ru" in v.languages or "Russian" in v.name:
            engine.setProperty('voice', v.id)
            break
    engine.say(text)
    engine.runAndWait()

def sq(x):
    return x * x


def resolve_r(x1, y1, x, y):
    return sqrt(sq(x1 - x) + sq(y1 - y))


def resolve_y(x2, y2, x3, y3, x):
    n1 = sq(x3) + sq(y3) - sq(x2) - sq(y2)
    d1 = 2 * y3 - 2 * y2

    n2 = x3 - x2
    d2 = y3 - y2

    return n1 / d1 - x * n2 / d2


def resolve_x(x1, y1, x2, y2, x3, y3):
    n1 = sq(x2) + sq(y2) - sq(x1) - sq(y1)
    d1 = 2 * x2 - 2 * x1
    f1 = n1 / d1

    par = (y2 - y1) / (x2 - x1)

    n2 = sq(x3) + sq(y3) - sq(x2) - sq(y2)
    d2 = 2 * y3 - 2 * y2
    f2 = n2 / d2

    n3 = x3 - x2
    d3 = y3 - y2
    f3 = n3 / d3

    return (f1 - par * f2) / (1 - par * f3)


def move_to_center(x1, y1, x2, y2, x3, y3):
    x = resolve_x(x1, y1, x2, y2, x3, y3)
    y = resolve_y(x2, y2, x3, y3, x)
    r = resolve_r(x1, y1, x, y)

    pyautogui.moveTo(x, y)
    return r


start_waiting = False
lock = Lock()
base = 1000


def wait_for_press_on_circle():
    global start_waiting
    clicks = []

    try:
        while len(clicks) < 5:
            keyboard.wait('p')
            x, y = pyautogui.position()
            clicks.append((x, y))

        dx = abs(clicks[1][0] - clicks[0][0])
        dy = abs(clicks[1][1] - clicks[0][1])
        pixels_in_base = max(dx, dy)

        radius_pixels = move_to_center(clicks[2][0], clicks[2][1], clicks[3][0], clicks[3][1], clicks[4][0], clicks[4][1])
        meters = int(radius_pixels / pixels_in_base * base)
        print(meters, meters_rus(meters))

    finally:
        with lock:
            global start_waiting
            start_waiting = False


def meters_rus(count: int):
    if 11 <= count <= 14:
        return 'метров'
    if count % 10 == 1:
        return 'метр'
    if 2 <= (count % 10) <= 4:
        return 'метра'
    return 'метров'


def on_start_tracking():
    global start_waiting
    with lock:
        if start_waiting:
            return
        start_waiting = True

    speak_russian('старт')
    Thread(target=wait_for_press_on_circle, daemon=True).start()

def main():
    keyboard.add_hotkey('u', on_start_tracking)
    keyboard.wait()


if __name__ == "__main__":
    main()
