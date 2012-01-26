// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.testutil;

import static com.agical.bumblebee.junit4.Storage.store;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dtangler.swingui.resource.icons.IconKey;
import org.dtangler.swingui.resource.icons.IconProvider;
import org.dtangler.swingui.windowmanager.SwingView;

public class SnapShotTaker {

	private static boolean napshotsEnabled = false;

	public static void setNapshotsEnabled(boolean napshotsEnabled) {
		SnapShotTaker.napshotsEnabled = napshotsEnabled;
	}

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
	}

	private static JFrame createFrame(SwingView view) {
		JFrame frame = new JFrame();
		frame.setTitle(view.getTitle());
		frame.setSize(view.getPreferredSize());
		frame.getContentPane().add(view.getViewComponent());
		frame.setJMenuBar(view.getMenuBar());
		frame.setIconImage(IconProvider.getIcon(IconKey.dtangler16).getImage());
		return frame;
	}

	public static void snap(final String viewName, SwingView view) {
		if (!napshotsEnabled)
			return;
		final JFrame frame = createFrame(view);
		frame.setVisible(true);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					try {
						snap(viewName, frame);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			frame.dispose();
		}
	}

	public static void snap(String screenName, Component component)
			throws IOException {
		if (!napshotsEnabled)
			return;
		String fileName = UUID.randomUUID().toString() + ".png";
		FileOutputStream fileOutputStream = null;
		try {
			File file = new File("target/site/" + fileName);
			file.getParentFile().mkdirs();
			fileOutputStream = new FileOutputStream(file);
			BufferedImage bufferedImage = createImage(component);
			ImageIO.write(bufferedImage, "png", fileOutputStream);
			store(screenName, "\n[[" + fileName + "][]]");
		} finally {
			try {
				fileOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static BufferedImage createImage(Component component) {
		Dimension componentSize = component.getSize();
		BufferedImage img = new BufferedImage(componentSize.width,
				componentSize.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D grap = img.createGraphics();
		grap.fillRect(0, 0, img.getWidth(), img.getHeight());
		component.paint(grap);
		return img;
	}

}
