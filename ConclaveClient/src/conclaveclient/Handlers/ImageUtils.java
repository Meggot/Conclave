/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Handlers;

import java.awt.image.BufferedImage;


public class ImageUtils {

	  public static BufferedImage convertToType(BufferedImage sourceImage,
	      int targetType)
	  {
	    BufferedImage image;

	    // if the source image is already the target type, return the source image

	    if (sourceImage.getType() == targetType)
	      image = sourceImage;

	    // otherwise create a new image of the target type and draw the new
	    // image

	    else
	    {
	      image = new BufferedImage(sourceImage.getWidth(),
	          sourceImage.getHeight(), targetType);
	      image.getGraphics().drawImage(sourceImage, 0, 0, null);
	    }

	    return image;
	  }
}