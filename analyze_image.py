
import sys
from PIL import Image

def analyze_image(input_path):
    try:
        img = Image.open(input_path)
        img = img.convert("RGBA")
        width, height = img.size
        pixels = img.load()
        
        print(f"Size: {width}x{height}")
        print(f"Top-Left: {pixels[0,0]}")
        print(f"Top-Right: {pixels[width-1,0]}")
        print(f"Bottom-Left: {pixels[0,height-1]}")
        print(f"Bottom-Right: {pixels[width-1,height-1]}")
        print(f"Center: {pixels[width//2, height//2]}")
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    analyze_image(sys.argv[1])
