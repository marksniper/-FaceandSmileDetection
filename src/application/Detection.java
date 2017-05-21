package application;

import java.util.Vector;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Detection {
	
	private String path = "./foto/";
	private int numberFace;

	@SuppressWarnings("rawtypes")
	private Vector diffHis;
	private Vector<String> nameDiffHis;
	private int tresholding =800; //4855; //800;//40751 per Lbp
							   //50000 4855
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Detection(int numberFace,long timeExe) 
	{
		this.diffHis = new Vector();
		this.nameDiffHis = new Vector<String>();
		this.numberFace = numberFace;
		
		for(int i=1; i< this.numberFace +1 ; i++)	
		{
			int diffHist =0 ;
			int countMatch = 0;
			boolean noIteration = false;	
			Mat h1 = calcAndNorHis(i);
			if(i != 1 )
			{
				for(int k = 1; k < i; k++)
				{
					Mat h2 = calcAndNorHis(k);	
					diffHist = (int) Imgproc.compareHist(h1,h2,Imgproc.CV_COMP_CHISQR_ALT) ;
					if(diffHist < this.tresholding)
					{
						noIteration = true;
						break;
					}
				}
				
			}
			if(noIteration)
			{
				//System.out.println("Face already processed --> i: "+i);
			} 
			else
			{
				countMatch = 0;
				for(int  j=i+1; j< this.numberFace +1; j++ )
				{
					if(i==j)
					{
						continue;
					}
					Mat h2 = calcAndNorHis(j);
					diffHist = (int) Imgproc.compareHist(h1,h2,Imgproc.CV_COMP_CHISQR_ALT) ;
					this.diffHis.add(diffHist);
					if(diffHist <= this.tresholding)
					{
						countMatch = countMatch + 1 ;
						this.nameDiffHis.add(path+""+i+".jpg"+" ----> "+path+""+j+".jpg");
					}		
				}
				if(countMatch > 0)
				{
					new ShowResult(countMatch, this.nameDiffHis,timeExe);
				}
				else
				{
					this.nameDiffHis.add(path+""+i+".jpg"+" ---->");
					new ShowResult(countMatch, this.nameDiffHis,timeExe);
				}
				this.nameDiffHis.removeAllElements();
				//System.out.println("MAX difference histograms: "+max(this.diffHis));
			  }
			}
	}

//	private int max(Vector<Integer> diffHis2) {
//		// TODO Auto-generated method stub
//		int max = (int) diffHis2.get(0);
//		for(int i=0; i<diffHis2.size();i++)
//		{
//			int app  = (int) diffHis2.get(i);
//			if(max < app)
//			{
//				max=app;
//			}	
//		}
//		return max;
//	}

	private Mat calcAndNorHis(int i) 
	{
	  try
	  {
		Mat image = Imgcodecs.imread(path+""+i+".jpg");
	    Mat src = new Mat(image.height(), image.width(), CvType.CV_8UC2);
	    Imgproc.cvtColor(image, src, Imgproc.COLOR_RGB2GRAY);   
	    Vector<Mat> bgr_planes = new Vector<>();
	    Core.split(src, bgr_planes);// Divides a multi-channel array into several single-channel arrays.
	    MatOfInt histSize = new MatOfInt(256);
	    final MatOfFloat histRange = new MatOfFloat(0f, 256f);
	    boolean accumulate = false;
	    Mat b_hist = new  Mat();
	    Imgproc.calcHist(bgr_planes, new MatOfInt(0),new Mat(), b_hist, histSize, histRange, accumulate);
	    int hist_w = 512;
	    int hist_h = 512;   
	    Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC1);
	    Core.normalize(b_hist, b_hist);//, 3, histImage.rows(), Core.NORM_MINMAX);
		return b_hist;	
	  }
	  catch(Exception e)
		{
			System.out.println(e.getStackTrace());
			return null;
		}
	}
}
