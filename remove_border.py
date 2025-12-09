
import sys
from PIL import Image, ImageDraw

def process_image(input_path, output_path):
    try:
        img = Image.open(input_path)
        img = img.convert("RGBA")
        
        # Get dimensions
        width, height = img.size
        pixels = img.load()
        
        # We will flood fill from the 4 corners if they are black
        # A simple stack-based flood fill
        
        # Tolerance for "black"
        threshold = 30 
        
        queue = [(0, 0), (width-1, 0), (0, height-1), (width-1, height-1)]
        visited = set()
        
        # Check if corners are actually dark enough to eliminate
        # If a corner is white/bright, don't touch it
        start_nodes = []
        for x, y in queue:
            r, g, b, a = pixels[x, y]
            if r < threshold and g < threshold and b < threshold:
                start_nodes.append((x, y))
        
        if not start_nodes:
            print(f"Corners are not black (R={pixels[0,0][0]}), skipping flood fill.")
            img.save(output_path)
            return

        queue = start_nodes
        
        while queue:
            x, y = queue.pop(0)
            if (x, y) in visited:
                continue
            
            visited.add((x, y))
            
            r, g, b, a = pixels[x, y]
            
            # Make transparent
            pixels[x, y] = (0, 0, 0, 0)
            
            # Neighbors
            for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
                nx, ny = x + dx, y + dy
                if 0 <= nx < width and 0 <= ny < height:
                    if (nx, ny) not in visited:
                        nr, ng, nb, na = pixels[nx, ny]
                        # Check if neighbor is also black/dark
                        if nr < threshold and ng < threshold and nb < threshold:
                            queue.append((nx, ny))
                            visited.add((nx, ny)) # Add to visited immediately to prevent duplicates in queue

        img.save(output_path, "PNG")
        print(f"Successfully processed {input_path} to {output_path}")
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python script.py <input> <output>")
    else:
        process_image(sys.argv[1], sys.argv[2])
