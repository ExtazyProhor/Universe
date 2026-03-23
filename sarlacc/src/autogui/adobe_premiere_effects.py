import pyautogui
import keyboard
import threading

is_running = False
pyautogui.PAUSE = 0.1

def apply_effects_for_cs2():
    global is_running
    if is_running:
        return

    is_running = True
    print("Start of applying...")

    x, y = pyautogui.position()
    pyautogui.click(x=2373, y=265)
    pyautogui.click(x=2380, y=245)
    pyautogui.write("133,3")
    pyautogui.click(x=2377, y=202)
    pyautogui.write("1280")
    pyautogui.moveTo(x=x, y=y)

    is_running = False
    print("Applying ended")


def on_key_event(e):
    if e.name.lower() in ['k', 'л']:
        threading.Thread(target=apply_effects_for_cs2).start()


keyboard.on_press(on_key_event)
print("Script started. Press 'K' for activation, or 'Esc' for exit")
keyboard.wait('esc')
