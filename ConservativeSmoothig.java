/*
 * Copyright (C) 2021 Mattia Ferraretto 737521 <ferrar3tto.mattia@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * Questa classe implementa il flitro Conservative smoothing per le immagni in bianco e nero.
 * Fromato supportato JPG.
 *
 * @author Mattia Ferraretto 737521
 */
public class ConservativeSmoothig {


    private String path;
    private BufferedImage img;
    private int height;
    private int width;
    private int[][] pixels;

    private void readImage() throws IOException{

        img = ImageIO.read(new File(path));

        height = img.getWidth();
        width = img.getHeight();

        pixels = new int[height+2][width+2];

        for(int x = 1; x < height ; x++)
            for (int y = 1; y < width; y++)
                pixels[x][y] = img.getRGB(x, y) & 0xFF;

        for (int y = 1; y < width; y++){
            pixels[0][y] = pixels[1][y];
            pixels[height+1][y] = pixels[height][y];
        }

        for(int x = 1; x < height; x++){
            pixels[x][0] = pixels[x][1];
            pixels[x][width+1] = pixels[x][width];
        }

        pixels[0][0] = pixels[1][1];
        pixels[0][width+1] = pixels[1][width];
        pixels[height+1][0] = pixels[height][1];
        pixels[height+1][width+1] = pixels[height][width];
    }

    private void writeImage() throws IOException{

        StringTokenizer stk = new StringTokenizer(path, ".");
        String outputPath = stk.nextToken()+"-smoothed.jpg";

        File outputfile = new File(outputPath);

        ImageIO.write(img, "jpg", outputfile);
    }


    private int getMin(int x, int y){

        int min = pixels[x-1][y-1];

        for(int j = 1; j < 3; j++)
            if (pixels[x-1][(y-1)+j]< min)
                min = pixels[x-1][(y-1)+j];

        for(int j = 0; j < 3; j++)
            if (pixels[x+1][(y-1)+j]< min)
                min = pixels[x-1][(y-1)+j];

        if(pixels[x][y-1] < min)
            min = pixels[x][y-1];

        if(pixels[x][y+1] < min)
            min = pixels[x][y+1];

        return min;
    }

    private int getMax(int x, int y){

        int max = pixels[x-1][y-1];

        for(int j = 1; j < 3; j++)
            if (pixels[x-1][(y-1)+j] > max)
                max = pixels[x-1][(y-1)+j];

        for(int j = 0; j < 3; j++)
            if (pixels[x+1][(y-1)+j] > max)
                max = pixels[x-1][(y-1)+j];

        if(pixels[x][y-1] > max)
            max = pixels[x][y-1];

        if(pixels[x][y+1] > max)
            max = pixels[x][y+1];

        return max;
    }


    private void setPixelValue(int x, int y, int pxv){

        pxv =  pxv + (pxv << 8) + (pxv << 16);
        img.setRGB(x, y, pxv);
    }


    public void smoothImage(String path) throws IOException{

        this.path = path;
        readImage();

        for(int x = 1; x < height ; x++)
            for (int y = 1; y < width; y++){

                int min = getMin(x,y);
                int max = getMax(x,y);

                if(pixels[x][y] < min)
                    setPixelValue(x-1, y-1, min);
                else if(pixels[x][y] > max)
                    setPixelValue(x-1, y-1, max);
                else
                    setPixelValue(x-1, y-1, pixels[x][y]);

            }

        writeImage();
    }

    public static void main(String[] args) throws IOException{

        ConservativeSmoothig filter = new ConservativeSmoothig();

        filter.smoothImage(args[0]);
    }
}
