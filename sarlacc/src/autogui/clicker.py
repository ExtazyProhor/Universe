import time
import pyautogui
import keyboard
import threading


def autoclicker():
    global clicking
    while True:
        if clicking:
            pyautogui.click()
            time.sleep(0.1)


def toggle_clicking():
    global clicking
    clicking = not clicking
    if clicking:
        print("autoclicker on")
    else:
        print("autoclicker off")


pyautogui.PAUSE = 0
clicking = False
keyboard.add_hotkey("F6", toggle_clicking)
threading.Thread(target=autoclicker, daemon=True).start()
keyboard.wait("esc")
