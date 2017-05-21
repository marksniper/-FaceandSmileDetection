package application;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

@SuppressWarnings("serial")
public class ShowResult extends JPanel 
{
	private JTextArea textArea = new JTextArea();
	private JPanel panel;
	private JLabel image;
	private Vector<String> smileDetect;
	private Vector<String> nameFile;
	public ShowResult(int countMatch, Vector<String> nameDiffHis, long timeExe) 
	{
		smileDetect = new Vector<String>();
		nameFile = new Vector<String>();

//		for(int i =0; i< nameDiffHis.size(); i++)
//		{
//			System.out.println("nameDiffHis.get("+i+"): "+nameDiffHis.get(i));
//		}
		
		for(int i =0; i< nameDiffHis.size(); i++)
		{
			StringTokenizer st2 = new StringTokenizer(nameDiffHis.get(i), " ----> ");
			while (st2.hasMoreElements()) {
				nameFile.add(st2.nextElement().toString());
			}
		}
		
		removeDuplicateWithOrder(nameFile);
		
		for(int k=0;k < nameFile.size();k++)
		{
					//System.out.println("nameFile("+k+"): "+nameFile.get(k));
					Smile(nameFile.get(k).toString());
		}
		String nameFileLoad ="";
		if(smileDetect.isEmpty())
		{
			textArea.setText("\nYou are sad in every time!!! :(");
			nameFileLoad = nameFile.get(nameFile.size()-1).toString();
		}
		else
		{
			textArea.setText("\nYou are happy !!! :D"+
					"\nNumber of smile: "+smileDetect.size()+
					"\nTime smiling: "+(float)((smileDetect.size())*0.033)+" s");
			nameFileLoad = smileDetect.get(smileDetect.size()-1);
		}
		panel = new JPanel(new GridLayout(0,2));
		image = new JLabel(new ImageIcon(nameFileLoad));
		textArea.setEditable(false);
		
		textArea.setText(textArea.getText()+"\n"+
				"\nImage file choosen: "+ nameFileLoad+
			    "\nTime capture: "+timeExe+" s"+
				"\nTime in scene: "+(float)((nameFile.size())*0.033)+" s"+
				"\nMatch file:");
		
		for(int k=0;k < nameFile.size();k++)
		{
			textArea.setText(textArea.getText()+"\n"+nameFile.get(k));
		}
	
		Font font = new Font("TimesRoman", Font.PLAIN, 16);
		textArea.setFont(font);
		panel.add(image);
		panel.add(textArea);
		JScrollPane jsp = new JScrollPane(panel) ;
		JFrame f = new JFrame("Result");
		f.add(jsp);
		f.setVisible(true);
		f.pack();
	}

	public JPanel getPanel() {
		return panel;
	}
	
	private static void removeDuplicateWithOrder(Vector<String> arlList)
	{
	   Set set = new HashSet();
	   List newList = new Vector<String>();
	   for (Iterator iter = arlList.iterator(); iter.hasNext();) {
	      Object element = iter.next();
	      if (set.add(element))
	         newList.add(element);
	   }
	   arlList.clear();
	   arlList.addAll(newList);
	}
	
	private void Smile(String string)
	{
		MatOfRect smiles = new MatOfRect();
		Mat grayFrame = new Mat();
		Mat image = Imgcodecs.imread(string);
		
		// convert the frame in gray scale
		Imgproc.cvtColor(image, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		CascadeClassifier smileCascade  = new CascadeClassifier();
		String smilePath = "resources/haarcascades/haarcascade_smile.xml";
		smileCascade.load(smilePath);
		int absoluteSmileSize = 25 ;
		int height = grayFrame.rows();
		if (Math.round(height * 0.2f) > 0)
		{
			absoluteSmileSize = Math.round(height * 0.01f);
		}
		smileCascade.detectMultiScale(grayFrame, smiles, 1.7, 30, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(25, 25), new Size());
		Rect[] smileArray = smiles.toArray();
		for (int i = 0; i < smileArray.length; i++)
		{
			Imgproc.rectangle(image, smileArray[i].tl(), smileArray[i].br(), new Scalar(0, 0, 255), 2);
			Imgcodecs.imwrite(string, image);
			this.smileDetect.add(string);
		}
	}
	
}
