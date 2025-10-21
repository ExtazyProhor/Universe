import math

import keyboard
import pyautogui
from threading import Thread, Lock
import pyttsx3

start_waiting = False
lock = Lock()


def speak_russian(text):
    engine = pyttsx3.init()
    voices = engine.getProperty('voices')
    for v in voices:
        if "ru" in v.languages or "Russian" in v.name:
            engine.setProperty('voice', v.id)
            break
    engine.say(text)
    engine.runAndWait()


def wait_for_press_on_coordinates():
    global start_waiting
    clicks = []

    try:
        while len(clicks) < 4:
            keyboard.wait('p')
            x, y = pyautogui.position()
            clicks.append((x, y))

        dx = abs(clicks[1][0] - clicks[0][0])
        dy = abs(clicks[1][1] - clicks[0][1])
        m100 = max(dx, dy)

        dx = abs(clicks[3][0] - clicks[2][0])
        dy = abs(clicks[3][1] - clicks[2][1])
        distance = math.sqrt(dx ** 2 + dy ** 2)

        meters = int(distance / m100 * 100)

        speak_russian(f"{meters} {meters_rus(meters)}")
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
    Thread(target=wait_for_press_on_coordinates, daemon=True).start()


def main():
    keyboard.add_hotkey('u', on_start_tracking)
    keyboard.wait()


if __name__ == "__main__":
    main()
