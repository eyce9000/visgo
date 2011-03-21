package srl.visgo.gui;

import java.util.HashMap;
import java.awt.Image;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {
	private static File IMAGE_DIR = new File("image");
	static boolean loaded = false;
	static HashMap<String,Image> imageMap = new HashMap<String,Image>();
	public static void loadResources(){
		try {
			loadImages();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loaded = true;
	}
	private static void loadImages() throws IOException{
		File[] images = IMAGE_DIR.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				String[] parts = file.getName().split("\\.");
				String extension = parts[parts.length-1];
				if(extension.equalsIgnoreCase("PNG") || extension.equalsIgnoreCase("JPG")){
					return true;
				}
				return false;
			}
		});
		
		for(int i=0; i<images.length; i++){
			File imageFile = images[i];
			Image image = ImageIO.read(imageFile);
			imageMap.put(imageFile.getName(), image);
		}
	}
	public static Image getImage(String name){
		if(!loaded)loadResources();
		return imageMap.get(name);
	}
	
	public static void main(String[] args){
		loadResources();
		Image i = getImage("doc.png");
		
	}
}
