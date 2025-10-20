import math

import keyboard
import pyautogui
import tkinter as tk
from threading import Thread
import pyttsx3

start_waiting = False


def speak_russian(text):
    engine = pyttsx3.init()
    voices = engine.getProperty('voices')
    for v in voices:
        if "ru" in v.languages or "Russian" in v.name:
            engine.setProperty('voice', v.id)
            break
    engine.say(text)
    engine.runAndWait()


def show_overlay(text: str):
    root = tk.Tk()
    root.overrideredirect(True)
    root.attributes('-topmost', True)
    root.attributes('-alpha', 0.7)
    root.configure(bg='black')

    try:
        import ctypes
        hwnd = ctypes.windll.user32.GetParent(root.winfo_id())
        styles = ctypes.windll.user32.GetWindowLongW(hwnd, -20)
        ctypes.windll.user32.SetWindowLongW(hwnd, -20, styles | 0x80000 | 0x20)
    except Exception as e:
        print("skip transparent: ", e)

    label = tk.Label(root,
                     text=text,
                     font=('Consolas', 22, 'bold'),
                     fg='lime', bg='black')
    label.pack(padx=30, pady=20)

    sw, sh = root.winfo_screenwidth(), root.winfo_screenheight()
    root.geometry(f"+{sw // 2 - 100}+{sh // 2 - 50}")

    root.after(8000, root.destroy)
    root.mainloop()


def wait_for_press_on_coordinates():
    clicks = []

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

    speak_russian(f"{meters} метров")
    show_overlay(f"{meters} метров")


def on_start_tracking():
    global start_waiting
    if not start_waiting:
        start_waiting = True
        Thread(target=wait_for_press_on_coordinates, daemon=True).start()
        start_waiting = False


def main():
    keyboard.add_hotkey('ctrl+i', on_start_tracking)
    keyboard.wait()


if __name__ == "__main__":
    main()
