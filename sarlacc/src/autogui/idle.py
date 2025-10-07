from PIL import ImageGrab


def get_pixel_color(x, y):
    return ImageGrab.grab().getpixel((x, y))


def is_in_range(number, value_from, value_to):
    return value_from < number < value_to
