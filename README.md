# QuadtreeVision

> **Course homework: BIL212 - HW2.** This repository contains an assignment submission, not a production library.

A Java program that applies a quadtree to square PPM (P3) images for two tasks: image compression and edge detection.

## How It Works

- **Compression:** The image is recursively split into four quadrants. A region becomes a leaf when its color error is below a threshold (or it is a single pixel), and is then drawn as one flat color. A binary search finds the threshold that reaches a target compression level, measured as leaf count divided by pixel count.
- **Edge detection:** Uses a quadtree with a fixed threshold together with a convolution step to mark edges.
- **Outlines:** The optional `-t` flag draws the quadtree cell borders on the output.

## Repository Structure

```
.
├── src/
│   ├── Main.java         command-line handling, threshold search
│   ├── QuadTree.java     tree construction, rendering, edge detection
│   ├── PPMImage.java     image data holder
│   └── PPMAnalyzer.java  PPM reader and writer
├── images/               sample input images (kira.ppm, kira_copy.ppm)
└── README.md
```

## Usage

```bash
cd src
javac *.java
java Main -c -i ../images/kira.ppm -o out
```

| Flag | Meaning |
| --- | --- |
| `-i <file>` | Input PPM file (must be square) |
| `-o <name>` | Output file name prefix |
| `-c` | Compression mode (writes 8 images, `name-1.ppm` to `name-8.ppm`) |
| `-e` | Edge detection mode (writes `name.ppm`) |
| `-t` | Draw quadtree outlines on the output |

Either `-c` or `-e` is required.
