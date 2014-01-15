import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.*;

/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class quorumSenseCA extends JPanel
    implements ActionListener, WindowListener, ChangeListener, MouseListener {
    //Set up animation parameters.
    static final int FPS_MIN = 0;
    static final int FPS_MAX = 30;
    static final int FPS_INIT = 15;    //initial frames per second
    int frameNumber = 0;
    int NUM_FRAMES = 2;
    ImageIcon[] images = new ImageIcon[NUM_FRAMES];
    ImageIcon[] imagesH = new ImageIcon[NUM_FRAMES];
    ImageIcon[] imagesA = new ImageIcon[NUM_FRAMES];
    int delay;
    Timer timer;
    boolean frozen = false;
    static final int w = 300;
    static final int h = 300;
    Image img;
    static MemoryImageSource misource;
    static int[] pix = new int[w*h];
    int[] pixTmp = new int[w * h];
    ColorModel cm = getColorModel();
    JButton startStop;
    //int pixStart = 245;
    int pixStart = 255;
    Random r = new Random(3);
    float growProb = 0.0f;
    float redGrowProb = 0.2f;
    float greenGrowProb = 0.4f;
    float redWinProb = 0.3f;
    float greenWinProb = 1.0f-redWinProb;
    int[] Apix = new int[w*h];
    int[] Hpix = new int[w*h];
    float penaltyFight = 0.4f;
    float penaltyRepro = 0.4f;
    int Hthresh = 100;
    int ACthresh = 50;
    int[] ApixTmp = new int[w*h];
    int[] HpixTmp = new int[w*h];
    JLabel picture;
    JLabel pictureH;
    JLabel pictureA;
    JTextField rgpTF, ggpTF, rwpTF, htTF, actTF, acfbTF, acrlTF;

    public quorumSenseCA() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        delay = 1000 / FPS_INIT;

        //Create the label.
        JLabel sliderLabel = new JLabel("Frames Per Second", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the slider.
        JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                                              FPS_MIN, FPS_MAX, FPS_INIT);
        framesPerSecond.addChangeListener(this);

        //Turn on labels at major tick marks.
        framesPerSecond.setMajorTickSpacing(10);
        framesPerSecond.setMinorTickSpacing(1);
        framesPerSecond.setPaintTicks(true);
        framesPerSecond.setPaintLabels(true);
        //framesPerSecond.setBorder(
	//      BorderFactory.createEmptyBorder(0,0,10,0));

        //Create the label that displays the animation.
	JPanel critterPanel = new JPanel();
	critterPanel.setLayout(new BoxLayout(critterPanel,BoxLayout.PAGE_AXIS));
        picture = new JLabel();
        picture.setHorizontalAlignment(JLabel.CENTER);
        picture.setAlignmentX(Component.CENTER_ALIGNMENT);
	critterPanel.add(picture);
	JLabel critterLabel = new JLabel("bacteria");
	critterPanel.add(critterLabel);

	JPanel AIPanel = new JPanel();
	AIPanel.setLayout(new BoxLayout(AIPanel, BoxLayout.PAGE_AXIS));
        pictureH = new JLabel();
        pictureH.setHorizontalAlignment(JLabel.CENTER);
        pictureH.setAlignmentX(Component.CENTER_ALIGNMENT);
	AIPanel.add(pictureH);
	JLabel AILabel = new JLabel("autoinducers");
	AIPanel.add(AILabel);

	JPanel ACPanel = new JPanel();
	ACPanel.setLayout(new BoxLayout(ACPanel, BoxLayout.PAGE_AXIS));
        pictureA = new JLabel();
        pictureA.setHorizontalAlignment(JLabel.CENTER);
        pictureA.setAlignmentX(Component.CENTER_ALIGNMENT);
	ACPanel.add(pictureA);
	JLabel ACLabel = new JLabel("attack chemicals");
	ACPanel.add(ACLabel);

	JPanel allPics = new JPanel();
	//allPics.add(picture);
	allPics.add(critterPanel);
	//allPics.add(pictureH);
	allPics.add(AIPanel);
	//allPics.add(pictureA);
	allPics.add(ACPanel);

	// create start/stop button
	startStop = new JButton("start");
	startStop.addActionListener(this);
	
	for(int i=0; i<pix.length; i++){
	    pix[i] = 255<<24|0<<16|0<<8|0;
	    Apix[i] = 255<<24|0<<16|0<<8|0;
	    Hpix[i] = 255<<24|0<<16|0<<8|0;
	}
        updatePicture(0, pix, 0); //display first frame
        updatePicture(0, Apix, 1); //display first frame
        updatePicture(0, Hpix, 2); //display first frame

        //Put everything together.
        //add(sliderLabel);
        //add(framesPerSecond);

        //add(picture);
	//add(pictureH);
	//add(pictureA);
	add(allPics);
	add(startStop);
	
	JPanel p0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel l0 = new JLabel("red grow probability:");
	p0.add(l0);
	rgpTF = new JTextField(5);
	rgpTF.setText(Float.toString(redGrowProb));
	rgpTF.addActionListener(this);
	p0.add(rgpTF);
	add(p0);

	JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel l1 = new JLabel("green grow probability:");
	p1.add(l1);
	ggpTF = new JTextField(5);
	ggpTF.setText(Float.toString(greenGrowProb));
	ggpTF.addActionListener(this);
	p1.add(ggpTF);
	add(p1);

	JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel l2 = new JLabel("red win probability:");
	p2.add(l2);
	rwpTF = new JTextField(5);
	rwpTF.setText(Float.toString(redWinProb));
	rwpTF.addActionListener(this);
	p2.add(rwpTF);
	add(p2);

	JPanel p4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel l4 = new JLabel("hormone threshold:");
	p4.add(l4);
	htTF = new JTextField(5);
	htTF.setText(Integer.toString(Hthresh));
	htTF.addActionListener(this);
	p4.add(htTF);
	add(p4);

	JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel l5 = new JLabel("attack chemical threshold:");
	p5.add(l5);
	actTF = new JTextField(5);
	actTF.setText(Integer.toString(ACthresh));
	actTF.addActionListener(this);
	p5.add(actTF);
	add(p5);

	JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel l6 = new JLabel("attack chemical combat benefit:");
	p6.add(l6);
	acfbTF = new JTextField(5);
	acfbTF.setText(Float.toString(penaltyFight));
	acfbTF.addActionListener(this);
	p6.add(acfbTF);
	add(p6);

	JPanel p7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel l7 = new JLabel("attack chemical reproduction penalty:");
	p7.add(l7);
	acrlTF = new JTextField(5);
	acrlTF.setText(Float.toString(penaltyRepro));
	acrlTF.addActionListener(this);
	p7.add(acrlTF);
	add(p7);

        //setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Set up a timer that calls this object's action handler.
        timer = new Timer(delay, this);
        timer.setInitialDelay(delay * 7); //We pause animation twice per cycle
                                          //by restarting the timer
        timer.setCoalesce(true);
    }

    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    //React to window events.
    public void windowIconified(WindowEvent e) {
        stopAnimation();
    }
    public void windowDeiconified(WindowEvent e) {
        startAnimation();
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            int fps = (int)source.getValue();
            if (fps == 0) {
                if (!frozen) stopAnimation();
            } else {
                delay = 1000 / fps;
                timer.setDelay(delay);
                timer.setInitialDelay(delay * 10);
                if (frozen) startAnimation();
            }
        }
    }

    public void startAnimation() {
        //Start (or restart) animating!
	getParameters();
        timer.start();
        frozen = false;
    }

    public void stopAnimation() {
        //Stop the animating thread.
        timer.stop();
        frozen = true;
    }

    //Called when the Timer fires.
    public void actionPerformed(ActionEvent e) {
        //Advance the animation frame.
        /*if (frameNumber == (NUM_FRAMES - 1)) {
            frameNumber = 0;
        } else {
            frameNumber++;
        }

        updatePicture(frameNumber); //display the next picture

        if ( frameNumber==(NUM_FRAMES - 1)
          || frameNumber==(NUM_FRAMES/2 - 1) ) {
            timer.restart();
	    }*/
	if("start".equals(e.getActionCommand())){
	    startStop.setText("stop");
	    startAnimation();
	}else if("stop".equals(e.getActionCommand())){
	    startStop.setText("start");
	    stopAnimation();
	}else{
	    if(frameNumber == 0)
		frameNumber = 1;
	    else
		frameNumber = 0;
	    // need to update pix here
	    hormone();
	    attackChem();
	    fight();
	    reproduce();
	    updatePicture(frameNumber, pix, 0);
	    updatePicture(frameNumber, Hpix, 1);
	    updatePicture(frameNumber, Apix, 2);
	}
	
    }

    /** Update the label to display the image for the current frame. */
    protected void updatePicture(int frameNum, int pix[], int mode) {

	misource = new MemoryImageSource(w,h,cm,pix,0,w);
	Image i = createImage(misource);
	//System.out.println("setting iamge " + frameNumber);
	images[frameNumber] = new ImageIcon(i);

        //Set the image.
        if (images[frameNumber] != null) {
	    if(mode == 0){
		picture.setIcon(images[frameNumber]);
		picture.addMouseListener(this);
	    }else if(mode == 1){
		pictureH.setIcon(images[frameNumber]);
		pictureH.addMouseListener(this);
	    }else if(mode == 2){
		pictureA.setIcon(images[frameNumber]);
		pictureA.addMouseListener(this);
	    }
        } else { //image not found
            picture.setText("image #" + frameNumber + " not found");
        }
    }

    public void mouseClicked(MouseEvent e) {
	startStop.setText("start");
	stopAnimation();
	//System.out.println(e.getX() + " " + e.getY());
	int pad = 0;
	if(e.getX() < w+pad && e.getY() < h+pad){
	    int idx = ((e.getY()-pad)*w)+(e.getX()-pad);
	    if(SwingUtilities.isLeftMouseButton(e))
		pix[idx] = 255<<24|0<<16|pixStart<<8|0;
	    else if(SwingUtilities.isRightMouseButton(e))
		pix[idx] = 255<<24|pixStart<<16|0<<8|0;
	    if(frameNumber == 0)
		frameNumber = 1;
	    else
		frameNumber = 0;
	    updatePicture(frameNumber, pix, 0);
	    updatePicture(frameNumber, Hpix, 1);
	    updatePicture(frameNumber, Apix, 2);
	}
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("quorum sense");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        quorumSenseCA animator = new quorumSenseCA();
        animator.setOpaque(true); //content panes must be opaque
        frame.setContentPane(animator);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        //animator.startAnimation(); 
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // private utility methods

    // get input parameters from textfields
    private void getParameters(){
	redGrowProb = Float.parseFloat(rgpTF.getText());
	greenGrowProb = Float.parseFloat(ggpTF.getText());
	redWinProb = Float.parseFloat(rwpTF.getText());
	penaltyFight = Float.parseFloat(acfbTF.getText());
	penaltyRepro = Float.parseFloat(acrlTF.getText());
	Hthresh = Integer.parseInt(htTF.getText());
	ACthresh = Integer.parseInt(actTF.getText());
    }

    // return a list of open cells for cell at row and col
    // code for return array is: 1=n, 2=ne, 3=e, 4=se, 5=s, 6=sw, 7=w, 8=nw
    private ArrayList<Integer> openCells(int row, int col, int height, 
					 int width){
	int[] empty = {0,0,0};

	ArrayList<Integer> outAL = new ArrayList<Integer>();
	
	if(row != 0 && 
	   Arrays.equals(getCellValue(pix, row-1, col, height),empty))
	    outAL.add(1);
	if(row != 0 && col != width-1 && 
	   Arrays.equals(getCellValue(pix, row-1, col+1, height), empty))
	    outAL.add(2);
	if(col != width-1 && 
	   Arrays.equals(getCellValue(pix, row, col+1, height), empty))
	    outAL.add(3);
	if(row != height-1 && col != width-1 && 
	   Arrays.equals(getCellValue(pix, row+1, col+1, height), empty))
	    outAL.add(4);
	if(row != height-1 && 
	   Arrays.equals(getCellValue(pix, row+1, col, height), empty))
	    outAL.add(5);
	if(row != height-1 && col != 0 && 
	   Arrays.equals(getCellValue(pix, row+1, col-1, height), empty))
	    outAL.add(6);
	if(col != 0 && 
	   Arrays.equals(getCellValue(pix, row, col-1, height), empty))
	    outAL.add(7);
	if(row != 0 && col != 0 && 
	   Arrays.equals(getCellValue(pix, row-1, col-1, height), empty))
	    outAL.add(8);
	
	return outAL;
    }

    // return codes for cells with less value for given color than the center
    private ArrayList<Integer> lessCells(int row, int col, int height, 
					 int width, int colorIdx){
	// colorIdx is 0=red, 1=green, 2=blue
	int[] currCell = getCellValue(pix, row, col, height);
	int currVal = currCell[colorIdx];
	ArrayList<Integer> outAL = new ArrayList<Integer>();
	
	if(row != 0 && 
	   getCellValue(pix, row-1, col, height)[colorIdx] < currVal)
	    outAL.add(1);
	if(row != 0 && col != width-1 && 
	   getCellValue(pix, row-1, col+1, height)[colorIdx] < currVal)
	    outAL.add(2);
	if(col != width-1 && 
	   getCellValue(pix, row, col+1, height)[colorIdx] < currVal)
	    outAL.add(3);
	if(row != height-1 && col != width-1 && 
	   getCellValue(pix, row+1, col+1, height)[colorIdx] < currVal)
	    outAL.add(4);
	if(row != height-1 && 
	   getCellValue(pix, row+1, col, height)[colorIdx] < currVal)
	    outAL.add(5);
	if(row != height-1 && col != 0 && 
	   getCellValue(pix, row+1, col-1, height)[colorIdx] < currVal)
	    outAL.add(6);
	if(col != 0 && 
	   getCellValue(pix, row, col-1, height)[colorIdx] < currVal)
	    outAL.add(7);
	if(row != 0 && col != 0 && 
	   getCellValue(pix, row-1, col-1, height)[colorIdx] < currVal)
	    outAL.add(8);
	
	return outAL;
    }

    private ArrayList<Integer> enemyCells(int row, int col, int height, 
					  int width, String eType){
	ArrayList<Integer> outAL = new ArrayList<Integer>();
	int[] red = {255,0,0};
	int[] green = {0,255,0};

	if(row != 0 && 
	   ( (Arrays.equals(getCellValue(pix, row-1, col, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row-1, col, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(1);
	if(row != 0 && col != width-1 && 
	   //Arrays.equals(getCellValue(pix, row-1, col+1, height), empty))
	   ( (Arrays.equals(getCellValue(pix, row-1, col+1, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row-1, col+1, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(2);
	if(col != width-1 && 
	   //Arrays.equals(getCellValue(pix, row, col+1, height), empty))
	   ( (Arrays.equals(getCellValue(pix, row, col+1, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row, col+1, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(3);
	if(row != height-1 && col != width-1 && 
	   //Arrays.equals(getCellValue(pix, row+1, col+1, height), empty))
	   ( (Arrays.equals(getCellValue(pix, row+1, col+1, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row+1, col+1, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(4);
	if(row != height-1 && 
	   //Arrays.equals(getCellValue(pix, row+1, col, height), empty))
	   ( (Arrays.equals(getCellValue(pix, row+1, col, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row+1, col, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(5);
	if(row != height-1 && col != 0 && 
	   //Arrays.equals(getCellValue(pix, row+1, col-1, height), empty))
	   ( (Arrays.equals(getCellValue(pix, row+1, col-1, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row+1, col-1, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(6);
	if(col != 0 && 
	   //Arrays.equals(getCellValue(pix, row, col-1, height), empty))
	   ( (Arrays.equals(getCellValue(pix, row, col-1, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row, col-1, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(7);
	if(row != 0 && col != 0 && 
	   //Arrays.equals(getCellValue(pix, row-1, col-1, height), empty))
	   ( (Arrays.equals(getCellValue(pix, row-1, col-1, height), red) &&
	      "red".equals(eType)) ||
	     (Arrays.equals(getCellValue(pix, row-1, col-1, height), green) &&
	      "green".equals(eType)) ))
	    outAL.add(8);
	
	return outAL;
    }

    // return index of cell to grow into
    private int growCellIdx(int row, int col, int height, int cellCode){
	int idx = -1;
	if(cellCode == 1)
	    idx = ((row-1)*height)+col;
	else if(cellCode == 2)
	    idx = ((row-1)*height)+col+1;
	else if(cellCode == 3)
	    idx = (row*height)+col+1;
	else if(cellCode == 4)
	    idx = ((row+1)*height)+col+1;
	else if(cellCode == 5)
	    idx = ((row+1)*height)+col;
	else if(cellCode == 6)
	    idx = ((row+1)*height)+col-1;
	else if(cellCode == 7)
	    idx = (row*height)+col-1;
	else if(cellCode == 8)
	    idx = ((row-1)*height)+col-1;

	return idx;
    }

    private int[] getCellValue(int[] cells, int row, int col, int height){
	int[] rgb = new int[3];

	rgb[0] = (cells[(row*height)+col]>>16) & 0x000000FF;
	rgb[1] = (cells[(row*height)+col]>>8) & 0x000000FF;
        rgb[2] = cells[(row*height)+col] & 0x000000FF;

	return rgb;
    }

    private void setTmpCellValue(int row, int col, int height, 
				 int red, int green, int blue){
	pixTmp[(row*height)+col] = 255<<24|red<<16|green<<8|blue;
    }

    private void setHtmpCellValue(int row, int col, int height, 
				  int red, int green, int blue){
	if(red <= 255 && red >= 0)
	    HpixTmp[(row*height)+col] = 255<<24|red<<16|green<<8|blue;
	else
	    System.out.println("row=" + row + " col=" + col + " red=" + red);
    }

    private void reproduce_old(){
	int i, j, idx;
	int[] currPix;
	
	for (i=0;i<w;i++) {
	    for (j=0; j<h; j++){
		idx = (i*h)+j;
		currPix = getCellValue(pix,i,j,h);
		if(currPix[0] > 0 || currPix[1] > 0){
		    pixTmp[idx]=255<<24|currPix[0]<<16|currPix[1]<<8|currPix[2];
		    if(r.nextFloat() > growProb){
			ArrayList<Integer> oc = openCells(i,j,h,w);
			if(oc.size() > 0){
			    int ridx = r.nextInt(oc.size());
			    int targ = oc.get(ridx);
			    int ocIdx = growCellIdx(i, j, h, targ);
			    if(currPix[0] > 0)
				pixTmp[ocIdx] = 255<<24|255<<16|0<<8|0;
			    else if(currPix[1] > 0)
				pixTmp[ocIdx] = 255<<24|0<<16|255<<8|0;
			}
		    }
		}
	    }
	}
	System.arraycopy(pixTmp, 0, pix, 0, pix.length);
    }

    private void reproduce(){
	int i, idx;
	int[] currPix;
	int[] rc;

	ArrayList<Integer> allIdx = new ArrayList<Integer>();
	for(i=0; i<w*h; i++)
	    allIdx.add(i);
	Collections.shuffle(allIdx);
	
	for(i=0; i<w*h; i++){
	    idx = allIdx.get(i);
	    rc = idx2rc(idx, w);
	    currPix = getCellValue(pix, rc[0], rc[1], h);

	    // only one critter per cell
	    if(currPix[0] > currPix[1])
		currPix[1] = 0;
	    else
		currPix[0] = 0;

	    for(int k=0; k<2; k++){
		if(currPix[k] == 255){
		    float tmpGrowProb = 0.0f;
		    if(k == 0)
			tmpGrowProb = redGrowProb;
		    else if(k == 1)
			tmpGrowProb = greenGrowProb;
		    if(r.nextFloat() > tmpGrowProb){
			// is reproducing, so goes dormant
			if(k == 0)
			    pixTmp[idx] = 255<<24|pixStart<<16|currPix[1]<<8|
				currPix[2];
			else if(k == 1)
			    pixTmp[idx] = 255<<24|currPix[0]<<16|pixStart<<8|
				currPix[2];
			
			ArrayList<Integer> oc = openCells(rc[0], rc[1], h, w);
			if(oc.size() > 0){
			    int ridx = r.nextInt(oc.size());
			    int targ = oc.get(ridx);
			    int ocIdx = growCellIdx(rc[0], rc[1], h, targ);
			    if(k == 0)
				pixTmp[ocIdx] = 255<<24|255<<16|0<<8|0;
			    else if(k == 1)
				pixTmp[ocIdx] = 255<<24|0<<16|255<<8|0;
			}
		    }else{
			// didn't reproduce, but stays able to
			if(k == 0)
			    pixTmp[idx] = 255<<24|255<<16|currPix[1]<<8|
				currPix[2];
			else if(k == 1)
			    pixTmp[idx] = 255<<24|currPix[0]<<16|255<<8|
				currPix[2];

		    }
		}
	    }

	    if(currPix[0] > 0 && currPix[0] < 255)
		pixTmp[idx] = 255<<24|currPix[0]+1<<16|currPix[1]<<8|currPix[2];
	    if(currPix[1] > 0 && currPix[1] < 255)
		pixTmp[idx] = 255<<24|currPix[0]<<16|currPix[1]+1<<8|currPix[2];
	}
	System.arraycopy(pixTmp, 0, pix, 0, pix.length);
    }
    
    private void fight(){
	int i, j, idx;
	int[] currPix, rc, currAC;
	
        ArrayList<Integer> allIdx = new ArrayList<Integer>();
	for(i=0; i<w*h; i++)
	    allIdx.add(i);
	Collections.shuffle(allIdx);
	
	for(i=0; i<w*h; i++){
	    idx = allIdx.get(i);
	    rc = idx2rc(idx, w);
	    currPix = getCellValue(pix, rc[0], rc[1], h);
	    if(currPix[0] > 0 || currPix[1] > 0){
		pixTmp[idx] = 255<<24|currPix[0]<<16|currPix[1]<<8|
		    currPix[2];
		ArrayList<Integer> ec = new ArrayList<Integer>();
		if(currPix[0] > 0)
		    ec = enemyCells(rc[0], rc[1], h, w, "green");
		else if(currPix[1] > 0)
		    ec = enemyCells(rc[0], rc[1], h, w, "red");
		if(ec.size() > 0){
		    int ridx = r.nextInt(ec.size());
		    int targ = ec.get(ridx);
		    int ecIdx = growCellIdx(rc[0], rc[1], h, targ);
		    float tmpRedWinProb = redWinProb;
		    // get attack chem levels for cell under attack and 
		    //   adjust probability
		    currAC = getCellValue(Apix, rc[0], rc[1], h);
		    float redBonus = 0.0f;
		    if(currAC[0] > ACthresh)
			redBonus = penaltyFight;
		    float greenBonus = 0.0f;
		    if(currAC[1] > ACthresh)
			greenBonus = penaltyFight;
		    if(r.nextFloat()+greenBonus < redWinProb+redBonus)
			pixTmp[ecIdx] = 255<<24|255<<16|0<<8|0;
		    else
			pixTmp[ecIdx] = 255<<24|0<<16|255<<8|0;
		}
	    }
	}
	System.arraycopy(pixTmp, 0, pix, 0, pix.length);	
    }

    private void hormone(){
	int i, j, idx;
	int[] currPix;
	int[] currH;
	int[] rc;
	int hstep = 3;

	// release homone
	for (i=0;i<w;i++) {
	    for (j=0; j<h; j++){
		idx = (i*h)+j;
		currPix = getCellValue(pix, i, j, h);
		currH = getCellValue(Hpix, i, j, h);
		if(currPix[0] > 0 && currH[0] < 255-hstep)
		    HpixTmp[idx] = 255<<24|currH[0]+hstep<<16|currH[1]<<8|
			currH[2];
		if(currPix[1] > 0 && currH[1] < 255-hstep)
		    HpixTmp[idx] = 255<<24|currH[0]<<16|currH[1]+hstep<<8|
			currH[2];
	    }
	}
        // diffuse hormone
        ArrayList<Integer> allIdx = new ArrayList<Integer>();
	for(i=0; i<w*h; i++)
	    allIdx.add(i);
	Collections.shuffle(allIdx);
	
	for(i=0; i<w*h; i++){
	    idx = allIdx.get(i);
	    rc = idx2rc(idx, w);
	    //diffuseH(rc[0], rc[1], w, h);
	    diffuse(rc[0], rc[1], w, h, 0);
	}
	//currPix = getCellValue(HpixTmp, w/2, h/2, h);
	//System.out.println(currPix[0] + " " + currPix[1] + " " +currPix[2]);
	
	System.arraycopy(HpixTmp, 0, Hpix, 0, pix.length);	
    }

    private int rc2idx(int row, int col, int height){
	return (row*height)+col;
    }

    private int[] idx2rc(int idx, int width){
	int[] rc = {0, 0};
	rc[0] = idx / width;     // row
	rc[1] = idx % width;     // col

	return rc;
    }

    // does the code point to a valid cell from cell at row & col
    private boolean validCell(int row, int col, int width, int height, 
			      int code){
	if(row == 0 && (code == 1 || code == 2 || code == 8))
	    return false;
	if(row == height-1 && (code == 4 || code == 5 || code == 6))
	    return false;
	if(col == 0 && (code == 6 || code == 7 || code == 8))
	    return false;
	if(col == width-1 && (code == 2 || code == 3 || code == 4))
	    return false;

	return true;
    }

    private void attackChem(){
	int i, j, idx;
	int[] currPix, currH, currA, rc;
	//int Hthresh = 10;
	int astep = 10;

	// release attack chem
	for (i=0;i<w;i++) {
	    for (j=0; j<h; j++){
		idx = (i*h)+j;
		currPix = getCellValue(pix, i, j, h);
		currH = getCellValue(Hpix, i, j, h);
		currA = getCellValue(Apix, i, j, h);
		if(currPix[0] > 0 && currA[0] < 255-astep && currH[0] > Hthresh)
		    ApixTmp[idx] = 255<<24|currA[0]+astep<<16|currA[1]<<8|
			currA[2];
		if(currPix[1] > 0 && currA[1] < 255-astep && currH[1] > Hthresh)
		    ApixTmp[idx] = 255<<24|currA[0]<<16|currA[1]+astep<<8|
			currA[2];
	    }
	}
        // diffuse attack chem
        ArrayList<Integer> allIdx = new ArrayList<Integer>();
	for(i=0; i<w*h; i++)
	    allIdx.add(i);
	Collections.shuffle(allIdx);
	
	for(i=0; i<w*h; i++){
	    idx = allIdx.get(i);
	    rc = idx2rc(idx, w);
	    //diffuseA(rc[0], rc[1], w, h);
	    diffuse(rc[0], rc[1], w, h, 1);
	}
	
	System.arraycopy(ApixTmp, 0, Apix, 0, pix.length);
    }

    // mode 0=hormone, 1=attack chem.
    private void diffuse(int row, int col, int w, int h, int mode){
	int[] currPix, targPix;
	int[] CpixTmp = null;
	
	if(mode == 0)
	    CpixTmp = HpixTmp;
	else if(mode == 1)
	    CpixTmp = ApixTmp;
	else{
	    System.out.println("Error: bad mode " + mode + " in diffuse!");
	    System.exit(1);
	}

	currPix = getCellValue(CpixTmp, row, col, h);

	for(int c=0; c<2; c++){  // cycle through critter types
	    // list of neighboring cells where concentration is less
	    ArrayList<Integer> cellCodes = lessCells(row, col, h, w, 0);
	    // randomize list
	    Collections.shuffle(cellCodes);
	    // go through list moving N from center cell to neighbor if still less
	    for(int i=0; i<cellCodes.size(); i++){
		
		currPix = getCellValue(CpixTmp, row, col, h);
		if(cellCodes.get(i) == 1 && validCell(row, col, w, h, 1) && 
		   getCellValue(CpixTmp, row-1, col, h)[c] < currPix[c] &&
		   getCellValue(CpixTmp, row-1, col, h)[c] < 255 && 
		   currPix[c] > 1){
		    targPix = getCellValue(CpixTmp, row-1, col, h);
		    if(c == 0){
			setHtmpCellValue(row-1, col, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row-1, col, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		}else if(cellCodes.get(i) == 2 && 
			 validCell(row, col, w, h, 2) &&
			 getCellValue(CpixTmp, row-1, col+1, h)[c]<currPix[c] &&
			 getCellValue(CpixTmp, row-1, col+1, h)[c] < 255 &&
			 getCellValue(CpixTmp, row, col, h)[c] > 1){
		    targPix = getCellValue(CpixTmp, row-1, col+1, h);
		    if(c == 0){
			setHtmpCellValue(row-1, col+1, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row-1, col+1, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		}else if(cellCodes.get(i) == 3 && 
			 validCell(row, col, w, h, 3) &&
			 getCellValue(CpixTmp, row, col+1, h)[c] < currPix[c] &&
			 getCellValue(CpixTmp, row, col+1, h)[c] < 255 &&
			 getCellValue(CpixTmp, row, col, h)[c] > 1){
		    targPix = getCellValue(CpixTmp, row, col+1, h);
		    if(c == 0){
			setHtmpCellValue(row, col+1, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row, col+1, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		}else if(cellCodes.get(i) == 4 && 
			 validCell(row, col, w, h, 4) &&
			 getCellValue(CpixTmp, row+1, col+1, h)[c]<currPix[c] &&
			 getCellValue(CpixTmp, row+1, col+1, h)[c] < 255 &&
			 getCellValue(CpixTmp, row, col, h)[c] > 1){
		    targPix = getCellValue(CpixTmp, row+1, col+1, h);
		    if(c == 0){
			setHtmpCellValue(row+1, col+1, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row+1, col+1, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		}else if(cellCodes.get(i) == 5 && 
			 validCell(row, col, w, h, 5) &&	
			 getCellValue(CpixTmp, row+1, col, h)[c] < currPix[c] &&
			 getCellValue(CpixTmp, row+1, col, h)[c] < 255 &&
			 getCellValue(CpixTmp, row, col, h)[c] > 1){
		    targPix = getCellValue(CpixTmp, row+1, col, h);
		    if(c == 0){
			setHtmpCellValue(row+1, col, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row+1, col, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		}else if(cellCodes.get(i) == 6 && 
			 validCell(row, col, w, h, 6) &&
			 getCellValue(CpixTmp, row+1, col-1, h)[c]<currPix[c] &&
			 getCellValue(CpixTmp, row+1, col-1, h)[c] < 255 &&
			 getCellValue(CpixTmp, row, col, h)[c] > 1){
		    targPix = getCellValue(CpixTmp, row+1, col-1, h);
		    if(c == 0){
			setHtmpCellValue(row+1, col-1, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row+1, col-1, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		}else if(cellCodes.get(i) == 7 && 
			 validCell(row, col, w, h, 7) &&
			 getCellValue(CpixTmp, row, col-1, h)[c] < currPix[c] &&
			 getCellValue(CpixTmp, row, col-1, h)[c] < 255 &&
			 getCellValue(CpixTmp, row, col, h)[c] > 1){
		    targPix = getCellValue(CpixTmp, row, col-1, h);
		    if(c == 0){
			setHtmpCellValue(row, col-1, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row, col-1, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		}else if(cellCodes.get(i) == 8 && 
			 validCell(row, col, w, h, 8) &&	
			 getCellValue(CpixTmp, row-1, col-1, h)[c]<currPix[c] &&
			 getCellValue(CpixTmp, row-1, col-1, h)[c] < 255 &&
			 getCellValue(CpixTmp, row, col, h)[c] > 1){
		    targPix = getCellValue(CpixTmp, row-1, col-1, h);
		    if(c == 0){
			setHtmpCellValue(row-1, col-1, h, targPix[0]+1, 
					 targPix[1], targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0]-1, currPix[1], 
					 currPix[2]);
		    }else if(c == 1){
			setHtmpCellValue(row-1, col-1, h, targPix[0], 
					 targPix[1]+1, targPix[2]);
			setHtmpCellValue(row, col, h, currPix[0], currPix[1]-1, 
					 currPix[2]);
		    }
		    
		}
	    }
	}
    } 
}
