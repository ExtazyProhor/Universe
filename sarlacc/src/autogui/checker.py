import pyautogui
import pynput

from idle import get_pixel_color


def on_press_x(key):
    try:
        if key.char == 'x':
            x, y = pyautogui.position()
            print(f"current cursor coordinates: x={x}, y={y}, color: {get_pixel_color(x, y)}")
    except AttributeError:
        pass


listener = pynput.keyboard.Listener(on_press=on_press_x)
listener.start()
listener.join()
