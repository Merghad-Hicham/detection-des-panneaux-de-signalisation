package Detect_paneaux;

import static org.opencv.imgproc.Imgproc.resize;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import org.opencv.videoio.Videoio;


public class App_detecte 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
	}
	private JFrame frame;
	private JButton loadButton;
	private JButton saveButton;
	private VideoCapture capture;
	private Mat currentImage = new Mat();
	private ImageProcessor imageProcessor = new ImageProcessor();
	private double distanceCS = 6.0;
	private double videoFPS;
	private volatile String videoPath;
	private volatile String savePath;
 	private JFormattedTextField decAmountField;
	private JLabel imageView;
	private JLabel Viewimage;
	private JFormattedTextField currentTimeField;
	private CascadeClassifier faceDetector;
	
	
	public static void main(String[] args) throws IOException {
		App_detecte app = new App_detecte();
		app.init();
		
		
	}
	
	public void init() throws IOException {
	    setSystemLookAndFeel();
	    App_detecte app = new App_detecte();
		app.initGUI();
		app.loadCascade();		
		app.runMainLoop();
	


	}
	
	private void loadCascade() {
		String cascadePath = "Detection_panneaux\\40.xml";
	    faceDetector = new CascadeClassifier(cascadePath);
	}

	private void initGUI() {
           
		frame = createJFrame("detection des panneux de signalisation");
		frame.pack();
	    frame.setLocationRelativeTo(null);
	  
	    frame.setVisible(true);
	     
	     
	}
	private JFrame createJFrame(String windowName) {
	    frame = new JFrame(windowName);
	     
	    frame.setLayout(new GridBagLayout());
        
	 
	    setupImage(frame);
	    setupVideo(frame);    
	    loadFile(frame);
	    saveFile(frame);
	    infoDectance(frame);
	     
	    currentTime(frame);   

	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    return frame;
	}
	
	private static ImageIcon createImageIcon(String path) {
	    java.net.URL imgURL = App_detecte.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	private void setupVideo(JFrame frame) {
	    imageView = new JLabel();


	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 5;
	    c.gridy = 2;
	    c.gridwidth =7;
	    c.gridheight = 9;

	    frame.add(imageView, c);

	    Mat localImage = new Mat(new Size(500, 360), CvType.CV_8UC3, new Scalar(255, 255, 255));
	    resize(localImage, localImage, new Size(500, 360));
	    updateView(localImage);
	}
	private void setupImage(JFrame frame) {
	    Viewimage = new JLabel();

	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		//c.gridheight = 2;
		frame.add(Viewimage, c);
		

	    Mat localImage = new Mat(new Size(300, 200), CvType.CV_8UC3, new Scalar(0, 0, 0));
	    resize(localImage, localImage, new Size(300, 200));
	    ViewUpdate(localImage);
	  
	}
	private void infoDectance(JFrame frame) {

	    JLabel decLabel = new JLabel("dectence :", JLabel.CENTER);
	    decLabel.setFont(new Font("defaut", Font.BOLD, 12));

	    NumberFormat numberFormat = NumberFormat.getNumberInstance();
	    decAmountField = new JFormattedTextField(numberFormat);
	    decAmountField.setValue(new Integer(0));
	    //decAmountField.setBackground(Color.YELLOW);
	    decAmountField.setEditable(false);
	    decAmountField.setPreferredSize(new Dimension(50, 20));
	    decAmountField.setHorizontalAlignment(JFormattedTextField.CENTER);

	    
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy =10;
	    c.gridwidth = 1;
	    c.insets = new Insets(0, 0, 0, 20);
	    frame.add(decLabel, c);
	    c.insets = new Insets(0, 0, 0, 40);
	    c.gridx = 1;
	    frame.add(decAmountField, c);

	   
	}

	private void loadFile(JFrame frame) {

	    JTextField field = new JTextField();
	    field.setText(" ");
	    field.setEditable(false);

	    loadButton = new JButton("Open a video", createImageIcon("Open16.gif"));

	    JFileChooser fc = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Video Files", "avi", "mp4", "mpg", "mov");
	    fc.setFileFilter(filter);
	    fc.setCurrentDirectory(new File(System.getProperty("user.home"), "Desktop"));
	    fc.setAcceptAllFileFilterUsed(false);

	    loadButton.addActionListener(event -> {
	        int returnVal = fc.showOpenDialog(null);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();

	            videoPath = file.getPath();
	            field.setText(videoPath);
	            capture = new VideoCapture(videoPath);
	            capture.read(currentImage);
	            videoFPS = capture.get(Videoio.CAP_PROP_FPS);
	            resize(currentImage, currentImage, new Size(640, 360));
	            updateView(currentImage);

	        }
	    });
	    loadButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	    field.setAlignmentX(Component.LEFT_ALIGNMENT);

	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.insets = new Insets(0, 5, 0, 5);
	    c.gridx = 3;
	    c.gridy = 0;
	    c.gridwidth = 1;
	    frame.add(loadButton, c);

	    c.insets = new Insets(0, 0, 0, 10);
	    c.gridx = 4;
	    c.gridy = 0;
	    c.gridwidth = 3;
	    frame.add(field, c);
	}

	private void saveFile(JFrame frame) {

	    JTextField field = new JTextField();
	    field.setText(" ");
	    field.setPreferredSize(new Dimension(440, 20));
	    field.setEditable(false);

	    saveButton = new JButton("Save to a file", createImageIcon("Save16.gif"));

	    JFileChooser fc = new JFileChooser();
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    fc.setCurrentDirectory(new File(System.getProperty("user.home"), "Desktop"));
	    fc.setAcceptAllFileFilterUsed(false);

	    saveButton.addActionListener(event -> {
	        int returnVal = fc.showOpenDialog(null);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {

	            File file = fc.getSelectedFile();

	            savePath = file.getPath();
	            field.setText(savePath);

	        }
	    });
	    saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	    field.setAlignmentX(Component.LEFT_ALIGNMENT);

	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;

	    c.insets = new Insets(0, 5, 0, 5);
	    c.gridx = 3;
	    c.gridy = 1;
	    c.gridwidth = 1;
	    frame.add(saveButton, c);

	    c.insets = new Insets(0, 0, 0, 10);
	    c.gridx = 4;
	    c.gridy = 1;
	    c.gridwidth = 3;
	    frame.add(field, c);
	}


	
	private void updateView(Mat image) {
	    imageView.setIcon(new ImageIcon(imageProcessor.toBufferedImage(image)));
	}
	private void ViewUpdate(Mat image) {
	    Viewimage.setIcon(new ImageIcon(imageProcessor.toBufferedImage(image)));
	}
   
	private void runMainLoop() {
	
		ImageProcessor imageProcessor = new ImageProcessor();
		Mat webcamMatImage = new Mat();  
		Image tempImage;  
		VideoCapture capture = new VideoCapture();
                capture.open("Detection_panneaux\\v1.mp4");
		capture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH,640);
		capture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT,480);
		
	
		if( capture.isOpened()){  
			while (true){  
				boolean cap = capture.read(webcamMatImage);  
				if (!cap) {
					break;
				}
				
			//	MatOfRect carDetections = new MatOfRect();
			//	faceDetector.detectMultiScale(webcamMatImage, carDetections);
                                
				if( !webcamMatImage.empty() ){   
					
					detectAll(webcamMatImage);
					tempImage= imageProcessor.toBufferedImage(webcamMatImage);
					
					ImageIcon imageIcon = new ImageIcon(tempImage, "Video capturée");
					imageView.setIcon(imageIcon);
					frame.pack();   
				} 
				else{  
					System.out.println(" -- Video n'est pas capturée -- Erreur!"); 
					break;  
				} 
			}          
		}
                
		else{
			System.out.println("Ne peut pas capturer.");
		}
		
	}
	
	private void currentTime(JFrame frame) {

	    JLabel currentTimeLabel = new JLabel("Real time:", JLabel.RIGHT);
	    currentTimeLabel.setFont(new Font("Arial", Font.BOLD, 12));

	    currentTimeField = new JFormattedTextField();
	    currentTimeField.setValue("0 sec");
	    currentTimeField.setHorizontalAlignment(JFormattedTextField.CENTER);
	    currentTimeField.setEditable(false);

	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;

	    c.gridx = 0;
	    c.gridy = 13;
	    c.gridwidth = 1;
	    c.insets = new Insets(0, 0, 0, 20);
	    frame.add(currentTimeLabel, c);
	    c.insets = new Insets(0, 0, 0, 40);
	    c.gridx = 1;
	    frame.add(currentTimeField, c);
	}






	private void setSystemLookAndFeel() {
	    try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (InstantiationException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (UnsupportedLookAndFeelException e) {
	        e.printStackTrace();
	    }
	}
	/*private void detectAndDrawFace(Mat image) {
	    MatOfRect faceDetections = new MatOfRect();
	    //faceDetector.detectMultiScale(	image, faceDetections, 1.1, 7,0,new Size(250,40),new Size());
	    faceDetector.detectMultiScale(	image, faceDetections);
	    // Draw a bounding box around each face.
	    for (Rect rect : faceDetections.toArray()) {
	        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	    }
	}*/
	
    public void detectAll(Mat img){
        detect40(img);
        //detect60(img);
        detectPassage(img);
        detectpassage1(img);
        detectInterdire(img);
        
        
    }
	
    public void detect40(Mat img){
        CascadeClassifier Detector = new CascadeClassifier();
        Detector.load("Detection_panneaux\\a40.xml");
        MatOfRect faceDetections = new MatOfRect();
        Detector.detectMultiScale(img, faceDetections);
        String filePath = "40.png";
		Mat newImage = Imgcodecs.imread(filePath);
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(255, 255,0));
            System.out.print("nombre de panneau 40:"+faceDetections.rows()+"\n");
            ViewUpdate(newImage);
        }
        
    }
  /*  public void detect60(Mat img){
        CascadeClassifier Detector = new CascadeClassifier();
        Detector.load("Detection_panneaux\\aa60.xml");
        MatOfRect faceDetections = new MatOfRect();
        Detector.detectMultiScale(img, faceDetections);
        String filePath = "60.png";
		Mat newImage = Imgcodecs.imread(filePath);
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 0,255));
            System.out.print("nombre de panneau 60 :"+faceDetections.rows()+"\n");
            ViewUpdate(newImage);
        }
    }*/
    
    public void detectInterdire(Mat img){
        CascadeClassifier Detector = new CascadeClassifier();
        Detector.load("Detection_panneaux\\int.xml");
        MatOfRect faceDetections = new MatOfRect();
        Detector.detectMultiScale(img, faceDetections);
        String filePath = "interdire.png";
		Mat newImage = Imgcodecs.imread(filePath);
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 0,255));
            System.out.print("nombre de panneau Interdire :"+faceDetections.rows()+"\n");
            ViewUpdate(newImage);
        }
    }
    
   public void detectpassage1(Mat img){
            CascadeClassifier Detector = new CascadeClassifier();
            Detector.load("Detection_panneaux\\passag.xml");
            MatOfRect faceDetections = new MatOfRect();
            Detector.detectMultiScale(img, faceDetections);
            String filePath = "passage.png";
    		Mat newImage = Imgcodecs.imread(filePath);
            for (Rect rect : faceDetections.toArray()) {
                Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 0,255));
                System.out.print("nombre de panneau passage1 :"+faceDetections.rows()+"\n");
                ViewUpdate(newImage);
            }
        
    }
    public void detectPassage(Mat img){
        CascadeClassifier Detector = new CascadeClassifier();
        Detector.load("Detection_panneaux\\passage.xml");
        MatOfRect faceDetections = new MatOfRect();
        Detector.detectMultiScale(img, faceDetections);
        String filePath = "passage.png";
		Mat newImage = Imgcodecs.imread(filePath);
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255,0));
            System.out.print("nombre de panneau passage :"+faceDetections.rows()+"\n");
            ViewUpdate(newImage);
        }
        
    }
    
   /* public void detectRend(Mat img){
        CascadeClassifier Detector = new CascadeClassifier();
        Detector.load("Detection_panneaux\\rend.xml");
        MatOfRect faceDetections = new MatOfRect();
        Detector.detectMultiScale(img, faceDetections);
        String filePath = "passage.png";
		Mat newImage = Imgcodecs.imread(filePath);
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255,0));
            System.out.print("nombre de panneau red :"+faceDetections.rows()+"\n");
            ViewUpdate(newImage);
        }
        
    }
    */
}