
package net.shadowmage.ancientwarfare.npc.gamedata;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import com.google.common.math.IntMath;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import lotr.common.LOTRMod;
import lotr.common.world.genlayer.LOTRGenLayerWorld;
import net.minecraft.util.MathHelper;

public class States { 
	static boolean isLoaded=false; 
	public static final int scalePower = 7; 
	private static int[] biomeImageData; 
	public static final int originX = 810; 
	public static final int originZ = 730; 
	public static final int scale = IntMath.pow(2, scalePower);
	public static BufferedImage biomeImage = null;
	public static int imageWidth; 
	public static int imageHeight;

	public static void loadImage() { 
		if(!isLoaded) { 
			try {
				biomeImage = null;
				String imageName = "assets/lotr/map/states.png";
				ModContainer mc = LOTRMod.getModContainer();
				if (mc.getSource().isFile()) {
					ZipFile zip = new ZipFile(mc.getSource()); 
					Enumeration entries = zip.entries(); 
					while (entries.hasMoreElements()) {
						ZipEntry entry = (ZipEntry)entries.nextElement();
						if(entry.getName().equals(imageName)) {
							biomeImage = ImageIO.read(zip.getInputStream(entry)); } } zip.close(); } else {
								File file = new File(LOTRMod.class.getResource("/" + imageName).toURI());
								biomeImage = ImageIO.read(new FileInputStream(file)); 
								if (biomeImage == null) {throw new RuntimeException("Could not load LOTR state map image"); }						    
							}
				imageWidth = biomeImage.getWidth(); 
				imageHeight = biomeImage.getHeight();

				int[] colors = biomeImage.getRGB(0, 0, imageWidth, imageHeight, null, 0,imageWidth);
				biomeImageData = new int[imageWidth * imageHeight];
				for (int i = 0; i < colors.length; i++) {
					int color = colors[i];
					Integer biomeID = (Integer)getIDFromColor(color); 
					if (biomeID !=null) { 
						biomeImageData[i] = biomeID.intValue();
					} else {
						FMLLog.log(Level.ERROR, "Found unknown biome on map " + Integer.toHexString(color), new Object[0]);
						biomeImageData[i] = (byte)0;
					}
					isLoaded = true; 
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Integer getIDFromColor(Integer c) { 
		Color color = new Color(c);
		//System.out.println("Color. R " + color.getRed() + " G " + color.getGreen() + " B " + color.getBlue());
		if(color.getRed() == 212){return 1;}
		return 5;
	}

	private static int[] transformMapCoords(int x, int z) {
		//x -= this.posX;
		//z -= this.posY;
		//x *= this.zoomScale;
		//z *= this.zoomScale;

		x = x / LOTRGenLayerWorld.scale + 810;
		z = z / LOTRGenLayerWorld.scale + 730;
		return new int[] {x, z };
	}

	public static int getId(int x, int z) {
		loadImage();
		int[] coords = transformMapCoords(x, z);
		try { 
			int color = biomeImage.getRGB(coords[0], coords[1]);
			int id = getIDFromColor(color); 
			return id;
		}catch(Exception e) { 
			e.printStackTrace();
			return -1; 
		} 
	}

	public static int getColor(int x, int z) {
		int[] coords = transformMapCoords(x, z);
		int color = biomeImage.getRGB(coords[0], coords[1]);
		return color;
	}


	public static String getStateName(int x, int z) { 

		int id = getId(x, z);

		//int id = biomeImageData[index];
		if(id == 1) { return "Salamgard "; }
		if(id == 5) {
			int color = getColor(x, z);
			Color c = new Color(color);
			return ("Color. " + color + " R " + c.getRed() + " G " + c.getGreen() + " B " + c.getBlue());
		}
		return Integer.toString(id); 

	} 
}
